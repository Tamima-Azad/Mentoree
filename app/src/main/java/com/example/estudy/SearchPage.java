package com.example.estudy;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchPage extends AppCompatActivity {

    public EditText searchName;
    private Button searchButton;
    private ProgressBar searchProgressBar;
    private RecyclerView searchRecyclerView;
    private UserAdapter userAdapter;
    private List<user3> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);

        // Initialize views
        searchName = findViewById(R.id.search_name);
        searchButton = findViewById(R.id.search_button);
        searchProgressBar = findViewById(R.id.search_progressbar);
        searchRecyclerView = findViewById(R.id.search_recyclerview);

        // Initialize user list and adapter
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchRecyclerView.setAdapter(userAdapter);

        // Set up the search button click listener
        searchButton.setOnClickListener(v -> {
            String name = searchName.getText().toString().trim();
            if (!TextUtils.isEmpty(name)) {
                searchForUser(name);
            } else {
                searchName.setError("Please enter a name");
                searchName.requestFocus();
            }
        });
    }

    private void searchForUser(String name) {
        searchProgressBar.setVisibility(View.VISIBLE);

        // Reference to the "All" node in the Firebase database
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("All");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                searchProgressBar.setVisibility(View.GONE);
                userList.clear();  // Clear previous search results

                boolean userFound = false;

                // Iterate through all users under the "All" node
                for (DataSnapshot nameSnapshot : snapshot.getChildren()) {
                    // Inside each name node, iterate through emails
                    for (DataSnapshot emailSnapshot : nameSnapshot.getChildren()) {
                        String userName = emailSnapshot.child("Name").getValue(String.class);
                        String email = emailSnapshot.child("Email").getValue(String.class);

                        if (userName != null && userName.equalsIgnoreCase(name)) {
                            // Add matching user to the list
                            user3 user = new user3(userName, email);
                            userList.add(user);
                            userFound = true;
                        }
                    }
                }

                if (userFound) {
                    // Notify adapter to refresh the RecyclerView with the new data
                    userAdapter.notifyDataSetChanged();
                } else {
                    // If no user is found, show a message
                    Toast.makeText(SearchPage.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                searchProgressBar.setVisibility(View.GONE);
                // Handle database error
                Toast.makeText(SearchPage.this, "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
