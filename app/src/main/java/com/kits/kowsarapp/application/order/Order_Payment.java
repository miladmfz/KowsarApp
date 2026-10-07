package com.kits.kowsarapp.application.order;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.google.gson.Gson;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.base.ThirdPartyRequest;
import com.kits.kowsarapp.application.base.ThirdPartyResult;
import com.kits.kowsarapp.model.base.NumberFunctions;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.order.Order_BasketInfo;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.order.Order_APIInterface;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Handles the legacy cash/POS dialog without changing its PHP wire contract. */
public class Order_Payment extends Activity {

    private static final int REQUEST_POS = 9001;
    private static final long INVALID_AMOUNT = -1L;

    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");
    private final ThirdPartyRequest BehPardakht_pos_request = new ThirdPartyRequest();
    private final Gson gson = new Gson();
    private final Context mContext;
    private final CallMethod callMethod;
    private final Dialog dialogProg;
    private final Order_APIInterface order_apiInterface;
    private final Order_OperationJournal operationJournal;

    private Dialog dialog_payment;
    private Order_BasketInfo BehPardakht_basketInfo;
    private Button currentPaymentButton;
    private boolean paymentSubmissionInProgress;
    private boolean posLaunchInProgress;
    private boolean statusCheckInProgress;
    private String activePaymentOperationId = "";
    private String activePaymentSubject = "";
    private String activeExpectedFactorCode = "";
    private long activeBaselineReceived = INVALID_AMOUNT;
    private long activeBaselineNotReceived = INVALID_AMOUNT;
    private boolean activeIncludePreviousCash;
    private Call<RetrofitResponse> statusCall;
    private AlertDialog reconciliationDialog;
    private String payment_type = "cash";
    private String totalprice = "0";
    private String takhfif = "0";

    /** Kept public for compatibility with older callers. */
    public Call<RetrofitResponse> call;

    public Order_Payment(Context context) {
        this.mContext = context;
        this.callMethod = new CallMethod(context);
        this.order_apiInterface = APIClient
                .getCleint(callMethod.ReadString("ServerURLUse"))
                .create(Order_APIInterface.class);
        this.dialogProg = new Dialog(context);
        this.operationJournal = new Order_OperationJournal(context);
    }

    public void dialogProg() {
        if (!canUseUi()) {
            callMethod.Log("Payment progress ignored: inactive Activity");
            return;
        }
        try {
            dialogProg.setContentView(R.layout.order_spinner_box);
            if (!dialogProg.isShowing()) dialogProg.show();
        } catch (RuntimeException exception) {
            callMethod.Log("Payment progress show failed: " + exception.getClass().getSimpleName());
        }
    }

    public void BasketInfopayment(Order_BasketInfo basketInfo) {
        showPaymentDialog(basketInfo, false);
    }

    /** Legacy variant retained for binary/source compatibility. */
    public void BasketInfopayment1(Order_BasketInfo basketInfo) {
        showPaymentDialog(basketInfo, true);
    }

    private void showPaymentDialog(Order_BasketInfo basketInfo, boolean includePreviousCash) {
        if (!canUseUi()) {
            callMethod.Log("Payment dialog ignored: inactive Activity");
            return;
        }
        if (paymentSubmissionInProgress
                || posLaunchInProgress
                || statusCheckInProgress
                || (activePaymentOperationId != null && !activePaymentOperationId.isEmpty())) {
            showMessage("درخواست پرداخت در حال انجام است");
            return;
        }
        if (basketInfo == null) {
            showMessage("اطلاعات پرداخت معتبر نیست");
            callMethod.Log("Payment dialog rejected: basketInfo is null");
            return;
        }

        Long sumPrice = parseServerAmount("SumPrice", basketInfo.getSumPrice(), false);
        Long taxAndMayor = parseServerAmount("SumTaxAndMayor", basketInfo.getSumTaxAndMayor(), false);
        Long received = parseServerAmount("Received", basketInfo.getReceived(), false);
        Long notReceived = parseServerAmount("NotReceived", basketInfo.getNotReceived(), false);
        Long decrement = parseServerAmount("DecrementValue", basketInfo.getDecrementValue(), true);
        long total = sumPrice == null || taxAndMayor == null
                ? INVALID_AMOUNT
                : Order_ValueParser.addOrDefault(sumPrice, taxAndMayor, INVALID_AMOUNT);

        if (received == null || notReceived == null || decrement == null || total < 0) {
            showMessage("مبالغ فاکتور معتبر نیست؛ اطلاعات را دوباره دریافت کنید");
            callMethod.Log("Payment dialog rejected: invalid basket amounts");
            return;
        }

        BehPardakht_basketInfo = basketInfo;
        activeIncludePreviousCash = includePreviousCash;
        payment_type = "cash";
        totalprice = String.valueOf(total);
        takhfif = "0";
        paymentSubmissionInProgress = false;
        posLaunchInProgress = false;

        dialog_payment = new Dialog(mContext);
        dialog_payment.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (dialog_payment.getWindow() != null) {
            dialog_payment.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        dialog_payment.setContentView(R.layout.order_payment_box);

        Button btnPayment = dialog_payment.findViewById(R.id.ord_payment_b_btn_final);
        Button btnToCash = dialog_payment.findViewById(R.id.ord_payment_b_btn_cash);
        Button btnToPos = dialog_payment.findViewById(R.id.ord_payment_b_btn_pos);

        LinearLayoutCompat llEdPay = dialog_payment.findViewById(R.id.ord_payment_b_ll_edpay);
        LinearLayoutCompat llEdLabel = dialog_payment.findViewById(R.id.ord_payment_b_ll_edlable);
        LinearLayoutCompat llNotReceived = dialog_payment.findViewById(R.id.ord_payment_b_ll_notreceived);
        LinearLayoutCompat llReceived = dialog_payment.findViewById(R.id.ord_payment_b_ll_received);
        LinearLayoutCompat llCashToPay = dialog_payment.findViewById(R.id.ord_payment_b_ll_cashtopay);
        LinearLayoutCompat llPosToPay = dialog_payment.findViewById(R.id.ord_payment_b_ll_postopay);
        LinearLayoutCompat llDecrement = dialog_payment.findViewById(R.id.ord_payment_b_ll_decrement);

        TextView tvSumPrice = dialog_payment.findViewById(R.id.ord_payment_b_sumprice);
        TextView tvTaxAndMayor = dialog_payment.findViewById(R.id.ord_payment_b_sumtaxmayor);
        TextView tvTotalPrice = dialog_payment.findViewById(R.id.ord_payment_b_totalprice);
        TextView tvReceived = dialog_payment.findViewById(R.id.ord_payment_b_received);
        TextView tvNotReceived = dialog_payment.findViewById(R.id.ord_payment_b_notreceived);
        TextView tvDecrement = dialog_payment.findViewById(R.id.ord_payment_b_decrement);

        EditText edAmount = dialog_payment.findViewById(R.id.ord_payment_b_mablagh);
        EditText edSellOff = dialog_payment.findViewById(R.id.ord_payment_b_selloff);
        EditText edCashToPay = dialog_payment.findViewById(R.id.ord_payment_b_cashtopay);
        EditText edPosToPay = dialog_payment.findViewById(R.id.ord_payment_b_postopay);

        boolean hasReceived = received > 0;
        long absoluteDecrement = safeAbsolute(decrement);
        llDecrement.setVisibility(absoluteDecrement > 0 ? View.VISIBLE : View.GONE);
        llEdPay.setVisibility(hasReceived ? View.GONE : View.VISIBLE);
        llEdLabel.setVisibility(hasReceived ? View.GONE : View.VISIBLE);
        llNotReceived.setVisibility(hasReceived ? View.VISIBLE : View.GONE);
        llReceived.setVisibility(hasReceived ? View.VISIBLE : View.GONE);
        llCashToPay.setVisibility(hasReceived ? View.GONE : View.VISIBLE);
        llPosToPay.setVisibility(View.GONE);

        tvSumPrice.setText(formatAmount(sumPrice));
        tvTaxAndMayor.setText(formatAmount(taxAndMayor));
        tvTotalPrice.setText(formatAmount(total));
        tvReceived.setText(formatAmount(received));
        tvNotReceived.setText(formatAmount(notReceived));
        tvDecrement.setText(formatAmount(decrement));

        long initialPayable = hasReceived ? notReceived : total;
        setAmount(edAmount, total);
        setAmount(edCashToPay, initialPayable);
        setAmount(edPosToPay, initialPayable);
        edSellOff.setText(NumberFunctions.PerisanNumber("0"));

        selectAllOnClick(edAmount);
        selectAllOnClick(edSellOff);
        selectAllOnClick(edCashToPay);
        selectAllOnClick(edPosToPay);

        if (callMethod.ReadBoolan("PosPayment")) {
            payment_type = "pos";
            if (!hasReceived) llPosToPay.setVisibility(View.VISIBLE);
            llCashToPay.setVisibility(View.GONE);
            btnToPos.setBackgroundColor(mContext.getResources().getColor(R.color.blue_500));
            btnToCash.setBackgroundColor(mContext.getResources().getColor(R.color.gray_secondary));
        }

        btnToCash.setOnClickListener(view -> {
            if (paymentSubmissionInProgress || posLaunchInProgress || statusCheckInProgress) return;
            payment_type = "cash";
            btnToCash.setBackgroundColor(mContext.getResources().getColor(R.color.blue_500));
            btnToPos.setBackgroundColor(mContext.getResources().getColor(R.color.gray_secondary));
            boolean hideForLegacyVariant = includePreviousCash && hasReceived;
            llCashToPay.setVisibility(hideForLegacyVariant ? View.GONE : View.VISIBLE);
            llPosToPay.setVisibility(hideForLegacyVariant ? View.VISIBLE : View.GONE);
        });

        btnToPos.setOnClickListener(view -> {
            if (paymentSubmissionInProgress || posLaunchInProgress || statusCheckInProgress) return;
            String posName = callMethod.ReadString("PosName");
            if (posName == null || posName.trim().length() <= 1) {
                btnToCash.callOnClick();
                showMessage("پوزی انتخاب نشده است");
                return;
            }
            payment_type = "pos";
            llCashToPay.setVisibility(View.GONE);
            llPosToPay.setVisibility(hasReceived && includePreviousCash ? View.GONE : View.VISIBLE);
            btnToPos.setBackgroundColor(mContext.getResources().getColor(R.color.blue_500));
            btnToCash.setBackgroundColor(mContext.getResources().getColor(R.color.gray_secondary));
        });

        addAmountWatcher(edAmount, edSellOff, edCashToPay, edPosToPay, total);
        addDiscountWatcher(edSellOff, edAmount, edCashToPay, edPosToPay, total);

        btnPayment.setOnClickListener(view -> submitPayment(
                basketInfo,
                includePreviousCash,
                hasReceived,
                notReceived,
                absoluteDecrement,
                edSellOff,
                edCashToPay,
                edPosToPay,
                btnPayment
        ));

        try {
            dialog_payment.show();
        } catch (RuntimeException exception) {
            callMethod.Log("Payment dialog show failed: " + exception.getClass().getSimpleName());
            showMessage("نمایش پنجره پرداخت ممکن نیست");
        }
    }

    private void submitPayment(
            Order_BasketInfo basketInfo,
            boolean includePreviousCash,
            boolean hasReceived,
            long notReceived,
            long absoluteDecrement,
            EditText edSellOff,
            EditText edCashToPay,
            EditText edPosToPay,
            Button paymentButton
    ) {
        if (paymentSubmissionInProgress || posLaunchInProgress || statusCheckInProgress) {
            showMessage("درخواست پرداخت در حال انجام است");
            return;
        }

        currentPaymentButton = paymentButton;
        if (startUnknownPaymentReconciliationIfNeeded(basketInfo)) return;

        int maxSellOff = Order_ValueParser.intInRangeOrDefault(
                callMethod.ReadString("MaxSellOff"), 0, 100, 0);
        int sellOff = hasReceived ? 0 : Order_ValueParser.intInRangeOrDefault(
                normalizedNumber(edSellOff.getText().toString()), 0, maxSellOff, -1);
        long total = Order_ValueParser.nonNegativeLongOrDefault(totalprice, INVALID_AMOUNT);
        long discount = Order_ValueParser.percentageOfOrDefault(total, sellOff, INVALID_AMOUNT);
        if (sellOff < 0 || discount < 0) {
            edSellOff.setError("درصد تخفیف معتبر نیست");
            showMessage("درصد تخفیف را اصلاح کنید");
            return;
        }
        takhfif = includePreviousCash ? "0" : String.valueOf(discount);

        if ("pos".equals(payment_type)) {
            long posAmount = hasReceived ? notReceived : readInputAmount(edPosToPay);
            if (posAmount < 0) {
                edPosToPay.setError("مبلغ معتبر نیست");
                showMessage("مبلغ پرداخت معتبر نیست");
                return;
            }
            startPosPayment(String.valueOf(posAmount));
            return;
        }

        long cashAmount;
        if (hasReceived) {
            cashAmount = notReceived;
            if (includePreviousCash) {
                Long previousCash = parseServerAmount(
                        "ShopNaghdReceive", basketInfo.getShopNaghdReceive(), false);
                if (previousCash == null) {
                    showMessage("مبلغ نقدی قبلی معتبر نیست");
                    return;
                }
                cashAmount = Order_ValueParser.addOrDefault(
                        cashAmount, previousCash, INVALID_AMOUNT);
            }
        } else {
            cashAmount = readInputAmount(edCashToPay);
        }
        if (cashAmount < 0) {
            edCashToPay.setError("مبلغ معتبر نیست");
            showMessage("مبلغ پرداخت معتبر نیست");
            return;
        }

        String decrementToSend = hasReceived
                ? String.valueOf(absoluteDecrement)
                : (includePreviousCash ? "0" : String.valueOf(discount));
        if (!acquirePaymentGuard(basketInfo)) return;
        beginPaymentOperation(Order_OperationJournal.Type.PAYMENT_CASH, basketInfo);
        markActivePayment(Order_OperationStateMachine.State.SUBMITTING, false);
        try {
            Call<RetrofitResponse> paymentCall = order_apiInterface.Factor_Payment_Cash(
                    "Factor_Payment_Cash",
                    basketInfo.getFactorCode(),
                    String.valueOf(cashAmount),
                    "0",
                    decrementToSend
            );
            enqueuePayment(paymentCall, false);
        } catch (RuntimeException exception) {
            markActivePayment(Order_OperationStateMachine.State.FAILED, true);
            setPaymentButtonEnabled(true);
            callMethod.Log("Cash payment request creation failed: "
                    + exception.getClass().getSimpleName());
            showMessage("ایجاد درخواست پرداخت ممکن نیست");
        }
    }

    @SuppressLint("SetTextI18n")
    private void addAmountWatcher(
            EditText amount,
            EditText sellOff,
            EditText cash,
            EditText pos,
            long total
    ) {
        amount.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!amount.hasFocus()) return;
                long entered = readInputAmount(amount);
                if (entered < 0 || total <= 0) {
                    amount.setError("مبلغ معتبر نیست");
                    return;
                }
                int percent = entered >= total
                        ? 0
                        : (int) Math.max(0, Math.min(100,
                        ((total - entered) * 100.0d) / total));
                sellOff.setText(NumberFunctions.PerisanNumber(String.valueOf(percent)));
                setAmount(cash, entered);
                setAmount(pos, entered);
            }
        });
    }

    private void addDiscountWatcher(
            EditText sellOff,
            EditText amount,
            EditText cash,
            EditText pos,
            long total
    ) {
        sellOff.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!sellOff.hasFocus()) return;
                int max = Order_ValueParser.intInRangeOrDefault(
                        callMethod.ReadString("MaxSellOff"), 0, 100, 0);
                int percent = Order_ValueParser.intInRangeOrDefault(
                        normalizedNumber(sellOff.getText().toString()), 0, max, -1);
                if (percent < 0) {
                    sellOff.setError("حداکثر تخفیف " + max + " درصد است");
                    return;
                }
                long discount = Order_ValueParser.percentageOfOrDefault(
                        total, percent, INVALID_AMOUNT);
                long payable = Order_ValueParser.subtractOrDefault(
                        total, discount, INVALID_AMOUNT);
                if (payable < 0) {
                    sellOff.setError("محاسبه مبلغ ممکن نیست");
                    return;
                }
                setAmount(amount, payable);
                setAmount(cash, payable);
                setAmount(pos, payable);
            }
        });
    }

    public void BasketInfopayment_request(ThirdPartyResult result, String resultJson) {
        posLaunchInProgress = false;
        if (result == null || BehPardakht_basketInfo == null) {
            markActivePayment(Order_OperationStateMachine.State.FAILED, true);
            showMessage("نتیجه پرداخت معتبر نیست");
            callMethod.Log("POS persistence rejected: missing result or basket");
            setPaymentButtonEnabled(true);
            return;
        }

        markActivePayment(Order_OperationStateMachine.State.SUBMITTING, false);
        try {
            Call<RetrofitResponse> paymentCall = order_apiInterface.Factor_Payment_Pos_new(
                    "Factor_Payment_Pos",
                    BehPardakht_basketInfo.getFactorCode(),
                    callMethod.ReadString("PosCode"),
                    result.transactionAmount,
                    takhfif,
                    result.sessionId,
                    result.resultCode,
                    result.resultDescription,
                    result.transactionAmount,
                    result.referenceID,
                    result.retrievalReferencedNumber,
                    result.maskedCardNumber,
                    result.terminalID,
                    result.dateOfTransaction,
                    result.timeOfTransaction,
                    result.echoData,
                    resultJson,
                    "",
                    BehPardakht_basketInfo.getAppBasketInfoCode()
            );
            enqueuePayment(paymentCall, true);
        } catch (RuntimeException exception) {
            markActivePayment(Order_OperationStateMachine.State.FAILED, true);
            setPaymentButtonEnabled(true);
            callMethod.Log("POS persistence request creation failed: "
                    + exception.getClass().getSimpleName());
            showMessage("ایجاد درخواست ثبت پرداخت ممکن نیست");
        }
    }

    private void enqueuePayment(Call<RetrofitResponse> paymentCall, boolean posPayment) {
        if (paymentCall == null) {
            markActivePayment(Order_OperationStateMachine.State.FAILED, true);
            showMessage("ایجاد درخواست پرداخت ممکن نیست");
            setPaymentButtonEnabled(true);
            return;
        }
        if (paymentSubmissionInProgress) {
            markActivePayment(Order_OperationStateMachine.State.CANCELED, true);
            showMessage("درخواست پرداخت در حال انجام است");
            return;
        }
        if (!canUseUi()) {
            paymentCall.cancel();
            markActivePayment(Order_OperationStateMachine.State.CANCELED, true);
            callMethod.Log("Payment enqueue ignored: inactive Activity");
            return;
        }

        paymentSubmissionInProgress = true;
        setPaymentButtonEnabled(false);
        call = paymentCall;
        dialogProg();
        try {
            paymentCall.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(
                        @NotNull Call<RetrofitResponse> completedCall,
                        @NotNull Response<RetrofitResponse> response
                ) {
                    if (completedCall != call) return;
                    Order_ApiResult result = posPayment
                            ? Order_ApiResult.posPayment(response)
                            : Order_ApiResult.cashPayment(response);
                    finishPaymentRequest(result, completedCall.isCanceled());
                }

                @Override
                public void onFailure(
                        @NotNull Call<RetrofitResponse> failedCall,
                        @NotNull Throwable throwable
                ) {
                    if (failedCall != call) return;
                    finishPaymentRequest(
                            Order_ApiResult.networkFailure(throwable),
                            failedCall.isCanceled()
                    );
                }
            });
        } catch (RuntimeException exception) {
            finishPaymentRequest(Order_ApiResult.networkFailure(exception), false);
        }
    }

    private void finishPaymentRequest(Order_ApiResult result, boolean canceled) {
        paymentSubmissionInProgress = false;
        call = null;

        if (canceled) {
            markActivePayment(Order_OperationStateMachine.State.UNKNOWN, true);
            finishPaymentUiAfterUnknown("نتیجه ثبت پرداخت مشخص نیست؛ پیش از تلاش دوباره وضعیت فاکتور را بررسی کنید");
            return;
        }
        if (!canUseUi()) {
            markActivePayment(Order_OperationStateMachine.State.UNKNOWN, true);
            callMethod.Log("Payment callback ignored: inactive Activity");
            return;
        }
        if (result != null && result.isSuccess()) {
            markActivePayment(Order_OperationStateMachine.State.VERIFYING, false);
            startSubmittedPaymentVerification();
            return;
        }

        boolean ambiguous = result == null || result.isAmbiguousMutationResult();
        if (ambiguous) {
            markActivePayment(Order_OperationStateMachine.State.UNKNOWN, true);
            finishPaymentUiAfterUnknown(
                    "نتیجه ثبت پرداخت مشخص نیست؛ پیش از تلاش دوباره وضعیت فاکتور بررسی می‌شود");
            return;
        }

        markActivePayment(Order_OperationStateMachine.State.FAILED, true);
        safeDismiss(dialogProg, "progress");
        setPaymentButtonEnabled(true);
        callMethod.Log("Payment failed: " + result.getTechnicalReason());
        if (result.getStatus() == Order_ApiResult.Status.SERVER_ERROR) {
            showMessage("سرور پرداخت را تأیید نکرد؛ وضعیت فاکتور را بررسی کنید");
        } else {
            showMessage("پاسخ سرور پرداخت معتبر نیست");
        }
    }

    public void dissmiss_all() {
        markActivePayment(Order_OperationStateMachine.State.UNKNOWN, true);
        posLaunchInProgress = false;
        paymentSubmissionInProgress = false;
        statusCheckInProgress = false;
        setPaymentButtonEnabled(true);
        safeDismiss(dialog_payment, "payment");
        safeDismiss(dialogProg, "progress");
        safeDismiss(reconciliationDialog, "reconciliation");
    }

    public void cancelPending() {
        if (call != null) call.cancel();
        if (statusCall != null) statusCall.cancel();
        call = null;
        statusCall = null;
        dissmiss_all();
    }

    /** Records a definitive POS decline or an ambiguous missing external result. */
    public void rejectPosResult(boolean resultMissing) {
        markActivePayment(
                resultMissing
                        ? Order_OperationStateMachine.State.UNKNOWN
                        : Order_OperationStateMachine.State.FAILED,
                true
        );
        dissmiss_all();
    }

    private boolean startUnknownPaymentReconciliationIfNeeded(Order_BasketInfo basketInfo) {
        String subject = paymentSubject(basketInfo);
        if (subject.isEmpty()) {
            showMessage("شناسه فاکتور برای بررسی پرداخت معتبر نیست");
            return true;
        }
        Order_OperationJournal.Entry unknown;
        try {
            unknown = operationJournal.findLatestUnknownPayment(subject);
        } catch (RuntimeException exception) {
            callMethod.Log("Payment reconciliation lookup unavailable: "
                    + exception.getClass().getSimpleName());
            showMessage("بررسی ایمنی پرداخت قبلی ممکن نیست؛ دوباره تلاش کنید");
            return true;
        }
        if (unknown == null) return false;
        if (!acquirePaymentGuard(basketInfo)) return true;
        startPaymentStatusQuery(unknown, false);
        return true;
    }

    private void startSubmittedPaymentVerification() {
        Order_OperationJournal.Entry active = operationJournal.get(activePaymentOperationId);
        if (active == null) {
            callMethod.Log("Payment verification continuing without readable audit entry");
        }
        startPaymentStatusQuery(active, true);
    }

    private void startPaymentStatusQuery(
            Order_OperationJournal.Entry operation,
            boolean afterSubmission
    ) {
        String basketCode = BehPardakht_basketInfo == null
                ? ""
                : safeTrim(BehPardakht_basketInfo.getAppBasketInfoCode());
        String expectedFactor = afterSubmission
                ? activeExpectedFactorCode
                : BehPardakht_basketInfo == null
                ? ""
                : safeTrim(BehPardakht_basketInfo.getFactorCode());
        if (basketCode.isEmpty() || expectedFactor.isEmpty()) {
            finishUnavailableStatusCheck(afterSubmission,
                    "شناسه فاکتور برای بررسی پرداخت معتبر نیست");
            return;
        }

        statusCheckInProgress = true;
        setPaymentButtonEnabled(false);
        dialogProg();
        try {
            statusCall = order_apiInterface.OrderGetSummmary(
                    "OrderGetSummmary", basketCode);
            statusCall.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(
                        @NotNull Call<RetrofitResponse> completedCall,
                        @NotNull Response<RetrofitResponse> response
                ) {
                    if (completedCall != statusCall) return;
                    statusCall = null;
                    Order_BasketInfo current = firstBasket(response);
                    if (current == null) {
                        finishUnavailableStatusCheck(afterSubmission,
                                "وضعیت فعلی فاکتور از سرور دریافت نشد");
                        return;
                    }

                    Order_PaymentReconciliation.Evaluation evaluation = afterSubmission
                            ? Order_PaymentReconciliation.afterSubmission(
                                    expectedFactor,
                                    activeBaselineReceived,
                                    activeBaselineNotReceived,
                                    current.getFactorCode(),
                                    normalizedNumber(current.getReceived()),
                                    normalizedNumber(current.getNotReceived()))
                            : Order_PaymentReconciliation.beforeRetry(
                                    expectedFactor,
                                    current.getFactorCode(),
                                    normalizedNumber(current.getReceived()),
                                    normalizedNumber(current.getNotReceived()));

                    if (afterSubmission) {
                        finishSubmittedPaymentVerification(evaluation, current);
                    } else {
                        finishUnknownPaymentReconciliation(operation, evaluation, current);
                    }
                }

                @Override
                public void onFailure(
                        @NotNull Call<RetrofitResponse> failedCall,
                        @NotNull Throwable throwable
                ) {
                    if (failedCall != statusCall) return;
                    statusCall = null;
                    finishUnavailableStatusCheck(afterSubmission,
                            "ارتباط برای بررسی وضعیت فاکتور برقرار نشد");
                }
            });
        } catch (RuntimeException exception) {
            statusCall = null;
            callMethod.Log("Payment status query failed before enqueue: "
                    + exception.getClass().getSimpleName());
            finishUnavailableStatusCheck(afterSubmission,
                    "شروع بررسی وضعیت فاکتور ممکن نیست");
        }
    }

    private void finishSubmittedPaymentVerification(
            Order_PaymentReconciliation.Evaluation evaluation,
            Order_BasketInfo current
    ) {
        statusCheckInProgress = false;
        safeDismiss(dialogProg, "progress");
        setPaymentButtonEnabled(true);
        if (evaluation.decision == Order_PaymentReconciliation.Decision.CONFIRMED_SETTLED
                || evaluation.decision == Order_PaymentReconciliation.Decision.CONFIRMED_PROGRESS) {
            BehPardakht_basketInfo = current;
            markActivePayment(Order_OperationStateMachine.State.SUCCEEDED, true);
            safeDismiss(dialog_payment, "payment");
            showMessage("ثبت پرداخت با وضعیت جدید فاکتور تأیید شد");
            return;
        }

        markActivePayment(Order_OperationStateMachine.State.UNKNOWN, true);
        showMessage("اثر پرداخت روی فاکتور تأیید نشد؛ تلاش دوباره فقط بعد از بررسی مجاز است");
    }

    private void finishUnknownPaymentReconciliation(
            Order_OperationJournal.Entry operation,
            Order_PaymentReconciliation.Evaluation evaluation,
            Order_BasketInfo current
    ) {
        statusCheckInProgress = false;
        safeDismiss(dialogProg, "progress");
        if (evaluation.decision == Order_PaymentReconciliation.Decision.CONFIRMED_SETTLED) {
            transitionRecordedOperation(
                    operation.operationId,
                    Order_OperationStateMachine.State.RECONCILED_SETTLED
            );
            releasePaymentGuard();
            setPaymentButtonEnabled(true);
            safeDismiss(dialog_payment, "payment");
            showMessage("طبق وضعیت فعلی سرور، مانده فاکتور تسویه شده است");
            return;
        }
        if (evaluation.decision != Order_PaymentReconciliation.Decision.REVIEW_REQUIRED) {
            releasePaymentGuard();
            setPaymentButtonEnabled(true);
            showMessage("وضعیت فاکتور برای تصمیم‌گیری معتبر نیست؛ پرداخت دوباره مسدود ماند");
            return;
        }

        showManualReconciliationDialog(operation, current, evaluation.outstandingAmount);
    }

    private void showManualReconciliationDialog(
            Order_OperationJournal.Entry operation,
            Order_BasketInfo current,
            long outstandingAmount
    ) {
        if (!canUseUi()) {
            releasePaymentGuard();
            return;
        }
        statusCheckInProgress = true;
        final boolean[] reopenWithFreshStatus = {false};
        boolean wasPos = Order_OperationJournal.Type.PAYMENT_POS.name().equals(operation.type);
        String warning = wasPos
                ? "نتیجه ثبت قبلی نامشخص است و ممکن است مبلغ از کارت کسر شده باشد. "
                + "مانده فعلی سرور: " + formatAmount(outstandingAmount)
                + ". فقط پس از بررسی رسید پوز و فاکتور اجازه تلاش جدید بدهید."
                : "نتیجه ثبت قبلی نامشخص است. مانده فعلی سرور: "
                + formatAmount(outstandingAmount)
                + ". فقط پس از بررسی صندوق و فاکتور اجازه تلاش جدید بدهید.";
        try {
            reconciliationDialog = new AlertDialog.Builder(mContext, R.style.AlertDialogCustom)
                    .setTitle("بررسی پرداخت قبلی")
                    .setMessage(warning)
                    .setPositiveButton("بررسی شد؛ اجازه تلاش جدید", (dialog, which) -> {
                        Order_OperationStateMachine.TransitionResult result =
                                transitionRecordedOperation(
                                        operation.operationId,
                                        Order_OperationStateMachine.State.RECONCILED_NOT_APPLIED);
                        reopenWithFreshStatus[0] =
                                result == Order_OperationStateMachine.TransitionResult.APPLIED;
                    })
                    .setNegativeButton("لغو", null)
                    .create();
            reconciliationDialog.setOnDismissListener(dialog -> {
                reconciliationDialog = null;
                statusCheckInProgress = false;
                releasePaymentGuard();
                setPaymentButtonEnabled(true);
                if (reopenWithFreshStatus[0] && canUseUi()) {
                    safeDismiss(dialog_payment, "payment");
                    showPaymentDialog(current, activeIncludePreviousCash);
                    showMessage("وضعیت تازه فاکتور بارگیری شد؛ در صورت نیاز دوباره پرداخت کنید");
                }
            });
            reconciliationDialog.show();
        } catch (RuntimeException exception) {
            reconciliationDialog = null;
            statusCheckInProgress = false;
            releasePaymentGuard();
            setPaymentButtonEnabled(true);
            callMethod.Log("Payment reconciliation dialog failed: "
                    + exception.getClass().getSimpleName());
            showMessage("نمایش بررسی پرداخت قبلی ممکن نیست");
        }
    }

    private void finishUnavailableStatusCheck(boolean afterSubmission, String message) {
        statusCheckInProgress = false;
        safeDismiss(dialogProg, "progress");
        setPaymentButtonEnabled(true);
        if (afterSubmission) {
            markActivePayment(Order_OperationStateMachine.State.UNKNOWN, true);
        } else {
            releasePaymentGuard();
        }
        showMessage(message + "؛ پرداخت دوباره به‌صورت خودکار انجام نمی‌شود");
    }

    private void finishPaymentUiAfterUnknown(String message) {
        safeDismiss(dialogProg, "progress");
        setPaymentButtonEnabled(true);
        showMessage(message);
    }

    private Order_BasketInfo firstBasket(Response<RetrofitResponse> response) {
        if (response == null || !response.isSuccessful() || response.body() == null
                || response.body().getBasketInfos() == null
                || response.body().getBasketInfos().isEmpty()) {
            return null;
        }
        return response.body().getBasketInfos().get(0);
    }

    private Order_OperationStateMachine.TransitionResult transitionRecordedOperation(
            String operationId,
            Order_OperationStateMachine.State state
    ) {
        try {
            return operationJournal.transition(operationId, state);
        } catch (RuntimeException exception) {
            callMethod.Log("Payment reconciliation transition unavailable: "
                    + exception.getClass().getSimpleName());
            return Order_OperationStateMachine.TransitionResult.INVALID;
        }
    }

    private void startPosPayment(String amount) {
        if (!canUseUi()) {
            callMethod.Log("startPosPayment ignored: inactive Activity");
            setPaymentButtonEnabled(true);
            return;
        }
        if (posLaunchInProgress || paymentSubmissionInProgress || statusCheckInProgress) {
            showMessage("درخواست پرداخت در حال انجام است");
            return;
        }
        if (Order_ValueParser.nonNegativeLongOrDefault(amount, INVALID_AMOUNT) < 0) {
            showMessage("مبلغ پرداخت معتبر نیست");
            setPaymentButtonEnabled(true);
            return;
        }

        Activity activity = (Activity) mContext;
        BehPardakht_pos_request.versionName = "2.0.0";
        BehPardakht_pos_request.sessionId = "Kits_" + System.currentTimeMillis();
        BehPardakht_pos_request.applicationId = 10135;
        BehPardakht_pos_request.totalAmount = amount;
        BehPardakht_pos_request.transactionType = "PURCHASE";
        BehPardakht_pos_request.echoData = "TestEcho";

        Intent posIntent = new Intent("com.behpardakht.thirdparty.payment");
        posIntent.setPackage("com.behpardakht.app");
        posIntent.putExtra("paymentData", gson.toJson(BehPardakht_pos_request));
        if (posIntent.resolveActivity(mContext.getPackageManager()) == null) {
            showMessage("اپ به‌پرداخت نصب نیست یا این Intent را پشتیبانی نمی‌کند");
            setPaymentButtonEnabled(true);
            return;
        }

        if (!acquirePaymentGuard(BehPardakht_basketInfo)) return;
        beginPaymentOperation(Order_OperationJournal.Type.PAYMENT_POS, BehPardakht_basketInfo);
        markActivePayment(Order_OperationStateMachine.State.EXTERNAL_PENDING, false);
        posLaunchInProgress = true;
        setPaymentButtonEnabled(false);
        activity.runOnUiThread(() -> {
            if (!canUseUi()) {
                markActivePayment(Order_OperationStateMachine.State.CANCELED, true);
                posLaunchInProgress = false;
                setPaymentButtonEnabled(true);
                return;
            }
            try {
                activity.startActivityForResult(posIntent, REQUEST_POS);
                showMessage("در حال ارسال درخواست به پوز...");
            } catch (RuntimeException exception) {
                markActivePayment(Order_OperationStateMachine.State.FAILED, true);
                posLaunchInProgress = false;
                setPaymentButtonEnabled(true);
                callMethod.Log("POS app launch failed: " + exception.getClass().getSimpleName());
                showMessage("اجرای برنامه پرداخت ممکن نیست");
            }
        });
    }

    private void beginPaymentOperation(
            Order_OperationJournal.Type type,
            Order_BasketInfo basketInfo
    ) {
        String subject = paymentSubject(basketInfo);
        activeExpectedFactorCode = basketInfo == null
                ? ""
                : safeTrim(basketInfo.getFactorCode());
        activeBaselineReceived = basketInfo == null
                ? INVALID_AMOUNT
                : Order_ValueParser.nonNegativeLongOrDefault(
                        normalizedNumber(basketInfo.getReceived()), INVALID_AMOUNT);
        activeBaselineNotReceived = basketInfo == null
                ? INVALID_AMOUNT
                : Order_ValueParser.nonNegativeLongOrDefault(
                        normalizedNumber(basketInfo.getNotReceived()), INVALID_AMOUNT);
        try {
            activePaymentOperationId = operationJournal.begin(type, subject);
        } catch (RuntimeException exception) {
            activePaymentOperationId = "";
            callMethod.Log("Payment audit begin unavailable: "
                    + exception.getClass().getSimpleName());
        }
    }

    private boolean acquirePaymentGuard(Order_BasketInfo basketInfo) {
        String subject = paymentSubject(basketInfo);
        if (subject.isEmpty()) {
            showMessage("شناسه فاکتور برای شروع پرداخت معتبر نیست");
            return false;
        }
        if (!Order_PaymentExecutionGuard.tryAcquire(subject)) {
            showMessage("عملیات دیگری برای این فاکتور در حال انجام یا بررسی است");
            return false;
        }
        activePaymentSubject = subject;
        return true;
    }

    private void releasePaymentGuard() {
        Order_PaymentExecutionGuard.release(activePaymentSubject);
        activePaymentSubject = "";
        activeExpectedFactorCode = "";
        activeBaselineReceived = INVALID_AMOUNT;
        activeBaselineNotReceived = INVALID_AMOUNT;
    }

    private String paymentSubject(Order_BasketInfo basketInfo) {
        if (basketInfo == null) return "";
        String factor = safeTrim(basketInfo.getFactorCode());
        return factor.isEmpty()
                ? safeTrim(basketInfo.getAppBasketInfoCode())
                : factor;
    }

    private void markActivePayment(
            Order_OperationStateMachine.State state,
            boolean clearAfterTransition
    ) {
        if (activePaymentOperationId == null || activePaymentOperationId.isEmpty()) {
            if (clearAfterTransition) releasePaymentGuard();
            return;
        }
        try {
            Order_OperationStateMachine.TransitionResult transition =
                    operationJournal.transition(activePaymentOperationId, state);
            if (transition == Order_OperationStateMachine.TransitionResult.INVALID) {
                callMethod.Log("Payment audit transition rejected state=" + state.name());
            }
        } catch (RuntimeException exception) {
            callMethod.Log("Payment audit transition unavailable: "
                    + exception.getClass().getSimpleName());
        } finally {
            if (clearAfterTransition) {
                activePaymentOperationId = "";
                releasePaymentGuard();
            }
        }
    }

    private boolean canUseUi() {
        if (!(mContext instanceof Activity)) return false;
        Activity activity = (Activity) mContext;
        return !activity.isFinishing() && !activity.isDestroyed();
    }

    private void showMessage(String message) {
        if (canUseUi()) callMethod.showToast(message);
    }

    private void safeDismiss(Dialog target, String name) {
        if (target == null) return;
        try {
            if (target.isShowing()) target.dismiss();
        } catch (RuntimeException exception) {
            callMethod.Log("Payment " + name + " dismiss failed: "
                    + exception.getClass().getSimpleName());
        }
    }

    private void setPaymentButtonEnabled(boolean enabled) {
        if (currentPaymentButton != null && canUseUi()) {
            currentPaymentButton.setEnabled(enabled);
        }
    }

    private Long parseServerAmount(String field, String value, boolean allowNegative) {
        String normalized = normalizedNumber(value);
        Long parsed = Order_ValueParser.longOrNull(normalized);
        if (parsed == null || (!allowNegative && parsed < 0)) {
            callMethod.Log("Invalid payment field: " + field);
            return null;
        }
        return parsed;
    }

    private long readInputAmount(EditText input) {
        if (input == null) return INVALID_AMOUNT;
        return Order_ValueParser.nonNegativeLongOrDefault(
                normalizedNumber(input.getText().toString()), INVALID_AMOUNT);
    }

    private static String normalizedNumber(String value) {
        if (value == null) return "";
        return NumberFunctions.EnglishNumber(value)
                .replace(",", "")
                .replace("٬", "")
                .replace(" ", "")
                .trim();
    }

    private static String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    private long safeAbsolute(long value) {
        if (value == Long.MIN_VALUE) {
            callMethod.Log("Invalid payment field: DecrementValue overflow");
            return 0;
        }
        return Math.abs(value);
    }

    private String formatAmount(long value) {
        return NumberFunctions.PerisanNumber(decimalFormat.format(value));
    }

    private void setAmount(EditText input, long value) {
        input.setText(formatAmount(value));
    }

    private static void selectAllOnClick(EditText input) {
        input.setOnClickListener(view -> input.selectAll());
    }
}
