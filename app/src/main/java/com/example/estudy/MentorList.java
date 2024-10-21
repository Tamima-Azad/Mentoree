package com.example.estudy;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MentorList extends AppCompatActivity {
    ArrayList<MentorItemModel>arrItem=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mentor_list);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) RecyclerView Mentorlist=findViewById(R.id.recylermentorlist);

        Mentorlist.setLayoutManager(new LinearLayoutManager(this));

        // MentorItemModel model=new MentorItemModel(R.drawable.ic_launcher_foreground,"ABC");

        //data add
        arrItem.add(new MentorItemModel(R.drawable.ic_launcher_background,"Tamima Azad"));
        arrItem.add(new MentorItemModel(R.drawable.ic_launcher_background,"B"));
        arrItem.add(new MentorItemModel(R.drawable.ic_launcher_background,"C"));
        arrItem.add(new MentorItemModel(R.drawable.ic_launcher_background,"D"));
        arrItem.add(new MentorItemModel(R.drawable.ic_launcher_background,"E"));
        arrItem.add(new MentorItemModel(R.drawable.ic_launcher_background,"F"));
        arrItem.add(new MentorItemModel(R.drawable.ic_launcher_background,"G"));
        arrItem.add(new MentorItemModel(R.drawable.ic_launcher_background,"H"));
        arrItem.add(new MentorItemModel(R.drawable.ic_launcher_background,"I"));
        arrItem.add(new MentorItemModel(R.drawable.ic_launcher_background,"J"));
        arrItem.add(new MentorItemModel(R.drawable.ic_launcher_background,"ABC"));
        arrItem.add(new MentorItemModel(R.drawable.ic_launcher_background,"ABC"));
        arrItem.add(new MentorItemModel(R.drawable.ic_launcher_background,"ABC"));
        arrItem.add(new MentorItemModel(R.drawable.ic_launcher_background,"ABC"));
        arrItem.add(new MentorItemModel(R.drawable.ic_launcher_foreground,"ABC"));
        arrItem.add(new MentorItemModel(R.drawable.ic_launcher_foreground,"ABC"));
        arrItem.add(new MentorItemModel(R.drawable.ic_launcher_foreground,"k"));
        arrItem.add(new MentorItemModel(R.drawable.ic_launcher_foreground,"M"));
        arrItem.add(new MentorItemModel(R.drawable.ic_launcher_foreground,"T"));
        arrItem.add(new MentorItemModel(R.drawable.ic_launcher_foreground,"ABC"));
        arrItem.add(new MentorItemModel(R.drawable.ic_launcher_foreground,"ABCD"));
        arrItem.add(new MentorItemModel(R.drawable.ic_launcher_foreground,"ABCE"));
        arrItem.add(new MentorItemModel(R.drawable.ic_launcher_foreground,"ABCF"));
        arrItem.add(new MentorItemModel(R.drawable.ic_launcher_foreground,"ABCG"));
        arrItem.add(new MentorItemModel(R.drawable.ic_launcher_foreground,"ABCH"));
        arrItem.add(new MentorItemModel(R.drawable.ic_launcher_foreground,"ABCI"));
        arrItem.add(new MentorItemModel(R.drawable.ic_launcher_foreground,"ABCJ"));
        arrItem.add(new MentorItemModel(R.drawable.ic_launcher_foreground,"ABCK"));
        arrItem.add(new MentorItemModel(R.drawable.ic_launcher_foreground,"ABCL"));
        arrItem.add(new MentorItemModel(R.drawable.ic_launcher_foreground,"ABCM"));
        arrItem.add(new MentorItemModel(R.drawable.ic_launcher_foreground,"ABCM"));
        arrItem.add(new MentorItemModel(R.drawable.ic_launcher_foreground,"ABCM"));
        arrItem.add(new MentorItemModel(R.drawable.ic_launcher_foreground,"ABCM"));
        arrItem.add(new MentorItemModel(R.drawable.ic_launcher_foreground,"ABCM"));
        arrItem.add(new MentorItemModel(R.drawable.ic_launcher_foreground,"ABCM"));
        arrItem.add(new MentorItemModel(R.drawable.ic_launcher_foreground,"ABCM"));
        arrItem.add(new MentorItemModel(R.drawable.ic_launcher_foreground,"ABCM"));
        arrItem.add(new MentorItemModel(R.drawable.ic_launcher_foreground,"ABCM"));
        arrItem.add(new MentorItemModel(R.drawable.ic_launcher_foreground,"ABCM"));
        //addapter works as like machine
        RecyclerMentorItemAdapter adapter=new RecyclerMentorItemAdapter(this,arrItem);
        Mentorlist.setAdapter(adapter);

    }
}