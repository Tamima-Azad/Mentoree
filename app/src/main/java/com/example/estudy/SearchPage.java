package com.example.estudy;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;
import java.util.List;

public class SearchPage extends AppCompatActivity {

    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;


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

        Intent intent = getIntent();
        String OwnEmail = intent.getStringExtra("USER_EMAIL");

        // Set up the search button click listener
        searchButton.setOnClickListener(v -> {
            String name = searchName.getText().toString().trim();
            if (!TextUtils.isEmpty(name)) {
                searchForUser(name,OwnEmail);
            } else {
                searchName.setError("Please enter a name");
                searchName.requestFocus();
            }
        });
    }

    private void searchForUser(String name, String OwnEmail) {
        searchProgressBar.setVisibility(View.VISIBLE);

        // Reference to the "All" node in the Firebase database
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("SearchALL");

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
                        String SearchingEmail = emailSnapshot.child("Email").getValue(String.class);
                        {
                            String str = userName;
                            for (int i = 0; i < str.length(); i++) {
                                // Inner loop to end position of the substring
                                for (int j = i + 1; j <= str.length(); j++) {
                                    if (str.substring(i, j).equalsIgnoreCase(name) && !OwnEmail.equals(SearchingEmail)) {
                                        // Add matching user to the list
                                        user3 user = new user3(userName, OwnEmail, SearchingEmail);
                                        userList.add(user);
                                        userFound = true;
                                    }
                                }
                            }
//                        if (userName != null && userName.equalsIgnoreCase(name)) {
//                            // Add matching user to the list
//                            user3 user = new user3(userName, email);
//                            userList.add(user);
//                            userFound = true;
//                        }
                        }
                    }
                }

                if (userFound) {
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
    private String encodeEmail(String email) {
        return email.replace(".", ",");
    }
}
