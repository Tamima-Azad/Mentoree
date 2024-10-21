package com.example.estudy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ForwardingListeningExecutorService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import android.os.Bundle;


import java.util.HashMap;
import java.util.Map;
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
    private EditText EditContactNo, EditEducation, EditCurrentWorkplace, EditSocialLinks, EditHomeTown, EditCurrentCity, EditSkills, EditAboutMe;
    private Button ProfileSaveButton;
    private boolean information = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        profilePicture = findViewById(R.id.profilePicture);
        coverPhoto = findViewById(R.id.coverPhoto);
        pName = findViewById(R.id.profileName);
        logout = findViewById(R.id.LogOutButton);
        EditContactNo = findViewById(R.id.EditContactNo);
        EditEducation = findViewById(R.id.EditEducation);
        EditCurrentWorkplace = findViewById(R.id.EditCurrentWorkplace);
        EditSocialLinks = findViewById(R.id.EditSocialLinks);
        EditHomeTown = findViewById(R.id.EditHomeTown);
        EditCurrentCity = findViewById(R.id.EditCurrentCity);
        EditSkills = findViewById(R.id.EditSkills);
        EditAboutMe = findViewById(R.id.EditAboutMe);
        ProfileSaveButton = findViewById(R.id.ProfileSaveButton);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // Get user email from intent and encode it
        Intent intent = getIntent();
        String userEmail = intent.getStringExtra("USER_EMAIL");
        String encodedEmail = encodeEmail(userEmail);
        databaseReference = FirebaseDatabase.getInstance().getReference(encodedEmail);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ImageLoader imageLoader = new ImageLoader();
                String profilePicUrl = dataSnapshot.child("RegistrationPageInformation").child("profilePictureUrl").getValue(String.class);
                String coverPicUrl = dataSnapshot.child("RegistrationPageInformation").child("coverPhotoUrl").getValue(String.class);
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
        Button ProfileSaveButton = findViewById(R.id.ProfileSaveButton);

        enableEditOnIconTouch(EditContactNo, EditEducation, EditCurrentWorkplace, EditSocialLinks, EditHomeTown, EditCurrentCity, EditSkills, EditAboutMe);


        ProfileSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve the text from the EditTexts
                String contactNo = EditContactNo.getText().toString().trim();
                if(contactNo != null)EditContactNo.setText(contactNo);
                String education = EditEducation.getText().toString().trim();
                if(education != null)EditEducation.setText(education);
                String currentWorkplace = EditCurrentWorkplace.getText().toString().trim();
                if(currentWorkplace != null)EditCurrentWorkplace.setText(currentWorkplace);
                String socialLinks = EditSocialLinks.getText().toString().trim();
                if(socialLinks != null)EditSocialLinks.setText(socialLinks);
                String homeTown = EditHomeTown.getText().toString().trim();
                if(homeTown != null)EditHomeTown.setText(homeTown);
                String currentCity = EditCurrentCity.getText().toString().trim();
                if(currentCity != null)EditCurrentCity.setText(currentCity);
                String skills = EditSkills.getText().toString().trim();
                if(skills != null)EditSkills.setText(skills);
                String aboutMe = EditAboutMe.getText().toString().trim();
                if(aboutMe != null)EditAboutMe.setText(aboutMe);
                informationSave(userEmail, contactNo, education, currentWorkplace, socialLinks, homeTown, currentCity, skills, aboutMe);
            }
        });


        profilePicture.setOnClickListener(v -> {
            isProfilePicture = true;
            openImagePicker();
        });
        logout.setOnClickListener(v -> {
            Intent intent1 = new Intent(profile.this, LoginActivity.class);
            startActivity(intent1);
            finish();
        });
        coverPhoto.setOnClickListener(v -> {
            isProfilePicture = false;
            openImagePicker();
        });

        // Call to retrieve user info
        retrieveUserInfo();
        if(information) retrieveUserInfo2(userEmail);
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
    private void setupEditTextWithIcon(final EditText editText) {
        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Check if the touch event is ACTION_DOWN (when the screen is touched)
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // Check if the touch is within the bounds of the right drawable
                    if (event.getRawX() >= (editText.getRight() - editText.getCompoundDrawables()[2].getBounds().width())) {
                        // Enable editing for the EditText
                        enableEditing(editText);
                        return true; // Return true to indicate that the event was handled
                    }
                }
                return false;
            }
        });
    }
    // Enable the EditText to be editable
    private void enableEditing(EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.setClickable(true);
        editText.requestFocus();
        editText.setSelection(editText.getText().length());
    }
    private void enableEditOnIconTouch(EditText... editTexts) {
        for (EditText editText : editTexts) {
            setupEditTextWithIcon(editText);
        }
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
    private void retrieveUserInfo2(String userEmail) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference((userEmail));

        myRef.child("Information").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String contactNo = dataSnapshot.child("contact").getValue(String.class);
                    String education = dataSnapshot.child("edu").getValue(String.class);
                    String currentWorkplace = dataSnapshot.child("workPlace").getValue(String.class);
                    String socialLinks = dataSnapshot.child("social_links").getValue(String.class);
                    String homeTown = dataSnapshot.child("homeTown").getValue(String.class);
                    String currentCity = dataSnapshot.child("current_city").getValue(String.class);
                    String skills = dataSnapshot.child("skills").getValue(String.class);
                    String aboutMe = dataSnapshot.child("about_me").getValue(String.class);

                    // Set the retrieved values to EditText fields if not null
                    if (contactNo != null) EditContactNo.setText(contactNo);
                    if (education != null) EditEducation.setText(education);
                    if (currentWorkplace != null) EditCurrentWorkplace.setText(currentWorkplace);
                    if (socialLinks != null) EditSocialLinks.setText(socialLinks);
                    if (homeTown != null) EditHomeTown.setText(homeTown);
                    if (currentCity != null) EditCurrentCity.setText(currentCity);
                    if (skills != null) EditSkills.setText(skills);
                    if (aboutMe != null) EditAboutMe.setText(aboutMe);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(profile.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void retrieveUserInfo() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("RegistrationPageInformation").child("name").getValue(String.class).toString().trim(); // Retrieve name
                    if (name != null) {
                        pName.setText(name);
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
            databaseReference.child("RegistrationPageInformation").child("profilePictureUrl").setValue(downloadUrl);
        } else if (imageType.equals("coverPhoto")) {
            databaseReference.child("RegistrationPageInformation").child("coverPhotoUrl").setValue(downloadUrl);
        }

        Toast.makeText(this, "Image URL saved successfully", Toast.LENGTH_SHORT).show();
    }

    private String encodeEmail(String email) {
        return email.replace(".", ",");
    }
    private void informationSave(String userEmail, String contactNo, String education, String currentWorkplace,
                                 String socialLinks, String homeTown, String currentCity, String skills, String aboutMe) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(encodeEmail(userEmail));

        // Save user information to Firebase
        user2 usr = new user2(contactNo, education, currentWorkplace, socialLinks, homeTown, currentCity, skills, aboutMe);
        myRef.child("Information").setValue(usr).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(profile.this, "User information saved successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(profile.this, profile.class);
                    intent.putExtra("USER_EMAIL", userEmail);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    Toast.makeText(profile.this, "Failed to save user information: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        });
    }
}