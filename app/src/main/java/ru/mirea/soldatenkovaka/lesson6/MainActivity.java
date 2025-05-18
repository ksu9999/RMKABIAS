package ru.mirea.soldatenkovaka.lesson6;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText NGroup;
    private EditText NList;
    private EditText LCinema;
    private Button button;

    private static final String PREFS_NAME = "PREFS";
    private static final String KEY_GROUP = "GROUP";
    private static final String KEY_LIST_NUMBER = "NUMBER";
    private static final String KEY_CINEMA = "CINEMA";
    private static final String KEY_LAUNCH_COUNT = "L_COUNT";

    private SharedPreferences sharedPref;

    private int launchCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NGroup = findViewById(R.id.N_group);
        NList = findViewById(R.id.N_list);
        LCinema = findViewById(R.id.L_cinema);
        button = findViewById(R.id.button);

        sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Счётчик запусков
        launchCount = sharedPref.getInt(KEY_LAUNCH_COUNT, 0);
        launchCount++;
        sharedPref.edit().putInt(KEY_LAUNCH_COUNT, launchCount).apply();

        // Загрузка сохранённых данных при повторном запуске
        if (launchCount > 1) {
            loadPreferences();
        }

        button.setOnClickListener(v -> {
            savePreferences();
            Toast.makeText(MainActivity.this, "Данные сохранены", Toast.LENGTH_LONG).show();
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Обнуление счётчика запусков при полном уничтожении приложения
        sharedPref.edit().putInt(KEY_LAUNCH_COUNT, 0).apply();
    }

    private void savePreferences() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY_GROUP, NGroup.getText().toString());
        editor.putString(KEY_LIST_NUMBER, NList.getText().toString());
        editor.putString(KEY_CINEMA, LCinema.getText().toString());
        editor.apply();
    }

    private void loadPreferences() {
        NGroup.setText(sharedPref.getString(KEY_GROUP, ""));
        NList.setText(sharedPref.getString(KEY_LIST_NUMBER, ""));
        LCinema.setText(sharedPref.getString(KEY_CINEMA, ""));
    }
}