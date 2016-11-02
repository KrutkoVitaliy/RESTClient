package appcorp.mmb.activities.search_feeds;

import android.app.ProgressDialog;
import android.content.Context;
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
import appcorp.mmb.activities.feeds.GlobalFeed;
import appcorp.mmb.activities.feeds.HairstyleFeed;
import appcorp.mmb.activities.feeds.MakeupFeed;
import appcorp.mmb.activities.feeds.ManicureFeed;
import appcorp.mmb.activities.user.Favorites;
import appcorp.mmb.activities.user.MyProfile;
import appcorp.mmb.activities.user.SignIn;
import appcorp.mmb.classes.FireAnal;
import appcorp.mmb.classes.Intermediates;
import appcorp.mmb.classes.Storage;
import appcorp.mmb.dto.HairstyleDTO;
import appcorp.mmb.fragment_adapters.SearchHairstyleFeedFragmentAdapter;

public class SearchHairstyleFeed extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private SearchHairstyleFeedFragmentAdapter adapter;
    private ProgressDialog progressDialog;
    private static String request, hairstyleLength, hairstyleType, hairstyleFor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hairstyle_feed);

        Storage.init(getApplicationContext());
        initFirebase();

        FireAnal.sendString("1", "Open", "SearchHairstyleFeed");

        request = getIntent().getStringExtra("Request");
        hairstyleLength = getIntent().getStringExtra("HairstyleLength");
        hairstyleType = getIntent().getStringExtra("HairstyleType");
        hairstyleFor = getIntent().getStringExtra("HairstyleFor");

        Storage.addString("SearchTempHairstyleRequest", request);
        Storage.addString("SearchTempHairstyleLength", hairstyleLength);
        Storage.addString("SearchTempHairstyleType", hairstyleType);
        Storage.addString("SearchTempHairstyleFor", hairstyleFor);

        initToolbar();
        initNavigationView();
        initViewPager();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(convertToString(getApplicationContext(), R.string.loading));
        progressDialog.show();

        new SearchHairstyleFeedLoader(request, hairstyleLength, hairstyleType, hairstyleFor, 1).execute();
    }

    public static String convertToString(Context context, int r) {
        TextView textView = new TextView(context);
        textView.setText(r);
        return textView.getText().toString();
    }

    private void initFirebase() {
        FireAnal.setContext(getApplicationContext());
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.hairstyleToolbar);
        toolbar.setTitle(R.string.menu_item_hairstyle);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                startActivity(new Intent(getApplicationContext(), SearchHairstyleMatrix.class)
                        .putExtra("Request", "" + request)
                        .putExtra("HairstyleLength", "" + hairstyleLength)
                        .putExtra("HairstyleType", "" + hairstyleType)
                        .putExtra("HairstyleFor", "" + hairstyleFor));
                return true;
            }
        });
        toolbar.inflateMenu(R.menu.menu_search_matrix);
    }

    private void initNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.hairstyleDrawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_toggle_open, R.string.drawer_toggle_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) drawerLayout.findViewById(R.id.hairstyleNavigation);
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
                        startActivity(new Intent(getApplicationContext(), Search.class)
                                .putExtra("hashTag", "empty"));
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
        if (!Storage.getString("PhotoURL", "").equals("")) {
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
        ViewPager viewPager = (ViewPager) findViewById(R.id.hairstyleViewPager);
        adapter = new SearchHairstyleFeedFragmentAdapter(getApplicationContext(), getSupportFragmentManager(), new ArrayList<HairstyleDTO>());
        viewPager.setAdapter(adapter);
    }

    public class SearchHairstyleFeedLoader extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlFeedConnection = null;
        BufferedReader reader = null;
        String resultJsonFeed = "";
        int position;
        private String request, hairstyleLength, hairstyleType, hairstyleFor;

        SearchHairstyleFeedLoader(String request, String hairstyleLength, String hairstyleType, String hairstyleFor, int position) {
            this.request = request;
            this.hairstyleFor = hairstyleFor;
            this.position = position;

            switch (hairstyleLength) {
                case "0":
                    this.hairstyleLength = "";
                    break;
                case "1":
                    this.hairstyleLength = "short";
                    break;
                case "2":
                    this.hairstyleLength = "medium";
                    break;
                case "3":
                    this.hairstyleLength = "long";
                    break;
            }

            switch (hairstyleType) {
                case "0":
                    this.hairstyleType = "";
                    break;
                case "1":
                    this.hairstyleType = "straight";
                    break;
                case "2":
                    this.hairstyleType = "braid";
                    break;
                case "3":
                    this.hairstyleType = "tail";
                    break;
                case "4":
                    this.hairstyleType = "bunch";
                    break;
                case "5":
                    this.hairstyleType = "netting";
                    break;
                case "6":
                    this.hairstyleType = "curls";
                    break;
                case "7":
                    this.hairstyleType = "custom";
                    break;
            }

            switch (hairstyleFor) {
                case "0":
                    this.hairstyleFor = "";
                    break;
                case "1":
                    this.hairstyleFor = "kids";
                    break;
                case "2":
                    this.hairstyleFor = "everyday";
                    break;
                case "3":
                    this.hairstyleFor = "wedding";
                    break;
                case "4":
                    this.hairstyleFor = "evening";
                    break;
                case "5":
                    this.hairstyleFor = "exclusive";
                    break;
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                if (position == 1) {
                    URL feedURL = new URL("http://195.88.209.17/search/hairstyle.php?request=" + Intermediates.encodeToURL(request) + "&hairstyle_length=" + hairstyleLength + "&hairstyle_type=" + hairstyleType + "&hairstyle_for=" + hairstyleFor + "&position=" + position);
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
                        URL feedURL = new URL("http://195.88.209.17/search/hairstyle.php?request=" + Intermediates.encodeToURL(request) + "&hairstyle_length=" + hairstyleLength + "&hairstyle_type=" + hairstyleType + "&hairstyle_for=" + hairstyleFor + "&position=" + position);
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

            if (resultJsonFeed.equals("[]")) {
                List<HairstyleDTO> data = new ArrayList<>();
                HairstyleDTO hairstyleDTO = new HairstyleDTO(
                        -1,
                        "nothing",
                        "nothing",
                        "nothing",
                        "nothing",
                        new ArrayList<String>(),
                        new ArrayList<String>(),
                        -1,
                        "nothing",
                        "nothing",
                        "nothing");
                data.add(hairstyleDTO);
                adapter.setData(data);
                if (progressDialog != null)
                    progressDialog.hide();
            } else {
                List<HairstyleDTO> data = new ArrayList<>();

                try {
                    JSONArray items = new JSONArray(resultJsonFeed);

                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);
                        List<String> images = new ArrayList<>();
                        List<String> hashTags = new ArrayList<>();

                        for (int j = 0; j < 10; j++)
                            if (!item.getString("screen" + j).equals("empty.jpg"))
                                images.add(item.getString("screen" + j));

                        String[] tempTags = item.getString("tags").split(",");
                        Collections.addAll(hashTags, tempTags);

                        HairstyleDTO hairstyleDTO = new HairstyleDTO(
                                item.getLong("id"),
                                item.getString("uploadDate"),
                                item.getString("authorName"),
                                item.getString("authorPhoto"),
                                item.getString("hairstyleType"),
                                images,
                                hashTags,
                                item.getLong("likes"),
                                item.getString("length"),
                                item.getString("type"),
                                item.getString("for"));
                        data.add(hairstyleDTO);
                        if (adapter != null)
                            adapter.setData(data);
                        if (progressDialog != null)
                            progressDialog.dismiss();

                        FireAnal.sendString("1", "Open", "SearchHairstyleLoaded");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}