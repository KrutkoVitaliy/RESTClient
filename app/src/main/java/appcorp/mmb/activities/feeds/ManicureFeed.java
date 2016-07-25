package appcorp.mmb.activities.feeds;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import appcorp.mmb.R;
import appcorp.mmb.activities.Options;
import appcorp.mmb.activities.Search;
import appcorp.mmb.activities.Support;
import appcorp.mmb.dto.ManicureDTO;
import appcorp.mmb.fragment_adapters.ManicureFeedFragmentAdapter;
import appcorp.mmb.loaders.ManicureFeedLoader;

public class ManicureFeed extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ViewPager viewPager;
    private static ManicureFeedFragmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manicure_feed);

        initToolbar();
        initNavigationView();
        initViewPager();

        new ManicureFeedLoader(adapter, 1).execute();
    }

    public static void addFeed(int position) {
        new ManicureFeedLoader(adapter, position).execute();
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.manicureToolbar);
        //toolbar.setTitle(R.string.menu_item_manicure);
        toolbar.setTitle("dsfsdfsdfsdf");
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent intent = new Intent(getApplicationContext(), Search.class);
                intent.putExtra("hashTag", "empty");
                startActivity(intent);
                return true;
            }
        });
        toolbar.inflateMenu(R.menu.menu);
    }

    private void initNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.manicureDrawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_toggle_open, R.string.drawer_toggle_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) drawerLayout.findViewById(R.id.manicureNavigation);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                drawerLayout.closeDrawers();
                switch (item.getItemId()) {
                    case R.id.navMenuGlobalFeed:
                        break;
                    case R.id.navMenuSearch:
                        startActivity(new Intent(getApplicationContext(), Search.class)
                                .putExtra("hashTag", "empty"));
                        break;
                    case R.id.navMenuMakeup:
                        startActivity(new Intent(getApplicationContext(), GlobalFeed.class));
                        break;
                    case R.id.navMenuHairstyle:
                        startActivity(new Intent(getApplicationContext(), GlobalFeed.class));
                        break;
                    case R.id.navMenuManicure:
                        startActivity(new Intent(getApplicationContext(), ManicureFeed.class));
                        break;
                    case R.id.navMenuLips:
                        startActivity(new Intent(getApplicationContext(), GlobalFeed.class));
                        break;
                    case R.id.navMenuProfile:
                        startActivity(new Intent(getApplicationContext(), GlobalFeed.class));
                        break;
                    case R.id.navMenuFavorites:
                        startActivity(new Intent(getApplicationContext(), GlobalFeed.class));
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

    private void initViewPager() {
        viewPager = (ViewPager) findViewById(R.id.manicureViewPager);
        adapter = new ManicureFeedFragmentAdapter(getApplicationContext(), getSupportFragmentManager(), new ArrayList<ManicureDTO>());
        viewPager.setAdapter(adapter);
    }
}