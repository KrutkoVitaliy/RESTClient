package appcorp.mmb.activities.search_feeds;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
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
import appcorp.mmb.activities.user.FavoriteVideos;
import appcorp.mmb.activities.user.Favorites;
import appcorp.mmb.activities.user.MyProfile;
import appcorp.mmb.activities.user.SignIn;
import appcorp.mmb.classes.FireAnal;
import appcorp.mmb.classes.Intermediates;
import appcorp.mmb.classes.Storage;
import appcorp.mmb.dto.ManicureDTO;
import appcorp.mmb.fragment_adapters.SearchManicureFeedFragmentAdapter;

public class SearchManicureFeed extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private SearchManicureFeedFragmentAdapter adapter;
    private String request, shape, design, toolbarTitle;
    private ArrayList<String> colors = new ArrayList<>();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manicure_feed);

        FireAnal.sendString("Search manicure feed", "Open", "Activity");

        this.toolbarTitle = getIntent().getStringExtra("Toolbar");
        this.request = getIntent().getStringExtra("Request");
        this.colors = getIntent().getStringArrayListExtra("ManicureColors");
        this.shape = getIntent().getStringExtra("Shape");
        this.design = getIntent().getStringExtra("Design");

        String colorString = "";
        for (int i = 0; i < this.colors.size(); i++) {
            colorString += this.colors.get(i) + ",";
        }

        Storage.addString("SearchTempManicureToolbar", toolbarTitle);
        Storage.addString("SearchTempManicureRequest", request);
        Storage.addString("SearchTempManicureColors", colorString);
        Storage.addString("SearchTempManicureShape", shape);
        Storage.addString("SearchTempManicureDesign", design);

        initToolbar();
        initNavigationView();
        initViewPager();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(Intermediates.convertToString(getApplicationContext(), R.string.loading));
        progressDialog.show();

        new SearchManicureFeedLoader(request, colors, shape, design, 1).execute();
    }

    private void initFirebase() {
        FireAnal.setContext(getApplicationContext());
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.manicureToolbar);
        toolbar.setTitle(toolbarTitle);

        if (this.colors.size() > 0)
            if (!this.colors.get(0).equals("FFFFFF"))
                toolbar.setBackgroundColor(Color.parseColor("#" + this.colors.get(0)));
            else {
                toolbar.setBackgroundColor(Color.parseColor("#DDDDDD"));
            }
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                startActivity(new Intent(getApplicationContext(), SearchManicureMatrix.class)
                        .putStringArrayListExtra("ManicureColors", colors)
                        .putExtra("Request", "" + request)
                        .putExtra("Shape", "" + shape)
                        .putExtra("Design", "" + design)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                return true;
            }
        });
        toolbar.inflateMenu(R.menu.menu_search_matrix);
    }

    private void initNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.manicureDrawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_toggle_open, R.string.drawer_toggle_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) drawerLayout.findViewById(R.id.manicureNavigation);
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
                    case R.id.navMenuFavoriteVideos:
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
        ViewPager viewPager = (ViewPager) findViewById(R.id.manicureViewPager);
        adapter = new SearchManicureFeedFragmentAdapter(getApplicationContext(), getSupportFragmentManager(), new ArrayList<ManicureDTO>());
        viewPager.setAdapter(adapter);
    }

    public class SearchManicureFeedLoader extends AsyncTask<Void, Void, String> {
        HttpURLConnection urlFeedConnection = null;
        BufferedReader reader = null;
        String resultJsonFeed = "";
        int position;
        private String request, shape, design;
        String colorsStr = "";
        List<String> colors = new ArrayList<>();

        SearchManicureFeedLoader(String request, ArrayList<String> colors, String shape, String design, int position) {
            this.request = request;

            switch (shape) {
                case "0":
                    this.shape = "";
                    break;
                case "1":
                    this.shape = "square";
                    break;
                case "2":
                    this.shape = "oval";
                    break;
                case "3":
                    this.shape = "stiletto";
                    break;
            }

            switch (design) {
                case "0":
                    this.design = "";
                    break;
                case "1":
                    this.design = "french_classic";
                    break;
                case "2":
                    this.design = "french_chevron";
                    break;
                case "3":
                    this.design = "french_millennium";
                    break;
                case "4":
                    this.design = "french_fun";
                    break;
                case "5":
                    this.design = "french_crystal";
                    break;
                case "6":
                    this.design = "french_colorful";
                    break;
                case "7":
                    this.design = "french_designer";
                    break;
                case "8":
                    this.design = "french_spa";
                    break;
                case "9":
                    this.design = "french_moon";
                    break;
                case "10":
                    this.design = "art";
                    break;
                case "11":
                    this.design = "designer";
                    break;
                case "12":
                    this.design = "volume";
                    break;
                case "13":
                    this.design = "aqua";
                    break;
                case "14":
                    this.design = "american";
                    break;
                case "15":
                    this.design = "photo";
                    break;
            }

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
                    URL feedURL = new URL("http://195.88.209.17/search/manicure.php?request=" + Intermediates.encodeToURL(request) + "&colors=" + colorsStr + "&shape=" + shape + "&design=" + design + "&position=" + position);
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
                        URL feedURL = new URL("http://195.88.209.17/search/manicure.php?request=" + Intermediates.encodeToURL(request) + "&colors=" + colorsStr + "&shape=" + shape + "&design=" + design + "&position=" + position);
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
                List<ManicureDTO> data = new ArrayList<>();
                ManicureDTO manicureDTO = new ManicureDTO(
                        -1,
                        "nothing",
                        "nothing",
                        "nothing",
                        "nothing",
                        "nothing",
                        new ArrayList<String>(),
                        "nothing",
                        new ArrayList<String>(),
                        -1);
                data.add(manicureDTO);
                adapter.setData(data);
                if (progressDialog != null)
                    progressDialog.hide();
            } else {
                long id, likes;
                List<ManicureDTO> data = new ArrayList<>();
                String availableDate, colors, shape, design, tags = "", authorPhoto, authorName, published;

                try {
                    JSONArray items = new JSONArray(resultJsonFeed);

                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);
                        List<String> images = new ArrayList<>();
                        List<String> hashTags = new ArrayList<>();

                        for (int j = 0; j < 10; j++)
                            if (!item.getString("screen" + j).equals("empty.jpg"))
                                images.add(item.getString("screen" + j));

                        if (!this.request.isEmpty())
                            toolbar.setTitle("#" + this.request + " - " + item.getString("count"));
                        else {
                            if (toolbar.getTitle() == null)
                                toolbar.setTitle(R.string.menu_item_manicure);
                            if (!toolbar.getTitle().toString().contains(" - "))
                                toolbar.setTitle(toolbar.getTitle() + " - " + item.getString("count"));
                        }
                        id = item.getLong("id");
                        authorPhoto = item.getString("authorPhoto");
                        authorName = item.getString("authorName");
                        availableDate = item.getString("uploadDate");

                        if (Storage.getString("Localization", "").equals("English"))
                            tags = item.getString("tags");
                        else if (Storage.getString("Localization", "").equals("Russian"))
                            tags = item.getString("tagsRu");
                        shape = item.getString("shape");
                        design = item.getString("design");
                        colors = item.getString("colors");
                        likes = item.getLong("likes");
                        published = item.getString("published");

                        String[] tempTags = tags.split(",");
                        Collections.addAll(hashTags, tempTags);

                        if (published.equals("t")) {
                            ManicureDTO manicureDTO = new ManicureDTO(id, availableDate, authorName, authorPhoto, shape, design, images, colors, hashTags, likes);
                            data.add(manicureDTO);
                        }
                        if (adapter != null)
                            adapter.setData(data);
                        if (progressDialog != null)
                            progressDialog.dismiss();

                        FireAnal.sendString("1", "Open", "ManicureFeedLoaded");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}