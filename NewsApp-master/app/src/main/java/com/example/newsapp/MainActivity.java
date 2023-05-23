package com.example.newsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.newsapp.Models.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Database.initUser();
        executorService.schedule(() -> {
            // Code to be executed after the delay

            Database.addPost(new Post("testPost","","",0.0f,""));
            Intent intent= new Intent(MainActivity.this, PostsList.class);
            intent.putExtra("postTitle","testPost");
            intent.putExtra("postD","");
            intent.putExtra("postImage","");
            startActivity(intent);

        }, 4, TimeUnit.SECONDS);

        // Shutdown the executor service when it is no longer needed
        executorService.shutdown();
    }
}