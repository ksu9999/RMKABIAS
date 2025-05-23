package ru.mirea.soldatenkovaka.mireaproject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkResourceFragment extends Fragment {

    private TextView IP, City, Region, Country, Latitude, Longitude, Temperature, WindSpeed;
    private Button button;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Инфлейтим layout фрагмента (создайте соответствующий XML)
        return inflater.inflate(R.layout.fragment_network_resource, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Инициализация UI элементов через view
        IP = view.findViewById(R.id.IP);
        City = view.findViewById(R.id.City);
        Region = view.findViewById(R.id.Region);
        Country = view.findViewById(R.id.Country);
        Latitude = view.findViewById(R.id.Latitude);
        Longitude = view.findViewById(R.id.Longitude);
        Temperature = view.findViewById(R.id.Temperature);
        WindSpeed = view.findViewById(R.id.WindSpeed);
        button = view.findViewById(R.id.button);

        button.setOnClickListener(v -> new DownloadIpInfoTask().execute("https://ipinfo.io/json"));
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
                if (getContext() != null)
                    Toast.makeText(getContext(), "Ошибка загрузки данных", Toast.LENGTH_LONG).show();
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
                if (getContext() != null)
                    Toast.makeText(getContext(), "Ошибка обработки данных", Toast.LENGTH_LONG).show();
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
