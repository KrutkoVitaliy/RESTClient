package appcorp.mmb.activities.feeds;

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
import appcorp.mmb.activities.search_feeds.Search;
import appcorp.mmb.activities.search_feeds.SearchStylist;
import appcorp.mmb.activities.user.FavoriteVideos;
import appcorp.mmb.activities.user.Favorites;
import appcorp.mmb.activities.user.MyProfile;
import appcorp.mmb.activities.user.SignIn;
import appcorp.mmb.classes.FireAnal;
import appcorp.mmb.classes.Intermediates;
import appcorp.mmb.classes.Storage;
import appcorp.mmb.dto.GlobalDTO;
import appcorp.mmb.dto.HairstyleDTO;
import appcorp.mmb.fragment_adapters.HairstyleFeedFragmentAdapter;

public class HairstyleFeed extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private HairstyleFeedFragmentAdapter adapter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hairstyle_feed);

        initToolbar();
        initNavigationView();
        initViewPager();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(convertToString(getApplicationContext(), R.string.loading));
        progressDialog.show();

        new HairstyleFeedLoader(1).execute();
        FireAnal.sendString("Hairstyle feed", "Open", "Feeds");
    }

    public static String convertToString(Context context, int r) {
        TextView textView = new TextView(context);
        textView.setText(r);
        return textView.getText().toString();
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.hairstyleToolbar);
        toolbar.setTitle(R.string.menu_item_hairstyle);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent intent = new Intent(getApplicationContext(), Search.class);
                intent.putExtra("from", "hairstyleFeed");
                startActivity(intent);
                return true;
            }
        });
        toolbar.inflateMenu(R.menu.menu);
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
        ViewPager viewPager = (ViewPager) findViewById(R.id.hairstyleViewPager);
        adapter = new HairstyleFeedFragmentAdapter(getApplicationContext(), getSupportFragmentManager(), new ArrayList<HairstyleDTO>());
        viewPager.setAdapter(adapter);
    }

    public class HairstyleFeedLoader extends AsyncTask<Void, Void, List<JSONArray>> {

        private HttpURLConnection urlFeedConnection = null;
        private BufferedReader reader = null;
        private int position;
        private List<JSONArray> dataArrays = new ArrayList<>();

        HairstyleFeedLoader(int position) {
            this.position = position;
        }

        @Override
        protected List<JSONArray> doInBackground(Void... params) {
            try {
                URL feedURL = new URL("http://195.88.209.17/app/static/hairstyle" + position + ".html");
                urlFeedConnection = (HttpURLConnection) feedURL.openConnection();
                urlFeedConnection.setRequestMethod("GET");
                urlFeedConnection.connect();
                InputStream inputStream = urlFeedConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer buffer;
                buffer = new StringBuffer();
                String line;
                while ((line = reader.readLine()) != null)
                    buffer.append(line);
                dataArrays.add(new JSONArray(String.valueOf(buffer)));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return dataArrays;
        }

        @Override
        protected void onPostExecute(List<JSONArray> dataArrays) {
            super.onPostExecute(dataArrays);

            List<JSONObject> items = new ArrayList<>();
            List<HairstyleDTO> exportData = new ArrayList<>();

            try {
                for (int i = 0; i < dataArrays.get(0).length(); i++) {
                    if (dataArrays.get(0).getJSONObject(i) != null)
                        items.add(dataArrays.get(0).getJSONObject(i));
                }
                for (int i = 0; i < items.size(); i++) {
                    List<String> images = new ArrayList<>();

                    if (!items.get(i).has("videoSource")) {
                        for (int j = 0; j < 10; j++)
                            if (!items.get(i).getString("screen" + j).equals("empty.jpg"))
                                images.add(items.get(i).getString("screen" + j));

                        List<String> tags = new ArrayList<>();
                        if (Storage.getString("Localization", "").equals("English")) {
                            Collections.addAll(tags, items.get(i).getString("tags").split(","));
                        } else if (Storage.getString("Localization", "").equals("Russian")) {
                            Collections.addAll(tags, items.get(i).getString("tagsRu").split(","));
                        }

                        HairstyleDTO post = new HairstyleDTO(
                                items.get(i).getLong("id"),
                                "content",
                                items.get(i).getString("uploadDate"),
                                items.get(i).getString("authorName"),
                                items.get(i).getString("authorPhoto"),
                                items.get(i).getString("hairstyleType"),
                                images,
                                tags,
                                items.get(i).getLong("likes"),
                                items.get(i).getString("length"),
                                items.get(i).getString("type"),
                                items.get(i).getString("for"),
                                0,
                                "",
                                "",
                                "",
                                "",
                                0,
                                "");
                        exportData.add(post);
                    }
                }
                if (adapter != null)
                    adapter.setData(exportData);
                if (progressDialog != null)
                    progressDialog.dismiss();

                FireAnal.sendString("1", "Open", "HairstyleFeedLoaded");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}