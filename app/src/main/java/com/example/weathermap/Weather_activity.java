package com.example.weathermap;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class Weather_activity extends AppCompatActivity {
    private static String forecastDaysNum = "3";
    String city = "Ahmedabad, IN";

    private TextView cityText = (TextView) findViewById(R.id.cityText);
    private TextView temp = (TextView) findViewById(R.id.temp);
    private TextView unitTemp = (TextView) findViewById(R.id.unittemp);
    //private  unitTemp.setText("C");
    private TextView condDescr = (TextView) findViewById(R.id.skydesc);
    private ViewPager pager = (ViewPager) findViewById(R.id.pager);
    private ImageView imgView = (ImageView) findViewById(R.id.condIcon);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_activity);
    }
}
