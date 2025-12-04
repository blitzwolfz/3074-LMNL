package com.example.lmnl.auth;

import android.content.ContentValues;
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
import com.example.lmnl.user.UserContract;
import com.example.lmnl.user.UserDbHelper;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etName, etRegisterEmail, etRegisterPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvGoToLogin;
    private UserDbHelper userDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        etUsername = findViewById(R.id.etUsername);
        etName = findViewById(R.id.etName);
        etRegisterEmail = findViewById(R.id.etRegisterEmail);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);

        userDbHelper = new UserDbHelper(this);

        btnRegister.setOnClickListener(v -> registerUser());

        tvGoToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void registerUser() {
        String username = etUsername.getText().toString().trim();
        String fullName = etName.getText().toString().trim();
        String email = etRegisterEmail.getText().toString().trim();
        String password = etRegisterPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(username)) {
            etUsername.setError("Username is required");
            etUsername.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(fullName)) {
            etName.setError("Full name is required");
            etName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etRegisterEmail.setError("Email is required");
            etRegisterEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etRegisterPassword.setError("Password is required");
            etRegisterPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etRegisterPassword.setError("Password must be at least 6 characters");
            etRegisterPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;
        }

        // Check if username already exists
        SQLiteDatabase db = userDbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                UserContract.UserEntry.TABLE_NAME,
                null,
                UserContract.UserEntry.COLUMN_USERNAME + " = ?",
                new String[]{username},
                null,
                null,
                null
        );

        if (cursor.getCount() > 0) {
            cursor.close();
            Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
            return;
        }
        cursor.close();

        // Check if email already exists
        cursor = db.query(
                UserContract.UserEntry.TABLE_NAME,
                null,
                UserContract.UserEntry.COLUMN_EMAIL + " = ?",
                new String[]{email},
                null,
                null,
                null
        );

        if (cursor.getCount() > 0) {
            cursor.close();
            Toast.makeText(this, "Email already exists", Toast.LENGTH_SHORT).show();
            return;
        }
        cursor.close();

        // Insert new user
        SQLiteDatabase dbWrite = userDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserContract.UserEntry.COLUMN_USERNAME, username);
        values.put(UserContract.UserEntry.COLUMN_FULL_NAME, fullName);
        values.put(UserContract.UserEntry.COLUMN_EMAIL, email);
        values.put(UserContract.UserEntry.COLUMN_PASSWORD, password);

        long newRowId = dbWrite.insert(UserContract.UserEntry.TABLE_NAME, null, values);

        if (newRowId == -1) {
            Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Registration successful! Please login.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        userDbHelper.close();
        super.onDestroy();
    }
}