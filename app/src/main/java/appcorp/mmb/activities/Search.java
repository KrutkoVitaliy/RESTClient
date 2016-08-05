package appcorp.mmb.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import appcorp.mmb.R;
import appcorp.mmb.activities.feeds.GlobalFeed;
import appcorp.mmb.activities.feeds.HairstyleFeed;
import appcorp.mmb.activities.feeds.LipsFeed;
import appcorp.mmb.activities.feeds.MakeupFeed;
import appcorp.mmb.activities.feeds.ManicureFeed;
import appcorp.mmb.activities.search_feeds.SearchFeed;
import appcorp.mmb.classes.Storage;

public class Search extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private String hashTag = "", request = "", eyeColor = "", difficult = "",
            photoURL = "", name = "";
    private String arrayColors = "";
    private EditText requestField, stylistCityRequest, stylistSkillRequest;
    private Button search, stylistSearch;
    private ImageView circlePink, circlePurple, circleBlue, circleTeal, circleGreen, circleYellow,
            circleOrange, circleRed, circleNeutral, circleCopper, circleBrown, circleHazel,
            circleGray, circleBlack, eyeBlueCircle, eyeGreenCircle, eyeHazelCircle, eyeBrownCircle,
            eyeGrayCircle, eyeBlackCircle;
    private ImageView easyDifficult, mediumDifficult, hardDifficult;
    private LinearLayout stylistFields, makeupCategory;
    private TextView selectLook, selectStylist;
    private ScrollView lookFields;
    private Spinner occasion, category;
    private ImageView c000000, c404040, cFF0000, cFF6A00, cFFD800, cB6FF00, c4CFF00, c00FF21, c00FF90,
            c00FFFF, c0094FF, c0026FF, c4800FF, cB200FF, cFF00DC, cFF006E, c808080, cFFFFFF, cF79F49,
            c8733DD, c62B922, cF9F58D, cA50909, c1D416F, cBCB693, c644949, cF9CBCB, cD6C880;
    private List<String> colors = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        name = Storage.getString("Name", "Make Me Beauty");
        photoURL = Storage.getString("PhotoURL", "" + R.mipmap.icon);

        initToolbar();
        initNavigationView();
        initViews();


        selectLook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectLook.setBackgroundColor(Color.WHITE);
                selectLook.setTextColor(Color.parseColor("#43A047"));
                selectStylist.setBackgroundColor(Color.parseColor("#43A047"));
                selectStylist.setTextColor(Color.WHITE);
                lookFields.setVisibility(View.VISIBLE);
                stylistFields.setVisibility(View.INVISIBLE);
            }
        });

        selectStylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectStylist.setBackgroundColor(Color.WHITE);
                selectStylist.setTextColor(Color.parseColor("#43A047"));
                selectLook.setBackgroundColor(Color.parseColor("#43A047"));
                selectLook.setTextColor(Color.WHITE);
                lookFields.setVisibility(View.INVISIBLE);
                stylistFields.setVisibility(View.VISIBLE);
            }
        });


        if (!getIntent().getStringExtra("hashTag").equals("empty")) {
            hashTag = getIntent().getStringExtra("hashTag");
            requestField.setText(hashTag);
        }

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (requestField.getText() == null || requestField.getText().equals(""))
                    request = "";
                else
                    request = requestField.getText().toString();
                for (int i = 0; i < colors.size(); i++) {
                    arrayColors += colors.get(i) + ",";
                }
                if (eyeColor == null || eyeColor.equals(""))
                    eyeColor = "";
                if (difficult == null || difficult.equals(""))
                    difficult = "";
                Intent intent = new Intent(getApplicationContext(), SearchFeed.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent
                        .putExtra("Category", "" + category.getSelectedItemPosition())
                        .putExtra("Request", request)
                        .putExtra("Colors", arrayColors)
                        .putExtra("EyeColor", eyeColor)
                        .putExtra("Difficult", difficult)
                        .putExtra("Occasion", "" + occasion.getSelectedItemPosition());
                getApplicationContext().startActivity(intent);
            }
        });

        /*stylistSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SearchStylistFeed.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent
                        .putExtra("City", stylistCityRequest.getText().toString())
                        .putExtra("Skill", stylistSkillRequest.getText().toString());
                getApplicationContext().startActivity(intent);
            }
        });*/

        initColors();
        initEyeColors();
        initDifficult();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void initViews() {
        occasion = (Spinner) findViewById(R.id.occasionField);
        category = (Spinner) findViewById(R.id.searchType);
        /*selectLook = (TextView) findViewById(R.id.searchLook);
        selectStylist = (TextView) findViewById(R.id.searchStylist);*/
        lookFields = (ScrollView) findViewById(R.id.lookFields);
        /*stylistFields = (LinearLayout) findViewById(R.id.stylistFields);
        stylistCityRequest = (EditText) findViewById(R.id.stylistCityRequestField);
        stylistSkillRequest = (EditText) findViewById(R.id.stylistSkillRequestField);*/
        requestField = (EditText) findViewById(R.id.requestField);
        //stylistSearch = (Button) findViewById(R.id.stylistSearch);
        search = (Button) findViewById(R.id.lookSearch);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.searchToolbar);
        toolbar.setTitle(R.string.toolbar_title_search);
    }

    private void initNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.searchDrawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_toggle_open, R.string.drawer_toggle_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) drawerLayout.findViewById(R.id.searchNavigation);

        ImageView navAvatar = (ImageView) findViewById(R.id.searchNavAvatar);
        if (photoURL != null && photoURL != "")
            Picasso.with(getApplicationContext()).load(photoURL).resize(300, 300).into(navAvatar);
        else
            navAvatar.setImageResource(R.mipmap.icon);

        navAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name != null)
                    startActivity(new Intent(getApplicationContext(), MyProfile.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                else
                    startActivity(new Intent(getApplicationContext(), Authorization.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        TextView navName = (TextView) findViewById(R.id.searchNavName);
        if (name != null && name != "")
            navName.setText(name + "");
        else
            navName.setText("Вы не авторизованы");

        TextView navLocation = (TextView) findViewById(R.id.searchNavLocation);
        if (navLocation.getText().equals("Click to sign in"))
            navLocation.setText("Click to change account");
        else if (navLocation.getText().equals("Click to change account"))
            navLocation.setText("Click to sign in");

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                drawerLayout.closeDrawers();
                switch (item.getItemId()) {
                    case R.id.navMenuGlobalFeed:
                        startActivity(new Intent(getApplicationContext(), GlobalFeed.class));
                        break;
                    case R.id.navMenuSearch:
                        startActivity(new Intent(getApplicationContext(), Search.class).putExtra("hashTag", "empty"));
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
                        if (!Storage.getString("Name", "Make Me Beauty").equals("Make Me Beauty"))
                            startActivity(new Intent(getApplicationContext(), MyProfile.class));
                        else
                            startActivity(new Intent(getApplicationContext(), Authorization.class));
                        break;
                    case R.id.navMenuFavorites:
                        if (!Storage.getString("Name", "Make Me Beauty").equals("Make Me Beauty"))
                            startActivity(new Intent(getApplicationContext(), Favorites.class));
                        else
                            startActivity(new Intent(getApplicationContext(), Authorization.class));
                        break;
                }
                return true;
            }
        });
    }

    private void initEyeColors() {
        eyeBlueCircle = (ImageView) findViewById(R.id.blueEyesCircle);
        eyeGreenCircle = (ImageView) findViewById(R.id.greenEyeCircle);
        eyeHazelCircle = (ImageView) findViewById(R.id.hazelEyeCircle);
        eyeBrownCircle = (ImageView) findViewById(R.id.brownEyeCircle);
        eyeGrayCircle = (ImageView) findViewById(R.id.grayEyeCircle);
        eyeBlackCircle = (ImageView) findViewById(R.id.blackEyeCircle);
        setEye(eyeBlueCircle, "blue");
        setEye(eyeGreenCircle, "green");
        setEye(eyeHazelCircle, "hazel");
        setEye(eyeBrownCircle, "brown");
        setEye(eyeGrayCircle, "gray");
        setEye(eyeBlackCircle, "black");
    }

    private void setEye(ImageView imageView, final String inEyeColor) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inEyeColor.equals("blue")) {
                    eyeBlueCircle.setAlpha(1.0F);
                    eyeGreenCircle.setAlpha(0.3F);
                    eyeHazelCircle.setAlpha(0.3F);
                    eyeBrownCircle.setAlpha(0.3F);
                    eyeGrayCircle.setAlpha(0.3F);
                    eyeBlackCircle.setAlpha(0.3F);
                    eyeColor = inEyeColor;
                } else if (inEyeColor.equals("green")) {
                    eyeBlueCircle.setAlpha(0.3F);
                    eyeGreenCircle.setAlpha(1.0F);
                    eyeHazelCircle.setAlpha(0.3F);
                    eyeBrownCircle.setAlpha(0.3F);
                    eyeGrayCircle.setAlpha(0.3F);
                    eyeBlackCircle.setAlpha(0.3F);
                    eyeColor = inEyeColor;
                } else if (inEyeColor.equals("hazel")) {
                    eyeBlueCircle.setAlpha(0.3F);
                    eyeGreenCircle.setAlpha(0.3F);
                    eyeHazelCircle.setAlpha(1.0F);
                    eyeBrownCircle.setAlpha(0.3F);
                    eyeGrayCircle.setAlpha(0.3F);
                    eyeBlackCircle.setAlpha(0.3F);
                    eyeColor = inEyeColor;
                } else if (inEyeColor.equals("brown")) {
                    eyeBlueCircle.setAlpha(0.3F);
                    eyeGreenCircle.setAlpha(0.3F);
                    eyeHazelCircle.setAlpha(0.3F);
                    eyeBrownCircle.setAlpha(1.0F);
                    eyeGrayCircle.setAlpha(0.3F);
                    eyeBlackCircle.setAlpha(0.3F);
                    eyeColor = inEyeColor;
                } else if (inEyeColor.equals("gray")) {
                    eyeBlueCircle.setAlpha(0.3F);
                    eyeGreenCircle.setAlpha(0.3F);
                    eyeHazelCircle.setAlpha(0.3F);
                    eyeBrownCircle.setAlpha(0.3F);
                    eyeGrayCircle.setAlpha(1.0F);
                    eyeBlackCircle.setAlpha(0.3F);
                    eyeColor = inEyeColor;
                } else if (inEyeColor.equals("black")) {
                    eyeBlueCircle.setAlpha(0.3F);
                    eyeGreenCircle.setAlpha(0.3F);
                    eyeHazelCircle.setAlpha(0.3F);
                    eyeBrownCircle.setAlpha(0.3F);
                    eyeGrayCircle.setAlpha(0.3F);
                    eyeBlackCircle.setAlpha(1.0F);
                    eyeColor = inEyeColor;
                }
            }
        });
    }

    private void initDifficult() {
        easyDifficult = (ImageView) findViewById(R.id.easyDifficult);
        mediumDifficult = (ImageView) findViewById(R.id.mediumDifficult);
        hardDifficult = (ImageView) findViewById(R.id.hardDifficult);
        setDifficult(easyDifficult, "easy");
        setDifficult(mediumDifficult, "medium");
        setDifficult(hardDifficult, "hard");
    }

    private void setDifficult(ImageView imageView, final String inDifficult) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inDifficult.equals("easy")) {
                    easyDifficult.setAlpha(1.0F);
                    mediumDifficult.setAlpha(0.3F);
                    hardDifficult.setAlpha(0.3F);
                    difficult = inDifficult;
                } else if (inDifficult.equals("medium")) {
                    easyDifficult.setAlpha(0.3F);
                    mediumDifficult.setAlpha(1.0F);
                    hardDifficult.setAlpha(0.3F);
                    difficult = inDifficult;
                } else if (inDifficult.equals("hard")) {
                    easyDifficult.setAlpha(0.3F);
                    mediumDifficult.setAlpha(0.3F);
                    hardDifficult.setAlpha(1.0F);
                    difficult = inDifficult;
                }
            }
        });
    }

    private void initColors() {
        c000000 = (ImageView) findViewById(R.id.c000000);
        c404040 = (ImageView) findViewById(R.id.c404040);
        cFF0000 = (ImageView) findViewById(R.id.cFF0000);
        cFF6A00 = (ImageView) findViewById(R.id.cFF6A00);
        cFFD800 = (ImageView) findViewById(R.id.cFFD800);
        cB6FF00 = (ImageView) findViewById(R.id.cB6FF00);
        c4CFF00 = (ImageView) findViewById(R.id.c4CFF00);
        c00FF21 = (ImageView) findViewById(R.id.c00FF21);
        c00FF90 = (ImageView) findViewById(R.id.c00FF90);
        c00FFFF = (ImageView) findViewById(R.id.c00FFFF);
        c0094FF = (ImageView) findViewById(R.id.c0094FF);
        c0026FF = (ImageView) findViewById(R.id.c0026FF);
        c4800FF = (ImageView) findViewById(R.id.c4800FF);
        cB200FF = (ImageView) findViewById(R.id.cB200FF);
        cFF00DC = (ImageView) findViewById(R.id.cFF00DC);
        cFF006E = (ImageView) findViewById(R.id.cFF006E);
        c808080 = (ImageView) findViewById(R.id.c808080);
        cFFFFFF = (ImageView) findViewById(R.id.cFFFFFF);
        cF79F49 = (ImageView) findViewById(R.id.cF79F49);
        c8733DD = (ImageView) findViewById(R.id.c8733DD);
        c62B922 = (ImageView) findViewById(R.id.c62B922);
        cF9F58D = (ImageView) findViewById(R.id.cF9F58D);
        cA50909 = (ImageView) findViewById(R.id.cA50909);
        c1D416F = (ImageView) findViewById(R.id.c1D416F);
        cBCB693 = (ImageView) findViewById(R.id.cBCB693);
        c644949 = (ImageView) findViewById(R.id.c644949);
        cF9CBCB = (ImageView) findViewById(R.id.cF9CBCB);
        cD6C880 = (ImageView) findViewById(R.id.cD6C880);

        setListener(c000000, "000000");
        setListener(c404040, "404040");
        setListener(cFF0000, "FF0000");
        setListener(cFF6A00, "FF6A00");
        setListener(cFFD800, "FFD800");
        setListener(cB6FF00, "B6FF00");
        setListener(c4CFF00, "4CFF00");
        setListener(c00FF21, "00FF21");
        setListener(c00FF90, "00FF90");
        setListener(c00FFFF, "00FFFF");
        setListener(c0094FF, "0094FF");
        setListener(c0026FF, "0026FF");
        setListener(c4800FF, "4800FF");
        setListener(cB200FF, "B200FF");
        setListener(cFF00DC, "FF00DC");
        setListener(cFF006E, "FF006E");
        setListener(c808080, "808080");
        setListener(cFFFFFF, "FFFFFF");
        setListener(cF79F49, "F79F49");
        setListener(c8733DD, "8733DD");
        setListener(c62B922, "62B922");
        setListener(cF9F58D, "F9F58D");
        setListener(cA50909, "A50909");
        setListener(c1D416F, "1D416F");
        setListener(cBCB693, "BCB693");
        setListener(c644949, "644949");
        setListener(cF9CBCB, "F9CBCB");
        setListener(cD6C880, "D6C880");
    }

    private void setListener(final ImageView imageView, final String color) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageView.getAlpha() == 1F) {
                    imageView.setAlpha(0.3F);
                    if (!colors.contains(color))
                        colors.add(color);
                } else if (imageView.getAlpha() == 0.3F) {
                    imageView.setAlpha(1F);
                    if (colors.contains(color))
                        colors.remove(color);
                }
            }
        });
    }
}