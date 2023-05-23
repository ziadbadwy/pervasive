package com.example.newsapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.newsapp.Controllers.PostAdapter;
import com.example.newsapp.databinding.ActivityPostsListBinding;

public class PostsList extends AppCompatActivity {

    ActivityPostsListBinding binding;
    SharedPreferences sharedPreferences;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostsListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        searchView = findViewById(R.id.search);
        searchView.clearFocus();

        sharedPreferences=getSharedPreferences("NewsApp",MODE_PRIVATE);
        if(sharedPreferences.getString("Role","").equals("Admin")){
            binding.floatingBtnToProfile.setVisibility(View.INVISIBLE);
            binding.addPostBtn.setVisibility(View.VISIBLE);
            binding.addPostBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(PostsList.this,UploadActivity.class));
                    finish();
                }
            });
        }

        Database.getPosts().thenAccept(posts -> {
            binding.postsList.setLayoutManager(new LinearLayoutManager(this));
            binding.postsList.setAdapter(new PostAdapter(posts));
        });
        binding.floatingBtnToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PostsList.this,Profile.class));
                finish();
            }
        });
        binding.floatinglogoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Database.auth.signOut();
            startActivity(new Intent(PostsList.this,LoginActivity.class));
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                Database.getPostsByTitle(newText).thenAccept(posts -> {
                    binding.postsList.setLayoutManager(new LinearLayoutManager(PostsList.this));
                    binding.postsList.setAdapter(new PostAdapter(posts));
                });
                return true;
            }
        });
    }


    }
