package com.example.newsapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.newsapp.Controllers.PostAdapter;
import com.example.newsapp.databinding.ActivityProfileBinding;

public class Profile extends AppCompatActivity {
ActivityProfileBinding binding;
String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        email =Database.authEmail;
        Database.getSavePost(email).thenAccept(posts -> {
            binding.savedpostsList.setLayoutManager(new LinearLayoutManager(this));
            binding.savedpostsList.setAdapter(new PostAdapter(posts));
        });
        binding.profileEmail.setText(email);
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Profile.this,PostsList.class));
                finish();
            }
        });
    }
}