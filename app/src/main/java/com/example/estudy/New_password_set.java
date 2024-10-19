package com.example.estudy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.badge.BadgeUtils;

public class New_password_set extends AppCompatActivity {
    private Button saveButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_password_set);
        saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(v -> {
            Intent intent = new Intent(New_password_set.this,LoginActivity.class);
            startActivity(intent);
        });
    }
}