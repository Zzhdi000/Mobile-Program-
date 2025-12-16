package com.example.financialapp;

import android.net.Uri;
import android.os.Bundle;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

public class ScanActivity extends AppCompatActivity {

    ImageView imgPreview;
    Button btnUpload, btnScan;

    Uri selectedImageUri = null;

    // LAUNCHER UNTUK PILIH GAMBAR DARI GALLERY
    private ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    imgPreview.setImageURI(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        imgPreview = findViewById(R.id.imgPreview);
        btnUpload = findViewById(R.id.btnUpload);
        btnScan = findViewById(R.id.btnScan);

        // UPLOAD IMAGE → BUKA GALLERY
        btnUpload.setOnClickListener(v ->
                pickImageLauncher.launch("image/*")
        );

        // SCAN (OCR) → HARUS ADA GAMBAR DULU
        btnScan.setOnClickListener(v -> {
            if (selectedImageUri == null) {
                Toast.makeText(this, "Please upload an image first", Toast.LENGTH_SHORT).show();
            } else {
                scanOCR(selectedImageUri);
            }
        });
    }

    // FUNSI OCR (TEXT RECOGNITION)
    private void scanOCR(Uri uri) {
        try {
            InputImage image = InputImage.fromFilePath(this, uri);

            TextRecognizer recognizer =
                    TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

            recognizer.process(image)
                    .addOnSuccessListener(result -> {
                        String extractedText = result.getText();

                        if (extractedText.isEmpty()) {
                            Toast.makeText(this, "No text detected", Toast.LENGTH_SHORT).show();
                        } else {
                            showResultDialog(extractedText);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Scan failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    });

        } catch (Exception e) {
            Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // DIALOG UNTUK MENAMPILKAN RESULT OCR
    private void showResultDialog(String text) {
        new AlertDialog.Builder(this)
                .setTitle("Scan Result")
                .setMessage(text)
                .setPositiveButton("OK", null)
                .show();
    }
}
