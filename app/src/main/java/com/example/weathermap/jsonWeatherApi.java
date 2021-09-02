package com.example.weathermap;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface jsonWeatherApi {

    @GET("2e29188ca139841dc42c071c3973e714")
    Call<List<Post>> getWeather();
}
