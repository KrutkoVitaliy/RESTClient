package appcorp.mmb.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import appcorp.mmb.R;
import appcorp.mmb.loaders.Storage;

public class Search extends Activity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private String hashTag = "", colors = "", request = "", eyeColor = "", difficult = "",
            photoURL = "", name = "";
    private EditText requestField, stylistCityRequest, stylistSkillRequest;
    private Button search, stylistSearch;
    private ImageView circlePink, circlePurple, circleBlue, circleTeal, circleGreen, circleYellow,
            circleOrange, circleRed, circleNeutral, circleCopper, circleBrown, circleHazel,
            circleGray, circleBlack, eyeBlueCircle, eyeGreenCircle, eyeHazelCircle, eyeBrownCircle,
            eyeGrayCircle, eyeBlackCircle;
    private ImageView easyDifficult, mediumDifficult, hardDifficult;
    private LinearLayout stylistFields;
    private TextView selectLook, selectStylist;
    private ScrollView lookFields;
    private Spinner occasion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Storage.Init(getApplicationContext());

        if (Storage.Get("Autentification").equals("Success")) {
            name = Storage.Get("Name");
            photoURL = Storage.Get("PhotoURL");
        }
        initToolbar();
        initNavigationView();
        initViews();


/*        selectLook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectLook.setBackgroundColor(Color.WHITE);
                selectLook.setTextColor(Color.parseColor("#43A047"));
                selectStylist.setBackgroundColor(Color.parseColor("#43A047"));
                selectStylist.setTextColor(Color.WHITE);
                lookFields.setVisibility(View.VISIBLE);
                stylistFields.setVisibility(View.INVISIBLE);
            }
        });*/

        /*selectStylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectStylist.setBackgroundColor(Color.WHITE);
                selectStylist.setTextColor(Color.parseColor("#43A047"));
                selectLook.setBackgroundColor(Color.parseColor("#43A047"));
                selectLook.setTextColor(Color.WHITE);
                lookFields.setVisibility(View.INVISIBLE);
                stylistFields.setVisibility(View.VISIBLE);
            }
        });*/


        if (!getIntent().getStringExtra("hashTag").equals("empty")) {
            hashTag = getIntent().getStringExtra("hashTag");
            requestField.setText(hashTag);
        }

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tempOccasion = "";
                if (requestField.getText() == null || requestField.getText().equals(""))
                    request = "";
                else
                    request = requestField.getText().toString();
                if (colors == null || colors.equals(""))
                    colors = "";
                if (eyeColor == null || eyeColor.equals(""))
                    eyeColor = "";
                if (difficult == null || difficult.equals(""))
                    difficult = "";
                if (occasion.getSelectedItem().toString() == "empty" || occasion.getSelectedItem().toString().equals(""))
                    tempOccasion = "";
                Intent intent = new Intent(getApplicationContext(), SearchFeed.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent
                        .putExtra("Request", request)
                        .putExtra("Colors", colors)
                        .putExtra("EyeColor", eyeColor)
                        .putExtra("Difficult", difficult)
                        .putExtra("Occasion", tempOccasion);
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

    private void initViews() {
        occasion = (Spinner) findViewById(R.id.occasionField);
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
                    startActivity(new Intent(getApplicationContext(), Profile.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .putExtra("Name", name)
                            .putExtra("PhotoURL", photoURL)
                            .putExtra("From", "NavHeader"));
                else
                    startActivity(new Intent(getApplicationContext(), Introduction.class)
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
                        startActivity(new Intent(getApplicationContext(), GlobalFeed.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        break;
                    case R.id.navMenuSearch:
                        startActivity(new Intent(getApplicationContext(), Search.class).putExtra("hashTag", "empty"));
                        break;
                    case R.id.navMenuProfile:
                        if (name != null)
                            startActivity(new Intent(getApplicationContext(), MyProfile.class)

                                    .putExtra("Name", name)
                                    .putExtra("PhotoURL", photoURL)
                                    .putExtra("From", "NavMenu"));
                        else
                            startActivity(new Intent(getApplicationContext(), Introduction.class)
                                    );
                        break;
                    case R.id.navMenuFavorites:
                        startActivity(new Intent(getApplicationContext(), Favorites.class));
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
        circlePink = (ImageView) findViewById(R.id.pinkCircle);
        circlePurple = (ImageView) findViewById(R.id.purpleCircle);
        circleBlue = (ImageView) findViewById(R.id.blueCircle);
        circleTeal = (ImageView) findViewById(R.id.tealCircle);
        circleGreen = (ImageView) findViewById(R.id.greenCircle);
        circleYellow = (ImageView) findViewById(R.id.yellowCircle);
        circleOrange = (ImageView) findViewById(R.id.orangeCircle);
        circleRed = (ImageView) findViewById(R.id.redCircle);
        circleNeutral = (ImageView) findViewById(R.id.neutralCircle);
        circleCopper = (ImageView) findViewById(R.id.copperCircle);
        circleBrown = (ImageView) findViewById(R.id.brownCircle);
        circleHazel = (ImageView) findViewById(R.id.hazelCircle);
        circleGray = (ImageView) findViewById(R.id.grayCircle);
        circleBlack = (ImageView) findViewById(R.id.blackCircle);

        setListener(circlePink, "Pink");
        setListener(circlePurple, "Purple");
        setListener(circleBlue, "Blue");
        setListener(circleTeal, "Teal");
        setListener(circleGreen, "Green");
        setListener(circleYellow, "Yellow");
        setListener(circleOrange, "Orange");
        setListener(circleRed, "Red");
        setListener(circleNeutral, "Neutral");
        setListener(circleCopper, "Copper");
        setListener(circleBrown, "Brown");
        setListener(circleHazel, "Hazel");
        setListener(circleGray, "Gray");
        setListener(circleBlack, "Black");
    }

    private void setListener(final ImageView imageView, final String color) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageView.getAlpha() == 1F) {
                    imageView.setAlpha(0.3F);
                    if (!colors.contains(color))
                        colors += color + ",";
                } else if (imageView.getAlpha() == 0.3F) {
                    imageView.setAlpha(1F);
                    if (colors.contains(color + ","))
                        colors = colors.replace(color + ",", "");
                }
            }
        });
    }
}