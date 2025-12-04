package com.example.lmnl.post;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lmnl.R;
import com.example.lmnl.auth.SessionManager;

public class CreatePostActivity extends AppCompatActivity {

    private EditText etPostText;
    private Button btnSubmitPost;
    private PostsDbHelper dbHelper;
    private SessionManager sessionManager;
    private DailyLimitsManager limitsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        // Init views
        etPostText = findViewById(R.id.etPostText);
        btnSubmitPost = findViewById(R.id.btnSubmitPost);

        // Init DB helper, session, and limits manager
        dbHelper = new PostsDbHelper(this);
        sessionManager = new SessionManager(this);
        limitsManager = new DailyLimitsManager(this);

        btnSubmitPost.setOnClickListener(v -> savePost());
    }

    private void savePost() {
        String content = etPostText.getText().toString().trim();

        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "Post cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String username = sessionManager.getUsername();
        if (username == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Check if user can still post today
        if (!limitsManager.canPost()) {
            Toast.makeText(this, "Daily post limit reached. Try again tomorrow!", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PostContract.PostEntry.COLUMN_USERNAME, username);
        values.put(PostContract.PostEntry.COLUMN_CONTENT, content);
        // created_at will be set automatically by DEFAULT CURRENT_TIMESTAMP

        long newRowId = db.insert(PostContract.PostEntry.TABLE_NAME, null, values);

        if (newRowId == -1) {
            Toast.makeText(this, "Error saving post", Toast.LENGTH_SHORT).show();
        } else {
            // Increment post count
            limitsManager.incrementPostCount();
            Toast.makeText(this, "Post saved", Toast.LENGTH_SHORT).show();
            finish(); // Go back to feed
        }
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
