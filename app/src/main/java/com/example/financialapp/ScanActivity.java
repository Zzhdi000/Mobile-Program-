package com.example.financialapp;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class ScanActivity extends AppCompatActivity {

    ImageView imgPreview;
    Button btnUpload, btnScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        imgPreview = findViewById(R.id.imgPreview);
        btnUpload = findViewById(R.id.btnUpload);
        btnScan = findViewById(R.id.btnScan);

        btnUpload.setOnClickListener(v ->
                Toast.makeText(this, "Upload feature coming soon!", Toast.LENGTH_SHORT).show());

        btnScan.setOnClickListener(v ->
                Toast.makeText(this, "Scan feature coming soon!", Toast.LENGTH_SHORT).show());
    }
}
