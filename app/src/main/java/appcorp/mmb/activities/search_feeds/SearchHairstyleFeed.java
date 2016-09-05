package appcorp.mmb.activities.search_feeds;

import android.app.ProgressDialog;
import android.content.Intent;
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

import java.util.ArrayList;

import appcorp.mmb.R;
import appcorp.mmb.activities.user.Authorization;
import appcorp.mmb.activities.user.Favorites;
import appcorp.mmb.activities.user.MyProfile;
import appcorp.mmb.activities.feeds.HairstyleFeed;
import appcorp.mmb.activities.feeds.MakeupFeed;
import appcorp.mmb.activities.feeds.ManicureFeed;
import appcorp.mmb.classes.Intermediates;
import appcorp.mmb.classes.Storage;
import appcorp.mmb.dto.HairstyleDTO;
import appcorp.mmb.fragment_adapters.SearchHairstyleFeedFragmentAdapter;
import appcorp.mmb.loaders.SearchHairstyleFeedLoader;

public class SearchHairstyleFeed extends AppCompatActivity {

    private static Toolbar toolbar;
    private static DrawerLayout drawerLayout;
    private static ViewPager viewPager;
    private static SearchHairstyleFeedFragmentAdapter adapter;
    private static String request, hairstyleLength, hairstyleType, hairstyleFor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hairstyle_feed);

        initToolbar();
        initNavigationView();
        initViewPager();

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(Intermediates.convertToString(getApplicationContext(), R.string.loading));
        progressDialog.show();

        this.request = getIntent().getStringExtra("Request");
        this.hairstyleLength = getIntent().getStringExtra("HairstyleLength");
        this.hairstyleType = getIntent().getStringExtra("HairstyleType");
        this.hairstyleFor = getIntent().getStringExtra("HairstyleFor");

        new SearchHairstyleFeedLoader(toolbar, adapter, request, hairstyleLength, hairstyleType, hairstyleFor, 1, progressDialog).execute();
    }

    public static void addFeed(int position) {
        new SearchHairstyleFeedLoader(toolbar, adapter, request, hairstyleLength, hairstyleType, hairstyleFor, position).execute();
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.hairstyleToolbar);
        toolbar.setTitle(R.string.menu_item_hairstyle);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                startActivity(new Intent(getApplicationContext(), Search.class)
                        .putExtra("from", "hairstyleFeed"));
                return true;
            }
        });
        toolbar.inflateMenu(R.menu.menu);
    }

    private void initNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.hairstyleDrawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_toggle_open, R.string.drawer_toggle_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) drawerLayout.findViewById(R.id.hairstyleNavigation);
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
                            startActivity(new Intent(getApplicationContext(), Authorization.class));
                        break;
                    case R.id.navMenuFavorites:
                        if (!Storage.getString("E-mail", "").equals(""))
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
            Picasso.with(getApplicationContext()).load("http://195.88.209.17/storage/images/" + Storage.getString("PhotoURL", "")).into(avatar);
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
                    startActivity(new Intent(getApplicationContext(), MyProfile.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
                } else {
                    startActivity(new Intent(getApplicationContext(), Authorization.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
                }
            }
        });
    }

    private void initViewPager() {
        viewPager = (ViewPager) findViewById(R.id.hairstyleViewPager);
        adapter = new SearchHairstyleFeedFragmentAdapter(getApplicationContext(), getSupportFragmentManager(), new ArrayList<HairstyleDTO>());
        viewPager.setAdapter(adapter);
    }

    /*public class SearchHairstyle extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlFeedConnection = null;
        BufferedReader reader = null;
        String resultJsonFeed = "", output = "";
        int position;
        private String request, hairstyleLength, hairstyleType, hairstyleFor;

        public SearchHairstyle(String request, String hairstyleLength, String hairstyleType, String hairstyleFor, int position) {
            this.request = request;
            this.hairstyleFor = hairstyleFor;
            this.position = position;

            if (hairstyleLength.equals("0"))
                this.hairstyleLength = "";
            else if (hairstyleLength.equals("1"))
                this.hairstyleLength = "short";
            else if (hairstyleLength.equals("2"))
                this.hairstyleLength = "medium";
            else if (hairstyleLength.equals("3"))
                this.hairstyleLength = "long";

            if (hairstyleType.equals("0"))
                this.hairstyleType = "";
            else if (hairstyleType.equals("1"))
                this.hairstyleType = "straight";
            else if (hairstyleType.equals("2"))
                this.hairstyleType = "braid";
            else if (hairstyleType.equals("3"))
                this.hairstyleType = "tail";
            else if (hairstyleType.equals("4"))
                this.hairstyleType = "bunch";
            else if (hairstyleType.equals("5"))
                this.hairstyleType = "netting";
            else if (hairstyleType.equals("6"))
                this.hairstyleType = "curls";
            else if (hairstyleType.equals("7"))
                this.hairstyleType = "custom";

            if (hairstyleFor.equals("0"))
                this.hairstyleFor = "";
            else if (hairstyleFor.equals("1"))
                this.hairstyleFor = "kids";
            else if (hairstyleFor.equals("2"))
                this.hairstyleFor = "everyday";
            else if (hairstyleFor.equals("3"))
                this.hairstyleFor = "wedding";
            else if (hairstyleFor.equals("4"))
                this.hairstyleFor = "evening";
            else if (hairstyleFor.equals("5"))
                this.hairstyleFor = "exclusive";
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                if (position == 1) {
                    URL feedURL = new URL("http://195.88.209.17/search/hairstyle.php?request=" + request + "&hairstyle_length=" + hairstyleLength + "&hairstyle_type=" + hairstyleType + "&hairstyle_for=" + hairstyleFor + "&position=" + position);
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
                        URL feedURL = new URL("http://195.88.209.17/search/hairstyle.php?request=" + request + "&hairstyle_length=" + hairstyleLength + "&hairstyle_type=" + hairstyleType + "&hairstyle_for=" + hairstyleFor + "&position=" + position);
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
                    for (int j = 0; j < tempTags.length; j++) {
                        hashTags.add(tempTags[j]);
                    }

                    if (item.getString("published").equals("t")) {
                        if (!this.request.isEmpty())
                            toolbar.setTitle("#" + this.request + " - " + item.getString("count"));
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
    }*/
}