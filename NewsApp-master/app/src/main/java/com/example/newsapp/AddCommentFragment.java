package com.example.newsapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.newsapp.Models.Comment;
import com.example.newsapp.Models.User;
import com.example.newsapp.databinding.FragmentAddcommentBinding;
import com.example.newsapp.databinding.FragmentCommnetsBinding;

public class AddCommentFragment extends DialogFragment {

    FragmentAddcommentBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding= FragmentAddcommentBinding.inflate(inflater,container,false);

        String userName= Database.currentUser.getUsername();
        String postTitle= getArguments().getString("postTitle");
        binding.addCommentFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              String comment=binding.commentFrag.getText().toString();
                if(comment.isEmpty())
                {
                    Toast.makeText(getContext(),"can't comment with empty text!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Database.addComment(postTitle,new Comment(userName,binding.commentFrag.getText().toString()));
                dismiss();
            }
        });
        return binding.getRoot();
    }
}
