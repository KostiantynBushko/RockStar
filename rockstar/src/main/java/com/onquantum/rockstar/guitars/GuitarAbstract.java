package com.onquantum.rockstar.guitars;

import android.content.Context;
import android.opengl.GLSurfaceView;

/**
 * Created by Admin on 8/12/14.
 */
public abstract class GuitarAbstract extends GLSurfaceView {

    protected OnSoundLoadedCompleteListener onSoundLoadedCompleteListener;

    public interface OnSoundLoadedCompleteListener {
        public void onSoundLoadedComplete();
    }

    public void setOnSoundLoadedCompleteListener(OnSoundLoadedCompleteListener onSoundLoadedCompleteListener) {
        this.onSoundLoadedCompleteListener = onSoundLoadedCompleteListener;
    }

    public GuitarAbstract(Context context) {
        super(context);
    }

    public abstract void LoadPentatonicFile(String fileName);
    public abstract void ClosePlayPentatonic();

}
