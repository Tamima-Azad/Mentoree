package com.example.estudy;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class PostPage extends AppCompatActivity {

    private static final int GALLERY_REQUEST_CODE = 100;

    private EditText postInput;
    private ImageButton addImageButton;
    private Button postButton;
    private ImageView imagePreview;
    private RecyclerView postsRecyclerView;
    private FloatingActionButton fabCreatePost;

    private Uri imageUri = null;
    private DatabaseReference postsRef, usersRef;
    private StorageReference storageRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private List<PostModel> postList;
    private PostAdapter postAdapter;

    private String userEmail, userName, userProfileUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_page);

        // Initialize views
        postInput = findViewById(R.id.post_input);
        addImageButton = findViewById(R.id.add_image_button);
        postButton = findViewById(R.id.post_button);
        imagePreview = findViewById(R.id.image_preview);
        postsRecyclerView = findViewById(R.id.posts_recyclerview);
        fabCreatePost = findViewById(R.id.fab_create_post);

        // Initialize Firebase Authentication and get current user
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            userEmail = currentUser.getEmail();
            userProfileUrl = currentUser.getPhotoUrl() != null ? currentUser.getPhotoUrl().toString() : null;
        } else {
            Log.e("PostPage", "User not logged in.");
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Firebase references
        postsRef = FirebaseDatabase.getInstance().getReference().child("posts");
        usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        storageRef = FirebaseStorage.getInstance().getReference().child("post_images");

        // Initialize RecyclerView and adapter
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(postList, this);
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        postsRecyclerView.setAdapter(postAdapter);

        // Open gallery to select an image
        addImageButton.setOnClickListener(v -> openGallery());

        // Handle posting text and image
        postButton.setOnClickListener(v -> {
            String postText = postInput.getText().toString().trim();
            if (!postText.isEmpty() || imageUri != null) {
                retrieveUserNameAndPost(postText); // First, retrieve user name, then post
            } else {
                Toast.makeText(PostPage.this, "Enter text or select an image", Toast.LENGTH_SHORT).show();
            }
        });

        // Load posts from Firebase
        loadPostsFromFirebase();
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            imagePreview.setImageURI(imageUri);
            imagePreview.setVisibility(View.VISIBLE);
        }
    }

    private void retrieveUserNameAndPost(String postText) {
        if (userEmail != null) {
            String encodedEmail = encodeEmail(userEmail);
            usersRef.child(encodedEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        userName = dataSnapshot.child("name").getValue(String.class);
                        if (userName != null) {
                            uploadPost(postText);
                        } else {
                            Toast.makeText(PostPage.this, "Failed to retrieve user name", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(PostPage.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void uploadPost(String postText) {
        if (imageUri != null) {
            StorageReference fileRef = storageRef.child(System.currentTimeMillis() + ".jpg");
            fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();
                savePostToDatabase(postText, imageUrl);
            })).addOnFailureListener(e -> Toast.makeText(PostPage.this, "Image upload failed", Toast.LENGTH_SHORT).show());
        } else {
            savePostToDatabase(postText, null);
        }
    }

    private void savePostToDatabase(String postText, @Nullable String imageUrl) {
        if (userEmail != null && userName != null) {
            String encodedEmail = encodeEmail(userEmail);
            String postId = postsRef.child(encodedEmail).push().getKey();

            if (postId != null) {
                PostModel post = new PostModel(postId,userEmail,userName, postText, imageUrl, System.currentTimeMillis());

                postsRef.child(encodedEmail).child(postId).setValue(post).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(PostPage.this, "Post uploaded successfully", Toast.LENGTH_SHORT).show();
                        resetPostInput();
                    } else {
                        Toast.makeText(PostPage.this, "Failed to upload post", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void resetPostInput() {
        postInput.setText("");
        imagePreview.setVisibility(View.GONE);
        imageUri = null;
    }

    private void loadPostsFromFirebase() {
        //String encodedEmail = encodeEmail(userEmail);
        if (userEmail != null) {
            String encodedEmail = encodeEmail(userEmail);
            postsRef.child(encodedEmail).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    postList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        PostModel post = snapshot.getValue(PostModel.class);
                        if (post != null) {
                            postList.add(post);
                        }
                    }
                    postAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(PostPage.this, "Failed to load posts", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private String encodeEmail(String email) {
        return email.replace(".", ",");
    }
}
