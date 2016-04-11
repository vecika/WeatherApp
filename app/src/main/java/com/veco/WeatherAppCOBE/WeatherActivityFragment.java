package com.veco.WeatherAppCOBE;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Veco on 30.1.2016..
 */


public class WeatherActivityFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_weather, container, false);
    }
}
