package com.wamashudunetshisaulu.myapplication.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.ChipGroup;
import com.wamashudunetshisaulu.myapplication.R;
import com.wamashudunetshisaulu.myapplication.utils.AppPrefs;

/**
 * Screen 5: Settings. The saved preference (days before expiry to warn) is stored in
 * SharedPreferences and used by the Pantry List to highlight items that expire soon.
 */
public class SettingsActivity extends AppCompatActivity {

    private TextView hint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        hint = findViewById(R.id.tvWarnHint);

        ChipGroup group = findViewById(R.id.cgWarnDays);
        int days = AppPrefs.getExpiryWarningDays(this);
        group.check(days == 7 ? R.id.chip7 : days == 14 ? R.id.chip14 : R.id.chip3);
        showHint(days);

        // Saved immediately when the user picks an option.
        group.setOnCheckedStateChangeListener((g, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            int value = id == R.id.chip7 ? 7 : id == R.id.chip14 ? 14 : 3;
            AppPrefs.setExpiryWarningDays(this, value);
            showHint(value);
        });

        BottomNav.setup(this, (BottomNavigationView) findViewById(R.id.bottomNav), R.id.nav_settings);
    }

    private void showHint(int days) {
        hint.setText("Items expiring within " + days + " days are highlighted on your pantry.");
    }
}
