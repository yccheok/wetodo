package com.yocto.wetodo;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {
    private AppBarLayout appBarLayout;

    private Toolbar toolbar;

    private CoordinatorLayout content;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        this.content = findViewById(R.id.content);
        this.appBarLayout = findViewById(R.id.app_bar_layout);
        this.toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(this.toolbar);

        this.drawerLayout = findViewById(R.id.drawer_layout);
        this.actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                this.drawerLayout,
                this.toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        this.drawerLayout.addDrawerListener(this.actionBarDrawerToggle);
        this.actionBarDrawerToggle.syncState();

        initNavigationView();

        // Shadow is projected by TabLayout in NoteFragment.
        setAppBarLayoutElevation(false);
    }

    private void initNavigationView() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Will not trigger onNavigationItemSelected.
        navigationView.setCheckedItem(R.id.nav_inbox);
    }

    private void setAppBarLayoutElevation(boolean on) {
        if (on) {
            ViewCompat.setElevation(appBarLayout, Utils.dpToPixel(4));
        } else {
            ViewCompat.setElevation(appBarLayout, 0);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}
