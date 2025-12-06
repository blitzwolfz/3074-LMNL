package com.example.lmnl.post;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;

import com.example.lmnl.AboutActivity;
import com.example.lmnl.R;
import com.example.lmnl.auth.SessionManager;
import com.example.lmnl.user.ProfileActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class FeedActivity extends AppCompatActivity {

    private RecyclerView rvPosts;
    private PostsAdapter postsAdapter;
    private PostsDbHelper dbHelper;
    private BottomNavigationView bottomNav;
    private FloatingActionButton fabPost;
    private SearchView searchView;
    private SessionManager sessionManager;
    private DailyLimitsManager limitsManager;
    private TextView tvFeedCount, tvPostCount, tvLikesCount, tvCommentsCount;
    private List<Post> allPosts = new ArrayList<>();
    private List<Post> originalPosts = new ArrayList<>();
    private int currentPostIndex = 0;
    private Button btnLoadNextPost;
    private MaterialToolbar topAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        // Fix: Use correct RecyclerView ID from layout
        rvPosts = findViewById(R.id.recyclerFeed);
        rvPosts.setLayoutManager(new LinearLayoutManager(this));

        postsAdapter = new PostsAdapter(new ArrayList<>());
        rvPosts.setAdapter(postsAdapter);

        dbHelper = new PostsDbHelper(this);
        sessionManager = new SessionManager(this);
        limitsManager = new DailyLimitsManager(this, sessionManager.getUsername());

        // Setup toolbar info icon click
        topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(FeedActivity.this, AboutActivity.class);
            startActivity(intent);
        });

        // Initialize daily limits TextViews
        tvFeedCount = findViewById(R.id.tvFeedCount);
        tvPostCount = findViewById(R.id.tvPostCount);
        tvLikesCount = findViewById(R.id.tvLikesCount);
        tvCommentsCount = findViewById(R.id.tvCommentsCount);

        // Setup search
        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterPosts(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterPosts(newText);
                return true;
            }
        });

        // Setup bottom navigation
        bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_back) {
                // Back button - show toast since we're already on home screen
                Toast.makeText(this, "Already on home screen", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.menu_feed) {
                // Already on feed, do nothing
                return true;
            } else if (itemId == R.id.menu_profile) {
                Intent intent = new Intent(FeedActivity.this, ProfileActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });

        // Setup FAB
        fabPost = findViewById(R.id.fabPost);
        fabPost.setOnClickListener(v -> {
            if (limitsManager.canPost()) {
                Intent intent = new Intent(FeedActivity.this, CreatePostActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Daily post limit reached. Try again tomorrow!", Toast.LENGTH_SHORT).show();
            }
        });

        // Setup Load Next Post button
        btnLoadNextPost = findViewById(R.id.btnLoadNextPost);
        btnLoadNextPost.setOnClickListener(v -> loadNextPost());

        // Load data the first time
        loadPostsFromDb();
        showCurrentPost();
        updateDailyLimitsUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Set feed as selected when returning
        bottomNav.setSelectedItemId(R.id.menu_feed);
        // Reload in case something changed (e.g., user created a new post)
        currentPostIndex = 0;
        loadPostsFromDb();
        showCurrentPost();
        updateDailyLimitsUI();
    }

    private void loadPostsFromDb() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                PostContract.PostEntry._ID,
                PostContract.PostEntry.COLUMN_USERNAME,
                PostContract.PostEntry.COLUMN_CONTENT,
                PostContract.PostEntry.COLUMN_CREATED_AT
        };

        // ORDER BY created_at DESC (latest first)
        String sortOrder = PostContract.PostEntry.COLUMN_CREATED_AT + " DESC";

        Cursor cursor = db.query(
                PostContract.PostEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );

        List<Post> posts = new ArrayList<>();

        try {
            int idColIndex = cursor.getColumnIndex(PostContract.PostEntry._ID);
            int usernameColIndex = cursor.getColumnIndex(PostContract.PostEntry.COLUMN_USERNAME);
            int contentColIndex = cursor.getColumnIndex(PostContract.PostEntry.COLUMN_CONTENT);
            int createdAtColIndex = cursor.getColumnIndex(PostContract.PostEntry.COLUMN_CREATED_AT);

            while (cursor.moveToNext()) {
                long id = cursor.getLong(idColIndex);
                String username = cursor.getString(usernameColIndex);
                String content = cursor.getString(contentColIndex);
                String createdAt = cursor.getString(createdAtColIndex);

                posts.add(new Post(id, username, content, createdAt));
            }
        } finally {
            cursor.close();
        }

        originalPosts = new ArrayList<>(posts);
        allPosts = new ArrayList<>(posts);
        // Don't pass posts to adapter or increment feed count here
        // This will be done in showCurrentPost() and loadNextPost()
    }

    private void filterPosts(String query) {
        if (query == null || query.trim().isEmpty()) {
            // Reset to all original posts
            allPosts = new ArrayList<>(originalPosts);
            currentPostIndex = 0;
            showCurrentPost();
            return;
        }

        String lowerCaseQuery = query.toLowerCase().trim();
        List<Post> filteredPosts = new ArrayList<>();

        for (Post post : originalPosts) {
            if (post.getContent().toLowerCase().contains(lowerCaseQuery) ||
                post.getUsername().toLowerCase().contains(lowerCaseQuery)) {
                filteredPosts.add(post);
            }
        }

        // Apply filter and reset to first matching post
        allPosts = filteredPosts;
        currentPostIndex = 0;
        showCurrentPost();
    }

    private void showCurrentPost() {
        if (allPosts.isEmpty()) {
            // No posts available
            postsAdapter.setPosts(new ArrayList<>());
            btnLoadNextPost.setText("No posts available");
            btnLoadNextPost.setEnabled(false);
            return;
        }

        if (currentPostIndex >= allPosts.size()) {
            // Already at or beyond the last post
            btnLoadNextPost.setText("No more posts");
            btnLoadNextPost.setEnabled(false);
            return;
        }

        // Show all posts from 0 to currentPostIndex (inclusive)
        List<Post> loadedPosts = new ArrayList<>();
        for (int i = 0; i <= currentPostIndex && i < allPosts.size(); i++) {
            loadedPosts.add(allPosts.get(i));
        }
        postsAdapter.setPosts(loadedPosts);

        // Update button text to show progress
        btnLoadNextPost.setText(String.format("Load Next Post (%d/%d)",
            currentPostIndex + 1, allPosts.size()));
        btnLoadNextPost.setEnabled(true);
    }

    private void loadNextPost() {
        // Check if feed limit reached
        if (!limitsManager.canViewFeed()) {
            Toast.makeText(this, "Daily feed limit reached (10/10)", Toast.LENGTH_SHORT).show();
            btnLoadNextPost.setEnabled(false);
            return;
        }

        // Check if there are more posts to load
        if (currentPostIndex < allPosts.size() - 1) {
            // Move to next post
            currentPostIndex++;

            // Increment feed counter
            limitsManager.incrementFeedCount();

            // Update UI
            updateDailyLimitsUI();
            showCurrentPost();
        } else {
            // No more posts
            Toast.makeText(this, "No more posts available", Toast.LENGTH_SHORT).show();
            btnLoadNextPost.setText("No more posts");
            btnLoadNextPost.setEnabled(false);
        }
    }

    private void updateDailyLimitsUI() {
        tvFeedCount.setText(String.format("Feed %d / %d",
                limitsManager.getFeedCount(), DailyLimitsManager.LIMIT_FEED));
        tvPostCount.setText(String.format("Posts %d / %d",
                limitsManager.getPostCount(), DailyLimitsManager.LIMIT_POSTS));
        tvLikesCount.setText(String.format("Likes %d / %d",
                limitsManager.getLikesCount(), DailyLimitsManager.LIMIT_LIKES));
        tvCommentsCount.setText(String.format("Comments %d / %d",
                limitsManager.getCommentsCount(), DailyLimitsManager.LIMIT_COMMENTS));
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}