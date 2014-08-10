package com.onquantum.rockstar.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.onquantum.rockstar.R;

/**
 * Created by Admin on 8/9/14.
 */
public class CounterView extends View {

    private int width, height;
    private Paint buttonPaintPlus;
    private Paint buttonPaintMinus;
    private float radius;

    public CounterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        buttonPaintPlus = new Paint(Paint.ANTI_ALIAS_FLAG);
        buttonPaintPlus.setColor(context.getResources().getColor(R.color.gray));
        buttonPaintMinus = new Paint(Paint.ANTI_ALIAS_FLAG);
        buttonPaintMinus.setColor(context.getResources().getColor(R.color.gray));
    }

    @Override
    public void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        this.width = width;
        this.height = height;
        if((height * 3) > width) {
            radius = (height - (height * 3 - width)) / 2;
        }else {
            radius = height / 2;
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawCircle(radius, height / 2,radius,buttonPaintMinus);
        canvas.drawCircle(width - radius,height/2,radius,buttonPaintPlus);
    }
}
