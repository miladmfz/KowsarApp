package com.kits.kowsarapp.adapter.ocr;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.kowsarapp.R;
import com.kits.kowsarapp.activity.ocr.Ocr_FactorListApiActivity;


import java.util.ArrayList;
import java.util.List;

public class Ocr_ItemAdapter extends RecyclerView.Adapter<Ocr_ItemAdapter.ViewHolder> {

    private final Context mContext;

    private List<String> items;
    private List<String> selectedItems = new ArrayList<>(); // List baraye zakhire item haye entekhab shode

    public Ocr_ItemAdapter(Context context, List<String> items) {

        this.items = items;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ocr_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = items.get(position);
        holder.textView.setText(item);
        holder.checkBox.setOnCheckedChangeListener(null); // Prevent re-binding issues

        // Mo'jodi az peida kardan state check baraye har item
        holder.checkBox.setChecked(selectedItems.contains(item));

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedItems.add(item);
            } else {
                selectedItems.remove(item);
            }

            Ocr_FactorListApiActivity activity = (Ocr_FactorListApiActivity) mContext;
            activity.CheckStackList();

        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public List<String> getSelectedItems() {
        return selectedItems;
    }
    public void Clear_selectedItems() {
        selectedItems.clear();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox);
            textView = itemView.findViewById(R.id.textview);
        }
    }
}
