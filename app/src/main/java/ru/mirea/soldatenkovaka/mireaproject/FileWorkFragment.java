package ru.mirea.soldatenkovaka.mireaproject;

import android.app.AlertDialog;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

public class FileWorkFragment extends Fragment {

    private EditText File;
    private EditText Quote;
    private Button buttonSave, buttonLoad, buttonConvert;
    private FloatingActionButton fab;

    public FileWorkFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_file_work, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        File = view.findViewById(R.id.file);
        Quote = view.findViewById(R.id.sport);
        buttonSave = view.findViewById(R.id.button);
        buttonLoad = view.findViewById(R.id.button2);
        buttonConvert = view.findViewById(R.id.button3);
        fab = view.findViewById(R.id.fab);

        buttonSave.setOnClickListener(v -> saveFile());
        buttonLoad.setOnClickListener(v -> loadFile());
        buttonConvert.setOnClickListener(v -> convertFileToPdf());
        fab.setOnClickListener(v -> showCreateRecordDialog());
    }

    private void saveFile() {
        String fileName = File.getText().toString().trim();
        String content = Quote.getText().toString();

        if (fileName.isEmpty()) {
            showToast("Введите название файла");
            return;
        }

        try {
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            if (!path.exists()) path.mkdirs();

            if (!fileName.endsWith(".txt")) {
                fileName += ".txt";
            }

            File file = new File(path, fileName);

            try (FileOutputStream fos = new FileOutputStream(file, false)) {
                fos.write(content.getBytes());
            }

            showToast("Файл сохранён: " + file.getAbsolutePath());
        } catch (Exception e) {
            showToast("Ошибка сохранения: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadFile() {
        String fileName = File.getText().toString().trim();

        if (fileName.isEmpty()) {
            showToast("Введите название файла");
            return;
        }

        try {
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

            if (!fileName.endsWith(".txt")) {
                fileName += ".txt";
            }

            File file = new File(path, fileName);

            if (!file.exists()) {
                showToast("Файл не найден");
                return;
            }

            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(file), "UTF-8"))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }

            Quote.setText(content.toString().trim());
            showToast("Данные успешно загружены");

        } catch (Exception e) {
            showToast("Ошибка загрузки: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void convertFileToPdf() {
        String fileName = File.getText().toString().trim();

        if (fileName.isEmpty()) {
            showToast("Введите название файла");
            return;
        }

        try {
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            if (!path.exists()) path.mkdirs();

            String txtFileName = fileName.endsWith(".txt") ? fileName : fileName + ".txt";
            String pdfFileName = fileName.endsWith(".pdf") ? fileName : fileName + ".pdf";

            File txtFile = new File(path, txtFileName);
            File pdfFile = new File(path, pdfFileName);

            if (!txtFile.exists()) {
                showToast("Файл не найден");
                return;
            }

            StringBuilder text = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(txtFile), "UTF-8"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    text.append(line).append("\n");
                }
            }

            PdfDocument pdfDocument = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create(); // A4
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);

            Canvas canvas = page.getCanvas();
            Paint paint = new Paint();
            paint.setTextSize(12);

            int x = 10, y = 25;
            for (String line : text.toString().split("\n")) {
                canvas.drawText(line, x, y, paint);
                y += paint.descent() - paint.ascent() + 5;
            }

            pdfDocument.finishPage(page);

            try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
                pdfDocument.writeTo(fos);
            }

            pdfDocument.close();

            showToast("Конвертация в PDF выполнена: " + pdfFile.getAbsolutePath());

        } catch (Exception e) {
            showToast("Ошибка конвертации: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showCreateRecordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Создать запись");

        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setHint("Введите текст записи");
        builder.setView(input);

        builder.setPositiveButton("Сохранить", (dialog, which) -> {
            String text = input.getText().toString().trim();
            if (!text.isEmpty()) {
                Quote.setText(text);
                showToast("Запись добавлена");
            } else {
                showToast("Пустая запись не сохранена");
            }
        });

        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
    }
}