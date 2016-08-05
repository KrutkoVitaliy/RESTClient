package appcorp.mmb.activities.search_feeds;

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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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
import appcorp.mmb.activities.Favorites;
import appcorp.mmb.activities.Authorization;
import appcorp.mmb.activities.Profile;
import appcorp.mmb.activities.Search;
import appcorp.mmb.activities.feeds.GlobalFeed;
import appcorp.mmb.activities.feeds.HairstyleFeed;
import appcorp.mmb.activities.feeds.LipsFeed;
import appcorp.mmb.activities.feeds.MakeupFeed;
import appcorp.mmb.activities.feeds.ManicureFeed;
import appcorp.mmb.fragment_adapters.SearchStylistFragmentAdapter;
import appcorp.mmb.dto.StylistDTO;
import appcorp.mmb.classes.Storage;

public class SearchStylistFeed extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ViewPager viewPager;
    private SearchStylistFragmentAdapter adapter;

    private String photoURL, name;
    private String requestCity, requestSkill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_stylist_feed);

        name = Storage.getString("Name", "Make Me Beauty");
        photoURL = Storage.getString("PhotoURL", ""+R.mipmap.icon);

        requestCity = getIntent().getStringExtra("City").replace(" ", "%20");
        requestSkill = getIntent().getStringExtra("Skill");

        initToolbar();
        initNavigationView();
        initViewPager();

        new SearchStylistLoad().execute();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), GlobalFeed.class));
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.searchStylistToolbar);
        toolbar.setTitle("Найденные визажисты");
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
        drawerLayout = (DrawerLayout) findViewById(R.id.searchStylistDrawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_toggle_open, R.string.drawer_toggle_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) drawerLayout.findViewById(R.id.searchStylistNavigation);

        ImageView navAvatar = (ImageView) findViewById(R.id.searchStylistNavAvatar);
        if (photoURL != null && photoURL != "")
            Picasso.with(getApplicationContext()).load(photoURL).resize(300, 300).into(navAvatar);
        else
            navAvatar.setImageResource(R.mipmap.icon);

        navAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name != null)
                    startActivity(new Intent(getApplicationContext(), Profile.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .putExtra("Name", name)
                            .putExtra("PhotoURL", photoURL)
                            .putExtra("From", "NavHeader"));
                else
                    startActivity(new Intent(getApplicationContext(), Authorization.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        TextView navName = (TextView) findViewById(R.id.searchStylistNavName);
        if (name != null && name != "")
            navName.setText(name + "");
        else
            navName.setText("Вы не авторизованы");

        TextView navLocation = (TextView) findViewById(R.id.searchStylistNavLocation);
        if (navLocation.getText().equals("Click to sign in"))
            navLocation.setText("Click to change account");
        else if (navLocation.getText().equals("Click to change account"))
            navLocation.setText("Click to sign in");

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
                    case R.id.navMenuMakeup:
                        startActivity(new Intent(getApplicationContext(), MakeupFeed.class));
                        break;
                    case R.id.navMenuHairstyle:
                        startActivity(new Intent(getApplicationContext(), HairstyleFeed.class));
                        break;
                    case R.id.navMenuManicure:
                        startActivity(new Intent(getApplicationContext(), ManicureFeed.class));
                        break;
                    case R.id.navMenuLips:
                        startActivity(new Intent(getApplicationContext(), LipsFeed.class));
                        break;
                    case R.id.navMenuProfile:
                        if (name != null)
                            startActivity(new Intent(getApplicationContext(), Profile.class)
                                    .putExtra("Name", name)
                                    .putExtra("PhotoURL", photoURL)
                                    .putExtra("From", "NavMenu"));
                        else
                            startActivity(new Intent(getApplicationContext(), Authorization.class)
                                    );
                        break;
                    case R.id.navMenuFavorites:
                        startActivity(new Intent(getApplicationContext(), Favorites.class));
                        break;
                }
                return true;
            }
        });
    }

    private void initViewPager() {
        viewPager = (ViewPager) findViewById(R.id.searchStylistViewPager);
        adapter = new SearchStylistFragmentAdapter(getApplicationContext(), getSupportFragmentManager(), new ArrayList<StylistDTO>());
        viewPager.setAdapter(adapter);
    }

    public class SearchStylistLoad extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlFeedConnection = null;
        BufferedReader reader = null;
        String resultJsonStylistSearch = "", str = "";

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL feedURL = new URL("http://195.88.209.17/search/stylist.php?city="+requestCity+"&skill="+requestSkill);
                urlFeedConnection = (HttpURLConnection) feedURL.openConnection();
                urlFeedConnection.setRequestMethod("GET");
                urlFeedConnection.connect();
                InputStream inputStream = urlFeedConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer buffer = new StringBuffer();
                String line;
                while ((line = reader.readLine()) != null)
                    buffer.append(line);
                resultJsonStylistSearch = buffer.toString();
                str = resultJsonStylistSearch.substring(0, resultJsonStylistSearch.length() - 2);
                str += "]";
            } catch (Exception e) {
                e.printStackTrace();
            }
            return str;
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

                    name = item.getString("first_name") + " " + item.getString("last_name");
                    id = item.getString("id");
                    sid = item.getString("aid");
                    likes = item.getString("likes");
                    followers = item.getString("followers");
                    userType = item.getString("user_type");
                    photo = item.getString("avatar");
                    location = item.getString("city") + ", " + item.getString("address");

                    if (userType.equals("Stylist")) {
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
