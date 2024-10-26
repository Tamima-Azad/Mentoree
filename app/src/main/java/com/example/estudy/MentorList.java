package com.example.estudy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
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

public class MentorList extends AppCompatActivity {
    ArrayList<MentorItemModel> arrItem = new ArrayList<>();
    RecyclerMentorItemAdapter adapter;
    private TextView mentorList;
    private String userEmail;
    private long count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentor_list);
        mentorList = findViewById(R.id.mentorList);

        RecyclerView mentorListRecyclerView = findViewById(R.id.recylermentorlist);
        mentorListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize adapter
        adapter = new RecyclerMentorItemAdapter(this, arrItem);
        mentorListRecyclerView.setAdapter(adapter);

        Intent intent = getIntent();
        userEmail = intent.getStringExtra("USER_EMAIL");

        fetchMentorData(userEmail);
        Mentors(userEmail);
    }

    private void fetchMentorData(String userEmail) {
        DatabaseReference mentorRef = FirebaseDatabase.getInstance()
                .getReference("Mentors")
                .child(encodeEmail(userEmail));

        mentorRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrItem.clear();
                for (DataSnapshot mentorSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot nameSnapshot : mentorSnapshot.getChildren()) {
                        String mentorName = nameSnapshot.child("Name").getValue(String.class);
                        String mentorEmail = nameSnapshot.child("Email").getValue(String.class);

                        if (mentorName != null && mentorEmail != null) {
                            arrItem.add(new MentorItemModel(mentorName, mentorEmail, userEmail));
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MentorList.this, "Failed to load mentors: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void Mentors(String userEmail) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Mentors").child(encodeEmail(userEmail));

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();
                Log.d("DataCount", "Number of entries: " + count);
                mentorList.setText("Mentors: " + count);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", "Error counting entries: " + databaseError.getMessage());
            }
        });
    }

    private String encodeEmail(String email) {
        return email.replace(".", ",");
    }
}
