package com.onquantum.rockstar.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Admin on 8/31/14.
 */
public class FretsSlider extends View {
    private int width, height;
    private int first = 0;
    private Paint paint;

    public FretsSlider(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        this.width = width;
        this.height = height;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(new Rect(0,0,width,height),paint);
    }

}
