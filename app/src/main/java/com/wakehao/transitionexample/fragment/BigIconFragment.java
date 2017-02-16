package com.wakehao.transitionexample.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wakehao.transitionexample.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BigIconFragment extends Fragment {


    public BigIconFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_big_icon, container, false);
    }

}
