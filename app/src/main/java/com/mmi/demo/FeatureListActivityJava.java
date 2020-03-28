package com.mmi.demo;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mmi.demo.java.fragments.AutoSuggestFragment;
import com.mmi.demo.java.fragments.DirectionPolylineFragment;
import com.mmi.demo.java.fragments.GeoCodeReverseGeoCodeFragment;
import com.mmi.demo.java.fragments.MapEventFragment;
import com.mmi.demo.java.fragments.MarkersClusteringFragment;
import com.mmi.demo.java.fragments.MarkersInfoWindowFragment;
import com.mmi.demo.java.fragments.MarkersTestFragment;
import com.mmi.demo.java.fragments.NearbyFragment;
import com.mmi.demo.java.fragments.PolygonFragment;
import com.mmi.demo.java.fragments.UserLocationFragment;
import com.mmi.demo.kotlin.fragments.AutoSuggestFragmentKt;
import com.mmi.demo.kotlin.fragments.DirectionPolylineFragmentKt;
import com.mmi.demo.kotlin.fragments.GeoCodeReverseGeoCodeFragmentKt;
import com.mmi.demo.kotlin.fragments.MapEventFragmentKt;
import com.mmi.demo.kotlin.fragments.MarkerInfoWindowFragmentKt;
import com.mmi.demo.kotlin.fragments.MarkersClusteringFragmentKt;
import com.mmi.demo.kotlin.fragments.MarkersTestFragmentKt;
import com.mmi.demo.kotlin.fragments.NearbyFragmentKt;
import com.mmi.demo.kotlin.fragments.PolygonFragmentKt;
import com.mmi.demo.kotlin.fragments.UserLocationFragmentKt;

import java.util.ArrayList;

public class FeatureListActivityJava extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;
    private ArrayList<String> testFragmentNames;
    private int selectedFragmentIndex = 0;
    private Type selectedType = Type.JAVA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_feature);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        testFragmentNames = new ArrayList<>();
        testFragmentNames.add(getString(R.string.map_markers));
        testFragmentNames.add(getString(R.string.polyline));
        testFragmentNames.add(getString(R.string.map_event));

        testFragmentNames.add(getString(R.string.marker_cluster));
        testFragmentNames.add(getString(R.string.polygons));
        testFragmentNames.add(getString(R.string.user_location));
        testFragmentNames.add(getString(R.string.marker_test));
        testFragmentNames.add(getString(R.string.geocode_reverse_geocode));
        testFragmentNames.add(getString(R.string.auto_suggest));
        testFragmentNames.add(getString(R.string.search_nearby));


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSupportActionBar().setElevation(4);
        }


        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, testFragmentNames));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());


        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, // nav drawer open - description for accessibility
                R.string.drawer_close // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);


                getSupportActionBar().setTitle(Html.fromHtml("<font color='#33b5e5'>" + testFragmentNames.get(selectedFragmentIndex) + selectedType.value + "</font>"));
            }


            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);


                getSupportActionBar().setTitle(Html.fromHtml("<font color='#33b5e5'>" + getString(R.string.app_name) + "</font>"));

            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);

            }
        };


        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Set MainTestFragment
        selectItem(0);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#33b5e5'>" + testFragmentNames.get(selectedFragmentIndex) + selectedType.value + "</font>"));
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (item.getItemId() == R.id.kotlin) {
            selectedType = Type.KOTLIN;
            selectItem(selectedFragmentIndex);
          getSupportActionBar().setTitle(Html.fromHtml("<font color='#33b5e5'>" + testFragmentNames.get(selectedFragmentIndex) + selectedType.value + "</font>"));
        } else if (item.getItemId() == R.id.java) {
            selectedType = Type.JAVA;
            selectItem(selectedFragmentIndex);
          getSupportActionBar().setTitle(Html.fromHtml("<font color='#33b5e5'>" + testFragmentNames.get(selectedFragmentIndex) + selectedType.value + "</font>"));
        }
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;


    }

    /**
     * Swaps fragments in the main content view
     */
    private void selectItem(int position) {
        selectedFragmentIndex = position;
        // Create a new fragment and specify the planet to show based on position
        Fragment fragment;

        switch (position) {
            case 0:
                if (selectedType == Type.JAVA) {
                    fragment = new MarkersInfoWindowFragment();
                } else {
                    fragment = new MarkerInfoWindowFragmentKt();
                }
                break;
            case 1:
                if (selectedType == Type.JAVA) {
                    fragment = new DirectionPolylineFragment();
                } else {
                    fragment = new DirectionPolylineFragmentKt();
                }
                break;
            case 2:
                if (selectedType == Type.JAVA) {
                    fragment = new MapEventFragment();
                } else {
                    fragment = new MapEventFragmentKt();
                }
                break;


            case 3:
                if (selectedType == Type.JAVA) {
                    fragment = new MarkersClusteringFragment();
                } else {
                    fragment = new MarkersClusteringFragmentKt();
                }
                break;
            case 4:
                if (selectedType == Type.JAVA) {
                    fragment = new PolygonFragment();
                } else {
                    fragment = new PolygonFragmentKt();
                }
                break;

            case 5:
                if (selectedType == Type.JAVA) {
                    fragment = new UserLocationFragment();
                } else {
                    fragment = new UserLocationFragmentKt();
                }
                break;
            case 6:
                if (selectedType == Type.JAVA) {
                    fragment = new MarkersTestFragment();
                } else {
                    fragment = new MarkersTestFragmentKt();
                }
                break;
            case 7:
                if (selectedType == Type.JAVA) {
                    fragment = new GeoCodeReverseGeoCodeFragment();
                } else {
                    fragment = new GeoCodeReverseGeoCodeFragmentKt();
                }
                break;
            case 8:
                if (selectedType == Type.JAVA) {
                    fragment = new AutoSuggestFragment();
                } else {
                    fragment = new AutoSuggestFragmentKt();
                }
                break;
            case 9:
                if (selectedType == Type.JAVA) {
                    fragment = new NearbyFragment();
                } else {
                    fragment = new NearbyFragmentKt();
                }
                break;
            default:
                if (selectedType == Type.JAVA) {
                    fragment = new MarkersInfoWindowFragment();
                } else {
                    fragment = new MarkerInfoWindowFragmentKt();
                }
                break;

        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        getSupportActionBar().setTitle(title);
    }

    private class DrawerItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    enum Type {

        KOTLIN(" Kt"),
        JAVA(" Java");

      String value;
      Type(String value) {
        this.value = value;
      }

      public String getValue() {
        return value;
      }
    }
}
