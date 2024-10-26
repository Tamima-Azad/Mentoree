package com.example.estudy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Random;

public class forgot_pass extends AppCompatActivity {

    private Button OTPButton;
    private EditText ForgotPassEmail;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

        OTPButton = findViewById(R.id.OTPButton);
        ForgotPassEmail = findViewById(R.id.ForgotPassEmail);

        // Initialize Firebase reference

         // Adjust "Users" if your database path is different

        OTPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = ForgotPassEmail.getText().toString().trim();

                if (email.isEmpty()) {
                    Toast.makeText(forgot_pass.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    return;
                }

                checkEmailExists(email);
            }
        });
    }

    private void checkEmailExists(String email) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(encodeEmail(email));
        databaseReference.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Email exists in the database, proceed with OTP generation
                    String otp = generateOTP();

                    sendOTPEmail(email, otp);

                    // Navigate to OTP page with email and OTP
                    Intent intent = new Intent(forgot_pass.this, otp_page.class);
                    intent.putExtra("email", email);
                    intent.putExtra("otp", otp);  // Pass the OTP to the OTP page
                    startActivity(intent);
                }
                else {
                    Toast.makeText(forgot_pass.this, "Email not found. Please check and try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(forgot_pass.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String generateOTP() {
        Random random = new Random();
        int otp = 1000 + random.nextInt(9000); // Generates a 4-digit OTP
        return String.valueOf(otp);
    }
    private void sendOTPEmail(String email, String otp) {
        String subject = "OTP from Mentoree";
        String message = "Your OTP code is: " + otp;
        JavaMailAPI javaMailAPI = new JavaMailAPI(email, subject, message);
        javaMailAPI.execute();
    }
    private String encodeEmail(String email) {
        return email.replace(".", ",");
    }
}
