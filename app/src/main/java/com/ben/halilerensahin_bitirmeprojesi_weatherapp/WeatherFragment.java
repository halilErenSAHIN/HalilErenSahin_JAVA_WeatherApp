package com.ben.halilerensahin_bitirmeprojesi_weatherapp;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.Context.LOCATION_SERVICE;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.ben.halilerensahin_bitirmeprojesi_weatherapp.MainActivity.WeatherResponse;
import com.google.gson.JsonSyntaxException;


public class WeatherFragment extends Fragment {


    private String url = "https://api.openweathermap.org/data/3.0/";
    private String key = "3894bfd0e606499d4e3abd8d506e98a6";
    private LocationManager locationManager;
    private TextView tv;
    private TextView tv_condition;
    private TextView tv_city;
    private ImageView img_weather;

    private LinearLayout linearLayoutForecast;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String query = url + "weather?q=istanbul&appid=" + key + "&units=metric&lang=tr";
        String result = fetchData(query);
        View root = inflater.inflate(R.layout.fragment_weather, container, false);
        tv = root.findViewById(R.id.textViewTemperature);
        tv_condition = root.findViewById(R.id.textViewCondition);
        tv_city = root.findViewById(R.id.textViewCity);
        linearLayoutForecast = root.findViewById(R.id.linearLayoutForecast);
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        requestLocationUpdate();
        return root;
    }


    //Bu kısım herhangi bir web api sitesine GET metodu ile bağlanıp gelen veriyi String olarak döner.
    private String fetchData(String apiUrl) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(apiUrl).build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    private void requestLocationUpdate() {
        boolean fine = ActivityCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED;
        boolean coarse = ActivityCompat.checkSelfPermission(getContext(), ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;
        if (fine && coarse) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, 100);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                String query = url + "weather?lat=" + location.getLatitude() + "&lon=" + location.getLongitude() + "&appid=" + key + "&units=metric&lang=tr";
                String result = fetchData(query);
                tv.setText(result);


                if (result != null) {
                    Gson gson = new Gson();
                    WeatherResponse weatherResponse = gson.fromJson(result, WeatherResponse.class);


                    // Update UI based on weatherResponse
                    double temperature = weatherResponse.getMain().getTemp();
                    String iconUrl = "https://openweathermap.org/img/w/" + weatherResponse.getWeather().get(0).getIcon() + ".png";

                    // Update your TextView and ImageView accordingly
                    String displayText = "Temperature: " + temperature + "°C";
                    tv.setText(displayText);
                    String condition = "";
                    tv_condition.setText(condition);

                    // Load weather icon using Glide or Picasso
                    img_weather = getView().findViewById(R.id.imageViewWeatherIcon);
                    Glide.with(requireContext()).load(iconUrl).into(img_weather);

                    // Update the city and country information
                    String cityCountry = WeatherResponse.Sys.getName() + ", " + weatherResponse.getSys().getCountry();
                    tv_city = getView().findViewById(R.id.textViewCity);
                    tv_city.setText(cityCountry);

                    List<MainActivity.Forecast> forecasts = MainActivity.Forecast.ForecastResponse.getForecasts();
                    addForecastItems(forecasts);

                } else {
                    // Handle error or inform the user that data couldn't be retrieved

                }


            }
        });
    }

    private void addForecastItems(List<MainActivity.Forecast> forecasts) {
        // Clear existing forecast items
        linearLayoutForecast.removeAllViews();

        for (MainActivity.Forecast forecast : forecasts) {
            // Create TextView for each forecast item
            TextView textViewForecastItem = new TextView(requireContext());
            textViewForecastItem.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            textViewForecastItem.setText(forecast.getDate() + ": " + forecast.getTemperature() + "°C");
            linearLayoutForecast.addView(textViewForecastItem);
        }
    }
}