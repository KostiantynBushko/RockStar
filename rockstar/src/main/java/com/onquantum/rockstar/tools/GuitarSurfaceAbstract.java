package com.onquantum.rockstar.tools;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;

/**
 * Created by Admin on 8/18/14.
 */
public abstract class GuitarSurfaceAbstract extends SurfaceView {
    public GuitarSurfaceAbstract(Context context, AttributeSet attributeSet) {
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
}
