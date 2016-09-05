package appcorp.mmb.activities.user;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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

import appcorp.mmb.R;
import appcorp.mmb.activities.search_feeds.Search;
import appcorp.mmb.activities.feeds.HairstyleFeed;
import appcorp.mmb.activities.feeds.LipsFeed;
import appcorp.mmb.activities.feeds.MakeupFeed;
import appcorp.mmb.activities.feeds.ManicureFeed;
import appcorp.mmb.classes.Intermediates;
import appcorp.mmb.classes.Storage;
import appcorp.mmb.network.GetRequest;

public class MyProfile extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private TextView name, location, phone, likes, followers;
    private TextView serviceMakeupText, serviceManicureText, serviceHairstyleText;
    private LinearLayout serviceMakeup, serviceManicure, serviceHairstyle;
    private ImageView gplusButton, fbButton, vkButton, instagramButton, okButton;
    private ImageView photo;
    private String id;
    private String gplusLink, fbLink, vkLink, instagramLink, okLink;
    private ScrollView makeupUserGallery, manicureUserGallery, hairstyleUserGallery;
    private FloatingActionButton addMakeup, addManicure, addHairstyle;
    private LinearLayout servicesMakeup;
    private LinearLayout servicesManicure;
    private LinearLayout servicesHairstyle;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        initToolbar();
        initNavigationView();
        initViews();
        new MyProfileLoader(Storage.getString("E-mail", "Click to sign in")).execute();
        changeServiceStatus(1);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(Intermediates.convertToString(getApplicationContext(), R.string.loading));
        progressDialog.show();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void initViews() {
        name = (TextView) findViewById(R.id.name);
        location = (TextView) findViewById(R.id.location);
        phone = (TextView) findViewById(R.id.phone);
        photo = (ImageView) findViewById(R.id.myProfileAvatar);
        likes = (TextView) findViewById(R.id.likes);
        followers = (TextView) findViewById(R.id.followers);
        serviceMakeupText = (TextView) findViewById(R.id.serviceMakeupText);
        serviceManicureText = (TextView) findViewById(R.id.serviceManicureText);
        serviceHairstyleText = (TextView) findViewById(R.id.serviceHairstyleText);
        serviceMakeupText.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Galada.ttf"));
        serviceManicureText.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Galada.ttf"));
        serviceHairstyleText.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Galada.ttf"));
        serviceMakeup = (LinearLayout) findViewById(R.id.serviceMakeup);
        serviceManicure = (LinearLayout) findViewById(R.id.serviceManicure);
        serviceHairstyle = (LinearLayout) findViewById(R.id.serviceHairstyle);
        gplusButton = (ImageView) findViewById(R.id.gplusButton);
        fbButton = (ImageView) findViewById(R.id.fbButton);
        vkButton = (ImageView) findViewById(R.id.vkButton);
        instagramButton = (ImageView) findViewById(R.id.instagramButton);
        okButton = (ImageView) findViewById(R.id.okButton);
        makeupUserGallery = (ScrollView) findViewById(R.id.makeupUserGallery);
        manicureUserGallery = (ScrollView) findViewById(R.id.manicureUserGallery);
        hairstyleUserGallery = (ScrollView) findViewById(R.id.hairstyleUserGallery);
        addMakeup = (FloatingActionButton) findViewById(R.id.addMakeupToGallery);
        addManicure = (FloatingActionButton) findViewById(R.id.addManicureToGallery);
        addHairstyle = (FloatingActionButton) findViewById(R.id.addHairstyleToGallery);
        servicesMakeup = (LinearLayout) findViewById(R.id.servicesMakeup);
        servicesManicure = (LinearLayout) findViewById(R.id.servicesManicure);
        servicesHairstyle = (LinearLayout) findViewById(R.id.servicesHairstyle);

        serviceMakeup.setOnClickListener(this);
        serviceManicure.setOnClickListener(this);
        serviceHairstyle.setOnClickListener(this);
        gplusButton.setOnClickListener(this);
        fbButton.setOnClickListener(this);
        vkButton.setOnClickListener(this);
        instagramButton.setOnClickListener(this);
        okButton.setOnClickListener(this);
        addMakeup.setOnClickListener(this);
        addManicure.setOnClickListener(this);
        addHairstyle.setOnClickListener(this);
        phone.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == serviceMakeup) {
            changeServiceStatus(1);
        }
        if (view == serviceManicure) {
            changeServiceStatus(2);
        }
        if (view == serviceHairstyle) {
            changeServiceStatus(3);
        }
        if (view == gplusButton) {
            if (!gplusLink.equals(""))
                createThreeButtonsAlertDialog("Google Plus", gplusLink);
        }
        if (view == fbButton) {
            if (!fbLink.equals(""))
                createThreeButtonsAlertDialog("Facebook", fbLink);
        }
        if (view == vkButton) {
            if (!vkLink.equals(""))
                createThreeButtonsAlertDialog("Vkontakte", vkLink);
        }
        if (view == instagramButton) {
            if (!instagramLink.equals(""))
                createThreeButtonsAlertDialog("Instagram", instagramLink);
        }
        if (view == okButton) {
            if (!okLink.equals(""))
                createThreeButtonsAlertDialog("Odnoklassniki", okLink);
        }
        if (view == addMakeup) {
            newMakeup();
        }
        if (view == addManicure) {
            newManicure();
        }
        if (view == addHairstyle) {
            newHairstyle();
        }
        if (view == phone) {
            call();
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
                if (!service.getText().toString().equals("") && !cost.getText().toString().equals("")) {
                    FrameLayout stroke = new FrameLayout(getApplicationContext());
                    LinearLayout strService = new LinearLayout(getApplicationContext());
                    LinearLayout strCost = new LinearLayout(getApplicationContext());
                    strService.setOrientation(LinearLayout.HORIZONTAL);
                    strCost.setOrientation(LinearLayout.HORIZONTAL);
                    strService.setGravity(Gravity.LEFT);
                    strCost.setGravity(Gravity.RIGHT);
                    stroke.addView(strService);
                    stroke.addView(strCost);

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
                    servicesMakeup.addView(stroke);
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

    private void newManicure() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.add_manicure_service);
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
                if (!service.getText().toString().equals("") && !cost.getText().toString().equals("")) {
                    FrameLayout stroke = new FrameLayout(getApplicationContext());
                    LinearLayout strService = new LinearLayout(getApplicationContext());
                    LinearLayout strCost = new LinearLayout(getApplicationContext());
                    strService.setOrientation(LinearLayout.HORIZONTAL);
                    strCost.setOrientation(LinearLayout.HORIZONTAL);
                    strService.setGravity(Gravity.LEFT);
                    strCost.setGravity(Gravity.RIGHT);
                    stroke.addView(strService);
                    stroke.addView(strCost);

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
                    servicesManicure.addView(stroke);
                    new GetRequest("http://195.88.209.17/app/in/addmanicureservice.php" +
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

    private void newHairstyle() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.add_hairstyle_service);
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
                if (!service.getText().toString().equals("") && !cost.getText().toString().equals("")) {
                    FrameLayout stroke = new FrameLayout(getApplicationContext());
                    LinearLayout strService = new LinearLayout(getApplicationContext());
                    LinearLayout strCost = new LinearLayout(getApplicationContext());
                    strService.setOrientation(LinearLayout.HORIZONTAL);
                    strCost.setOrientation(LinearLayout.HORIZONTAL);
                    strService.setGravity(Gravity.LEFT);
                    strCost.setGravity(Gravity.RIGHT);
                    stroke.addView(strService);
                    stroke.addView(strCost);

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
                    servicesHairstyle.addView(s);
                    new GetRequest("http://195.88.209.17/app/in/addhairstyleservice.php" +
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

    private void createThreeButtonsAlertDialog(String title, final String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MyProfile.this);
        builder.setTitle(title);
        builder.setMessage(content);
        builder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //showMessage("Нажали Нет");
                    }
                });
        builder.setPositiveButton(R.string.open,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), ProfileMediaViewer.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .putExtra("URL", content));
                    }
                });
        // устанавливаем кнопку, которая отвечает за выбранный нами ответ
        // в данном случаем мы просто хотим всплывающее окно с отменой
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

    private void changeServiceStatus(int status) {
        switch (status) {
            case 1:
                serviceMakeup.setAlpha(1F);
                serviceManicure.setAlpha(0.6F);
                serviceHairstyle.setAlpha(0.6F);
                makeupUserGallery.setVisibility(View.VISIBLE);
                manicureUserGallery.setVisibility(View.INVISIBLE);
                hairstyleUserGallery.setVisibility(View.INVISIBLE);
                break;
            case 2:
                serviceMakeup.setAlpha(0.6F);
                serviceManicure.setAlpha(1F);
                serviceHairstyle.setAlpha(0.6F);
                makeupUserGallery.setVisibility(View.INVISIBLE);
                manicureUserGallery.setVisibility(View.VISIBLE);
                hairstyleUserGallery.setVisibility(View.INVISIBLE);
                break;
            case 3:
                serviceMakeup.setAlpha(0.6F);
                serviceManicure.setAlpha(0.6F);
                serviceHairstyle.setAlpha(1F);
                makeupUserGallery.setVisibility(View.INVISIBLE);
                manicureUserGallery.setVisibility(View.INVISIBLE);
                hairstyleUserGallery.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.myProfileToolbar);
        toolbar.setTitle(Storage.getString("Name", Intermediates.convertToString(getApplicationContext(), R.string.app_name)));
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                startActivity(new Intent(getApplicationContext(), EditMyProfile.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
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
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) drawerLayout.findViewById(R.id.myProfileNavigation);
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
                    case R.id.navMenuLips:
                        startActivity(new Intent(getApplicationContext(), LipsFeed.class));
                        break;
                    case R.id.navMenuProfile:
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

    private void initHeaderLayout(NavigationView navigationView) {
        View menuHeader = navigationView.getHeaderView(0);
        ImageView avatar = (ImageView) menuHeader.findViewById(R.id.accountPhoto);
        TextView switcherHint = (TextView) menuHeader.findViewById(R.id.accountHint);
        if (!Storage.getString("E-mail", "").equals("")) {
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

    public class MyProfileLoader extends AsyncTask<Void, Void, String> {

        HttpURLConnection profilePageConnection = null;
        BufferedReader profileReader = null;
        String url = "http://195.88.209.17/app/in/user.php?action=get&email=";
        String result = "";
        String email = "";

        public MyProfileLoader(String email) {
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
                StringBuffer buffer = new StringBuffer();
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
                    likes.setText(item.getString("likes"));
                    followers.setText(item.getString("followers"));
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
                    if (!item.getString("makeupServices").equals("")) {
                        String[] makeupServicesArray = item.getString("makeupServices").substring(1, item.getString("makeupServices").length()).split(",");
                        String[] makeupCostsArray = item.getString("makeupCosts").substring(1, item.getString("makeupCosts").length()).split(",");
                        for (int j = 0; j < makeupServicesArray.length; j++) {
                            LinearLayout stroke = new LinearLayout(getApplicationContext());
                            LinearLayout strService = new LinearLayout(getApplicationContext());
                            LinearLayout strCost = new LinearLayout(getApplicationContext());
                            strService.setLayoutParams(new ViewGroup.LayoutParams(Storage.getInt("Width", 480) - Storage.getInt("Width", 480) / 3, ViewGroup.LayoutParams.WRAP_CONTENT));
                            stroke.setOrientation(LinearLayout.HORIZONTAL);
                            strService.setOrientation(LinearLayout.HORIZONTAL);
                            strCost.setOrientation(LinearLayout.HORIZONTAL);
                            strService.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                            strCost.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                            stroke.addView(strService);
                            stroke.addView(strCost);

                            TextView serviceTextView = new TextView(getApplicationContext());
                            serviceTextView.setTextColor(Color.parseColor("#808080"));
                            serviceTextView.setTextSize(16);
                            serviceTextView.setPadding(0, 8, 0, 8);
                            serviceTextView.setText(makeupServicesArray[j].toString());
                            strService.addView(serviceTextView);

                            TextView costsTextView = new TextView(getApplicationContext());
                            costsTextView.setTextColor(Color.parseColor("#404040"));
                            costsTextView.setTextSize(16);
                            costsTextView.setPadding(0, 8, 0, 8);
                            costsTextView.setText(makeupCostsArray[j].toString());
                            strCost.addView(costsTextView);
                            servicesMakeup.addView(stroke);
                        }
                    }
                    if (!item.getString("manicureServices").equals("")) {
                        String[] manicureServicesArray = item.getString("manicureServices").substring(1, item.getString("manicureServices").length()).split(",");
                        String[] manicureCostsArray = item.getString("manicureCosts").substring(1, item.getString("manicureCosts").length()).split(",");
                        for (int j = 0; j < manicureServicesArray.length; j++) {
                            FrameLayout stroke = new FrameLayout(getApplicationContext());
                            LinearLayout strService = new LinearLayout(getApplicationContext());
                            LinearLayout strCost = new LinearLayout(getApplicationContext());
                            strService.setOrientation(LinearLayout.HORIZONTAL);
                            strCost.setOrientation(LinearLayout.HORIZONTAL);
                            strService.setGravity(Gravity.LEFT);
                            strCost.setGravity(Gravity.RIGHT);
                            stroke.addView(strService);
                            stroke.addView(strCost);

                            TextView serviceTextView = new TextView(getApplicationContext());
                            serviceTextView.setTextColor(Color.parseColor("#808080"));
                            serviceTextView.setTextSize(16);
                            serviceTextView.setPadding(0, 8, 0, 8);
                            serviceTextView.setText(manicureServicesArray[j].toString());
                            strService.addView(serviceTextView);

                            TextView costsTextView = new TextView(getApplicationContext());
                            costsTextView.setTextColor(Color.parseColor("#404040"));
                            costsTextView.setTextSize(16);
                            costsTextView.setPadding(0, 8, 0, 8);
                            costsTextView.setText(manicureCostsArray[j].toString());
                            strCost.addView(costsTextView);
                            servicesManicure.addView(stroke);
                        }
                    }
                    if (!item.getString("hairstyleServices").equals("")) {
                        String[] hairstyleServicesArray = item.getString("hairstyleServices").substring(1, item.getString("hairstyleServices").length()).split(",");
                        String[] hairstyleCostsArray = item.getString("hairstyleCosts").substring(1, item.getString("hairstyleCosts").length()).split(",");
                        for (int j = 0; j < hairstyleServicesArray.length; j++) {
                            FrameLayout stroke = new FrameLayout(getApplicationContext());
                            LinearLayout strService = new LinearLayout(getApplicationContext());
                            LinearLayout strCost = new LinearLayout(getApplicationContext());
                            strService.setOrientation(LinearLayout.HORIZONTAL);
                            strCost.setOrientation(LinearLayout.HORIZONTAL);
                            strService.setGravity(Gravity.LEFT);
                            strCost.setGravity(Gravity.RIGHT);
                            stroke.addView(strService);
                            stroke.addView(strCost);

                            TextView serviceTextView = new TextView(getApplicationContext());
                            serviceTextView.setTextColor(Color.parseColor("#808080"));
                            serviceTextView.setTextSize(16);
                            serviceTextView.setPadding(0, 8, 0, 8);
                            serviceTextView.setText(hairstyleServicesArray[j].toString());
                            strService.addView(serviceTextView);

                            TextView costsTextView = new TextView(getApplicationContext());
                            costsTextView.setTextColor(Color.parseColor("#404040"));
                            costsTextView.setTextSize(16);
                            costsTextView.setPadding(0, 8, 0, 8);
                            costsTextView.setText(hairstyleCostsArray[j].toString());
                            strCost.addView(costsTextView);
                            servicesHairstyle.addView(stroke);
                        }
                    }
                    String ss = item.getString("photo");
                    Picasso.with(getApplicationContext()).load("http://195.88.209.17/storage/images/" + ss).into(photo);
                    progressDialog.hide();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}