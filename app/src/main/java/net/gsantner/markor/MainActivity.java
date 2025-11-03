/*#######################################################
 *
 * SPDX-FileCopyrightText: 2017-2025 Gregor Santner <gsantner AT mailbox DOT org>
 * SPDX-FileCopyrightText: 2025 Milos Vasic
 * SPDX-License-Identifier: Apache-2.0
 *
 * Main Activity for Markor Android App
 *#########################################################*/

package net.gsantner.markor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.gsantner.markor.ui.FilesFragment;
import net.gsantner.markor.ui.TodoFragment;
import net.gsantner.markor.ui.QuickNoteFragment;
import net.gsantner.markor.ui.MoreFragment;
import net.gsantner.markor.util.AppSettings;

public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fab;
    private AppSettings appSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appSettings = new AppSettings(this);

        // Apply theme
        setTheme(appSettings.isDarkThemeEnabled() ? R.style.AppTheme_Dark : R.style.AppTheme_Light);

        initializeViews();
        setupViewPager();
        setupBottomNavigation();
        setupFab();
    }

    private void initializeViews() {
        viewPager = findViewById(R.id.view_pager);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        fab = findViewById(R.id.fab);
    }

    private void setupViewPager() {
        MainPagerAdapter pagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(3);
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_files:
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_todo:
                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_quicknote:
                    viewPager.setCurrentItem(2);
                    return true;
                case R.id.navigation_more:
                    viewPager.setCurrentItem(3);
                    return true;
            }
            return false;
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigationView.setSelectedItemId(R.id.navigation_files);
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.navigation_todo);
                        break;
                    case 2:
                        bottomNavigationView.setSelectedItemId(R.id.navigation_quicknote);
                        break;
                    case 3:
                        bottomNavigationView.setSelectedItemId(R.id.navigation_more);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    private void setupFab() {
        fab.setOnClickListener(v -> {
            int currentItem = viewPager.getCurrentItem();
            switch (currentItem) {
                case 0: // Files
                    // Open file creation dialog
                    break;
                case 1: // Todo
                    // Add new todo item
                    break;
                case 2: // QuickNote
                    // Quick note functionality
                    break;
                case 3: // More
                    // More options
                    break;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_about:
                // Show about dialog
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private static class MainPagerAdapter extends FragmentPagerAdapter {
        MainPagerAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new FilesFragment();
                case 1:
                    return new TodoFragment();
                case 2:
                    return new QuickNoteFragment();
                case 3:
                    return new MoreFragment();
                default:
                    return new FilesFragment();
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    }
}