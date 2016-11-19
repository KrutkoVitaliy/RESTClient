package appcorp.mmb.activities.user;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
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
import java.util.Collections;
import java.util.List;

import appcorp.mmb.R;
import appcorp.mmb.activities.feeds.GlobalFeed;
import appcorp.mmb.activities.feeds.HairstyleFeed;
import appcorp.mmb.activities.feeds.MakeupFeed;
import appcorp.mmb.activities.feeds.ManicureFeed;
import appcorp.mmb.activities.search_feeds.Search;
import appcorp.mmb.activities.search_feeds.SearchStylist;
import appcorp.mmb.classes.FireAnal;
import appcorp.mmb.classes.Storage;
import appcorp.mmb.dto.VideoHairstyleDTO;
import appcorp.mmb.dto.VideoMakeupDTO;
import appcorp.mmb.dto.VideoManicureDTO;
import appcorp.mmb.fragment_adapters.FavoriteVideosFragmentAdapter;

public class FavoriteVideos extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private FavoriteVideosFragmentAdapter adapter;
    private List<VideoManicureDTO> manicureVideoData = new ArrayList<>();
    private List<VideoMakeupDTO> makeupVideoData = new ArrayList<>();
    private List<VideoHairstyleDTO> hairstyleVideoData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_videos);

        FireAnal.sendString("Favorite videos", "Open", "Activity");

        initToolbar();
        initNavigationView();
        initViewPager();
        if (!Storage.getString("E-mail", "").equals("")) {
            addManicureFeed(1);
            addMakeupFeed(1);
            addHairstyleFeed(1);
        }
    }

    private void initFirebase() {
        FireAnal.setContext(getApplicationContext());
    }

    public void addManicureFeed(int position) {
        new FavoriteManicureVideosLoader(position).execute();
    }

    public void addMakeupFeed(int position) {
        new FavoriteMakeupVideosLoader(position).execute();
    }

    public void addHairstyleFeed(int position) {
        new FavoriteHairstyleVideosLoader(position).execute();
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.favoriteVideosToolbar);
        toolbar.setTitle(R.string.toolbar_title_favorite_videos);
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

    private void initViewPager() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.favoriteVideosViewPager);
        adapter = new FavoriteVideosFragmentAdapter(getApplicationContext(), getSupportFragmentManager(), new ArrayList<VideoManicureDTO>(), new ArrayList<VideoMakeupDTO>(), new ArrayList<VideoHairstyleDTO>());
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.favoriteVideosTabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.favoriteVideosDrawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_toggle_open, R.string.drawer_toggle_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.favoriteVideosNavigation);
        initHeaderLayout(navigationView);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawers();
                switch (item.getItemId()) {
                    case R.id.navMenuGlobalFeed:
                        startActivity(new Intent(getApplicationContext(), GlobalFeed.class));
                        break;
                    case R.id.navMenuSearch:
                        startActivity(new Intent(getApplicationContext(), Search.class).putExtra("hashTag", "empty"));
                        break;
                    case R.id.navMenuSearchStylist:
                        startActivity(new Intent(getApplicationContext(), SearchStylist.class));
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
                    case R.id.navMenuProfile:
                        if (!Storage.getString("E-mail", "").equals(""))
                            startActivity(new Intent(getApplicationContext(), MyProfile.class));
                        else
                            startActivity(new Intent(getApplicationContext(), SignIn.class));
                        break;
                    case R.id.navMenuFavorites:
                        if (!Storage.getString("E-mail", "").equals(""))
                            startActivity(new Intent(getApplicationContext(), FavoriteVideos.class));
                        else
                            startActivity(new Intent(getApplicationContext(), SignIn.class));
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
        if (!Storage.getString("E-mail", "").equals("")) {
            Picasso.with(getApplicationContext()).load("http://195.88.209.17/storage/photos/" + Storage.getString("PhotoURL", "")).resize(100, 100).centerCrop().into(avatar);
            switcherHint.setText(R.string.header_unauthorized_hint);
        } else {
            avatar.setImageResource(R.mipmap.nav_icon);
            switcherHint.setText(R.string.header_authorized_hint);
        }
        TextView accountName = (TextView) menuHeader.findViewById(R.id.accountName);
        accountName.setText(Storage.getString("Name", "Make Me Beauty"));

        /*menuHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Storage.getString("E-mail", "").equals("")) {
                    startActivity(new Intent(getApplicationContext(), MyProfile.class));
                } else {
                    startActivity(new Intent(getApplicationContext(), SignIn.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
                }
            }
        });*/
    }

    public class FavoriteManicureVideosLoader extends AsyncTask<Void, Void, String> {

        private HttpURLConnection urlFeedConnection = null;
        private BufferedReader reader = null;
        private String resultJsonFeed = "";
        private int position;

        HttpURLConnection connectionGet = null;
        String resultGet = "";
        String ids;

        FavoriteManicureVideosLoader(int position) {
            this.position = position;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL feedURL = new URL("http://195.88.209.17/app/in/favoriteVideosManicure.php?email=" + Storage.getString("E-mail", ""));
                connectionGet = (HttpURLConnection) feedURL.openConnection();
                connectionGet.setRequestMethod("GET");
                connectionGet.connect();
                InputStream inputStream = connectionGet.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder profileBuffer = new StringBuilder();
                String profileLine;
                while ((profileLine = reader.readLine()) != null) {
                    profileBuffer.append(profileLine);
                }
                resultGet = profileBuffer.toString();
                ids = resultGet;
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (position == 1) {
                    URL feedURL = new URL("http://195.88.209.17/favorites/manicureVideos.php?position=" + position + "&ids=" + ids);
                    urlFeedConnection = (HttpURLConnection) feedURL.openConnection();
                    urlFeedConnection.setRequestMethod("GET");
                    urlFeedConnection.connect();
                    InputStream inputStream = urlFeedConnection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder buffer = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null)
                        buffer.append(line);
                    resultJsonFeed += buffer.toString();
                } else {
                    for (int i = 1; i <= position; i++) {
                        URL feedURL = new URL("http://195.88.209.17/favorites/manicureVideos.php?position=" + position + "&ids=" + ids);
                        urlFeedConnection = (HttpURLConnection) feedURL.openConnection();
                        urlFeedConnection.setRequestMethod("GET");
                        urlFeedConnection.connect();
                        InputStream inputStream = urlFeedConnection.getInputStream();
                        reader = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder buffer = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null)
                            buffer.append(line);
                        resultJsonFeed += buffer.toString();
                        resultJsonFeed = resultJsonFeed.replace("][", "");
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

            try {
                JSONArray items = new JSONArray(resultJsonFeed);

                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);

                    List<String> tags = new ArrayList<>();
                    if (Storage.getString("Localization", "").equals("English"))
                        Collections.addAll(tags, item.getString("videoTags").split(","));
                    else if (Storage.getString("Localization", "").equals("Russian"))
                        Collections.addAll(tags, item.getString("videoTagsRu").split(","));

                    VideoManicureDTO videoManicureDTO = new VideoManicureDTO(
                            item.getLong("videoId"),
                            item.getString("videoTitle"),
                            item.getString("videoPreview"),
                            item.getString("videoSource"),
                            tags,
                            item.getLong("videoLikes"),
                            item.getString("videoUploadDate"));
                    manicureVideoData.add(videoManicureDTO);
                    if (adapter != null)
                        adapter.setManicureData(manicureVideoData);

                    FireAnal.sendString("1", "Open", "FavoriteManicureVideosLoaded");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class FavoriteMakeupVideosLoader extends AsyncTask<Void, Void, String> {

        private HttpURLConnection urlFeedConnection = null;
        private BufferedReader reader = null;
        private String resultJsonFeed = "";
        private int position;

        HttpURLConnection connectionGet = null;
        String resultGet = "";
        String ids;

        FavoriteMakeupVideosLoader(int position) {
            this.position = position;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL feedURL = new URL("http://195.88.209.17/app/in/favoriteVideosMakeup.php?email=" + Storage.getString("E-mail", ""));
                connectionGet = (HttpURLConnection) feedURL.openConnection();
                connectionGet.setRequestMethod("GET");
                connectionGet.connect();
                InputStream inputStream = connectionGet.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder profileBuffer = new StringBuilder();
                String profileLine;
                while ((profileLine = reader.readLine()) != null) {
                    profileBuffer.append(profileLine);
                }
                resultGet = profileBuffer.toString();
                ids = resultGet;
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (position == 1) {
                    URL feedURL = new URL("http://195.88.209.17/favorites/makeupVideos.php?position=" + position + "&ids=" + ids);
                    urlFeedConnection = (HttpURLConnection) feedURL.openConnection();
                    urlFeedConnection.setRequestMethod("GET");
                    urlFeedConnection.connect();
                    InputStream inputStream = urlFeedConnection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder buffer = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null)
                        buffer.append(line);
                    resultJsonFeed += buffer.toString();
                } else {
                    for (int i = 1; i <= position; i++) {
                        URL feedURL = new URL("http://195.88.209.17/favorites/makeupVideos.php?position=" + position + "&ids=" + ids);
                        urlFeedConnection = (HttpURLConnection) feedURL.openConnection();
                        urlFeedConnection.setRequestMethod("GET");
                        urlFeedConnection.connect();
                        InputStream inputStream = urlFeedConnection.getInputStream();
                        reader = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder buffer = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null)
                            buffer.append(line);
                        resultJsonFeed += buffer.toString();
                        resultJsonFeed = resultJsonFeed.replace("][", "");
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

            try {
                JSONArray items = new JSONArray(resultJsonFeed);

                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);

                    List<String> tags = new ArrayList<>();
                    if (Storage.getString("Localization", "").equals("English"))
                        Collections.addAll(tags, item.getString("videoTags").split(","));
                    else if (Storage.getString("Localization", "").equals("Russian"))
                        Collections.addAll(tags, item.getString("videoTagsRu").split(","));

                    VideoMakeupDTO videoMakeupDTO = new VideoMakeupDTO(
                            item.getLong("videoId"),
                            item.getString("videoTitle"),
                            item.getString("videoPreview"),
                            item.getString("videoSource"),
                            tags,
                            item.getLong("videoLikes"),
                            item.getString("videoUploadDate"));
                    makeupVideoData.add(videoMakeupDTO);

                    if (adapter != null)
                        adapter.setMakeupData(makeupVideoData);

                    FireAnal.sendString("1", "Open", "FavoriteManicureVideosLoaded");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class FavoriteHairstyleVideosLoader extends AsyncTask<Void, Void, String> {

        private HttpURLConnection urlFeedConnection = null;
        private BufferedReader reader = null;
        private String resultJsonFeed = "";
        private int position;
        String ids;

        HttpURLConnection connectionGet = null;
        String resultGet = "";

        FavoriteHairstyleVideosLoader(int position) {
            this.position = position;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL feedURL = new URL("http://195.88.209.17/app/in/favoriteVideosHairstyle.php?email=" + Storage.getString("E-mail", ""));
                connectionGet = (HttpURLConnection) feedURL.openConnection();
                connectionGet.setRequestMethod("GET");
                connectionGet.connect();
                InputStream inputStream = connectionGet.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder profileBuffer = new StringBuilder();
                String profileLine;
                while ((profileLine = reader.readLine()) != null) {
                    profileBuffer.append(profileLine);
                }
                resultGet = profileBuffer.toString();
                ids = resultGet;
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (position == 1) {
                    URL feedURL = new URL("http://195.88.209.17/favorites/hairstyleVideos.php?position=" + position + "&ids=" + ids);
                    urlFeedConnection = (HttpURLConnection) feedURL.openConnection();
                    urlFeedConnection.setRequestMethod("GET");
                    urlFeedConnection.connect();
                    InputStream inputStream = urlFeedConnection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder buffer = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null)
                        buffer.append(line);
                    resultJsonFeed += buffer.toString();
                } else {
                    for (int i = 1; i <= position; i++) {
                        URL feedURL = new URL("http://195.88.209.17/favorites/hairstyleVideos.php?position=" + position + "&ids=" + ids);
                        urlFeedConnection = (HttpURLConnection) feedURL.openConnection();
                        urlFeedConnection.setRequestMethod("GET");
                        urlFeedConnection.connect();
                        InputStream inputStream = urlFeedConnection.getInputStream();
                        reader = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder buffer = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null)
                            buffer.append(line);
                        resultJsonFeed += buffer.toString();
                        resultJsonFeed = resultJsonFeed.replace("][", "");
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

            List<VideoHairstyleDTO> hairstyleVideoData = new ArrayList<>();

            try {
                JSONArray items = new JSONArray(resultJsonFeed);

                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);

                    List<String> tags = new ArrayList<>();
                    if (Storage.getString("Localization", "").equals("English"))
                        Collections.addAll(tags, item.getString("videoTags").split(","));
                    else if (Storage.getString("Localization", "").equals("Russian"))
                        Collections.addAll(tags, item.getString("videoTagsRu").split(","));

                    VideoHairstyleDTO videoHairstyleDTO = new VideoHairstyleDTO(
                            item.getLong("videoId"),
                            item.getString("videoTitle"),
                            item.getString("videoPreview"),
                            item.getString("videoSource"),
                            tags,
                            item.getLong("videoLikes"),
                            item.getString("videoUploadDate"));
                    hairstyleVideoData.add(videoHairstyleDTO);

                    if (adapter != null)
                        adapter.setHairstyleData(hairstyleVideoData);

                    FireAnal.sendString("1", "Open", "FavoriteManicureVideosLoaded");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}