package com.kits.kowsarapp.fragment.ocr;

import com.kits.kowsarapp.model.ocr.Ocr_Good;

public interface OnGoodConfirmListener {
    void onGoodConfirmed(Ocr_Good good);

    void onGoodCanceled(Ocr_Good singleGood);
}