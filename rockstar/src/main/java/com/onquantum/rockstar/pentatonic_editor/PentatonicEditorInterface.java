package com.onquantum.rockstar.pentatonic_editor;

import android.view.MotionEvent;

import com.onquantum.rockstar.tabulature.SimpleTab;

/**
 * Created by Admin on 12/23/15.
 */
public interface PentatonicEditorInterface {
    public void OnPentatonicEditorClickListener(MotionEvent event);
    public void OnBPMChange();
    public void OnSelectTab(PentatonicEditorSurfaceView.Tab tab);
    public void OnAddTab(PentatonicEditorSurfaceView.Tab tab);
}
