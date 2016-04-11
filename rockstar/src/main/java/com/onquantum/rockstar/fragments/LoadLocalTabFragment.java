package com.onquantum.rockstar.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onquantum.rockstar.R;

/**
 * Created by Admin on 4/3/16.
 */
public class LoadLocalTabFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        return layoutInflater.inflate(R.layout.load_tabulature, viewGroup, false);
    }
}
