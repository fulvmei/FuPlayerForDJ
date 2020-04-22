package com.chengfu.music.player.ui.player;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chengfu.music.player.R;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;

public class CornerRadiusFrameLayout extends FrameLayout {

    private boolean noCornerRadius = true;
    private Path path = new Path();
    private RectF rect = new RectF();
    private float[] outerRadii = new float[]{0, 0, 0, 0, 0, 0, 0, 0};


    public CornerRadiusFrameLayout(@NonNull Context context) {
        this(context, null);
    }

    public CornerRadiusFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CornerRadiusFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        ShapeAppearanceModel shapeAppearanceModel = ShapeAppearanceModel.builder(
//                context,
//                attrs,
//                R.attr.bottomSheetStyle,
//                0
//        ).build();
    }


//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        super.onSizeChanged(w, h, oldw, oldh);
//
//        rect.set(0f, 0f, w, h);
//        resetPath();
//    }

//    @Override
//    protected void onDraw(Canvas canvas) {
//        if (noCornerRadius) {
//            super.draw(canvas);
//        } else {
//            int save = canvas.save();
//            canvas.clipPath(path);
//            super.draw(canvas);
//            canvas.restoreToCount(save);
//        }
//    }

    public void setCornerRadius(float radius) {
        // Top left corner
        outerRadii[0] = radius;
        outerRadii[1] = radius;

        // Top right corner
        outerRadii[2] = radius;
        outerRadii[3] = radius;

        noCornerRadius = (radius == 0f);

        if (getWidth() == 0 || getHeight() == 0) {
            // Discard invalid events
            return;
        }

        resetPath();
        invalidate();
    }

//    private void initView() {
//        if (hasMaximumSdk(Build.VERSION_CODES.JELLY_BEAN_MR1)) {
//            setLayerType(LAYER_TYPE_SOFTWARE, null);
//        }
//    }

    private void resetPath() {
        path.reset();
        path.addRoundRect(rect, outerRadii, Path.Direction.CW);
        path.close();
    }
}
