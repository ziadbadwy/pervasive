package com.example.newsapp.Controllers;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.newsapp.Database;
import com.example.newsapp.Models.Post;
import com.example.newsapp.PostActivity;
import com.example.newsapp.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;


public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder>{
    List<Post> postList;

    public PostAdapter(List<Post> postList) {

        this.postList=postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_card, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post=postList.get(position);
        if(!post.getImageUrl().equals(""))
            Glide.with(holder.itemView.getContext())
                    .load(post.getImageUrl())
                    .into(holder.postImage);
        else holder.postImage.setVisibility(View.GONE);
        holder.postTitle.setText(post.getTitle());
        notifyDataSetChanged();
        holder.readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(holder.itemView.getContext(), PostActivity.class);
                intent.putExtra("postTitle",post.getTitle());
                intent.putExtra("postD",post.getDescription());
                intent.putExtra("postImage",post.getImageUrl());
                holder.itemView.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }


    public class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView postImage;
        TextView postTitle;
        Button readButton;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            postImage=itemView.findViewById(R.id.image_card);
            postTitle=itemView.findViewById(R.id.text_title);
            readButton=itemView.findViewById(R.id.readButton);
        }

    }
}
