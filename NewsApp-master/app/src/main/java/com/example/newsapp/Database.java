package com.example.newsapp;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.newsapp.Models.Comment;
import com.example.newsapp.Models.Post;
import com.example.newsapp.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Database {
    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    static CollectionReference usersCollectionRef = db.collection("users");
    static CollectionReference postsCollectionRef = db.collection("posts");

    static FirebaseStorage storage = FirebaseStorage.getInstance();

    static FirebaseAuth auth= FirebaseAuth.getInstance();
    static boolean isAdmin = false;
    static User currentUser;
    static String authEmail=auth.getCurrentUser().getEmail() ;
    //check if user is admin or not



    public static void initUser() {
        String email = authEmail;
        if(email.isEmpty()){
            authEmail="mazenbakr2012@gmail.com";
        }
        usersCollectionRef.document(email).get()
                .addOnSuccessListener(
                        task -> {
                            if(task.exists())
                            {
                                Log.d("meow","User found");
                                currentUser = task.toObject(User.class);
                            }
                            else
                            {
                                Log.d("meow","User not found");
                            }
                        }
                )
                .addOnFailureListener(
                        task -> {
                            Log.d("meow","User not found");
                        }
                );
    }



    public static void addUser(User user) {

        usersCollectionRef.document(user.getEmail()).set(user)
                .addOnSuccessListener(
                task -> {
                    Log.d("meow","User added successfully");
                }
        )
                .addOnFailureListener(
                        task -> {
                            Log.d("meow","User not added");
                        }
                );
    }

    public static CompletableFuture<List<Post>> getPosts() {
        CompletableFuture<List<Post>> future = new CompletableFuture<>();
        postsCollectionRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Post> posts = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Post post = documentSnapshot.toObject(Post.class);
                        posts.add(post);
                    }
                    future.complete(posts);
                })
                .addOnFailureListener(e -> {
                    future.completeExceptionally(e);
                });
        return future;
    }
    public static List<User> getUsers() {
        List<User> users = new ArrayList<>();
        usersCollectionRef.get()
                .addOnSuccessListener(
                        task -> {
                            for (User user : task.toObjects(User.class)) {
                                Log.d("meow", user.toString());
                                users.add(new User(user.getUsername(),user.getEmail(), user.getPassword()));

                            }
                        }
                )
                .addOnFailureListener(
                        task -> {
                            Log.d("meow","Users not retrieved");
                        }
                );
        return users;
    }
    public static boolean checkUserEmail(String email) {
        usersCollectionRef.whereEqualTo("email", email).get()
                .addOnSuccessListener(
                        task -> {
                            if(task.isEmpty())
                            {
                                Log.d("meow","User not found");
                            }
                            else
                            {
                                Log.d("meow","User found");
                            }
                        }
                )
                .addOnFailureListener(
                        task -> {
                            Log.d("meow","User not found");
                        }
                );
        return false;
    }
    public static void deleteUser(String email) {
        usersCollectionRef.whereEqualTo("email", email).get()
                .addOnSuccessListener(
                        task -> {
                            if(task.isEmpty())
                            {
                                Log.d("meow","User not found");
                            }
                            else
                            {
                                Log.d("meow","User found");
                                usersCollectionRef.document(task.getDocuments().get(0).getId()).delete();
                            }
                        }
                )
                .addOnFailureListener(
                        task -> {
                            Log.d("meow","User not found");
                        }
                );
    }



    //Post functions

    public static void addPost(Post post) {

        postsCollectionRef.document(post.getTitle()).set(post)
                .addOnSuccessListener(
                        task -> {
                            Log.d("meow","Post added successfully");
                        }
                )
                .addOnFailureListener(
                        task -> {
                            Log.d("meow","Post not added");
                        }
                );
    }
    public static void deletePost(String title) {
        postsCollectionRef.document(title).delete();
    }
    //updata post
    public static void updatePost(String title,String newTitle ,String description, String imageUrl, float temperature) {
       if(newTitle!=null)
       {
              postsCollectionRef.document(newTitle).set(new Post(newTitle,description,imageUrl,temperature,""));
              return;
       }
       postsCollectionRef.document(title).update("description", description);
       postsCollectionRef.document(title).update("imageUrl", imageUrl);
       postsCollectionRef.document(title).update("temperature", temperature);
    }




    public static void likePost(String email,Post post)
    {
        usersCollectionRef.document(email)
                .collection("likedPosts")
                .document(post.getTitle()).set(post);
        postsCollectionRef.document(post.getTitle()).update("likes", FieldValue.increment(1));
        Log.d("meow","Post liked");

    }
    public static void unLikePost(String email,String title)
    {
        usersCollectionRef.document(email)
                .collection("likedPosts")
                .document(title).delete();

        postsCollectionRef.document(title).update("likes", FieldValue.increment(-1));
        Log.d("meow","Post unliked");

    }

    public static CompletableFuture<Boolean> isPostLiked(String email, String title) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        usersCollectionRef.document(email)
                .collection("likedPosts")
                .document(title)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        boolean isLiked = documentSnapshot.exists();
                        future.complete(isLiked);
                    } else {
                        future.complete(false);
                        Log.d("meow", "Error fetching document: " + task.getException());
                    }
                });
       
        return future;
    }



    public static void savePost(String email,Post post)
    {
        usersCollectionRef.document(email)
                .collection("savedPosts")
                .document(post.getTitle()).set(post);
        Log.d("meow","Post saved");
    }
    public static void unSavePost(String email,String title)
    {
        usersCollectionRef.document(email)
                .collection("savedPosts")
                .document(title).delete();
        Log.d("meow","Post unsaved");

    }
    public static CompletableFuture<Boolean> isPostSaved(String email,String title)
    {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        usersCollectionRef.document(email)
                .collection("savedPosts")
                .document(title)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        boolean isSaved = documentSnapshot.exists();
                        future.complete(isSaved);
                    } else {
                        future.complete(false);
                        Log.d("meow", "Error fetching document: " + task.getException());
                    }
                });
        return future;
    }


    //comment functions

    public static void addComment(String postTitle, Comment comment)
    {
       postsCollectionRef.document(postTitle).collection("Comments").add(comment);
    }

    //getpostcomments
    public static CompletableFuture<List<Comment>> getPostComments(String postTitle) {
        CompletableFuture<List<Comment>> future = new CompletableFuture<>();

        postsCollectionRef.document(postTitle).collection("Comments").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Comment> comments = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Comment comment = documentSnapshot.toObject(Comment.class);
                        comments.add(comment);
                    }
                    future.complete(comments);
                })
                .addOnFailureListener(e -> {
                    future.completeExceptionally(e);
                });

        return future;
    }


    //uploade image
    public static CompletableFuture<String> uploadImage(Bitmap bitmap, String imageName) {
        CompletableFuture<String> future = new CompletableFuture<>();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageData = baos.toByteArray();

        StorageReference imageRef = storage.getReference().child(imageName);
        UploadTask uploadTask = imageRef.putBytes(imageData);

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();
                future.complete(imageUrl);
            });
        }).addOnFailureListener(e -> {
            future.completeExceptionally(e);
        });

        return future;
    }
    public static CompletableFuture<List<Post>> getSavePost(String email) {
        CompletableFuture<List<Post>> future = new CompletableFuture<>();

        usersCollectionRef.document(email).collection("savePost").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Post> posts = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Post post = documentSnapshot.toObject(Post.class);
                        posts.add(post);
                    }
                    future.complete(posts);
                })
                .addOnFailureListener(e -> {
                    future.completeExceptionally(e);
                });

        return future;
    }
    public static CompletableFuture<List<Post>> getPostsByTitle(String title) {
        CompletableFuture<List<Post>> future = new CompletableFuture<>();
        postsCollectionRef.whereEqualTo("title", title).get().addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Post> posts = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Post post = documentSnapshot.toObject(Post.class);
                        posts.add(post);
                    }
                    future.complete(posts);
                })
                .addOnFailureListener(e -> {
                    future.completeExceptionally(e);
                });
        return future;
    }
}
