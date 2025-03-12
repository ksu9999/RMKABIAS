package ru.mirea.soldatenkovaka.buttonclicker;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;
import android.widget.CheckBox;

public class MainActivity extends AppCompatActivity {
    private TextView tvOut;
    private Button btnWhoAmI;
    private Button btnItIsNotMe;

    private CheckBox ChBox;

    public void onMyButtonClick(View view)
    {
        tvOut.setText("Это не я сделал 2");
        Toast.makeText(this, "Ещё один способ!", Toast.LENGTH_SHORT).show();
        ChBox.setChecked(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        tvOut = findViewById(R.id.tvOut);
        btnWhoAmI = findViewById(R.id.btnWhoAmI);
        btnItIsNotMe = findViewById(R.id.btnItIsNotMe);
        ChBox = findViewById(R.id.ChBox);

        View.OnClickListener oclBtnWhoAmI = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvOut.setText("Мой номер по списку № 14");
            }
        };
        btnWhoAmI.setOnClickListener(oclBtnWhoAmI);

        View.OnClickListener oclbtnItIsNotMe = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvOut.setText("Это не я сделал");
                ChBox.setChecked(true);
            }
        };
        btnItIsNotMe.setOnClickListener(oclbtnItIsNotMe);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}