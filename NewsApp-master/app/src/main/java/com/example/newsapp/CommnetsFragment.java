package com.example.newsapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.newsapp.Controllers.CommetsAdapter;
import com.example.newsapp.Models.Comment;
import com.example.newsapp.databinding.FragmentCommnetsBinding;

import java.util.ArrayList;
import java.util.List;

public class CommnetsFragment extends DialogFragment {
    FragmentCommnetsBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding=FragmentCommnetsBinding.inflate(inflater,container,false);

        Database.getPostComments(getArguments().getString("postTitle"))
                .thenAccept(comments -> {
                    binding.commentsList.setLayoutManager(new LinearLayoutManager(getContext()));
                    binding.commentsList.setAdapter(new CommetsAdapter(comments));
                })
                .exceptionally(e -> {
                    // Handle the failure case
                    Log.d("meow", "Comments not retrieved: " + e.getMessage());
                    return null;
                });
        return binding.getRoot();
    }
}
