package com.yavuzsyilmaz.javainstagramclone.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.yavuzsyilmaz.javainstagramclone.R;
import com.yavuzsyilmaz.javainstagramclone.adapter.PostAdapter;
import com.yavuzsyilmaz.javainstagramclone.databinding.ActivityFeedBinding;
import com.yavuzsyilmaz.javainstagramclone.model.Post;

import java.util.ArrayList;
import java.util.Map;

public class FeedActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;

    ArrayList<Post> postArrayList;
    private ActivityFeedBinding binding;

    PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFeedBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        postArrayList = new ArrayList<>();

        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        getData();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postAdapter = new PostAdapter(postArrayList);
        binding.recyclerView.setAdapter(postAdapter);
    }

    private void getData() {
        firebaseFirestore.collection("Posts").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(FeedActivity.this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
                if (value != null) {
                     for (DocumentSnapshot snapshot: value.getDocuments()) {
                         Map<String, Object> data = snapshot.getData();

                         String userEmail = (String) data.get("useremail");
                         String downloadUrl = (String) data.get("downloadUrl");
                         String comment = (String) data.get("comment");

                         Post post = new Post(userEmail,comment,downloadUrl);
                         postArrayList.add(post);
                         System.out.println(comment);
                     }
                     postAdapter.notifyDataSetChanged();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.uploadOption) {
            //Upload activity
            Intent uploadActivityIntent = new Intent(FeedActivity.this, UploadActivity.class);
            startActivity(uploadActivityIntent);

        } else if (item.getItemId() == R.id.signOutOption) {
            //Sign out
            auth.signOut();
            Intent uploadActivityIntent = new Intent(FeedActivity.this, MainActivity.class);
            startActivity(uploadActivityIntent);
            finish();
        }



        return super.onOptionsItemSelected(item);
    }
}