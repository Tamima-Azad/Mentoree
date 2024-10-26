package com.example.estudy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class otp_page extends AppCompatActivity {
    private Button loginButtonOTP;
    private EditText otpEditText;
    private String email;
    private String generatedOTP; // Variable to store the OTP passed from forgot_pass

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_page);

        email = getIntent().getStringExtra("email");
        generatedOTP = getIntent().getStringExtra("otp"); // Get the OTP passed from forgot_pass
        otpEditText = findViewById(R.id.otpEditText);
        loginButtonOTP = findViewById(R.id.LoginButtonOTP);

        loginButtonOTP.setOnClickListener(v -> {
            String enteredOTP = otpEditText.getText().toString().trim();
            verifyOTP(enteredOTP);
        });
    }

    private void verifyOTP(String enteredOTP) {
        // Directly compare the entered OTP with the generated OTP
        if (enteredOTP.isEmpty()) {
            Toast.makeText(otp_page.this, "Please enter the OTP.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (enteredOTP.equals(generatedOTP)) {
            Toast.makeText(otp_page.this, "Verification successful.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(otp_page.this, New_password_set.class);
            intent.putExtra("email",email);
            startActivity(intent);
        }
        else {
            Toast.makeText(otp_page.this, "Invalid OTP. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
}
