package appcorp.mmb.activities;

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
import appcorp.mmb.fragment_adapters.StylistFragmentAdapter;
import appcorp.mmb.classes.Intermediates;
import appcorp.mmb.dto.StylistDTO;
import appcorp.mmb.loaders.Storage;

public class StylistFeed extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ViewPager viewPager;
    private String photoURL, name;
    private StylistFragmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stylist_feed);

        Storage.Init(getApplicationContext());
        if (Storage.Get("Autentification").equals("Success")) {
            name = Storage.Get("Name");
            photoURL = Storage.Get("PhotoURL");
        }

        initToolbar();
        initNavigationView();
        initViewPager();

        new Load().execute();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.stylist_toolbar);
        toolbar.setTitle(R.string.toolbar_title_stylists);
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
        drawerLayout = (DrawerLayout) findViewById(R.id.stylist_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_toggle_open, R.string.drawer_toggle_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) drawerLayout.findViewById(R.id.stylist_navigation);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                drawerLayout.closeDrawers();
                switch (item.getItemId()) {
                    case R.id.navMenuGlobalFeed:
                        startActivity(new Intent(getApplicationContext(), GlobalFeed.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        break;
                    case R.id.navMenuSearch:
                        startActivity(new Intent(getApplicationContext(), Search.class).putExtra("hashTag", "empty"));
                        break;
                    case R.id.navMenuProfile:
                        if (name != null)
                            startActivity(new Intent(getApplicationContext(), Profile.class)
                                    .putExtra("Name", name)
                                    .putExtra("PhotoURL", photoURL)
                                    .putExtra("From", "StylistFeed"));
                        else
                            startActivity(new Intent(getApplicationContext(), Introduction.class)
                                    );
                        break;
                    case R.id.navMenuFavorites:
                        startActivity(new Intent(getApplicationContext(), Favorites.class));
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
        viewPager = (ViewPager) findViewById(R.id.stylist_viewPager);
        adapter = new StylistFragmentAdapter(getApplicationContext(), getSupportFragmentManager(), new ArrayList<StylistDTO>());
        viewPager.setAdapter(adapter);
    }

    public class Load extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlFeedConnection = null;
        BufferedReader reader = null;
        String resultJsonFeed = "";

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL feedURL = new URL(Intermediates.URL.GET_PROFILES);
                urlFeedConnection = (HttpURLConnection) feedURL.openConnection();
                urlFeedConnection.setRequestMethod("GET");
                urlFeedConnection.connect();
                InputStream inputStream = urlFeedConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer buffer = new StringBuffer();
                String line;
                while ((line = reader.readLine()) != null)
                    buffer.append(line);
                resultJsonFeed = buffer.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJsonFeed;
        }

        @Override
        protected void onPostExecute(String resultJsonFeed) {
            super.onPostExecute(resultJsonFeed);

            List<StylistDTO> data = new ArrayList<>();
            String name, id, sid, likes, followers, userType, photo, location;

            try {
                JSONArray items = new JSONArray(resultJsonFeed);

                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);

                    name = item.getString("first_name") +" "+ item.getString("last_name");
                    id = item.getString("id");
                    sid = item.getString("aid");
                    likes = item.getString("likes");
                    followers = item.getString("followers");
                    userType = item.getString("user_type");
                    photo = item.getString("avatar");
                    location = item.getString("city") +", "+ item.getString("address");

                    if(userType.equals("Stylist")) {
                        StylistDTO stylistDTO = new StylistDTO(id, sid, likes, followers, name, photo, location);
                        data.add(stylistDTO);
                    }
                }
                adapter.setData(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}