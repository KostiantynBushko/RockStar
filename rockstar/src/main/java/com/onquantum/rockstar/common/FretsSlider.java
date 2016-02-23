package com.onquantum.rockstar.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.onquantum.rockstar.R;

/**
 * Created by Admin on 8/31/14.
 */
public class FretsSlider extends View {
    private int width, height;
    private Paint paint;
    private Paint line;
    private Paint borderLine;
    private float fretWidth;
    private Paint sliderPaint;
    private int sliderWidth;
    private int slide = 0;
    private float touchesX = 0;
    private float textHeightCenter;
    private Paint textPaint;
    private Paint focusedTextPaint;
    private boolean isVisible = false;

    public interface SliderChangeListener{
        public void onSlideButtonListener(int slide);
    }
    private SliderChangeListener sliderChangeListener;

    public FretsSlider(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        line = new Paint(Paint.ANTI_ALIAS_FLAG);
        line.setColor(context.getResources().getColor(R.color.gray));
        line.setStrokeWidth(2.0f);
        borderLine = new Paint();
        borderLine.setColor(Color.DKGRAY);
        borderLine.setStrokeWidth(2.0f);
        sliderPaint = new Paint();
        sliderPaint.setColor(getResources().getColor(R.color.blue));
        sliderPaint.setAlpha(200);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(context.getResources().getColor(R.color.gray));
        focusedTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        focusedTextPaint.setTextAlign(Paint.Align.CENTER);
        focusedTextPaint.setColor(context.getResources().getColor(R.color.red));
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        this.width = width;
        this.height = height;
        fretWidth = width / 24.0f;
        textPaint.setTextSize(height/3);
        focusedTextPaint.setTextSize(height/3);
        textHeightCenter = ((height / 2) - ((textPaint.descent() + textPaint.ascent()) / 2)) ;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isVisible)
            return false;
        int actionMask = event.getActionMasked();
        //float x = event.getX();

        switch (actionMask) {
            case MotionEvent.ACTION_DOWN:
                touchesX = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                touchesX = 0;
                break;
            case MotionEvent.ACTION_MOVE: {
                float x = event.getX();
                float delta = touchesX - x;
                if (Math.abs(delta) >= (fretWidth * 50)) {
                    int s = delta > 0 ? 1 : -1;
                    if (((slide + s) >= 0) && ((slide + sliderWidth + s)) < 25) {
                        slide += s;
                        if (sliderChangeListener != null) {
                            sliderChangeListener.onSlideButtonListener(s);
                            //Log.i("info", " slide = " + Integer.toString(slide) + " s = " + Integer.toString(s));
                        }
                        touchesX = x;
                        invalidate();
                    }
                } else {
                    touchesX += delta;
                }
                break;
            }
            default:break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isVisible)
            return;
        float step = fretWidth;
        canvas.drawRect(new Rect(0,0,width,height),paint);
        canvas.drawLine(0,height-2,width,height-2,borderLine);

        canvas.drawRect(
                new RectF(width - (sliderWidth + slide) * fretWidth,
                        0,width - slide * fretWidth,height
                ),sliderPaint);

        for (int i = 1; i < 24; i++) {
            canvas.drawLine(width - step,1, width -step, height-2,line);
            if (i > slide && i < (sliderWidth + slide + 1)) {
                canvas.drawText(Integer.toString(i),width - step + fretWidth / 2,textHeightCenter,focusedTextPaint);
            }else {
                canvas.drawText(Integer.toString(i),width - step + fretWidth / 2,textHeightCenter,textPaint);
            }
            step += (fretWidth);
        }
        if (24 > slide && 24 < (sliderWidth + slide + 1)) {
            canvas.drawText(Integer.toString(24),width - step + fretWidth / 2,textHeightCenter,focusedTextPaint);
        }else {
            canvas.drawText(Integer.toString(24),width - step + fretWidth / 2,textHeightCenter,textPaint);
        }
    }

    public void setSliderWidth(int sliderWidth) {
        this.sliderWidth = sliderWidth;
    }

    public void setOnSliderChangeListener(SliderChangeListener sliderChangeListener) {
        this.sliderChangeListener = sliderChangeListener;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
        invalidate();
    }
}
