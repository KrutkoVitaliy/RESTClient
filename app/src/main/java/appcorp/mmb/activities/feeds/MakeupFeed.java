package appcorp.mmb.activities.feeds;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import java.util.Collections;
import java.util.List;

import appcorp.mmb.R;
import appcorp.mmb.activities.search_feeds.Search;
import appcorp.mmb.activities.search_feeds.SearchStylist;
import appcorp.mmb.activities.user.Favorites;
import appcorp.mmb.activities.user.MyProfile;
import appcorp.mmb.activities.user.SignIn;
import appcorp.mmb.classes.FireAnal;
import appcorp.mmb.classes.Intermediates;
import appcorp.mmb.classes.Storage;
import appcorp.mmb.dto.MakeupDTO;
import appcorp.mmb.fragment_adapters.MakeupFeedFragmentAdapter;

public class MakeupFeed extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private MakeupFeedFragmentAdapter adapter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_makeup_feed);

        Storage.init(getApplicationContext());
        initFirebase();

        FireAnal.sendString("1", "Open", "MakeupFeed");

        initToolbar();
        initNavigationView();
        initViewPager();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(Intermediates.convertToString(getApplicationContext(), R.string.loading));
        progressDialog.show();

        new MakeupFeedLoader(1).execute();
    }

    private void initFirebase() {
        FireAnal.setContext(getApplicationContext());
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.makeupToolbar);
        toolbar.setTitle(R.string.menu_item_makeup);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent intent = new Intent(getApplicationContext(), Search.class);
                intent.putExtra("from", "makeupFeed");
                startActivity(intent);
                return true;
            }
        });
        toolbar.inflateMenu(R.menu.menu);
    }

    private void initNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.makeupDrawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_toggle_open, R.string.drawer_toggle_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) drawerLayout.findViewById(R.id.makeupNavigation);
        initHeaderLayout(navigationView);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawers();
                switch (item.getItemId()) {
                    /*case R.id.navMenuGlobalFeed:
                        startActivity(new Intent(getApplicationContext(), GlobalFeed.class));
                        break;*/
                    case R.id.navMenuSearch:
                        startActivity(new Intent(getApplicationContext(), Search.class)
                                .putExtra("hashTag", "empty"));
                        break;
                    case R.id.navMenuSearchStylist:
                        startActivity(new Intent(getApplicationContext(), SearchStylist.class));
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
                            startActivity(new Intent(getApplicationContext(), Favorites.class));
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

        menuHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Storage.getString("E-mail", "").equals("")) {
                    startActivity(new Intent(getApplicationContext(), MyProfile.class));
                } else {
                    startActivity(new Intent(getApplicationContext(), SignIn.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
                }
            }
        });
    }

    private void initViewPager() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.makeupViewPager);
        adapter = new MakeupFeedFragmentAdapter(getApplicationContext(), getSupportFragmentManager(), new ArrayList<MakeupDTO>());
        viewPager.setAdapter(adapter);
    }

    private class MakeupFeedLoader extends AsyncTask<Void, Void, String> {

        private HttpURLConnection urlFeedConnection = null;
        private BufferedReader reader = null;
        private String resultJsonFeed = "";
        private int position;

        MakeupFeedLoader(int position) {
            this.position = position;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                if (position == 1) {
                    URL feedURL = new URL("http://195.88.209.17/app/static/makeup" + position + ".html");
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
                        URL feedURL = new URL("http://195.88.209.17/app/static/makeup" + i + ".html");
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

                    String[] tempTags;
                    if (Storage.getString("Localization", "").equals("English")) {
                        tempTags = item.getString("tags").split(",");
                        Collections.addAll(hashTags, tempTags);
                    } else if (Storage.getString("Localization", "").equals("Russian")) {
                        tempTags = item.getString("tagsRu").split(",");
                        Collections.addAll(hashTags, tempTags);
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
                }
                if (adapter != null)
                    adapter.setData(data);
                if (progressDialog != null)
                    progressDialog.dismiss();

                FireAnal.sendString("1", "Open", "MakeupFeedLoader");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}