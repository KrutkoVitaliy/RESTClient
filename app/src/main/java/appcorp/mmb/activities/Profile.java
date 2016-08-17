package appcorp.mmb.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import appcorp.mmb.classes.Intermediates;
import appcorp.mmb.R;
import appcorp.mmb.classes.Storage;

public class Profile extends Activity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;

    private long sid;
    private String avatar = "";
    private String name = "";
    private String lastname = "";
    private String id = "";
    private String aid = "";
    private String registration_date = "";
    private String last_visit;
    private String nickname = "";
    private String skills = "";
    private String likes = "";
    private String followers = "";
    private String city = "";
    private String address = "";
    private String user_type = "";
    private String phone_number = "";


    private String userName = "";
    private String userPhotoURL = "";

    private ImageView layout_avatar, search_back;
    private TextView layout_name;
    private TextView layout_likes;
    private TextView layout_followers;
    private TextView layout_location;
    private TextView layout_phone_number;
    private TextView skill1, skill2, skill3, skill4, skill5, skill6;

    public Profile() {

    }

    public Profile(long sid) {
        this.sid = sid;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initToolbar();
        initNavigationView();

        new Get().execute();
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.profileToolbar);
        toolbar.setTitle(Storage.getString("Name", Intermediates.convertToString(getApplicationContext(), R.string.app_name)));
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                //startActivity(new Intent(getApplicationContext(), Search.class).putExtra("hashTag", "empty"));
                return true;
            }
        });

        toolbar.inflateMenu(R.menu.menu);
    }

    private void initNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.profileDrawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_toggle_open, R.string.drawer_toggle_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) drawerLayout.findViewById(R.id.profileNavigation);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                drawerLayout.closeDrawers();
                switch (item.getItemId()) {
                    case R.id.navMenuGlobalFeed:
                        startActivity(new Intent(getApplicationContext(), SelectCategory.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        break;
                    case R.id.navMenuSearch:
                        startActivity(new Intent(getApplicationContext(), Search.class)
                                .putExtra("hashTag", "empty"));
                        break;
                    case R.id.navMenuProfile:
                        if (!name.equals("Click to sign in"))
                            startActivity(new Intent(getApplicationContext(), MyProfile.class));
                        else
                            startActivity(new Intent(getApplicationContext(), Authorization.class));
                        break;
                    case R.id.navMenuFavorites:
                        if (name != null)
                            startActivity(new Intent(getApplicationContext(), Favorites.class));
                        else
                            startActivity(new Intent(getApplicationContext(), Authorization.class));
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), SelectCategory.class));
    }

    class Get extends AsyncTask<Void, Void, String> {
        HttpURLConnection profileConnection = null;
        BufferedReader profileReader = null;
        String resultJsonProfile = "";

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL profileURL = new URL("fd");
                profileConnection = (HttpURLConnection) profileURL.openConnection();
                profileConnection.setRequestMethod("GET");
                profileConnection.connect();
                InputStream inputStream = profileConnection.getInputStream();
                profileReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer profileBuffer = new StringBuffer();
                String profileLine;
                while ((profileLine = profileReader.readLine()) != null) {
                    profileBuffer.append(profileLine);
                }
                resultJsonProfile = profileBuffer.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJsonProfile;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject profileItem = new JSONObject(s);
                avatar = profileItem.getString("avatar");
                layout_avatar = (ImageView) findViewById(R.id.avatar);
                layout_avatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), FullscreenPreview.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("screenshot", avatar);
                        getApplicationContext().startActivity(intent);
                    }
                });
                Picasso.with(getApplicationContext()).load(avatar).into(layout_avatar);

                name = profileItem.getString("first_name");
                lastname = profileItem.getString("last_name");
                layout_name = (TextView) findViewById(R.id.name);
                layout_name.setText(name + " " + lastname);

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

                id = profileItem.getString("id");
                aid = profileItem.getString("aid");
                registration_date = profileItem.getString("registration_date");
                user_type = profileItem.getString("user_type");
                nickname = profileItem.getString("nickname");

                skill1 = (TextView) findViewById(R.id.skill1);
                skill2 = (TextView) findViewById(R.id.skill2);
                skill3 = (TextView) findViewById(R.id.skill3);
                skill4 = (TextView) findViewById(R.id.skill4);
                skill5 = (TextView) findViewById(R.id.skill5);
                skill6 = (TextView) findViewById(R.id.skill6);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}