package com.onquantum.rockstar.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.onquantum.rockstar.R;

/**
 * Created by Admin on 8/7/14.
 */
public class SwitchButton extends View {
    private Context context;
    private Paint paintBackground;
    private Paint paintSwitcher;
    private Paint paintText;
    private int width;
    private int height;
    private int roundBackground;
    private int roundSlider;
    private int padding;
    private RectF sliderPoint;

    private final float paddingDegree = 0.06f;
    private final int paddingBg = 0;
    private final float round = 0.025f;

    private float textHeightCenter;

    private int touchX = 0;
    private boolean touched = false;

    private boolean isOn = false;

    public interface OnSwitchListener{
        public void onSwitchChange(boolean isOn);
    }

    public OnSwitchListener onSwitchListener;


    public SwitchButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        paintBackground = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBackground.setColor(context.getResources().getColor(R.color.dark_gray));
        paintSwitcher = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintSwitcher.setColor(context.getResources().getColor(R.color.gray));
        paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintText.setTextAlign(Paint.Align.CENTER);
        paintText.setColor(context.getResources().getColor(R.color.gray));
    }

    @Override
    public void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        this.width = width;
        this.height = height;
        this.padding = (int)(height * paddingDegree);
        this.roundBackground = (int)(height/2 - height*round);
        this.roundSlider = (int)((height-padding)/2 - (height-padding)*round);

        paintText.setTextSize(height/2);
        textHeightCenter = (int) ((height / 2) - ((paintText.descent() + paintText.ascent()) / 2)) ;
        sliderPoint = new RectF(padding + paddingBg,padding + paddingBg,width/2,height-(padding+paddingBg));

        if (!isOn){
            sliderPoint.left = padding + paddingBg;
            sliderPoint.right = width/2;
            paintSwitcher.setColor(context.getResources().getColor(R.color.gray));
        }else {
            sliderPoint.left = width/2;
            sliderPoint.right = width - padding + paddingBg;
            paintSwitcher.setColor(context.getResources().getColor(R.color.green));
        }

    }


    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRoundRect(new RectF(
                        paddingBg,
                        paddingBg,
                        canvas.getWidth()-paddingBg,
                        canvas.getHeight()-paddingBg),
                roundBackground,
                roundBackground,
                paintBackground
        );

        canvas.drawText("OFF",width - width/4,textHeightCenter,paintText);
        canvas.drawText("ON",width/4,textHeightCenter,paintText);

        canvas.drawRoundRect(sliderPoint,
                roundSlider,
                roundSlider,
                paintSwitcher);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int actionMask = motionEvent.getActionMasked();
        int x = (int)motionEvent.getX();

        switch (actionMask) {
            case MotionEvent.ACTION_DOWN: {
                touchX = (int)motionEvent.getX();
                touched = true;
                break;
            }
            case MotionEvent.ACTION_UP: {
                touched = false;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if(touched) {
                    int d = touchX - x;
                    if(Math.abs(d) > width/3) {
                        if ((d < 0 && isOn == false) || (d > 0 && isOn == true)) {
                            onSwitch();
                            touched = false;
                        }
                    }
                }
                break;
            }
            default:break;
        }
        return true;
    }

    private void onSwitch() {
        if (isOn == true){
            sliderPoint.left = padding + paddingBg;
            sliderPoint.right = width/2;
            isOn = false;
            paintSwitcher.setColor(context.getResources().getColor(R.color.gray));
            invalidate();
            if (onSwitchListener != null)
                onSwitchListener.onSwitchChange(false);
        }else {
            sliderPoint.left = width/2;
            sliderPoint.right = width - padding + paddingBg;
            isOn = true;
            paintSwitcher.setColor(context.getResources().getColor(R.color.green));
            invalidate();
            if (onSwitchListener != null)
                onSwitchListener.onSwitchChange(true);
        }
    }

    public void Set(boolean isOn) {
        this.isOn = isOn;
    }

    public void setOnSwitchListener(OnSwitchListener onSwitchListener) {
        this.onSwitchListener = onSwitchListener;
    }
}
