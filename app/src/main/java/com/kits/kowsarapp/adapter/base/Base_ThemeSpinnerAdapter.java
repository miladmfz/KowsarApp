package com.kits.kowsarapp.adapter.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kits.kowsarapp.R;

public class Base_ThemeSpinnerAdapter extends ArrayAdapter<String> {
    private final int[][] themeColors; // Array of color combinations
    private final String[] themeNames; // Array of theme names

    public Base_ThemeSpinnerAdapter(Context context, String[] themeNames, int[][] themeColors) {
        super(context, R.layout.order_spinner_item_theme, themeNames);
        this.themeColors = themeColors;
        this.themeNames = themeNames;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.order_spinner_item_theme, parent, false);
        }

        // Set theme name
        TextView themeNameView = view.findViewById(R.id.theme_name);
        themeNameView.setText(themeNames[position]);

        // Set primary color preview
        View primaryColorView = view.findViewById(R.id.theme_color_primary);
        primaryColorView.setBackgroundColor(themeColors[position][0]);

        // Set secondary color preview
        View secondaryColorView = view.findViewById(R.id.theme_color_secondary);
        secondaryColorView.setBackgroundColor(themeColors[position][1]);

        // Set surface color preview
        View surfaceColorView = view.findViewById(R.id.theme_color_surface);
        surfaceColorView.setBackgroundColor(themeColors[position][2]);

        return view;
    }
}
