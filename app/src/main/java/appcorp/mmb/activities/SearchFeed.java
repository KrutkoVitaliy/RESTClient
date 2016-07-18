package appcorp.mmb.activities;

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
import appcorp.mmb.adapter.SearchFragmentAdapter;
import appcorp.mmb.dto.SearchDTO;

public class SearchFeed extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ViewPager viewPager;
    private SearchFragmentAdapter adapter;
    private String request, colors, eyeColor, difficult, occasion;

    public SearchFeed() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_feed);

        this.request = getIntent().getStringExtra("Request");
        this.colors = getIntent().getStringExtra("Colors");
        this.eyeColor = getIntent().getStringExtra("EyeColor");
        this.difficult = getIntent().getStringExtra("Difficult");
        this.occasion = getIntent().getStringExtra("Occasion");

        initToolbar();
        initNavigationView();
        initViewPager();
        new Get().execute();
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.searchToolbar);
        toolbar.setTitle("#"+request);
        /*toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent intent = new Intent(getApplicationContext(), Search.class);
                intent.putExtra("hashTag", "empty");
                startActivity(intent);
                return true;
            }
        });
        toolbar.inflateMenu(R.menu.menu);*/
    }

    private void initNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.searchDrawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_toggle_open, R.string.drawer_toggle_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.searchNavigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                drawerLayout.closeDrawers();/*
                switch (item.getItemId()) {
                    case R.id.firstItem:
                        viewPager.setCurrentItem(0);
                }*/
                return true;
            }
        });
    }

    private void initViewPager() {
        viewPager = (ViewPager) findViewById(R.id.searchViewPager);
        adapter = new SearchFragmentAdapter(getApplicationContext(), getSupportFragmentManager(), new ArrayList<SearchDTO>());
        viewPager.setAdapter(adapter);
    }

    class Get extends AsyncTask<Void, Void, String> {

        HttpURLConnection searchConnection = null;
        BufferedReader searchReader = null;
        String resultJsonSearch = "";
        String str;

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL profileURL = new URL("http://195.88.209.17/search/index.php?request="+request);
                searchConnection = (HttpURLConnection) profileURL.openConnection();
                searchConnection.setRequestMethod("GET");
                searchConnection.connect();
                InputStream inputStream = searchConnection.getInputStream();
                searchReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer profileBuffer = new StringBuffer();
                String profileLine;
                while ((profileLine = searchReader.readLine()) != null) {
                    profileBuffer.append(profileLine);
                }
                resultJsonSearch = profileBuffer.toString();
                str = resultJsonSearch.substring(0, resultJsonSearch.length() - 2);
                str += "]";
            } catch (Exception e) {
                e.printStackTrace();
            }
            return str;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            List<SearchDTO> searchData = new ArrayList<>();
            long
                    id,
                    sid,
                    likes,
                    currentDate = System.currentTimeMillis();
            String
                    availableDate = "",
                    colors = "",
                    difficult = "",
                    eye_color = "",
                    occasion = "",
                    tags = "",
                    title = "",
                    authorPhoto = "",
                    authorName = "",
                    authorLastname = "",
                    publicate = "",
                    tempDate;

            try {
                JSONArray items = new JSONArray(s);
                for (int i = 0; i < items.length(); i++) {
                    JSONObject searchItem = items.getJSONObject(i);
                    List<String> images = new ArrayList<>();
                    List<String> hashTags = new ArrayList<>();

                    for (int j = 1; j < 11; j++) {
                        if (!searchItem.getString("screen" + j).equals("empty")) {
                            images.add(searchItem.getString("screen" + j));
                        }
                    }

                    String[] tempTags = searchItem.getString("tags").split(",");
                    for (int j = 0; j < tempTags.length; j++) {
                        hashTags.add(tempTags[j]);
                    }

                    id = searchItem.getLong("id");
                    difficult = searchItem.getString("difficult");
                    eye_color = searchItem.getString("eye_color");
                    likes = searchItem.getLong("likes");
                    title = searchItem.getString("title");
                    occasion = searchItem.getString("occasion");
                    publicate = searchItem.getString("publicate");
                    tags = searchItem.getString("tags");
                    title = searchItem.getString("title");
                    sid = searchItem.getLong("sid");
                    authorPhoto = searchItem.getString("authorPhoto");
                    authorName = searchItem.getString("authorName");
                    colors = searchItem.getString("colors");
                    tempDate = searchItem.getString("availableDate");

                    if (searchItem.getString("publicate").equals("t")) {
                        SearchDTO searchDTO = new SearchDTO(id, sid, tempDate, authorName, authorPhoto, images, colors, eye_color, occasion, difficult, hashTags, likes);
                        searchData.add(searchDTO);
                    }

                }

                /*avatar = profileItem.getString("avatar");
                layout_avatar = (ImageView) findViewById(R.id.avatar);
                Picasso.with(getApplicationContext()).load(avatar).into(layout_avatar);

                name = profileItem.getString("name");
                lastname = profileItem.getString("last_name");
                layout_name = (TextView) findViewById(R.id.name);
                layout_name.setText(name + " " + lastname);

                skills = profileItem.getString("skills");
                layout_skills = (TextView) findViewById(R.id.skills);
                layout_skills.setText(skills);

                city = profileItem.getString("city");
                address = profileItem.getString("address");
                layout_location = (TextView) findViewById(R.id.location);
                layout_location.setText(city + ", " + address);

                phone_number = profileItem.getString("phone_number");
                layout_phone_number = (TextView) findViewById(R.id.phone);
                layout_phone_number.setText(phone_number);

                likes = profileItem.getString("likes");
                layout_likes = (TextView) findViewById(R.id.likes);
                layout_likes.setText(likes);

                followers = profileItem.getString("followers");
                layout_followers = (TextView) findViewById(R.id.followers);
                layout_followers.setText(followers);

                last_visit = profileItem.getString("last_visit");
                layout_last_visit = (TextView) findViewById(R.id.last_visit);
                layout_last_visit.setText(last_visit);

                id = profileItem.getString("id");
                aid = profileItem.getString("aid");
                registration_date = profileItem.getString("registration_date");
                user_type = profileItem.getString("user_type");
                nickname = profileItem.getString("nickname");*/
            } catch (JSONException e) {
                e.printStackTrace();
            }
            adapter.setData(searchData);
        }
    }
}
