package com.example.weathermap;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Weather2Activity extends AppCompatActivity {
    private TextView textViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather2);

        textViewResult = findViewById(R.id.text_view_result);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.openweathermap.org/data/2.5/weather?q=")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        jsonWeatherApi jsonWeatherApi = retrofit.create(com.example.weathermap.jsonWeatherApi.class);

        Call<List<Post>> call = jsonWeatherApi.getWeather();

        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {

                if(!response.isSuccessful()) {
                    textViewResult.setText("Code: " + response.code());
                    return;
                }
                List<Post> posts = response.body();

                for (Post post : posts){
                    String content = "";
                   content += "ID: " + post.getId() + "\n";
                   content += "MAIN: " + post.getMain() + "\n";
                   content += "ICON: " + post.getIcon() + "\n";
                   content += "TEXT: " + post.getText() + "\n\n";
                }
            }


            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                textViewResult.setText(t.getMessage());
            }
        });

    }
}
