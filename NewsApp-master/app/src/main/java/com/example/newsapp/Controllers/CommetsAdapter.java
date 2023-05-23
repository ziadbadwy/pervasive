package com.example.newsapp.Controllers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsapp.Models.Comment;
import com.example.newsapp.R;

import java.util.List;

public class CommetsAdapter extends RecyclerView.Adapter<CommetsAdapter.ViewHolder>{

    List<Comment> comments;

    public CommetsAdapter(List<Comment> comments) {
        this.comments = comments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment c=comments.get(position);
        holder.account.setText(c.getAccount());
        holder.comment.setText(c.getComment());
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView account,comment;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            account=itemView.findViewById(R.id.usernameComment);
            comment=itemView.findViewById(R.id.textComment);
        }
    }
}
