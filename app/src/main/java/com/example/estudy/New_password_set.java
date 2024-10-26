package com.example.estudy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class New_password_set extends AppCompatActivity {
    private Button saveButton;
    private EditText newPasswordField, confirmPasswordField;
    private ProgressBar progressBar;
    private DatabaseReference databaseReference;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password_set);

        // Retrieve the email passed from the OTP page
        email = getIntent().getStringExtra("email");

        saveButton = findViewById(R.id.save_button);
        newPasswordField = findViewById(R.id.new_pass1);
        confirmPasswordField = findViewById(R.id.new_pass2);
        progressBar = findViewById(R.id.progressbar);

        // Initialize Firebase database reference


        saveButton.setOnClickListener(v -> {
            String newPassword = newPasswordField.getText().toString().trim();
            String confirmPassword = confirmPasswordField.getText().toString().trim();

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please enter both passwords", Toast.LENGTH_SHORT).show();
            }
            else if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            }
            else if (newPassword.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
            }
            else {
                progressBar.setVisibility(View.VISIBLE);
                saveButton.setEnabled(false);
                updatePassword(newPassword);
            }
        });
    }

    private void updatePassword(String newPassword) {
        // Use the email to create a unique reference in the database
        String sanitizedEmail = email.replace(".", ",");
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(sanitizedEmail).child("RegistrationPageInformation").child("password").setValue(newPassword)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    saveButton.setEnabled(true);
                    if (task.isSuccessful()) {
                        Toast.makeText(New_password_set.this, "Password successfully updated", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(New_password_set.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(New_password_set.this, "Failed to update password. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    saveButton.setEnabled(true);
                    Toast.makeText(New_password_set.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
