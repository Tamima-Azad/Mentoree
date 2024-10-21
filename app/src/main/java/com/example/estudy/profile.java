package com.example.estudy;

import android.app.AlertDialog;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class profile extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private boolean isProfilePicture = true;
    private ImageView profilePicture, coverPhoto;
    private Uri imageUri;
    public TextView pName;
    private ImageView logout;
    // Firebase references
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private ImageButton myPostButton, homeButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize views
        profilePicture = findViewById(R.id.profilePicture);
        coverPhoto = findViewById(R.id.coverPhoto);
        pName = findViewById(R.id.profileName);
        logout = findViewById(R.id.LogOutButton);
        // Initialize Firebase Storage
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // Get user email from intent and encode it
        Intent intent = getIntent();
        String userEmail = intent.getStringExtra("USER_EMAIL2");
        intent.putExtra("USER_EMAIL4", userEmail);
        String encodedEmail = encodeEmail(userEmail);
        databaseReference = FirebaseDatabase.getInstance().getReference(encodedEmail).child(encodedEmail);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ImageLoader imageLoader = new ImageLoader();
                String profilePicUrl = dataSnapshot.child("profilePictureUrl").getValue(String.class);
                String coverPicUrl = dataSnapshot.child("coverPhotoUrl").getValue(String.class);
                ImageView profileImageView = findViewById(R.id.profilePicture);
                imageLoader.loadImageIntoImageView(profilePicUrl, profileImageView, 180, 180, R.drawable.defaultpic);
                ImageView coverImageView = findViewById(R.id.coverPhoto);
                imageLoader.loadImageIntoImageView(coverPicUrl, coverImageView, 620, 360, R.drawable.coverphoto);
                if (profilePicUrl != null) {
                    ImageView imageView = findViewById(R.id.profilePicture);
                    Picasso.get().load(profilePicUrl).placeholder(R.drawable.defaultpic).into(imageView);
                }
                if (coverPicUrl != null) {
                    ImageView imageView = findViewById(R.id.coverPhoto);
                    Picasso.get().load(coverPicUrl).placeholder(R.drawable.coverphoto).into(imageView);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
                Log.e("FirebaseError", "Error retrieving image URL", databaseError.toException());
            }
        });


        // Set click listeners for profile picture and cover photo
        profilePicture.setOnClickListener(v -> {
            isProfilePicture = true;
            openImagePicker();
        });
        mAuth = FirebaseAuth.getInstance();
        logout.setOnClickListener(v -> {
            //showLogoutConfirmationDialog();
            mAuth.signOut();
            Intent intent1 = new Intent(profile.this, LoginActivity.class);
            startActivity(intent1);
            finish();
            Toast.makeText(profile.this, "LogOut Successfully", Toast.LENGTH_SHORT).show();
        });
        coverPhoto.setOnClickListener(v -> {
            isProfilePicture = false;
            openImagePicker();
        });

        // Call to retrieve user info
        retrieveUserInfo();
        myPostButton = findViewById(R.id.ProfileMyPostButton);
        myPostButton.setOnClickListener(v -> {
            Intent intent2 = new Intent(profile.this, PostPage.class);
            startActivity(intent2);
        });

        homeButton = findViewById(R.id.ProfileHomeButton);
        homeButton.setOnClickListener(v -> {
            Intent intent1 = new Intent(profile.this, HomePage.class);
            intent1.putExtra("USER_EMAIL", userEmail);
            startActivity(intent1);
            finish();
        });

    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            if (imageUri != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    if (isProfilePicture) {
                        profilePicture.setImageBitmap(bitmap);
                        uploadImageToFirebase("profilePicture");
                    } else {
                        coverPhoto.setImageBitmap(bitmap);
                        uploadImageToFirebase("coverPhoto");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(profile.this, "Failed to set image", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Image URI is null", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void retrieveUserInfo() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class).toString().trim(); // Retrieve name
                    if (name != null) {
                        pName.setText(name); // Set the name in the TextView
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(profile.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void uploadImageToFirebase(final String imageType) {
        if (imageUri != null) {
            // Create a unique file name for the image
            String fileName = UUID.randomUUID().toString();
            StorageReference ref = storageReference.child("images/" + fileName);

            ref.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();
                        // Save the download URL in Firebase Realtime Database
                        saveImageUrlToDatabase(imageType, downloadUrl);
                    }))
                    .addOnFailureListener(e -> {
                        Log.e("FirebaseUpload", "Failed to upload image", e);
                        Toast.makeText(profile.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void saveImageUrlToDatabase(String imageType, String downloadUrl) {
        if (imageType.equals("profilePicture")) {
            databaseReference.child("profilePictureUrl").setValue(downloadUrl);
        } else if (imageType.equals("coverPhoto")) {
            databaseReference.child("coverPhotoUrl").setValue(downloadUrl);
        }

        Toast.makeText(this, "Image URL saved successfully", Toast.LENGTH_SHORT).show();
    }

    private String encodeEmail(String email) {
        return email.replace(".", ",");
    }

    private void showLogoutConfirmationDialog(){
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    mAuth.signOut();
                    Intent intent = new Intent(profile.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(profile.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show();
    }
}