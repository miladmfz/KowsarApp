package com.kits.kowsarapp.application.base;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class ZoomHelper implements View.OnTouchListener {

    private final Matrix matrix = new Matrix();
    private final Matrix savedMatrix = new Matrix();

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;

    private final PointF start = new PointF();
    private final PointF mid = new PointF();
    private float oldDist = 1f;

    // فقط اجازه زوم به بالا
    private static final float MIN_ZOOM = 1f; // حالت اولیه
    private static final float MAX_ZOOM = 4f; // حداکثر بزرگ‌نمایی
    private float currentScale = 1f;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;
        view.setScaleType(ImageView.ScaleType.MATRIX);

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                mode = DRAG;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (mode == ZOOM) {
                    float newDist = spacing(event);
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        float scale = newDist / oldDist;

                        float newScale = currentScale * scale;

                        // جلوگیری از Zoom Out (کوچیک شدن)
                        if (newScale < MIN_ZOOM) {
                            scale = 1f; // اصلاً اجازه نده کوچیک‌تر بشه
                        } else if (newScale > MAX_ZOOM) {
                            scale = MAX_ZOOM / currentScale;
                            currentScale = MAX_ZOOM;
                        } else {
                            currentScale = newScale;
                        }

                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                // همیشه وقتی ول شد -> ریست به حالت اولیه
                resetImage(view);
                break;
        }

        view.setImageMatrix(matrix);
        return true;
    }

    private float spacing(MotionEvent event) {
        if (event.getPointerCount() < 2) return 0;
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    private void resetImage(ImageView view) {
        matrix.reset();
        currentScale = 1f;
        view.setImageMatrix(matrix);
    }
}
