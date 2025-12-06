package com.example.lmnl;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;

public class AboutActivity extends AppCompatActivity {

    private TextView tvMission, tvLimits, tvConversation;
    private MaterialToolbar topAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_about);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        tvMission = findViewById(R.id.tvMission);
        tvLimits = findViewById(R.id.tvLimits);
        tvConversation = findViewById(R.id.tvConversation);
        topAppBar = findViewById(R.id.topAppBar);

        // Setup toolbar back button
        topAppBar.setNavigationOnClickListener(v -> onBackPressed());

        // Set content
        setMissionContent();
    }

    private void setMissionContent() {
        String missionText = "At LMNL, we believe social media should enhance our lives, not dominate them. " +
                "We're building a platform that prioritizes meaningful connections over mindless scrolling. " +
                "Our vision is simple: create a space where every interaction counts, where quality trumps quantity, " +
                "and where users leave feeling enriched rather than exhausted.\n\n" +
                "In a world of infinite feeds and endless notifications, we're championing intentionality. " +
                "LMNL is designed to give you back control of your time and attention, helping you engage " +
                "with social media on your terms.";

        String limitsText = "Our daily limits aren't restrictions—they're liberations. By capping the number " +
                "of posts you can view, create, and interact with, we encourage you to make each one count. " +
                "This transforms social media from a time-sink into a curated experience.\n\n" +
                "When you know you only have 10 posts to view today, you become more selective. You engage " +
                "more deeply. You think before you post. You comment with intention. These limits create a " +
                "mindful environment where users are present, not just passing through.\n\n" +
                "Research shows that unlimited access to social media correlates with anxiety, comparison, " +
                "and decreased well-being. LMNL's finite feed is our answer to this crisis—a gentle boundary " +
                "that protects your mental health while keeping you connected.";

        String conversationText = "Great conversations don't happen in a flood of content. They happen when " +
                "people have time to think, reflect, and respond thoughtfully. LMNL's design encourages " +
                "this kind of dialogue.\n\n" +
                "With limited daily interactions, users invest more care in their words. Comments become " +
                "conversations. Likes become meaningful endorsements. Posts are crafted with purpose rather " +
                "than posted impulsively.\n\n" +
                "We're not trying to maximize engagement metrics or keep you scrolling for hours. We're " +
                "building a community where genuine connection matters more than viral moments. Where you " +
                "can have real discussions without drowning in noise. Where social media feels social again.\n\n" +
                "This is LMNL: a quieter, more intentional social network. A place where less truly is more.";

        tvMission.setText(missionText);
        tvLimits.setText(limitsText);
        tvConversation.setText(conversationText);
    }
}
