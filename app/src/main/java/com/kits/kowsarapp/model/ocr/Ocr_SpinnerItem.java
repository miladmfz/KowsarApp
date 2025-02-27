package com.kits.kowsarapp.model.ocr;

public class Ocr_SpinnerItem {
    private String name;
    private int value;

    public Ocr_SpinnerItem(int value, String name ) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return name; // نمایش فقط نام در Spinner
    }
}
