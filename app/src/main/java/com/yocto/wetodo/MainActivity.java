package com.yocto.wetodo;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;
import com.yocto.wetodo.todo.TodoFragment;

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

    private FragmentType fragmentType = FragmentType.Todo;

    private static final Map<FragmentType, Integer> menuIdLookupForTabNavigation;
    static {
        final Map<FragmentType, Integer> map = new EnumMap<>(FragmentType.class);

        map.put(FragmentType.Todo, R.id.nav_todo);

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

        ensureCorrectContentFragmentIsCommitted();
    }

    private void ensureCorrectContentFragmentIsCommitted() {
        final FragmentManager fragmentManager = getSupportFragmentManager();

        final Fragment fragment = fragmentManager.findFragmentById(R.id.content);

        if (fragmentType == FragmentType.Todo) {
            if (!(fragment instanceof TodoFragment)) {
                TodoFragment projectFragment = TodoFragment.newInstance();
                fragmentManager.beginTransaction().replace(R.id.content, projectFragment).commit();
            }
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }
}
