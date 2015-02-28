package com.onquantum.rockstar.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.onquantum.rockstar.R;

/**
 * Created by Admin on 8/9/14.
 */
public class CounterView extends View {

    private int width, height;
    private Paint buttonPaintPlus;
    private Paint buttonPaintMinus;
    private Paint paintBackground;
    private Paint textPaint;
    private float radius;
    private float textHeightCenter;
    private int countValue = 1;
    private int countMax = 100;
    private int countMin = 0;

    private final float paddingDegree = 0.06f;
    private final int paddingBg = 0;
    private final float round = 0.025f;
    private int roundBackground;
    private int padding;

    public interface OnCountChangeValue {
        public void onCountChangeValue(int countValue);
    }

    private OnCountChangeValue onCountChangeValue;

    public void setOnCountChangeValue(OnCountChangeValue onCountChangeValue) {
        this.onCountChangeValue = onCountChangeValue;
    }

    public CounterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paintBackground = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBackground.setColor(context.getResources().getColor(R.color.dark_gray));
        buttonPaintPlus = new Paint(Paint.ANTI_ALIAS_FLAG);
        buttonPaintPlus.setColor(context.getResources().getColor(R.color.gray));
        buttonPaintMinus = new Paint(Paint.ANTI_ALIAS_FLAG);
        buttonPaintMinus.setColor(context.getResources().getColor(R.color.gray));
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(context.getResources().getColor(R.color.blue));

    }

    @Override
    public void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        this.width = width;
        this.height = height;
        this.padding = (int)(height * paddingDegree);
        this.roundBackground = (int)(height/2);

        if((height * 3) > width) {
            radius = ((height - (height * 3 - width)) / 2) - padding;
        }else {
            radius = height / 2 - padding;
        }
        textPaint.setTextSize(height * 0.8f);
        textHeightCenter = (int) ((height / 2) - ((textPaint.descent() + textPaint.ascent()) / 2)) ;

    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawRoundRect(new RectF(
                        paddingBg,
                        paddingBg,
                        canvas.getWidth(),
                        canvas.getHeight()),
                roundBackground,
                roundBackground,
                paintBackground
        );

        Rect bound = new Rect();
        String textValue = Integer.toString(countValue);
        textPaint.getTextBounds(textValue, 0, textValue.length(), bound);
        int textValueWidth = bound.width();
        canvas.drawCircle(radius + padding, height / 2,radius,buttonPaintMinus);
        canvas.drawCircle(width - radius - padding,height/2,radius,buttonPaintPlus);

        canvas.drawText(textValue, width / 2 - textValueWidth / 2, textHeightCenter, textPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int actionMask = event.getActionMasked();
        int x = (int)event.getX();
        switch(actionMask) {
            case MotionEvent.ACTION_UP:
                if(x < width / 2) {
                    if(countValue > countMin) {
                        countValue -= 1;
                        if(onCountChangeValue != null)
                            onCountChangeValue.onCountChangeValue(countValue);
                    }
                }else {
                    if(countValue < countMax) {
                        countValue += 1;
                        if(onCountChangeValue != null)
                            onCountChangeValue.onCountChangeValue(countValue);
                    }
                }
                break;
            default:break;
        }
        this.invalidate();
        return true;
    }

    public void setMaxMinValue(int countMin, int countMax) {
        this.countMin = countMin;
        this.countMax = countMax;
    }

    public void setCountValue(int countValue) {
        if (countValue < countMin) {
            this.countValue = countMin;
        } else if(countValue > countMax) {
            this.countValue = countMax;
        }else {
            this.countValue = countValue;
        }
    }
}
