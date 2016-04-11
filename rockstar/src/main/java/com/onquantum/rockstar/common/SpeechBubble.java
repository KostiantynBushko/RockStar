package com.onquantum.rockstar.common;

/**
 * Created by Admin on 3/20/16.
 */
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by Admin on 3/19/16.
 */
public class SpeechBubble extends RelativeLayout {
    private final float smallTriangle = 0.1f;

    private Context context = null;
    private LinearLayout layout = null;

    private Paint paint;
    private Point anchor = new Point(0,0);

    public SpeechBubble(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SpeechBubble(Context context) {
        super(context);
        this.context = context;
        setWillNotDraw(false);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.DKGRAY);
        paint.setAlpha(220);
    }


    public void setAnchor(Point anchor) {
        this.anchor = anchor;
        invalidate();
    }

    @Override
    public void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width,height,oldWidth,oldHeight);
        int x = (this.anchor.x - (width / 2));
        if(x < 0) {
            x = 2;
            this.setX(x);
        } else if ((x + getWidth()) > ((View)getParent()).getWidth()) {
            x = ((View)getParent()).getWidth() - getWidth() - 2;
            this.setX(x);
        }
        else {
            this.setX((this.anchor.x - (width / 2)));
        }
        this.setY(this.anchor.y - height);
        requestLayout();
        invalidate();
    }


    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRoundRect(new RectF(0,0,this.getWidth(), getHeight() - (getHeight() * smallTriangle)), 5, 5, paint);
        Path path = new Path();
        path.moveTo(anchor.x - getX() - (getHeight() * 0.1f), getHeight() - (getHeight() * smallTriangle));
        path.lineTo(anchor.x - getX() + (getHeight() * 0.1f), getHeight() - (getHeight() * smallTriangle));
        path.lineTo(anchor.x - getX(), getHeight());
        path.lineTo(anchor.x - getX() - (getHeight() * 0.1f), getHeight() - (getHeight() * smallTriangle));
        path.close();
        canvas.drawPath(path, paint);
    }
}
