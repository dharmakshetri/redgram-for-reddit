package com.matie.redgram;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.support.v7.widget.Toolbar;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.matie.redgram.fragments.Fragments;
import com.matie.redgram.fragments.DrawerFragments.FragmentHome;
import com.matie.redgram.managers.ConnectionManager;
import com.matie.redgram.managers.rxbus.RxBus;
import com.matie.redgram.models.DrawerItem;
import com.matie.redgram.views.widgets.Drawer.DrawerView;
import com.matie.redgram.views.widgets.ScrimInsetsFrameLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;


public class MainActivity extends ActionBarActivity implements ScrimInsetsFrameLayout.OnInsetsCallback{

    private int currentSelectedPosition = 0;

    static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    @InjectView(R.id.navigationDrawerListViewWrapper)
    DrawerView mNavigationDrawerListViewWrapper;

    @InjectView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @InjectView(R.id.scrimInsetsFrameLayout)
    ScrimInsetsFrameLayout scrimInsetsFrameLayout;

    @InjectView(R.id.leftDrawerListView)
    ListView leftDrawerListView;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mTitle;

    private CharSequence mDrawerTitle;

    private List<DrawerItem> navigationItems;

    private RxBus mBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        mBus = null;

        mTitle = mDrawerTitle = getTitle();

        mDrawerLayout.setStatusBarBackgroundColor(
                getResources().getColor(R.color.red_dark));


        if (savedInstanceState == null) {
          getSupportFragmentManager().beginTransaction().add(R.id.container,
          Fragment.instantiate(MainActivity.this, Fragments.HOME.getFragment())).commit();
        } else {
          currentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
        }

         setup();
    }

    private void setup(){

        //ActionBar setup
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            scrimInsetsFrameLayout.setOnInsetsCallback(this);
        }

        navigationItems = new ArrayList<DrawerItem>();

        //menu items
        navigationItems.add(new DrawerItem(getString(R.string.fragment_home), R.drawable.ic_home_black_48dp, true));

        //non-menu items
        navigationItems.add(new DrawerItem(getString(R.string.fragment_about), R.drawable.ic_help_black_48dp, false));

        mNavigationDrawerListViewWrapper.replaceWith(navigationItems);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar()
                        .setTitle(navigationItems.get(currentSelectedPosition).getItemName());
                supportInvalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                supportInvalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        selectItem(currentSelectedPosition);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, currentSelectedPosition);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if(mDrawerToggle != null) { //create this check to prevent the call of syncState() when the user is not logged in
            mDrawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        } else if (item.getItemId() == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnItemClick(R.id.leftDrawerListView)
    public void OnItemClick(int position, long id) {
        if (mDrawerLayout.isDrawerOpen(scrimInsetsFrameLayout)) {
            mDrawerLayout.closeDrawer(scrimInsetsFrameLayout);
            onNavigationDrawerItemSelected(position);

            selectItem(position);
        }
    }

    private void selectItem(int position) {

        if (leftDrawerListView != null) {
            leftDrawerListView.setItemChecked(position, true);

            navigationItems.get(currentSelectedPosition).setSelected(false);
            navigationItems.get(position).setSelected(true);

            currentSelectedPosition = position;
//            getSupportActionBar()
//                    .setTitle(navigationItems.get(currentSelectedPosition).getItemName());
        }

        if (scrimInsetsFrameLayout != null) {
            mDrawerLayout.closeDrawer(scrimInsetsFrameLayout);
        }
    }

    private void onNavigationDrawerItemSelected(int position) {
        switch (position) {
            case 0:
                if (!(getSupportFragmentManager().getFragments()
                        .get(0) instanceof FragmentHome)) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, Fragment
                                    .instantiate(MainActivity.this, Fragments.HOME.getFragment()))
                            .commit();
                }
                break;

            //add one for each navigation item

        }

    }

    @Override
    public void onInsetsChanged(Rect insets) {
        Toolbar toolbar = this.toolbar;
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams)
                                                    toolbar.getLayoutParams();
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            lp.topMargin = insets.top;

        int top = insets.top;
        insets.top += toolbar.getHeight();
        toolbar.setLayoutParams(lp);
        insets.top = top; // revert
    }

}

