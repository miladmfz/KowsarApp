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
import com.kits.kowsarapp.activity.ocr.Ocr_Collect_List_Api_Activity;


import java.util.ArrayList;
import java.util.List;

public class Ocr_StacksAdapter extends RecyclerView.Adapter<Ocr_StacksAdapter.ViewHolder> {

      Context mContext;

     List<String> items;
     List<String> selectedItems = new ArrayList<>(); // List baraye zakhire item haye entekhab shode

    public Ocr_StacksAdapter(Context context, List<String> items) {

        this.items = items;
        this.mContext = context;
    }


    private OnStackSelectionChanged listener;

    public interface OnStackSelectionChanged {
        void onStackSelectionChanged();
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
//        holder.checkBox.setOnCheckedChangeListener(null); // Prevent re-binding issues


//
//        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if (isChecked) {
//                selectedItems.add(item);
//            } else {
//                selectedItems.remove(item);
//            }
//
//            if (listener != null) {
//                listener.onStackSelectionChanged();
//            }
//        });
//





        // Mo'jodi az peida kardan state check baraye har item
        holder.checkBox.setChecked(selectedItems.contains(item));

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedItems.add(item);
            } else {
                selectedItems.remove(item);
            }

            Ocr_Collect_List_Api_Activity activity = (Ocr_Collect_List_Api_Activity) mContext;
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
