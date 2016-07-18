package appcorp.mmb;

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

import com.google.android.gms.tagmanager.DataLayer;
import com.google.android.gms.tagmanager.TagManager;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import appcorp.mmb.activities.Favorites;
import appcorp.mmb.activities.InternetNotification;
import appcorp.mmb.activities.Introduction;
import appcorp.mmb.activities.MyProfile;
import appcorp.mmb.activities.Options;
import appcorp.mmb.activities.Profile;
import appcorp.mmb.activities.Search;
import appcorp.mmb.activities.Support;
import appcorp.mmb.adapter.GlobalFeedFragmentAdapter;
import appcorp.mmb.dto.TapeDTO;
import appcorp.mmb.loaders.ProfileDataLoader;
import appcorp.mmb.loaders.Storage;

public class GlobalFeed extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ViewPager viewPager;
    private GlobalFeedFragmentAdapter adapter;
    private FirebaseAnalytics firebaseAnalytics;

    private String photoURL, name, beauty;

    @Override
    protected void onStart() {
        super.onStart();
        DataLayer dataLayer = TagManager.getInstance(this).getDataLayer();
        dataLayer.pushEvent("Application ouverte", DataLayer.mapOf("login page", "un de plus"));
        TagManager.getInstance(this).getDataLayer().push("qwe", "rty");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_feed);

        if (!Intermediates.isConnected(getApplicationContext())) {
            startActivity(new Intent(getApplicationContext(), InternetNotification.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }

        Storage.Init(getApplicationContext());

        if (Storage.Get("Autentification").equals("Success")) {
            name = Storage.Get("Name");
            photoURL = Storage.Get("PhotoURL");
        }

        beauty = getIntent().getStringExtra("Beauty");

        if (beauty == null) {
            beauty = "GlobalFeed";
        }

        FireAnal.setContext(getApplicationContext());
        FireAnal.sendString("1", "Open", "GlobalFeed");

        initToolbar();
        initNavigationView();
        initViewPager();

        new GlobalFeedLoader().execute();
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (beauty == "GlobalFeed")
            toolbar.setTitle(R.string.toolbar_title_global_feed);
        else
            toolbar.setTitle(beauty);
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
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_toggle_open, R.string.drawer_toggle_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) drawerLayout.findViewById(R.id.navigation);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                drawerLayout.closeDrawers();
                switch (item.getItemId()) {
                    case R.id.navMenuGlobalFeed:
                        if (beauty == "Makeup" || beauty == "Hairstyle" || beauty == "Manicure" || beauty == "Lips")
                            startActivity(new Intent(getApplicationContext(), GlobalFeed.class)
                                    .putExtra("Beauty", "GlobalFeed"));
                        break;
                    case R.id.navMenuSearch:
                        startActivity(new Intent(getApplicationContext(), Search.class)
                                .putExtra("hashTag", "empty"));
                        break;
                    case R.id.navMenuMakeup:
                        startActivity(new Intent(getApplicationContext(), GlobalFeed.class)
                                .putExtra("Beauty", "Makeup"));
                        break;
                    case R.id.navMenuHairstyle:
                        startActivity(new Intent(getApplicationContext(), GlobalFeed.class)
                                .putExtra("Beauty", "Hairstyle"));
                        break;
                    case R.id.navMenuManicure:
                        startActivity(new Intent(getApplicationContext(), GlobalFeed.class)
                                .putExtra("Beauty", "Manicure"));
                        break;
                    case R.id.navMenuLips:
                        startActivity(new Intent(getApplicationContext(), GlobalFeed.class)
                                .putExtra("Beauty", "Lips"));
                        break;
                    case R.id.navMenuProfile:
                        if (name != null)
                            startActivity(new Intent(getApplicationContext(), MyProfile.class)
                                    .putExtra("Name", name)
                                    .putExtra("PhotoURL", photoURL)
                                    .putExtra("From", "NavMenu"));
                        else
                            startActivity(new Intent(getApplicationContext(), Introduction.class));
                        break;
                    case R.id.navMenuFavorites:
                        if (name != null)
                            startActivity(new Intent(getApplicationContext(), Favorites.class));
                        else
                            startActivity(new Intent(getApplicationContext(), Introduction.class));
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
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        adapter = new GlobalFeedFragmentAdapter(getApplicationContext(), getSupportFragmentManager(), new ArrayList<TapeDTO>());
        viewPager.setAdapter(adapter);
    }

    public class GlobalFeedLoader extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlFeedConnection = null;
        BufferedReader reader = null;
        String resultJsonFeed = "";
        URL feedURL;

        @Override
        protected String doInBackground(Void... params) {
            try {
                if (beauty.equals("GlobalFeed"))
                    feedURL = new URL(Intermediates.URL.GET_FEED);
                else if (beauty.equals("Makeup"))
                    feedURL = new URL("http://195.88.209.17/search/index.php?request=example");
                else if (beauty.equals("Hairstyle"))
                    feedURL = new URL(Intermediates.URL.GET_FEED);
                else if (beauty.equals("Manicure"))
                    feedURL = new URL(Intermediates.URL.GET_FEED);
                else if (beauty.equals("Lips"))
                    feedURL = new URL(Intermediates.URL.GET_FEED);
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

            long id, sid, likes, tempDate, currentDate = System.currentTimeMillis();
            List<TapeDTO> data = new ArrayList<>();
            String availableDate, colors, difficult, eye_color, occasion, tags, title_en, title_ru, authorPhoto, authorName, authorLastname, publicate;

            try {
                JSONArray items = new JSONArray(resultJsonFeed);

                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);
                    List<String> images = new ArrayList<>();
                    List<String> hashTags = new ArrayList<>();

                    for (int j = 1; j < 11; j++) {
                        if (!item.getString("screen" + j).equals("empty")) {
                            images.add(item.getString("screen" + j));
                        }
                    }

                    id = item.getLong("id");
                    tempDate = item.getLong("availableDate");
                    colors = item.getString("colors");
                    difficult = item.getString("difficult");
                    eye_color = item.getString("eye_color");
                    likes = item.getLong("likes");
                    title_en = upperCaseFirst(item.getString("title_en"));

                    title_ru = item.getString("title_ru");
                    occasion = item.getString("occasion");
                    publicate = item.getString("publicate");
                    tags = item.getString("tags");
                    sid = item.getLong("sid");
                    authorPhoto = item.getString("authorPhoto");
                    authorName = item.getString("authorName");

                    String[] tempTags = tags.split(",");
                    for (int j = 0; j < tempTags.length; j++) {
                        hashTags.add(tempTags[j]);
                    }

                    new ProfileDataLoader(sid, getApplicationContext()).execute();

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy", dateFormatSymbols);
                    availableDate = simpleDateFormat.format(new Date(tempDate));
                    if ((currentDate - tempDate) <= 259200000)
                        availableDate = calculateAvailableTime(tempDate, currentDate);

                    if (publicate.equals("t")) {
                        TapeDTO tapeDTO = new TapeDTO(id, sid, title_en, availableDate, authorName, authorPhoto, images, colors, eye_color, occasion, difficult, hashTags, likes);
                        data.add(tapeDTO);
                    }
                }
                adapter.setData(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private String upperCaseFirst(String word) {
            if (word == null || word.isEmpty()) return "";
            return word.substring(0, 1).toUpperCase() + word.substring(1);
        }

        private String calculateAvailableTime(long temp, long current) {
            String date = "";
            if ((current - temp) < 3600000)
                date = " Только что";
            else if ((current - temp) > 3600000 && (current - temp) <= 7200000)
                date = " 1 час назад";
            else if ((current - temp) > 7200000 && (current - temp) <= 10800000)
                date = " 2 часа назад";
            else if ((current - temp) > 10800000 && (current - temp) <= 14400000)
                date = " 3 часа назад";
            else if ((current - temp) > 14400000 && (current - temp) <= 18000000)
                date = " 4 часа назад";
            else if ((current - temp) > 18000000 && (current - temp) <= 21600000)
                date = " 5 часов назад";
            else if ((current - temp) > 21600000 && (current - temp) <= 25200000)
                date = " 6 часов назад";
            else if ((current - temp) > 25200000 && (current - temp) <= 28800000)
                date = " 7 часов назад";
            else if ((current - temp) > 28800000 && (current - temp) <= 32400000)
                date = " 8 часов назад";
            else if ((current - temp) > 32400000 && (current - temp) <= 36000000)
                date = " 9 часов назад";
            else if ((current - temp) > 36000000 && (current - temp) <= 39600000)
                date = " 10 часов назад";
            else if ((current - temp) > 39600000 && (current - temp) <= 43200000)
                date = " 11 часов назад";
            else if ((current - temp) > 43200000 && (current - temp) <= 46800000)
                date = " 12 часов назад";
            else if ((current - temp) > 46800000 && (current - temp) <= 50400000)
                date = " 13 часов назад";
            else if ((current - temp) > 50400000 && (current - temp) <= 54000000)
                date = " 14 часов назад";
            else if ((current - temp) > 54000000 && (current - temp) <= 57600000)
                date = " 15 часов назад";
            else if ((current - temp) > 57600000 && (current - temp) <= 61200000)
                date = " 16 часов назад";
            else if ((current - temp) > 61200000 && (current - temp) <= 64800000)
                date = " 17 часов назад";
            else if ((current - temp) > 64800000 && (current - temp) <= 68400000)
                date = " 18 часов назад";
            else if ((current - temp) > 68400000 && (current - temp) <= 72000000)
                date = " 19 часов назад";
            else if ((current - temp) > 72000000 && (current - temp) <= 75600000)
                date = " 20 часов назад";
            else if ((current - temp) > 75600000 && (current - temp) <= 79200000)
                date = " 21 час назад";
            else if ((current - temp) > 79200000 && (current - temp) <= 82800000)
                date = " 22 часа назад";
            else if ((current - temp) > 82800000 && (current - temp) <= 86400000)
                date = " 23 часа назад";
            else if ((current - temp) > 86400000 && (current - temp) <= 172800000)
                date = " 1 день назад";
            else if ((current - temp) > 172800000 && (current - temp) <= 259200000)
                date = " 2 дня назад";
            return date;
        }

        private DateFormatSymbols dateFormatSymbols = new DateFormatSymbols() {
            @Override
            public String[] getMonths() {
                return new String[]{"января", "февраля", "марта", "апреля", "мая", "июня", "июля", "августа", "сентября", "октября", "ноября", "декабря"};
            }
        };
    }
}
