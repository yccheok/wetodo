package com.yocto.wetodo;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

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
import com.yocto.wetodo.trash.TrashFragment;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import static com.yocto.wetodo.Utils.Assert;
import static com.yocto.wetodo.Utils.ensureToolbarTextViewBestTextSize;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {
    private AppBarLayout appBarLayout;

    private Toolbar toolbar;
    private TextView toolbarTextView;
    private float originalToolbarTextViewTextSize;

    private int noteToolbarForeground;
    private int toolbarForeground;

    private int colorPrimary;
    private int colorPrimaryDark;
    private int trashToolbarColor;
    private int trashStatusBarColor;
    private boolean windowLightStatusBar = false;

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

        initResource();

        initToolbar();

        this.content = findViewById(R.id.content);
        this.appBarLayout = findViewById(R.id.app_bar_layout);

        initDrawerLayoutAndActionBarDrawerToggle();

        initNavigationView();

        ensureCorrectContentFragmentIsCommitted();
    }

    private void initResource() {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getTheme();
        theme.resolveAttribute(R.attr.noteToolbarForeground, typedValue, true);
        noteToolbarForeground = typedValue.data;
        theme.resolveAttribute(R.attr.toolbarForeground, typedValue, true);
        toolbarForeground = typedValue.data;
        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
        colorPrimary = typedValue.data;
        theme.resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
        colorPrimaryDark = typedValue.data;
        theme.resolveAttribute(R.attr.trashToolbarColor, typedValue, true);
        trashToolbarColor = typedValue.data;
        theme.resolveAttribute(R.attr.trashStatusBarColor, typedValue, true);
        trashStatusBarColor = typedValue.data;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int [] attrs = new int[]{android.R.attr.windowLightStatusBar};

            TypedArray typedArray = getTheme().obtainStyledAttributes(attrs);
            try {
                windowLightStatusBar = typedArray.getBoolean(0, false);
            } finally {
                typedArray.recycle();
            }
        }
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

        for (int i = 0; i < toolbar.getChildCount(); i++){
            View view = toolbar.getChildAt(i);
            if (view instanceof TextView){
                toolbarTextView = (TextView) view;
                this.originalToolbarTextViewTextSize = toolbarTextView.getTextSize();
                toolbarTextView.setSingleLine(false);
                toolbarTextView.setMaxLines(2);
                toolbarTextView.setLineSpacing(Utils.spToPixelInFloat(4), 1.0f);
                break;
            }
        }        
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

    private Fragment getCurrentFragment() {
        return this.getSupportFragmentManager().findFragmentById(R.id.content);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        final int id = item.getItemId();

        final Fragment currentFragment = getCurrentFragment();

        if (id == R.id.nav_todo) {
            this.fragmentType = FragmentType.Todo;

            if (!(currentFragment instanceof TodoFragment)) {
                TodoFragment todoFragment = TodoFragment.newInstance();
                getSupportFragmentManager().beginTransaction().replace(R.id.content, todoFragment).commit();

                refresh(FragmentType.Todo, null);
            }
        } else if (id == R.id.nav_trash) {
            this.fragmentType = FragmentType.Trash;

            if (!(currentFragment instanceof TrashFragment)) {
                TrashFragment trashFragment = TrashFragment.newInstance();
                getSupportFragmentManager().beginTransaction().replace(R.id.content, trashFragment).commit();

                refresh(FragmentType.Trash, null);
            }
        }

        return true;
    }

    private void restoreToolbarTextViewTextSizeIfPossible() {
        if (this.toolbarTextView != null && this.originalToolbarTextViewTextSize > 0) {
            toolbarTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, originalToolbarTextViewTextSize);
        }
    }

    public void setStatusBarBackgroundColor(int color) {
        // This only workable for non action mode. For action mode, we should use
        // getWindow().setStatusBarColor(color);
        drawerLayout.setStatusBarBackgroundColor(color);
    }

    public void refreshMenuItems(FragmentType fragmentType) {

    }

    public void refresh(FragmentType fragmentType, String title) {
        switch (fragmentType) {
            case Todo: {

                // Shadow is projected by TabLayout in NoteFragment.
                setAppBarLayoutElevation(false);

                toolbar.setBackgroundColor(colorPrimary);
                setStatusBarBackgroundColor(colorPrimaryDark);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (windowLightStatusBar) {
                        View view = getWindow().getDecorView();
                        view.setSystemUiVisibility(view.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    } else {
                        View view = getWindow().getDecorView();
                        view.setSystemUiVisibility(view.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    }
                }

                toolbar.setTitleTextColor(noteToolbarForeground);
                toolbar.getOverflowIcon().setColorFilter(noteToolbarForeground, PorterDuff.Mode.SRC_ATOP);
                actionBarDrawerToggle.getDrawerArrowDrawable().setColor(noteToolbarForeground);

                if (title == null) {
                    setTitle(R.string.app_name);
                    restoreToolbarTextViewTextSizeIfPossible();
                } else {
                    setTitle(title);
                    ensureToolbarTextViewBestTextSize(toolbarTextView, originalToolbarTextViewTextSize);
                }

                refreshMenuItems(fragmentType);
            }
            break;

            case Trash: {
                setAppBarLayoutElevation(true);

                toolbar.setBackgroundColor(trashToolbarColor);
                setStatusBarBackgroundColor(trashStatusBarColor);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    View view = getWindow().getDecorView();
                    view.setSystemUiVisibility(view.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }

                toolbar.setTitleTextColor(toolbarForeground);
                toolbar.getOverflowIcon().setColorFilter(toolbarForeground, PorterDuff.Mode.SRC_ATOP);
                actionBarDrawerToggle.getDrawerArrowDrawable().setColor(toolbarForeground);

                if (title == null) {
                    setTitle(R.string.nav_trash);
                    restoreToolbarTextViewTextSizeIfPossible();
                } else {
                    setTitle(title);
                    ensureToolbarTextViewBestTextSize(toolbarTextView, originalToolbarTextViewTextSize);
                }

                refreshMenuItems(fragmentType);
            }
            break;

            default:
                Assert (false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }
}
