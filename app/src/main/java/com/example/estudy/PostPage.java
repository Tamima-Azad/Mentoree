package com.example.estudy;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class PostPage extends AppCompatActivity {

    private LinearLayout postContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_page);

        postContainer = findViewById(R.id.post_container);
        Button createPostButton = findViewById(R.id.create_post_button);

        // Handle create post button click
        createPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreatePostDialog();
            }
        });
    }

    private void showCreatePostDialog() {
        // Create a dialog to input post content
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Post");

        // Set up the input field
        final EditText input = new EditText(this);
        input.setHint("Enter your post content");
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Post", (dialog, which) -> {
            String postContent = input.getText().toString().trim();
            if (!TextUtils.isEmpty(postContent)) {
                addPost(postContent); // Add the post if content is not empty
            } else {
                showErrorDialog("Post content cannot be empty!");
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showErrorDialog(String message) {
        // Show error message in a dialog
        AlertDialog.Builder errorDialog = new AlertDialog.Builder(this);
        errorDialog.setTitle("Error");
        errorDialog.setMessage(message);
        errorDialog.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        errorDialog.show();
    }

    private void addPost(String postContent) {
        // Create a new TextView for each post
        TextView postTextView = new TextView(this);
        postTextView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        postTextView.setText(postContent);
        postTextView.setPadding(10, 10, 10, 10);
        postTextView.setTextSize(16); // Set text size for better visibility

        // Add the TextView to the LinearLayout
        postContainer.addView(postTextView);
    }
}
