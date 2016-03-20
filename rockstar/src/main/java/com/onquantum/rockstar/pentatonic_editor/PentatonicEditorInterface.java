package com.onquantum.rockstar.pentatonic_editor;

import com.onquantum.rockstar.tabulature.SimpleTab;

/**
 * Created by Admin on 12/23/15.
 */
public interface PentatonicEditorInterface {
    public void OnPentatonicEditorClickListener(int string, int quartet);
    public void OnBPMChange();
    public void OnSelectTab(PentatonicEditorSurfaceView.Tab tab);
}
