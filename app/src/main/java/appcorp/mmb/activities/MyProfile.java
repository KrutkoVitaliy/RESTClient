package appcorp.mmb.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import appcorp.mmb.activities.feeds.GlobalFeed;
import appcorp.mmb.classes.Intermediates;
import appcorp.mmb.R;

public class MyProfile extends Activity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private String name, photoURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        if (Intermediates.getData(getApplicationContext(), "Autentification").equals("Success")) {
            name = Intermediates.getData(getApplicationContext(), "Name");
            photoURL = Intermediates.getData(getApplicationContext(), "PhotoURL");
        }
        initToolbar();
        initNavigationView();
    }

    private void initToolbar() {
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.myProfileToolbar);
        toolbar.setTitle(R.string.toolbar_title_global_feed);
        toolbar.setOnMenuItemClickListener(new android.support.v7.widget.Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent intent = new Intent(getApplicationContext(), Search.class);
                intent.putExtra("hashTag", "empty");
                startActivity(intent);
                return true;
            }
        });

        toolbar.inflateMenu(R.menu.menu_myprofile);
    }

    private void initNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.myProfileDrawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_toggle_open, R.string.drawer_toggle_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) drawerLayout.findViewById(R.id.myProfileNavigation);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                drawerLayout.closeDrawers();
                switch (item.getItemId()) {
                    case R.id.navMenuGlobalFeed:
                        startActivity(new Intent(getApplicationContext(), GlobalFeed.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        break;
                    case R.id.navMenuSearch:
                        startActivity(new Intent(getApplicationContext(), Search.class)
                                .putExtra("hashTag", "empty"));
                        break;
                    case R.id.navMenuProfile: break;
                    case R.id.navMenuFavorites:
                        if (name != null)
                            startActivity(new Intent(getApplicationContext(), Favorites.class));
                        else
                            startActivity(new Intent(getApplicationContext(), Introduction.class));
                        break;
                    case R.id.navMenuSettings:
                        startActivity(new Intent(getApplicationContext(), Options.class));
                        break;
                    case R.id.navMenuSupport:
                        startActivity(new Intent(getApplicationContext(), Support.class));
                        break;
                }
                return true;
            }
        });
    }
}