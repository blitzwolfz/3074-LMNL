package com.example.lmnl.post;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DailyLimitsManager {
    private static final String PREF_NAME = "DailyLimits";
    private static final String KEY_LAST_RESET_DATE = "lastResetDate";
    private static final String KEY_FEED_COUNT = "feedCount";
    private static final String KEY_POST_COUNT = "postCount";
    private static final String KEY_LIKES_COUNT = "likesCount";
    private static final String KEY_COMMENTS_COUNT = "commentsCount";

    // Daily limits
    public static final int LIMIT_FEED = 10;
    public static final int LIMIT_POSTS = 5;
    public static final int LIMIT_LIKES = 5;
    public static final int LIMIT_COMMENTS = 5;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public DailyLimitsManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
        checkAndResetIfNewDay();
    }

    private void checkAndResetIfNewDay() {
        String today = getTodayDate();
        String lastReset = prefs.getString(KEY_LAST_RESET_DATE, "");

        if (!today.equals(lastReset)) {
            // New day, reset all counts
            editor.putString(KEY_LAST_RESET_DATE, today);
            editor.putInt(KEY_FEED_COUNT, 0);
            editor.putInt(KEY_POST_COUNT, 0);
            editor.putInt(KEY_LIKES_COUNT, 0);
            editor.putInt(KEY_COMMENTS_COUNT, 0);
            editor.apply();
        }
    }

    private String getTodayDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    public int getFeedCount() {
        return prefs.getInt(KEY_FEED_COUNT, 0);
    }

    public int getPostCount() {
        return prefs.getInt(KEY_POST_COUNT, 0);
    }

    public int getLikesCount() {
        return prefs.getInt(KEY_LIKES_COUNT, 0);
    }

    public int getCommentsCount() {
        return prefs.getInt(KEY_COMMENTS_COUNT, 0);
    }

    public void incrementFeedCount() {
        int current = getFeedCount();
        editor.putInt(KEY_FEED_COUNT, current + 1);
        editor.apply();
    }

    public void incrementPostCount() {
        int current = getPostCount();
        editor.putInt(KEY_POST_COUNT, current + 1);
        editor.apply();
    }

    public void incrementLikesCount() {
        int current = getLikesCount();
        editor.putInt(KEY_LIKES_COUNT, current + 1);
        editor.apply();
    }

    public void incrementCommentsCount() {
        int current = getCommentsCount();
        editor.putInt(KEY_COMMENTS_COUNT, current + 1);
        editor.apply();
    }

    public boolean canViewFeed() {
        return getFeedCount() < LIMIT_FEED;
    }

    public boolean canPost() {
        return getPostCount() < LIMIT_POSTS;
    }

    public boolean canLike() {
        return getLikesCount() < LIMIT_LIKES;
    }

    public boolean canComment() {
        return getCommentsCount() < LIMIT_COMMENTS;
    }
}
