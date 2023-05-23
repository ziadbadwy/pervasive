package com.example.newsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.newsapp.Models.Post;
import com.example.newsapp.Models.User;
import com.example.newsapp.databinding.ActivityPostBinding;

public class PostActivity extends AppCompatActivity {


    ActivityPostBinding binding;
    User user=Database.currentUser;
    String imageUrl;
    boolean isLikedBool;
    boolean isSavedBool;


    private SharedPreferences sharedPreferences;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Bundle bundle = getIntent().getExtras();
        bundle.getString("postTitle");
        bundle.getString("postD");
        sharedPreferences=getSharedPreferences("NewsApp",MODE_PRIVATE);

        if(sharedPreferences.getString("Role","").equals("Admin")){
            binding.deleteBtn.setVisibility(View.VISIBLE);
            binding.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Database.deletePost(bundle.getString("postTitle"));
                    startActivity(new Intent(PostActivity.this,PostsList.class));
                    finish();
                }
            });

            binding.editBtn.setVisibility(View.VISIBLE);
            binding.editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(PostActivity.this,EditActivity.class)
                            .putExtra("postTitle",bundle.getString("postTitle"))
                            .putExtra("Description",bundle.getString("postD"))
                            .putExtra("Image",imageUrl)
                    );
                    finish();
                }
            });

        }

        binding.postTitle.setText(bundle.getString("postTitle"));
        binding.postDescription.setText(bundle.getString("postD"));
        imageUrl = bundle.getString("postImage");
        if (!imageUrl.equals(""))
            Glide.with(this)
                    .load(imageUrl)
                    .into(binding.postImage);
        else binding.postImage.setVisibility(View.GONE);


        Database.isPostLiked(user.getEmail(), bundle.getString("postTitle"))
                .thenAccept(isLiked -> {
                    if (isLiked) {
                        isLikedBool= true;
                        binding.likeButton.setImageDrawable(getResources().getDrawable(R.drawable.liked));
                    } else {
                        isLikedBool= false;
                    }
                })
                .exceptionally(e -> {
                    System.err.println("Error: " + e);
                    return null;
                });
        binding.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isLikedBool) {
                    binding.likeButton.setImageDrawable(getResources().getDrawable(R.drawable.liked));
                    Database.likePost(user.getEmail(), new Post(bundle.getString("postTitle"), bundle.getString("postD"), imageUrl, 0.0f, ""));
                    isLikedBool=true;
                }
                else {
                    binding.likeButton.setImageDrawable(getResources().getDrawable(R.drawable.notliked));
                    Database.unLikePost(user.getEmail(), bundle.getString("postTitle"));
                    isLikedBool=false;
                }
            }
        });
        //save post


        Database.isPostSaved(user.getEmail(), bundle.getString("postTitle"))
                .thenAccept(isSaved -> {
                    if (isSaved) {
                        binding.saveButton.setImageDrawable(getResources().getDrawable(R.drawable.baseline_bookmark_24));
                    } else {
                        binding.saveButton.setImageDrawable(getResources().getDrawable(R.drawable.baseline_bookmark_border_24));
                    }
                })
                .exceptionally(e -> {
                    System.err.println("Error: " + e);
                    return null;
                });


        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isSavedBool) {
                    binding.saveButton.setImageDrawable(getResources().getDrawable(R.drawable.saved));
                    Database.savePost(user.getEmail(), new Post(bundle.getString("postTitle"), bundle.getString("postD"), imageUrl, 0.0f, ""));
                    isSavedBool=true;
                }
                else {
                    binding.saveButton.setImageDrawable(getResources().getDrawable(R.drawable.notsaved));
                    Database.unSavePost(user.getEmail(), bundle.getString("postTitle"));
                    isSavedBool=false;
                }
            }
        });

        binding.addCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddCommentFragment addCommentFragment=new AddCommentFragment();
                Bundle bundle=new Bundle();
                bundle.putString("postTitle",getIntent().getExtras().getString("postTitle"));
                addCommentFragment.setArguments(bundle);
                addCommentFragment.show(getSupportFragmentManager(),"AddCommentFragment");
            }
        });
        binding.showCommentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommnetsFragment commnetsFragment=new CommnetsFragment();
                Bundle bundle=new Bundle();
                bundle.putString("postTitle",getIntent().getExtras().getString("postTitle"));
                commnetsFragment.setArguments(bundle);
                commnetsFragment.show(getSupportFragmentManager(),"CommentsFragment");
            }
        });




    }
}