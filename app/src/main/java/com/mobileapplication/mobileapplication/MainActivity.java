package com.mobileapplication.mobileapplication;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarDrawerToggle;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobileapplication.mobileapplication.Portal.CvFragment;
import com.mobileapplication.mobileapplication.Portal.JobSearchFragment;
import com.mobileapplication.mobileapplication.Portal.ApplicationsPreviewFragment;

import java.util.ArrayList;

import core.jobPortal.Job;

public class MainActivity extends AppCompatActivity implements JobSearchFragment.JobSearchFragmentListener, CvFragment.CvFragmentListener{

    private String email;
    private String password;

    private static String TAG = MainActivity.class.getSimpleName();

    ListView mDrawerList;
    RelativeLayout mDrawerPane;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* retrieve credentials from login page */
        //this.password = getIntent().getStringExtra("password");
        //this.email = getIntent().getStringExtra("email");
        this.email = "email";

        //ToastUtil.createToast("Logged in as " + this.email, this);

        initializeSideMenu();
        selectItemFromDrawer(0);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle
        // If it returns true, then it has handled
        // the nav drawer indicator touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    private void setSideMenuProfile(String username, String email, int image){
        ImageView profileImage = (ImageView) findViewById(R.id.avatar);
        TextView usernameLabel = (TextView) findViewById(R.id.userName);
        TextView emailLabel = (TextView) findViewById(R.id.email);

        profileImage.setImageResource(image);
        usernameLabel.setText(username);
        emailLabel.setText(email);

    }

    private void initializeSideMenu(){
        //mNavItems.add(new NavItem("CV", R.drawable.user_icon48));
        //mNavItems.add(new NavItem("Recent Activities", R.drawable.star_fav_icon48));
        //mNavItems.add(new NavItem("Saved Jobs", R.drawable.bookmark_2_icon48));
        //mNavItems.add(new NavItem("Feedback", R.drawable.spechbubble_sq_line_icon48));
        //mNavItems.add(new NavItem("About BT MS Careers", R.drawable.info_icon48));
        //mNavItems.add(new NavItem("Log Out", R.drawable.on_off_icon48));

        mNavItems.add(new NavItem("BTMS Careers", R.drawable.user_icon48));
        mNavItems.add(new NavItem("View Submitted Applications", R.drawable.bookmark_2_icon48));


        //setSideMenuProfile("Paula Salcedo", this.email, R.drawable.photo);

        // DrawerLayout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                Log.d(TAG, "onDrawerClosed: " + getTitle());

                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // Populate the Navigtion Drawer with options
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        mDrawerList = (ListView) findViewById(R.id.navList);
        DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);
        mDrawerList.setAdapter(adapter);

        // Drawer Item click listeners
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItemFromDrawer(position);
            }
        });

        // Setup action bar
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorAccent)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    /*
    * Called when a particular item from the navigation drawer
    * is selected.
    */

    private Fragment selectItemFromDrawer(int position) {

        Fragment fragment = (Fragment) new JobSearchFragment();

        switch(position){
            case 0:
                fragment = JobSearchFragment.newInstance();
                break;

            case 1:
                fragment = ApplicationsPreviewFragment.newInstance();
                break;

            default:
                break;
        }

        setFragment(fragment);


        mDrawerList.setItemChecked(position, true);
        setTitle(mNavItems.get(position).mTitle);
        setTitleColor(getResources().getColor(R.color.colorPrimaryDark));
        // Close the drawer
        mDrawerLayout.closeDrawer(mDrawerPane);

        return fragment;
    }

    private void setFragment(Fragment fragment){
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.mainContent, fragment)
                .commit();
    }

    class NavItem {
        String mTitle;
        int mIcon;

        public NavItem(String title, int icon) {
            mTitle = title;
            mIcon = icon;
        }
    }

    class DrawerListAdapter extends BaseAdapter {

        Context mContext;
        ArrayList<NavItem> mNavItems;

        public DrawerListAdapter(Context context, ArrayList<NavItem> navItems) {
            mContext = context;
            mNavItems = navItems;
        }

        @Override
        public int getCount() {
            return mNavItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mNavItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.main_nav_item, null);
            }
            else {
                view = convertView;
            }

            TextView titleView = (TextView) view.findViewById(R.id.title);
            ImageView iconView = (ImageView) view.findViewById(R.id.icon);

            titleView.setText( mNavItems.get(position).mTitle );
            iconView.setImageResource(mNavItems.get(position).mIcon);

            return view;
        }
    }

    public void onPortalInteraction(Job jobClicked){
        CvFragment submitRequirementsFragment = CvFragment.newInstance(jobClicked);
        setFragment(submitRequirementsFragment);
    }

    public void onCvFormRequestReturn(){
        setFragment(JobSearchFragment.newInstance());
    }

}
