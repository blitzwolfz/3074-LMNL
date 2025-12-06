package com.example.lmnl.user;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "users.db";
    private static final int DATABASE_VERSION = 3;

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + UserContract.UserEntry.TABLE_NAME + " (" +
                    UserContract.UserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    UserContract.UserEntry.COLUMN_USERNAME + " TEXT NOT NULL UNIQUE, " +
                    UserContract.UserEntry.COLUMN_FULL_NAME + " TEXT NOT NULL, " +
                    UserContract.UserEntry.COLUMN_EMAIL + " TEXT NOT NULL UNIQUE, " +
                    UserContract.UserEntry.COLUMN_PASSWORD + " TEXT NOT NULL, " +
                    UserContract.UserEntry.COLUMN_BIO + " TEXT, " +
                    UserContract.UserEntry.COLUMN_WEBSITE + " TEXT" +
                    ");";

    private static final String SQL_DROP_TABLE =
            "DROP TABLE IF EXISTS " + UserContract.UserEntry.TABLE_NAME + ";";

    public UserDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // IMPORTANT: Never drop user data in production
        // Use version-based migration to preserve existing accounts

        if (oldVersion < 2) {
            // Migration from version 1 to 2 - no schema changes needed
            // Just version bump
        }

        if (oldVersion < 3) {
            // Migration from version 2 to 3 - no schema changes needed
            // Just version bump for fixing account persistence issue
        }

        // Future migrations should use ALTER TABLE to add columns
        // Example: db.execSQL("ALTER TABLE users ADD COLUMN created_at TEXT DEFAULT CURRENT_TIMESTAMP");
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Prevent data loss on downgrade - keep existing data
        // Don't drop tables on version rollback
    }
}
