package com.veco.WeatherAppCOBE;


import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.json.JSONObject;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Veco on 30.1.2016..
 */
public class WeatherFragment extends Fragment {
    Typeface weatherFont;

    TextView cityField;
    TextView updatedField;
    TextView detailsField;
    TextView humidityField;
    TextView pressureField;
    TextView windField;
    TextView minTemperatureField;
    TextView maxTemperatureField;
    CheckBox star;
    ArrayList favoriteArray;

    TextView currentTemperatureField;
    ImageView weatherIcon;
    ImageView cobeLogo;

    Handler handler;

    int windDirection = 0;


    public WeatherFragment(){
        handler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);

        cityField = (TextView)rootView.findViewById(R.id.city_field);
        updatedField = (TextView)rootView.findViewById(R.id.updated_field);
        detailsField = (TextView)rootView.findViewById(R.id.details_field);
        currentTemperatureField = (TextView)rootView.findViewById(R.id.current_temperature_field);
        weatherIcon = (ImageView)rootView.findViewById(R.id.weather_icon);
        humidityField = (TextView)rootView.findViewById(R.id.humidity_field);
        pressureField = (TextView)rootView.findViewById(R.id.pressure_field);
        windField = (TextView)rootView.findViewById(R.id.wind_field);
        minTemperatureField = (TextView) rootView.findViewById(R.id.min_field);
        maxTemperatureField = (TextView)rootView.findViewById(R.id.max_field);
        cobeLogo = (ImageView)rootView.findViewById(R.id.cobe_logo);
        star = (CheckBox)rootView.findViewById(R.id.star);

        favoriteArray = new ArrayList();

        cobeLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://cobeisfresh.com";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        weatherIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateWeatherData(new CityPreference(getActivity()).getCity());
                Toast.makeText(getActivity(),
                        getActivity().getString(R.string.updating),
                        Toast.LENGTH_SHORT).show();
            }
        });
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weather.ttf");
        updateWeatherData(new CityPreference(getActivity()).getCity());
    }

    private void updateWeatherData(final String city){
        new Thread(){
            public void run(){
                final JSONObject json = RemoteFetch.getJSON(getActivity(), city);
                if(json == null){
                    handler.post(new Runnable(){
                        public void run(){

                            cityField.setText(R.string.not_available);
                            detailsField.setText(R.string.not_available);
                            humidityField.setText(R.string.not_available);
                            pressureField.setText(R.string.not_available);
                            windField.setText(R.string.not_available);
                            weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.na));

                            Toast.makeText(getActivity(),
                                    getActivity().getString(R.string.place_not_found),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable(){
                        public void run(){
                            renderWeather(json);
                        }
                    });
                }
            }
        }.start();
    }


    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private void renderWeather(JSONObject json){
        try {
            cityField.setText(json.getString("name").toUpperCase(Locale.ENGLISH) + ", " + json.getJSONObject("sys").getString("country"));

            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");
            JSONObject wind = json.getJSONObject("wind");

            detailsField.setText(details.getString("description").toUpperCase(Locale.ENGLISH));
            humidityField.setText("Humidity: " +  main.getString("humidity") + "%");
            pressureField.setText("Pressure: " +  main.getString("pressure") + " hPa");

            windField.setText("Wind: " + String.format("%.1f", wind.getDouble("speed")) + " km/h");
            currentTemperatureField.setText(String.format("%.1f", main.getDouble("temp")) + " ℃");
            minTemperatureField.setText("Min:" + main.getInt("temp_min") + " ℃");
            maxTemperatureField.setText("Max:" + main.getInt("temp_max") + " ℃");

            DateFormat df = DateFormat.getDateTimeInstance();
            String updatedOn = df.format(new Date(json.getLong("dt")*1000));
            updatedField.setText("Last update: " + updatedOn);

            setWeatherIcon(details.getInt("id"),
                    json.getJSONObject("sys").getLong("sunrise") * 1000,
                    json.getJSONObject("sys").getLong("sunset") * 1000);

        }catch(Exception e){
            Log.e("1:", "Missing data in JSON object");
        }
    }

        //TODO: Mozda ubaciti jos weatherIcona?

    private void setWeatherIcon(int actualId, long sunrise, long sunset) {
        int id = actualId / 100;
        Drawable icon = getResources().getDrawable(R.drawable.na);
        if(actualId == 800){
            long currentTime = new Date().getTime();
            if(currentTime>=sunrise && currentTime<sunset ) {
                icon = getResources().getDrawable(R.drawable.sunny);
            } else {
                icon = getResources().getDrawable(R.drawable.clear_night);
            }
        } else {
            switch(id) {
                case 2 : icon = getResources().getDrawable(R.drawable.thunder);
                    break;
                case 3 : icon = getResources().getDrawable(R.drawable.drizzle);
                    break;
                case 7 : icon = getResources().getDrawable(R.drawable.foggy);
                    break;
                case 8 : icon = getResources().getDrawable(R.drawable.cloudy);
                    break;
                case 6 : icon = getResources().getDrawable(R.drawable.snowy);
                    break;
                case 5 : icon = getResources().getDrawable(R.drawable.rainy);
                    break;
            }
        }
        weatherIcon.setImageDrawable(icon);

    }
    public void changeCity(String city){
        updateWeatherData(city);
    }

}