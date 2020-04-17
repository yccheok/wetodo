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

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {
    private AppBarLayout appBarLayout;

    private Toolbar toolbar;

    private CoordinatorLayout content;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private FragmentType fragmentType = FragmentType.Inbox;

    private static final Map<FragmentType, Integer> menuIdLookupForTabNavigation;
    static {
        final Map<FragmentType, Integer> map = new EnumMap<>(FragmentType.class);

        map.put(FragmentType.Inbox, R.id.nav_inbox);

        menuIdLookupForTabNavigation = Collections.unmodifiableMap(map);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initToolbar();

        this.content = findViewById(R.id.content);
        this.appBarLayout = findViewById(R.id.app_bar_layout);

        initDrawerLayoutAndActionBarDrawerToggle();

        initNavigationView();

        // Shadow is projected by TabLayout in NoteFragment.
        setAppBarLayoutElevation(false);
    }

    private void initDrawerLayoutAndActionBarDrawerToggle() {
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
    }

    private void initToolbar() {
        this.toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(this.toolbar);
    }

    private void initNavigationView() {
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Will not trigger onNavigationItemSelected.
        Integer menuId = menuIdLookupForTabNavigation.get(fragmentType);
        if (menuId != null) {
            navigationView.setCheckedItem(menuId);
        }
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
