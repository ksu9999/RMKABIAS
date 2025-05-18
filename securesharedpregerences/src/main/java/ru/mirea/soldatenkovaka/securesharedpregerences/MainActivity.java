package ru.mirea.soldatenkovaka.securesharedpregerences;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private TextView Name;
    private ImageView Photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Name = findViewById(R.id.name);
        Photo = findViewById(R.id.photo);

        try {
            // Мастер-ключ для шифрования
            String mainKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

            // Создание SharedPreferences
            SharedPreferences secureSharedPreferences = EncryptedSharedPreferences.create(
                    "secure_shared_prefs",
                    mainKeyAlias,
                    getBaseContext(),
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            // Получение имени поэта
            String actorName = secureSharedPreferences.getString("secure", "Булат Окуджава (1924 - 1997)");
            Name.setText(actorName);

            // Перенос строк с именем файла в  SharedPreferences.Editor
            SharedPreferences.Editor editor = secureSharedPreferences.edit();
            editor.putString("secure", "Булат Окуджава (1924 - 1997)");
            editor.putString("bulat", "bulat");
            editor.apply();

            // Получение имени файла изображения из SharedPreferences
            String photoName = secureSharedPreferences.getString("bulat", "bulat");

            // Получение ресурса изображения из папки raw по имени
            int imageResId = getResources().getIdentifier(photoName, "raw", getPackageName());

            Resources res = getResources();
            InputStream inputStream = res.openRawResource(imageResId);
            Drawable drawable = Drawable.createFromStream(inputStream, photoName);
            Photo.setImageDrawable(drawable);

        } catch (Exception e) {
            e.printStackTrace();
            Name.setText("Ошибка загрузки данных");
        }
    }
}