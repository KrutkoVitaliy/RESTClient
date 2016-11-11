package appcorp.mmb.activities.user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import appcorp.mmb.R;
import appcorp.mmb.activities.feeds.GlobalFeed;
import appcorp.mmb.activities.feeds.HairstyleFeed;
import appcorp.mmb.activities.feeds.MakeupFeed;
import appcorp.mmb.activities.feeds.ManicureFeed;
import appcorp.mmb.activities.search_feeds.Search;
import appcorp.mmb.activities.search_feeds.SearchStylist;
import appcorp.mmb.classes.FireAnal;
import appcorp.mmb.classes.Intermediates;
import appcorp.mmb.classes.Storage;
import appcorp.mmb.network.GetRequest;
import appcorp.mmb.network.UploadProfileImage;

public class EditMyProfile extends AppCompatActivity {

    private ImageView editAvatar;
    private EditText editName, editLastname, editCity, editAddress, editPhone;
    private EditText editGplus, editFB, editVK, editInst, editOK;
    private int id;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ProgressDialog loadProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_my_profile);

        Storage.init(getApplicationContext());
        initFirebase();

        FireAnal.sendString("1", "Open", "EditProfile");

        loadProgressDialog = new ProgressDialog(this);
        loadProgressDialog.setMessage(Intermediates.convertToString(getApplicationContext(), R.string.loading));
        loadProgressDialog.show();

        initToolbar();
        initNavigationView();
        initFields();

        id = Integer.valueOf(getIntent().getStringExtra("ID"));
        editAvatar = (ImageView) findViewById(R.id.editAvatar);
        Picasso.with(getApplicationContext()).load("http://195.88.209.17/storage/images/" + Storage.getString("PhotoURL", "")).into(editAvatar);
        new LoadCurrentData(Storage.getString("E-mail", "Click to sign in")).execute();
        editAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                final int ACTIVITY_SELECT_IMAGE = 1234;
                startActivityForResult(i, ACTIVITY_SELECT_IMAGE);
            }
        });
    }

    private void initFirebase() {
        FireAnal.setContext(getApplicationContext());
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1234:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    if (cursor != null) {
                        cursor.moveToFirst();
                    }

                    int columnIndex = 0;
                    if (cursor != null) {
                        columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    }
                    String filePath = null;
                    if (cursor != null) {
                        filePath = cursor.getString(columnIndex);
                    }
                    if (cursor != null) {
                        cursor.close();
                    }

                    if (filePath != null && (filePath.endsWith(".jpg") || filePath.endsWith(".png"))) {
                        ProgressDialog progressDialog = new ProgressDialog(this);
                        progressDialog.setMessage(Intermediates.convertToString(getApplicationContext(), R.string.loading));
                        progressDialog.show();
                        new UploadProfileImage(filePath, progressDialog).execute();
                    }
                    new LoadCurrentData(Storage.getString("E-mail", "Click to sign in")).execute();
                }
        }
    }

    private void initFields() {
        editName = (EditText) findViewById(R.id.editName);
        editLastname = (EditText) findViewById(R.id.editLastname);
        editCity = (EditText) findViewById(R.id.editCity);
        editAddress = (EditText) findViewById(R.id.editAddress);
        editPhone = (EditText) findViewById(R.id.editPhone);
        editGplus = (EditText) findViewById(R.id.editGplus);
        editFB = (EditText) findViewById(R.id.editFB);
        editVK = (EditText) findViewById(R.id.editVK);
        editInst = (EditText) findViewById(R.id.editInst);
        editOK = (EditText) findViewById(R.id.editOK);
        Button logOut = (Button) findViewById(R.id.logOut);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Storage.clearString("E-mail");
                Storage.clearString("Name");
                Storage.clearString("PhotoURL");
                Storage.clearString("MyCity");
                startActivity(new Intent(getApplicationContext(), GlobalFeed.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        });
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.editMyProfileToolbar);
        toolbar.setTitle(Storage.getString("Name", Intermediates.convertToString(getApplicationContext(), R.string.app_name)));
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                new GetRequest("http://195.88.209.17/app/in/updateuser.php" +
                        "?firstname=" + Intermediates.encodeToURL(editName.getText().toString()) +
                        "&lastname=" + Intermediates.encodeToURL(editLastname.getText().toString()) +
                        "&city=" + Intermediates.encodeToURL(editCity.getText().toString()) +
                        "&address=" + Intermediates.encodeToURL(editAddress.getText().toString()) +
                        "&phone=" + Intermediates.encodeToURL(editPhone.getText().toString()) +
                        "&gplus=" + Intermediates.encodeToURL(editGplus.getText().toString()) +
                        "&facebook=" + Intermediates.encodeToURL(editFB.getText().toString()) +
                        "&vk=" + Intermediates.encodeToURL(editVK.getText().toString()) +
                        "&instagram=" + Intermediates.encodeToURL(editInst.getText().toString()) +
                        "&ok=" + Intermediates.encodeToURL(editOK.getText().toString()) +
                        "&id=" + id).execute();
                startActivity(new Intent(getApplicationContext(), MyProfile.class));
                return true;
            }
        });
        toolbar.inflateMenu(R.menu.menu_edit_myprofile);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void initNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.editMyProfileDrawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_toggle_open, R.string.drawer_toggle_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) drawerLayout.findViewById(R.id.editMyProfileNavigation);
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
            Picasso.with(getApplicationContext()).load("http://195.88.209.17/storage/photos/" + Storage.getString("PhotoURL", "")).into(avatar);
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

    public class LoadCurrentData extends AsyncTask<Void, Void, String> {

        HttpURLConnection profilePageConnection = null;
        BufferedReader profileReader = null;
        String url = "http://195.88.209.17/app/in/user.php?action=get&email=";
        String result = "";
        String email = "";

        LoadCurrentData(String email) {
            this.email = email;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL feedURL = new URL(url + email);
                profilePageConnection = (HttpURLConnection) feedURL.openConnection();
                profilePageConnection.setRequestMethod("GET");
                profilePageConnection.connect();
                InputStream inputStream = profilePageConnection.getInputStream();
                profileReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder buffer = new StringBuilder();
                String line;
                while ((line = profileReader.readLine()) != null)
                    buffer.append(line);
                result += buffer.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONArray items = new JSONArray(s);
                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);
                    editName.setText(item.getString("firstName"));
                    editLastname.setText(item.getString("lastName"));
                    editCity.setText(item.getString("city"));
                    editAddress.setText(item.getString("address"));
                    editPhone.setText(item.getString("phoneNumber"));
                    editGplus.setText(item.getString("gplus"));
                    editFB.setText(item.getString("facebook"));
                    editVK.setText(item.getString("vkontakte"));
                    editInst.setText(item.getString("instagram"));
                    editOK.setText(item.getString("odnoklassniki"));
                    Picasso.with(getApplicationContext()).load("http://195.88.209.17/storage/photos/" + item.getString("photo")).into(editAvatar);
                    Storage.addString("PhotoURL", item.getString("photo"));
                    loadProgressDialog.dismiss();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}