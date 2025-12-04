package com.example.lmnl.auth;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lmnl.R;
import com.example.lmnl.post.FeedActivity;
import com.example.lmnl.user.UserContract;
import com.example.lmnl.user.UserDbHelper;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvGoToRegister;
    private UserDbHelper userDbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);

        userDbHelper = new UserDbHelper(this);
        sessionManager = new SessionManager(this);

        btnLogin.setOnClickListener(v -> loginUser());

        tvGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        // Check credentials
        SQLiteDatabase db = userDbHelper.getReadableDatabase();
        String[] projection = {
                UserContract.UserEntry._ID,
                UserContract.UserEntry.COLUMN_USERNAME,
                UserContract.UserEntry.COLUMN_FULL_NAME,
                UserContract.UserEntry.COLUMN_EMAIL,
                UserContract.UserEntry.COLUMN_PASSWORD
        };

        Cursor cursor = db.query(
                UserContract.UserEntry.TABLE_NAME,
                projection,
                UserContract.UserEntry.COLUMN_EMAIL + " = ?",
                new String[]{email},
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            int passwordIndex = cursor.getColumnIndex(UserContract.UserEntry.COLUMN_PASSWORD);
            int usernameIndex = cursor.getColumnIndex(UserContract.UserEntry.COLUMN_USERNAME);
            int fullNameIndex = cursor.getColumnIndex(UserContract.UserEntry.COLUMN_FULL_NAME);

            String storedPassword = cursor.getString(passwordIndex);
            String username = cursor.getString(usernameIndex);
            String fullName = cursor.getString(fullNameIndex);

            if (password.equals(storedPassword)) {
                // Login successful
                sessionManager.createLoginSession(username, fullName, email);
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(LoginActivity.this, FeedActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Invalid password", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Email not found", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
    }

    @Override
    protected void onDestroy() {
        userDbHelper.close();
        super.onDestroy();
    }
}