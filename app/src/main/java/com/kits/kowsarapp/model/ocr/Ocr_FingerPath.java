package com.kits.kowsarapp.model.ocr;

import android.graphics.Path;

public class Ocr_FingerPath {
    public int color;
    public int strokeWidth;
    public Path path;

    public Ocr_FingerPath(int color, int strokeWidth, Path path) {
        this.color = color;
        this.strokeWidth = strokeWidth;
        this.path = path;
    }
}
