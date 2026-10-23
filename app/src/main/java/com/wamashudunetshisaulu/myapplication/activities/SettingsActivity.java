package com.wamashudunetshisaulu.myapplication.activities;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.wamashudunetshisaulu.myapplication.R;
import com.wamashudunetshisaulu.myapplication.utils.AppPrefs;

/**
 * Screen 5: Settings. The saved preference (days before expiry to warn) is stored in
 * SharedPreferences and used by the Pantry List to highlight items that expire soon.
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        RadioGroup group = findViewById(R.id.rgWarnDays);
        int days = AppPrefs.getExpiryWarningDays(this);
        int checkedId = days == 7 ? R.id.rb7 : days == 14 ? R.id.rb14 : R.id.rb3;
        ((RadioButton) findViewById(checkedId)).setChecked(true);

        // Saved immediately when the user picks an option.
        group.setOnCheckedChangeListener((g, id) -> {
            int value = id == R.id.rb7 ? 7 : id == R.id.rb14 ? 14 : 3;
            AppPrefs.setExpiryWarningDays(this, value);
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }
}
