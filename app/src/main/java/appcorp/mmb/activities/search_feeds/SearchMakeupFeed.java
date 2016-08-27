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
import appcorp.mmb.activities.Authorization;
import appcorp.mmb.activities.Favorites;
import appcorp.mmb.activities.MyProfile;
import appcorp.mmb.activities.Search;
import appcorp.mmb.activities.SelectCategory;
import appcorp.mmb.activities.feeds.HairstyleFeed;
import appcorp.mmb.activities.feeds.LipsFeed;
import appcorp.mmb.activities.feeds.MakeupFeed;
import appcorp.mmb.activities.feeds.ManicureFeed;
import appcorp.mmb.classes.Storage;
import appcorp.mmb.dto.MakeupDTO;
import appcorp.mmb.fragment_adapters.MakeupFeedFragmentAdapter;

public class SearchMakeupFeed extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ViewPager viewPager;
    private MakeupFeedFragmentAdapter adapter;
    private String request, eyeColor, difficult, occasion;
    private ArrayList<String> colors = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_makeup_feed);

        initToolbar();
        initNavigationView();
        initViewPager();

        this.request = getIntent().getStringExtra("Request");
        this.colors = getIntent().getStringArrayListExtra("Colors");
        this.eyeColor = getIntent().getStringExtra("EyeColor");
        this.difficult = getIntent().getStringExtra("Difficult");
        this.occasion = getIntent().getStringExtra("Occasion");

        new SearchMakeup(request, colors, eyeColor, difficult, occasion, 1).execute();
    }

    public void addFeed(String request, ArrayList<String> colors, String eyeColor, String difficult, String occasion, int position) {
        new SearchMakeup(request, colors, eyeColor, difficult, occasion, position).execute();
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.makeupToolbar);
        toolbar.setTitle(R.string.menu_item_makeup);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                startActivity(new Intent(getApplicationContext(), Search.class)
                        .putExtra("from", "makeupFeed"));
                return true;
            }
        });
        toolbar.inflateMenu(R.menu.menu);
    }

    private void initNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.makeupDrawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_toggle_open, R.string.drawer_toggle_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) drawerLayout.findViewById(R.id.makeupNavigation);
        initHeaderLayout(navigationView);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                drawerLayout.closeDrawers();
                switch (item.getItemId()) {
                    /*case R.id.navMenuGlobalFeed:
                        startActivity(new Intent(getApplicationContext(), SelectCategory.class));
                        break;*/
                    case R.id.navMenuSearch:
                        startActivity(new Intent(getApplicationContext(), Search.class)
                                .putExtra("hashTag", "empty"));
                        break;
                    case R.id.navMenuHairstyle:
                        startActivity(new Intent(getApplicationContext(), HairstyleFeed.class));
                        break;
                    case R.id.navMenuMakeup:
                        startActivity(new Intent(getApplicationContext(), MakeupFeed.class));
                        break;
                    case R.id.navMenuManicure:
                        startActivity(new Intent(getApplicationContext(), ManicureFeed.class));
                        break;
                    case R.id.navMenuLips:
                        startActivity(new Intent(getApplicationContext(), LipsFeed.class));
                        break;
                    case R.id.navMenuProfile:
                        if (!Storage.getString("Name", "Make Me Beauty").equals("Make Me Beauty"))
                            startActivity(new Intent(getApplicationContext(), MyProfile.class));
                        else
                            startActivity(new Intent(getApplicationContext(), Authorization.class));
                        break;
                    case R.id.navMenuFavorites:
                        if (!Storage.getString("Name", "Make Me Beauty").equals("Make Me Beauty"))
                            startActivity(new Intent(getApplicationContext(), Favorites.class));
                        else
                            startActivity(new Intent(getApplicationContext(), Authorization.class));
                        break;
                }
                return true;
            }
        });
    }

    private void initHeaderLayout(NavigationView navigationView) {
        View menuHeader = navigationView.getHeaderView(0);
        ImageView avatar = (ImageView) menuHeader.findViewById(R.id.accountPhoto);
        TextView switcherHint = (TextView) menuHeader.findViewById(R.id.accountHint);
        if (!Storage.getString("PhotoURL", "").equals("")) {
            Picasso.with(getApplicationContext()).load("http://195.88.209.17/storage/images/"+Storage.getString("PhotoURL", "")).into(avatar);
            switcherHint.setText(R.string.header_unauthorized_hint);
        } else {
            avatar.setImageResource(R.mipmap.nav_icon);
            switcherHint.setText(R.string.header_authorized_hint);
        }
        TextView accountName = (TextView) menuHeader.findViewById(R.id.accountName);
        accountName.setText(Storage.getString("Name", "Make Me Beauty"));

        menuHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Storage.getString("E-mail", "").equals("")) {
                    startActivity(new Intent(getApplicationContext(), MyProfile.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                } else {
                    startActivity(new Intent(getApplicationContext(), Authorization.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            }
        });
    }

    private void initViewPager() {
        viewPager = (ViewPager) findViewById(R.id.makeupViewPager);
        adapter = new MakeupFeedFragmentAdapter(getApplicationContext(), getSupportFragmentManager(), new ArrayList<MakeupDTO>());
        viewPager.setAdapter(adapter);
    }

    public class SearchMakeup extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlFeedConnection = null;
        BufferedReader reader = null;
        String resultJsonFeed = "", output = "";
        int position;
        private String request, eyeColor, difficult, occasion;
        private ArrayList<String> colors = new ArrayList<>();
        String colorsStr = "";

        public SearchMakeup(String request, ArrayList<String> colors, String eyeColor, String difficult, String occasion, int position) {
            this.request = request;
            this.eyeColor = eyeColor;
            this.difficult = difficult;
            if (occasion.equals("0"))
                this.occasion = "";
            else if(occasion.equals("1"))
                this.occasion = "everyday";
            else if(occasion.equals("2"))
                this.occasion = "celebrity";
            else if(occasion.equals("3"))
                this.occasion = "dramatic";
            else if(occasion.equals("4"))
                this.occasion = "holiday";
            this.colors = colors;
            for (int i = 0; i < colors.size(); i++) {
                this.colorsStr += colors.get(i) + ",";
            }
            if (colorsStr.length() > 0)
                this.colorsStr = colorsStr.substring(0, colorsStr.length() - 1);
            this.position = position;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                if (position == 1) {
                    URL feedURL = new URL("http://195.88.209.17/search/makeup.php?request=" + request + "&colors=" + colorsStr + "&eye_color=" + eyeColor + "&difficult=" + difficult + "&occasion=" + occasion + "&position=" + position);
                    urlFeedConnection = (HttpURLConnection) feedURL.openConnection();
                    urlFeedConnection.setRequestMethod("GET");
                    urlFeedConnection.connect();
                    InputStream inputStream = urlFeedConnection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuffer buffer = new StringBuffer();
                    String line;
                    while ((line = reader.readLine()) != null)
                        buffer.append(line);
                    resultJsonFeed += buffer.toString();
                } else {
                    for (int i = 1; i <= position; i++) {
                        URL feedURL = new URL("http://195.88.209.17/search/makeup.php?request=" + request + "&colors=" + colorsStr + "&eye_color=" + eyeColor + "&difficult=" + difficult + "&occasion=" + occasion + "&position=" + position);
                        urlFeedConnection = (HttpURLConnection) feedURL.openConnection();
                        urlFeedConnection.setRequestMethod("GET");
                        urlFeedConnection.connect();
                        InputStream inputStream = urlFeedConnection.getInputStream();
                        reader = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuffer buffer = new StringBuffer();
                        String line;
                        while ((line = reader.readLine()) != null)
                            buffer.append(line);
                        resultJsonFeed += buffer.toString();
                        resultJsonFeed = resultJsonFeed.replace("][", ",");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJsonFeed;
        }

        @Override
        protected void onPostExecute(String resultJsonFeed) {
            super.onPostExecute(resultJsonFeed);
            List<MakeupDTO> data = new ArrayList<>();

            try {
                JSONArray items = new JSONArray(resultJsonFeed);

                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);
                    List<String> images = new ArrayList<>();
                    List<String> hashTags = new ArrayList<>();

                    for (int j = 0; j < 10; j++)
                        if (!item.getString("screen" + j).equals("empty.jpg"))
                            images.add(item.getString("screen" + j));

                    String[] tempTags = item.getString("tags").replace(" ", "").split(",");
                    for (int j = 0; j < tempTags.length; j++) {
                        hashTags.add(tempTags[j]);
                    }

                    if (item.getString("published").equals("t") && !images.isEmpty()) {
                        MakeupDTO makeupDTO = new MakeupDTO(
                                item.getLong("id"),
                                item.getString("uploadDate"),
                                item.getString("authorName"),
                                item.getString("authorPhoto"),
                                images,
                                item.getString("colors"),
                                item.getString("eyeColor"),
                                item.getString("occasion"),
                                item.getString("difficult"),
                                hashTags,
                                item.getLong("likes"));
                        data.add(makeupDTO);
                    }
                    adapter.setData(data);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private String upperCaseFirst(String word) {
            if (word == null || word.isEmpty()) return "";
            return word.substring(0, 1).toUpperCase() + word.substring(1);
        }
    }
}