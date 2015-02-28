package com.onquantum.rockstar.common;

import android.content.Context;
import android.util.AttributeSet;

import com.onquantum.rockstar.R;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

/**
 * Created by Admin on 1/20/15.
 */
public class CustomTwitterLoginButton extends TwitterLoginButton {
    public CustomTwitterLoginButton(Context context) {
        super(context);
    }

    public CustomTwitterLoginButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        //init();
    }

    public CustomTwitterLoginButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //init();
    }

    private void init() {
        if (isInEditMode()){
            return;
        }
        setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.button_twitter), null, null, null);
        //setBackgroundResource(R.drawable.button_face_book);
        //setTextSize(20);
        //setPadding(30, 0, 10, 0);
        //setTextColor(getResources().getColor(R.color.tw__blue_default));
        //setTypeface(App.getInstance().getTypeface());
    }
}
