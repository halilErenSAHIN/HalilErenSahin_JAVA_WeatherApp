package com.ben.halilerensahin_bitirmeprojesi_weatherapp;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    // WeatherResponse.java
    public static class WeatherResponse {
        private Main main;
        private List<Weather> weather;

        public List<Weather> getWeather() {
            return weather;
        }

        public Main getMain() {
            return main;
        }

        public void setMain(Main main) {
            this.main = main;
        }

        private List<Forecast> forecasts;

        public List<Forecast> getForecasts() {
            return forecasts;
        }

        private Sys sys;

        public Sys getSys() {
            return sys;
        }



        public static class Sys {
            private String country;
            private static String name;

            public String getCountry() {
                return country;
            }

            public static String getName() {
                return name;
            }
        }
    }

    // Main.java
    public static class Main {
        private double temp;

        public double getTemp() {
            return temp;
        }
    }

    // Weather.java
    public static class Weather {
        private String icon;

        public String getIcon() {
            return icon;
        }
    }

    public static class Forecast {
        @SerializedName("dt_txt")
        private String date;

        @SerializedName("main")
        private Main main;

        // Add other properties as needed

        public String getDate() {
            return date;
        }

        public double getTemperature() {
            return main != null ? main.getTemp() : 0.0;
        }



        public List<Forecast> getForecastList(String jsonString) {
            try {
                Gson gson = new Gson();
                ForecastResponse forecastResponse = gson.fromJson(jsonString, ForecastResponse.class);
                return forecastResponse != null ? forecastResponse.getForecasts() : null;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        // Main class for wrapping the response
        public static class ForecastResponse {
            private static List<Forecast> forecasts;

            public static List<Forecast> getForecasts() {
                return forecasts;
            }
        }



    }

    private LocationManager locationManager;
    private Location currentLocation;
    private TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Hangi Activity üzerinden internet verisi alıp verilecekse politika ayarları yapılmalı. Yoksa güvenlik nedeniyle engellenebiliriz.
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //tv = findViewById(R.id.textViewTemperature);

        //Konum servisi alındı.
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        requestLocationUpdate();
    }

    private void requestLocationUpdate() {
        boolean fine = ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED;
        boolean coarse = ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;
        if (fine && coarse) {
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, 100);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                tv.setText(location.toString());
            }
        });
    }

}
