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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import appcorp.mmb.R;
import appcorp.mmb.activities.feeds.HairstyleFeed;
import appcorp.mmb.activities.feeds.MakeupFeed;
import appcorp.mmb.activities.feeds.ManicureFeed;
import appcorp.mmb.activities.search_feeds.Search;
import appcorp.mmb.activities.search_feeds.SearchStylist;
import appcorp.mmb.classes.Storage;

public class Profile extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private TextView name, location, phone, likes, followers;
    private TextView serviceMakeupText, serviceManicureText, serviceHairstyleText;
    private LinearLayout serviceMakeup, serviceManicure, serviceHairstyle;
    private ImageView gplusButton, fbButton, vkButton, instagramButton, okButton;
    private ImageView photo;
    private String gplusLink = "", fbLink = "", vkLink = "", instagramLink = "", okLink = "";
    private ScrollView makeupUserGallery, manicureUserGallery, hairstyleUserGallery;
    private LinearLayout servicesMakeup;
    private LinearLayout servicesManicure;
    private LinearLayout servicesHairstyle;
    private ProgressDialog progressDialog;
    private int id;

    public Profile() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initToolbar();
        initNavigationView();
        initViews();

        id = new Integer(getIntent().getStringExtra("ID"));
        changeServiceStatus(1);
        new Get().execute();
    }

    private void initViews() {
        name = (TextView) findViewById(R.id.name);
        location = (TextView) findViewById(R.id.location);
        phone = (TextView) findViewById(R.id.phone);
        photo = (ImageView) findViewById(R.id.profileAvatar);
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
        phone.setOnClickListener(this);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.profileToolbar);
    }

    private void initNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.profileDrawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_toggle_open, R.string.drawer_toggle_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) drawerLayout.findViewById(R.id.profileNavigation);
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
                    startActivity(new Intent(getApplicationContext(), MyProfile.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                } else {
                    startActivity(new Intent(getApplicationContext(), Authorization.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
                }
            }
        });
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

    private void createThreeButtonsAlertDialog(String title, final String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
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

    class Get extends AsyncTask<Void, Void, String> {
        HttpURLConnection profileConnection = null;
        BufferedReader profileReader = null;
        String resultJsonProfile = "";

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL profileURL = new URL("http://195.88.209.17/app/out/stylist.php?id=" + id);
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
            s = s.replace("[", "");
            s = s.replace("]", "");

            try {
                JSONObject profileItem = new JSONObject(s);
                toolbar.setTitle(profileItem.getString("firstName") + " " + profileItem.getString("lastName"));
                Picasso.with(getApplicationContext()).load("http://195.88.209.17/storage/images/" + profileItem.getString("photo")).into(photo);
                name.setText(profileItem.getString("firstName") + " " + profileItem.getString("lastName"));
                location.setText(profileItem.getString("city") + ", " + profileItem.getString("address"));
                phone.setText(profileItem.getString("phoneNumber"));
                if (profileItem.getString("gplus").equals(""))
                    gplusButton.setAlpha(0.4F);
                if (profileItem.getString("facebook").equals(""))
                    fbButton.setAlpha(0.4F);
                if (profileItem.getString("vkontakte").equals(""))
                    vkButton.setAlpha(0.4F);
                if (profileItem.getString("instagram").equals(""))
                    instagramButton.setAlpha(0.4F);
                if (profileItem.getString("odnoklassniki").equals(""))
                    okButton.setAlpha(0.4F);

                gplusLink = profileItem.getString("gplus");
                fbLink = profileItem.getString("facebook");
                vkLink = profileItem.getString("vkontakte");
                instagramLink = profileItem.getString("instagram");
                okLink = profileItem.getString("odnoklassniki");

                if (!profileItem.getString("makeupServices").equals("")) {
                    String[] makeupServicesArray = profileItem.getString("makeupServices").substring(1, profileItem.getString("makeupServices").length()).split(",");
                    String[] makeupCostsArray = profileItem.getString("makeupCosts").substring(1, profileItem.getString("makeupCosts").length()).split(",");
                    for (int j = 0; j < makeupServicesArray.length; j++) {
                        LinearLayout stroke = new LinearLayout(getApplicationContext());
                        LinearLayout strService = new LinearLayout(getApplicationContext());
                        LinearLayout strCost = new LinearLayout(getApplicationContext());
                        strService.setLayoutParams(new ViewGroup.LayoutParams(Storage.getInt("Width", 480) - (int)(Storage.getInt("Width", 480) / 3.5F), ViewGroup.LayoutParams.WRAP_CONTENT));
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
                if (!profileItem.getString("manicureServices").equals("")) {
                    String[] manicureServicesArray = profileItem.getString("manicureServices").substring(1, profileItem.getString("manicureServices").length()).split(",");
                    String[] manicureCostsArray = profileItem.getString("manicureCosts").substring(1, profileItem.getString("manicureCosts").length()).split(",");
                    for (int j = 0; j < manicureServicesArray.length; j++) {
                        LinearLayout stroke = new LinearLayout(getApplicationContext());
                        LinearLayout strService = new LinearLayout(getApplicationContext());
                        LinearLayout strCost = new LinearLayout(getApplicationContext());
                        strService.setLayoutParams(new ViewGroup.LayoutParams(Storage.getInt("Width", 480) - (int)(Storage.getInt("Width", 480) / 3.5F), ViewGroup.LayoutParams.WRAP_CONTENT));
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
                if (!profileItem.getString("hairstyleServices").equals("")) {
                    String[] hairstyleServicesArray = profileItem.getString("hairstyleServices").substring(1, profileItem.getString("hairstyleServices").length()).split(",");
                    String[] hairstyleCostsArray = profileItem.getString("hairstyleCosts").substring(1, profileItem.getString("hairstyleCosts").length()).split(",");
                    for (int j = 0; j < hairstyleServicesArray.length; j++) {
                        LinearLayout stroke = new LinearLayout(getApplicationContext());
                        LinearLayout strService = new LinearLayout(getApplicationContext());
                        LinearLayout strCost = new LinearLayout(getApplicationContext());
                        strService.setLayoutParams(new ViewGroup.LayoutParams(Storage.getInt("Width", 480) - (int)(Storage.getInt("Width", 480) / 3.5F), ViewGroup.LayoutParams.WRAP_CONTENT));
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

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}