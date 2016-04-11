package com.veco.WeatherAppCOBE;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Created by Veco on 30.1.2016..
 */
public class CityPreference {

    SharedPreferences prefs;

    public CityPreference(Activity activity){
        prefs = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    String getCity(){
        return prefs.getString("city", "osijek");
    }

    void setCity(String city){
        prefs.edit().putString("city", city).commit();
    }
}
