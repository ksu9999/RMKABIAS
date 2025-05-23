package ru.mirea.soldatenkovaka.httpurlconnection;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView IP, City, Region, Country, Latitude, Longitude, Temperature, WindSpeed;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IP = findViewById(R.id.IP);
        City = findViewById(R.id.City);
        Region = findViewById(R.id.Region);
        Country = findViewById(R.id.Country);
        Latitude = findViewById(R.id.Latitude);
        Longitude = findViewById(R.id.Longitude);
        Temperature = findViewById(R.id.Temperature);
        WindSpeed = findViewById(R.id.WindSpeed);
        button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new DownloadIpInfoTask().execute("https://ipinfo.io/json");
            }
        });
    }

    // Загрузка информации о IP и локации
    private class DownloadIpInfoTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            button.setEnabled(false);
            IP.setText("Загрузка...");
            City.setText("");
            Region.setText("");
            Country.setText("");
            Latitude.setText("");
            Longitude.setText("");
            Temperature.setText("");
            WindSpeed.setText("");
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            button.setEnabled(true);
            if (result == null) {
                Toast.makeText(MainActivity.this, "Ошибка загрузки данных", Toast.LENGTH_LONG).show();
                return;
            }

            try {
                JSONObject json = new JSONObject(result);

                String ip = json.optString("ip", "N/A");
                String city = json.optString("city", "N/A");
                String region = json.optString("region", "N/A");
                String country = json.optString("country", "N/A");
                String loc = json.optString("loc", "0,0");

                String[] coords = loc.split(",");
                String latitude = coords.length > 0 ? coords[0] : "0";
                String longitude = coords.length > 1 ? coords[1] : "0";

                IP.setText("IP: " + ip);
                City.setText("Город: " + city);
                Region.setText("Регион: " + region);
                Country.setText("Страна: " + country);
                Latitude.setText("Широта: " + latitude);
                Longitude.setText("Долгота: " + longitude);

                // Запускаем загрузку погоды по координатам
                new DownloadWeatherTask().execute(latitude, longitude);

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Ошибка обработки данных", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Задача для загрузки погоды с Open-Meteo
    private class DownloadWeatherTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Temperature.setText("Загрузка погоды...");
            WindSpeed.setText("");
        }

        @Override
        protected String doInBackground(String... params) {
            String latitude = params[0];
            String longitude = params[1];
            String url = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude + "&longitude=" + longitude + "&current_weather=true";

            try {
                return downloadUrl(url);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                Temperature.setText("Ошибка загрузки погоды");
                return;
            }

            try {
                JSONObject json = new JSONObject(result);
                JSONObject currentWeather = json.optJSONObject("current_weather");
                if (currentWeather != null) {
                    double temperature = currentWeather.optDouble("temperature", Double.NaN);
                    double windspeed = currentWeather.optDouble("windspeed", Double.NaN);

                    Temperature.setText("Температура: " + (Double.isNaN(temperature) ? "N/A" : temperature + " °C"));
                    WindSpeed.setText("Скорость ветра: " + (Double.isNaN(windspeed) ? "N/A" : windspeed + " км/ч"));
                } else {
                    Temperature.setText("Погода недоступна");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Temperature.setText("Ошибка обработки погоды");
            }
        }
    }

    // Метод для скачивания данных по URL
    private String downloadUrl(String urlString) throws IOException {
        InputStream inputStream = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return null;
            }

            inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            return sb.toString();

        } finally {
            if (inputStream != null) inputStream.close();
            if (connection != null) connection.disconnect();
        }
    }
}