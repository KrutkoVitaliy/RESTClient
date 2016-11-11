package appcorp.mmb.activities.user;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.List;

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

public class MyProfile extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private TextView name, location, phone;
    private ImageView gplusButton, fbButton, vkButton, instagramButton, okButton;
    private ImageView photo;
    private String id;
    private String gplusLink, fbLink, vkLink, instagramLink, okLink;
    private LinearLayout myProfileServices;
    private FloatingActionButton addToGallery;
    private ProgressDialog progressDialog;
    List<Integer> numbers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        Storage.init(getApplicationContext());
        initFirebase();

        FireAnal.sendString("1", "Open", "MyProfile");

        for (int i = 0; i < 10; i++) {
            numbers.add(i);
        }

        initToolbar();
        initViews();
        initNavigationView();
        new MyProfileLoader(Storage.getString("E-mail", "Click to sign in")).execute();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(Intermediates.convertToString(getApplicationContext(), R.string.loading));
        progressDialog.show();
    }

    private void initFirebase() {
        FireAnal.setContext(getApplicationContext());
    }

    private void initViews() {
        name = (TextView) findViewById(R.id.myProfileName);
        location = (TextView) findViewById(R.id.myProfileLocation);
        phone = (TextView) findViewById(R.id.myProfilePhone);
        photo = (ImageView) findViewById(R.id.myProfileAvatar);
        myProfileServices = (LinearLayout) findViewById(R.id.myProfileServices);
        gplusButton = (ImageView) findViewById(R.id.myProfileGplusButton);
        fbButton = (ImageView) findViewById(R.id.myProfileFbButton);
        vkButton = (ImageView) findViewById(R.id.myProfileVkButton);
        instagramButton = (ImageView) findViewById(R.id.myProfileInstagramButton);
        okButton = (ImageView) findViewById(R.id.myProfileOkButton);
        addToGallery = (FloatingActionButton) findViewById(R.id.myProfileAddToGallery);

        gplusButton.setOnClickListener(this);
        fbButton.setOnClickListener(this);
        vkButton.setOnClickListener(this);
        instagramButton.setOnClickListener(this);
        okButton.setOnClickListener(this);
        addToGallery.setOnClickListener(this);
        phone.setOnClickListener(this);
        photo.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == gplusButton) {
            if (!gplusLink.equals(""))
                createThreeButtonsAlertDialog("Google Plus", "gplus", gplusLink);
        }
        if (view == fbButton) {
            if (!fbLink.equals(""))
                createThreeButtonsAlertDialog("Facebook", "fb", fbLink);
        }
        if (view == vkButton) {
            if (!vkLink.equals(""))
                createThreeButtonsAlertDialog("Vkontakte", "vk", vkLink);
        }
        if (view == instagramButton) {
            if (!instagramLink.equals(""))
                createThreeButtonsAlertDialog("Instagram", "instagram", instagramLink);
        }
        if (view == okButton) {
            if (!okLink.equals(""))
                createThreeButtonsAlertDialog("Odnoklassniki", "ok", okLink);
        }
        if (view == addToGallery) {
            newMakeup();
        }
        if (view == phone) {
            call();
        }
        if (view == photo) {
            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            final int ACTIVITY_SELECT_IMAGE = 1234;
            startActivityForResult(i, ACTIVITY_SELECT_IMAGE);
        }
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
                        progressDialog = new ProgressDialog(this);
                        progressDialog.setMessage(Intermediates.convertToString(getApplicationContext(), R.string.loading));
                        progressDialog.show();
                        new UploadProfileImage(filePath, progressDialog).execute();
                    }
                    new MyProfileLoader(Storage.getString("E-mail", "Click to sign in")).execute();
                }
        }

    }

    private void call() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.chooseAction);
        alert.setMessage(phone.getText());

        alert.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //showMessage("Нажали Нет");
                    }
                });
        alert.setPositiveButton(R.string.call,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (phone != null) {
                            try {
                                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone.getText())));
                            } catch (SecurityException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
        alert.setNeutralButton(R.string.copy,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ClipboardManager clipboard = (ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("", phone.getText().toString());
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(getApplicationContext(), R.string.copied, Toast.LENGTH_SHORT).show();
                    }
                });

        alert.show();
    }

    private void newMakeup() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.add_makeup_service);
        alert.setMessage(R.string.enter_service_name);

        final LinearLayout inputFields = new LinearLayout(getApplicationContext());
        inputFields.setOrientation(LinearLayout.VERTICAL);
        final EditText service = new EditText(this);
        service.setHint(R.string.service_name);
        inputFields.addView(service);
        final EditText cost = new EditText(this);
        cost.setHint(R.string.service_cost);
        inputFields.addView(cost);
        alert.setView(inputFields);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                service.setText(service.getText().toString().trim());
                cost.setText(cost.getText().toString().trim());
                int tempCost;
                try {
                    tempCost = Integer.valueOf(cost.getText().toString());
                } catch (NumberFormatException e) {
                    tempCost = -1;
                    e.printStackTrace();
                }
                if (!service.getText().toString().equals("") && !cost.getText().toString().equals("") && tempCost > 0) {
                    final LinearLayout stroke = new LinearLayout(getApplicationContext());
                    LinearLayout strService = new LinearLayout(getApplicationContext());
                    LinearLayout strCost = new LinearLayout(getApplicationContext());
                    LinearLayout removeService = new LinearLayout(getApplicationContext());
                    ImageView remove = new ImageView(getApplicationContext());
                    remove.setImageResource(R.mipmap.ic_close_circle_outline);
                    removeService.addView(remove);
                    strCost.setLayoutParams(new ViewGroup.LayoutParams(Storage.getInt("Width", 480) / 6, ViewGroup.LayoutParams.WRAP_CONTENT));
                    strService.setLayoutParams(new ViewGroup.LayoutParams((Storage.getInt("Width", 480) - Storage.getInt("Width", 480) / 6) - Storage.getInt("Width", 480) / 5, ViewGroup.LayoutParams.WRAP_CONTENT));
                    removeService.setLayoutParams(new ViewGroup.LayoutParams(Storage.getInt("Width", 480) / 5, ViewGroup.LayoutParams.WRAP_CONTENT));
                    strService.setOrientation(LinearLayout.HORIZONTAL);
                    strCost.setOrientation(LinearLayout.HORIZONTAL);
                    removeService.setOrientation(LinearLayout.HORIZONTAL);
                    stroke.addView(strService);
                    stroke.addView(strCost);
                    stroke.addView(removeService);

                    TextView s = new TextView(getApplicationContext());
                    s.setTextColor(Color.parseColor("#808080"));
                    s.setTextSize(16);
                    s.setPadding(0, 8, 0, 8);
                    s.setText(service.getText().toString());
                    strService.addView(s);
                    TextView c = new TextView(getApplicationContext());
                    c.setTextColor(Color.parseColor("#404040"));
                    c.setTextSize(16);
                    c.setPadding(0, 8, 0, 8);
                    c.setText(cost.getText().toString());
                    strCost.addView(c);

                    final String tempService = service.getText().toString();
                    final String tempCosts = cost.getText().toString();

                    removeService.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new GetRequest("http://195.88.209.17/app/in/removemakeupservice.php" +
                                    "?service=" + Intermediates.encodeToURL(tempService) +
                                    "&cost=" + Intermediates.encodeToURL(tempCosts) +
                                    "&id=" + id).execute();
                            stroke.removeAllViews();
                        }
                    });

                    myProfileServices.addView(stroke);
                    new GetRequest("http://195.88.209.17/app/in/addmakeupservice.php" +
                            "?service=" + Intermediates.encodeToURL(service.getText().toString()) +
                            "&cost=" + Intermediates.encodeToURL(cost.getText().toString()) +
                            "&id=" + id).execute();
                }
            }
        });
        alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        alert.show();
    }


    private void createThreeButtonsAlertDialog(String title, final String type, final String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MyProfile.this);
        builder.setTitle(title);
        builder.setMessage(content);
        builder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.setPositiveButton(R.string.open,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), ProfileMediaViewer.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .putExtra("Type", type)
                                .putExtra("URL", content));
                    }
                });
        builder.setNeutralButton(R.string.copy,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ClipboardManager clipboard = (ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("", content);
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(getApplicationContext(), R.string.copied, Toast.LENGTH_SHORT).show();
                    }
                });

        builder.show();
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.myProfileToolbar);
        toolbar.setTitle(Storage.getString("Name", Intermediates.convertToString(getApplicationContext(), R.string.app_name)));
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                startActivity(new Intent(getApplicationContext(), EditMyProfile.class)
                        .putExtra("ID", id)
                        .putExtra("Name", name.getText())
                        .putExtra("Location", location.getText())
                        .putExtra("Phone", phone.getText()));
                return true;
            }
        });
        toolbar.inflateMenu(R.menu.menu_myprofile);
    }

    private void initNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.myProfileDrawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_toggle_open, R.string.drawer_toggle_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) drawerLayout.findViewById(R.id.myProfileNavigation);
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
        if (!Storage.getString("E-mail", "").equals("")) {
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

    class MyProfileLoader extends AsyncTask<Void, Void, String> {

        HttpURLConnection profilePageConnection = null;
        BufferedReader profileReader = null;
        String url = "http://195.88.209.17/app/in/user.php?action=get&email=";
        String result = "";
        String email = "";

        MyProfileLoader(String email) {
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
                    name.setText(item.getString("firstName") + " " + item.getString("lastName"));
                    Storage.addString("Name", item.getString("firstName") + " " + item.getString("lastName"));
                    location.setText(item.getString("city") + "  " + item.getString("address"));
                    phone.setText(item.getString("phoneNumber"));
                    //likes.setText(item.getString("likes"));
                    //followers.setText(item.getString("followers"));
                    id = item.getString("id");
                    gplusLink = item.getString("gplus");
                    fbLink = item.getString("facebook");
                    vkLink = item.getString("vkontakte");
                    instagramLink = item.getString("instagram");
                    okLink = item.getString("odnoklassniki");
                    if (item.getString("gplus").equals(""))
                        gplusButton.setAlpha(0.4F);
                    if (item.getString("facebook").equals(""))
                        fbButton.setAlpha(0.4F);
                    if (item.getString("vkontakte").equals(""))
                        vkButton.setAlpha(0.4F);
                    if (item.getString("instagram").equals(""))
                        instagramButton.setAlpha(0.4F);
                    if (item.getString("odnoklassniki").equals(""))
                        okButton.setAlpha(0.4F);
                    if (!item.getString("services").equals("")) {
                        String[] makeupServicesArray = item.getString("services").substring(1, item.getString("services").length()).split(",");
                        String[] makeupCostsArray = item.getString("costs").substring(1, item.getString("costs").length()).split(",");

                        for (int j = 0; j < makeupServicesArray.length; j++) {
                            final LinearLayout stroke = new LinearLayout(getApplicationContext());
                            LinearLayout strService = new LinearLayout(getApplicationContext());
                            LinearLayout strCost = new LinearLayout(getApplicationContext());
                            LinearLayout removeService = new LinearLayout(getApplicationContext());
                            ImageView remove = new ImageView(getApplicationContext());
                            remove.setImageResource(R.mipmap.ic_close_circle_outline);
                            removeService.addView(remove);
                            strCost.setLayoutParams(new ViewGroup.LayoutParams(Storage.getInt("Width", 480) / 6, ViewGroup.LayoutParams.WRAP_CONTENT));
                            strService.setLayoutParams(new ViewGroup.LayoutParams((Storage.getInt("Width", 480) - Storage.getInt("Width", 480) / 6) - Storage.getInt("Width", 480) / 5, ViewGroup.LayoutParams.WRAP_CONTENT));
                            removeService.setLayoutParams(new ViewGroup.LayoutParams(Storage.getInt("Width", 480) / 5, ViewGroup.LayoutParams.WRAP_CONTENT));
                            strService.setOrientation(LinearLayout.HORIZONTAL);
                            strCost.setOrientation(LinearLayout.HORIZONTAL);
                            removeService.setOrientation(LinearLayout.HORIZONTAL);
                            stroke.addView(strService);
                            stroke.addView(strCost);
                            stroke.addView(removeService);

                            TextView serviceTextView = new TextView(getApplicationContext());
                            serviceTextView.setTextColor(Color.parseColor("#808080"));
                            serviceTextView.setTextSize(16);
                            serviceTextView.setPadding(0, 8, 0, 8);
                            serviceTextView.setText(makeupServicesArray[j]);
                            strService.addView(serviceTextView);

                            TextView costsTextView = new TextView(getApplicationContext());
                            costsTextView.setTextColor(Color.parseColor("#404040"));
                            costsTextView.setTextSize(16);
                            costsTextView.setPadding(0, 8, 0, 8);
                            costsTextView.setText(makeupCostsArray[j]);
                            strCost.addView(costsTextView);

                            final String tempService = serviceTextView.getText().toString();
                            final String tempCosts = costsTextView.getText().toString();

                            removeService.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    new GetRequest("http://195.88.209.17/app/in/removemakeupservice.php" +
                                            "?service=" + Intermediates.encodeToURL(tempService) +
                                            "&cost=" + Intermediates.encodeToURL(tempCosts) +
                                            "&id=" + id).execute();
                                    stroke.removeAllViews();
                                }
                            });

                            myProfileServices.addView(stroke);
                        }
                    }
                    String photoUrl = "http://195.88.209.17/storage/photos/" + item.getString("photo");
                    Storage.addString("PhotoURL", item.getString("photo"));
                    Storage.addString("PhotoURL", item.getString("photo"));
                    Storage.addString("MyCity", item.getString("city"));
                    Picasso.with(getApplicationContext()).load(photoUrl).resize(photo.getWidth(), photo.getHeight()).centerCrop().into(photo);
                    initHeaderLayout((NavigationView) drawerLayout.findViewById(R.id.myProfileNavigation));
                    if (progressDialog != null)
                        progressDialog.dismiss();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}