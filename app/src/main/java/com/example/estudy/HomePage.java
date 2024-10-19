package com.example.estudy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.util.concurrent.ForwardingListeningExecutorService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class HomePage extends AppCompatActivity {
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        ImageButton profilePictureButton = findViewById(R.id.profilePictureButton);
        Intent intent = getIntent();
        String userEmail = intent.getStringExtra("USER_EMAIL");
        String encodedEmail = encodeEmail(userEmail);
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference(encodeEmail(userEmail)).child(encodedEmail);


        profilePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a new intent for navigating to the profile activity
                Intent profileIntent = new Intent(HomePage.this, profile.class);
                profileIntent.putExtra("USER_EMAIL2", userEmail); // Pass user email to profile
                startActivity(profileIntent);
            }
        });

        ImageButton notificationButton = findViewById(R.id.notificationButton);
        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, Notification.class);
                startActivity(intent);
            }
        });

    }
    private String encodeEmail(String email) {
        return email.replace(".", ",");
    }

}