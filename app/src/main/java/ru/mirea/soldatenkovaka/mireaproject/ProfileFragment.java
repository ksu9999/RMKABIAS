package ru.mirea.soldatenkovaka.mireaproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ProfileFragment extends Fragment {

    private static final String PREFS_NAME = "Prefs";
    private static final String KEY_NAME = "key_name";
    private static final String KEY_GROUPN = "key_surname";
    private static final String KEY_BOOK = "key_book";

    private EditText Name;
    private EditText GroupN;
    private EditText Book;
    private Button buttonSave;
    private Button buttonLoad;

    private SharedPreferences sharedPref;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Name = view.findViewById(R.id.fio);
        GroupN = view.findViewById(R.id.group_N);
        Book = view.findViewById(R.id.book);
        buttonSave = view.findViewById(R.id.button);
        buttonLoad = view.findViewById(R.id.button2);

        sharedPref = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        buttonSave.setOnClickListener(v -> {
            savePreferences();
            Toast.makeText(getContext(), "Данные сохранены", Toast.LENGTH_LONG).show();
        });

        buttonLoad.setOnClickListener(v -> {
            loadPreferences();
            Toast.makeText(getContext(), "Данные загружены", Toast.LENGTH_SHORT).show();
        });
    }

    private void savePreferences() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY_NAME, Name.getText().toString());
        editor.putString(KEY_GROUPN, GroupN.getText().toString());
        editor.putString(KEY_BOOK, Book.getText().toString());
        editor.apply();
    }

    private void loadPreferences() {
        Name.setText(sharedPref.getString(KEY_NAME, ""));
        GroupN.setText(sharedPref.getString(KEY_GROUPN, ""));
        Book.setText(sharedPref.getString(KEY_BOOK, ""));
    }
}