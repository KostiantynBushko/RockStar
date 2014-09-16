package com.onquantum.rockstar.guitars;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;

/**
 * Created by Admin on 8/18/14.
 */
public abstract class GuitarAbstract extends SurfaceView {
    protected boolean isTouchEnable = false;


    public GuitarAbstract(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    protected OnSoundLoadedCompleteListener onSoundLoadedCompleteListener;

    public interface OnSoundLoadedCompleteListener {
        public void onSoundLoadedComplete();
    }

    public void setOnSoundLoadedCompleteListener(OnSoundLoadedCompleteListener onSoundLoadedCompleteListener) {
        this.onSoundLoadedCompleteListener = onSoundLoadedCompleteListener;
    }


    public abstract void LoadPentatonicFile(String fileName);
    public abstract void ClosePlayPentatonic();
    public void Stop(){}

    public void SetFretsNumberVisible(boolean visibility){}
    public void SetShowTouchesVisible(boolean visibility){}
    public void SetFretsSliderVisible(boolean visibility){}

    public boolean isTouchEnable() { return isTouchEnable; }
    public void setTouchEnable(boolean touchEnable) { isTouchEnable = touchEnable; }
}
