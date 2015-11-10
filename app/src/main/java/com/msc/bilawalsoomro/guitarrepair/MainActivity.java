package com.msc.bilawalsoomro.guitarrepair;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.widget.DrawerLayout;
import android.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.parse.Parse;
import java.io.IOException;
import com.msc.bilawalsoomro.guitarrepair.helpers.DatabaseHelper;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        SelectGuitarFragment.onGuitarSelectedListener,
        ComponentListFragment.onGuitarComponentSelected,
        ArticleListFragment.onGuitarArticleSelected,
        BookmarkFragment.OnFragmentInteractionListener,
        HomeFragment.onStandClicked,
        HomeFragment.getComponents,
        HomeFragment.changeGuitar,
        ViewArticleFragment.OnFragmentInteractionListener,
        CommunityQAFragment.OnQuestionSelected,
        PostQuestionFragment.onSubmitPost {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        DatabaseHelper myDbHelper = new DatabaseHelper(this);
        try {
            myDbHelper.createDatabase();
        }
        catch(IOException ioe) {
            Toast.makeText(this, "database failed", Toast.LENGTH_SHORT).show();
        }

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "XEhKioLWEyKPhbxvg6hp8ajAa9tSSMmDY0mRr2lQ", "yq26iWWpBct3InIU3ImjcHN0KheqCP1EpHUt8AQ7");
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments

        clearBackStack();
        FragmentManager fragmentManager = getFragmentManager();

        if (position == 0) {
            mTitle = "Guitar Repair";
            HomeFragment homeFragment = new HomeFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, homeFragment,"homeFragment")
                    .commit();
        }
        else if (position == 1) {
            mTitle = "Guitar Repair";
            SelectGuitarFragment selectGuitarFragment = new SelectGuitarFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, selectGuitarFragment,"guitarSelectorFragment")
                    .commit();
        }
        else if (position == 2) {
            mTitle = getString(R.string.title_guides);
            ComponentListFragment componentFragment = new ComponentListFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, componentFragment.newInstance(true), "showAllComponents")
                    .commit();
        }
        else if (position == 3) {
            mTitle = getString(R.string.title_bookmarks);
            BookmarkFragment bookmarkFragment = new BookmarkFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, bookmarkFragment)
                    .commit();
        }

        else if (position == 4) {
            if(isUserConnected()) {
                mTitle = "Post Question";
                PostQuestionFragment postQuestionFragment = new PostQuestionFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, postQuestionFragment)
                        .commit();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("You need to be connected to the internet to use this feature.")
                        .setTitle("Sorry!")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }

        }

        else if (position == 5) {
            if(isUserConnected()) {
                mTitle = "Community Q & A";
                CommunityQAFragment communityQAFragment = new CommunityQAFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, communityQAFragment)
                        .commit();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("You need to be connected to the internet to use this feature.")
                        .setTitle("Sorry!")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
        getSupportActionBar().setTitle(mTitle);
    }

    private void clearBackStack() {
        FragmentManager manager = getFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
            manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    public void openQuestion(String id) {
        ViewQuestionFragment viewQuestionFragment = new ViewQuestionFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, viewQuestionFragment.newInstance(id), "viewQuestionFragment")
                .addToBackStack(null)
                .commit();
        getSupportActionBar().setTitle("Question");
    }

    public void onPostSubmitted(String id) {
        ViewQuestionFragment viewQuestionFragment = new ViewQuestionFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, viewQuestionFragment.newInstance(id), "viewQuestionFragment")
                .addToBackStack(null)
                .commit();
        getSupportActionBar().setTitle("Question");
    }

    public boolean isUserConnected() {
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public void onGuitarSelected(int position) {
        int guitarIndex = position + 1;
        SharedPreferences sharedPref = getSharedPreferences("com.msc.bilawalsoomro.guitarrepair", Context.MODE_PRIVATE);
        String guitarSelected = "com.msc.bilawalsoomro.guitarrepair.guitar";
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(guitarSelected, guitarIndex);
        editor.commit();

        String type = "Electric"; //Default value is electric
        switch (position) {
            case 0:
                getSupportActionBar().setTitle("Electric Guitar");
                type = "Electric";
                break;
            case 1:
                getSupportActionBar().setTitle("Bass Guitar");
                type = "Bass";
                break;
            case 2:
                getSupportActionBar().setTitle("Acoustic Guitar");
                type = "Acoustic";
                break;
            case 3:
                getSupportActionBar().setTitle("Classical Guitar");
                type = "Classical";
                break;
        }

        ComponentListFragment componentFragment = new ComponentListFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, componentFragment.newInstance(type), "showComponents")
                .addToBackStack(null)
                .commit();
    }

    public void viewComponents(int id) {
        onGuitarSelected(id);
    }

    public void onFragmentInteraction(String id) {
        onArticleSelected(Integer.parseInt(id), null);
    }

    public void changeSelectedGuitar() {
        SelectGuitarFragment selectGuitarFragment = new SelectGuitarFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, selectGuitarFragment, "guitarSelectorFragment")
                .commit();
        getSupportActionBar().setTitle("Select Guitar");
    }

    public void onArticleOpened() {
        getMenuInflater().inflate(R.menu.menu_view_article, menu);
    }

    public void onStandClick() {
        SelectGuitarFragment selectGuitarFragment = new SelectGuitarFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, selectGuitarFragment,"guitarSelectorFragment")
                .addToBackStack(null)
                .commit();
        getSupportActionBar().setTitle("Select Guitar");
    }

    public void onComponentSelected(int id) {
        ArticleListFragment alf = new ArticleListFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, alf.newInstance(id), "viewArticleList")
                .addToBackStack(null)
                .commit();
    }

    public void onArticleSelected(int id, String componentId) {
        ViewArticleFragment viewArticleFragment = new ViewArticleFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, viewArticleFragment.newInstance(id, componentId), "viewArticle")
                .addToBackStack(null)
                .commit();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main_activity, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0 ){
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
