package appcorp.mmb.activities.search_feeds;

import android.content.Intent;
import android.os.Bundle;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import appcorp.mmb.R;
import appcorp.mmb.activities.feeds.GlobalFeed;
import appcorp.mmb.activities.feeds.HairstyleFeed;
import appcorp.mmb.activities.feeds.MakeupFeed;
import appcorp.mmb.activities.feeds.ManicureFeed;
import appcorp.mmb.activities.user.FavoriteVideos;
import appcorp.mmb.activities.user.Favorites;
import appcorp.mmb.activities.user.MyProfile;
import appcorp.mmb.activities.user.SignIn;
import appcorp.mmb.classes.FireAnal;
import appcorp.mmb.classes.Storage;

public class Search extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private String eyeColor = "", difficult = "";
    private String category = "";
    private EditText requestField;
    private Spinner hairstyleLength, hairstyleType, hairstyleFor;
    private ImageView eyeBlueCircle, eyeGreenCircle, eyeHazelCircle, eyeBrownCircle, eyeGrayCircle, eyeBlackCircle;
    private LinearLayout easyDifficult, mediumDifficult, hardDifficult;
    private Spinner occasion, shape, design;
    private ArrayList<String> colors = new ArrayList<>();
    private ArrayList<String> manicureColors = new ArrayList<>();
    private LinearLayout makeupFrame;
    private LinearLayout hairstyleFrame;
    private LinearLayout manicureFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        FireAnal.sendString("Search", "Open", "Activity");

        initToolbar();
        initNavigationView();
        initViews();
        checkLocation();
        initCategorySelector();
        initMakeupColors();
        initManicureColors();
        initEyeColors();
        initDifficult();
    }

    private void initFirebase() {
        FireAnal.setContext(getApplicationContext());
    }

    public void initCategorySelector() {
        LinearLayout catMakeup = (LinearLayout) findViewById(R.id.catMakeup);
        LinearLayout catManicure = (LinearLayout) findViewById(R.id.catManicure);
        LinearLayout catHairstyle = (LinearLayout) findViewById(R.id.catHairstyle);

        catMakeup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = "makeup";
                setUI("makeup");
            }
        });
        catHairstyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = "hairstyle";
                setUI("hairstyle");
            }
        });
        catManicure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = "manicure";
                setUI("manicure");
            }
        });
    }

    public void setUI(String currentUI) {
        switch (currentUI) {
            case "makeup":
                findViewById(R.id.catMakeup).setAlpha(1.0F);
                findViewById(R.id.catHairstyle).setAlpha(0.2F);
                findViewById(R.id.catManicure).setAlpha(0.2F);
                makeupFrame.setVisibility(View.VISIBLE);
                hairstyleFrame.setVisibility(View.INVISIBLE);
                manicureFrame.setVisibility(View.INVISIBLE);
                break;
            case "hairstyle":
                findViewById(R.id.catMakeup).setAlpha(0.2F);
                findViewById(R.id.catHairstyle).setAlpha(1.0F);
                findViewById(R.id.catManicure).setAlpha(0.2F);
                makeupFrame.setVisibility(View.INVISIBLE);
                hairstyleFrame.setVisibility(View.VISIBLE);
                manicureFrame.setVisibility(View.INVISIBLE);
                break;
            case "manicure":
                findViewById(R.id.catMakeup).setAlpha(0.2F);
                findViewById(R.id.catHairstyle).setAlpha(0.2F);
                findViewById(R.id.catManicure).setAlpha(1.0F);
                makeupFrame.setVisibility(View.INVISIBLE);
                hairstyleFrame.setVisibility(View.INVISIBLE);
                manicureFrame.setVisibility(View.VISIBLE);
                break;
            case "":
                findViewById(R.id.catMakeup).setAlpha(1.0F);
                findViewById(R.id.catHairstyle).setAlpha(1.0F);
                findViewById(R.id.catManicure).setAlpha(1.0F);
                makeupFrame.setVisibility(View.INVISIBLE);
                hairstyleFrame.setVisibility(View.INVISIBLE);
                manicureFrame.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void checkLocation() {
        if (getIntent().getStringExtra("from") != null)
            switch (String.valueOf(getIntent().getStringExtra("from"))) {
                case "hairstyleFeed":
                    category = "hairstyle";
                    setUI("hairstyle");
                    break;
                case "lipsFeed":
                    category = "lips";
                    setUI("lips");
                    break;
                case "makeupFeed":
                    category = "makeup";
                    setUI("makeup");
                    break;
                case "manicureFeed":
                    category = "manicure";
                    setUI("manicure");
                    break;
            }
        else {
            category = "makeup";
            setUI("makeup");
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void initViews() {
        occasion = (Spinner) findViewById(R.id.occasionField);
        shape = (Spinner) findViewById(R.id.shape);
        design = (Spinner) findViewById(R.id.design);
        requestField = (EditText) findViewById(R.id.requestField);
        hairstyleLength = (Spinner) findViewById(R.id.hairstyleLength);
        hairstyleType = (Spinner) findViewById(R.id.hairstyleType);
        hairstyleFor = (Spinner) findViewById(R.id.hairstyleFor);
        makeupFrame = (LinearLayout) findViewById(R.id.makeupFrame);
        hairstyleFrame = (LinearLayout) findViewById(R.id.hairstyleFrame);
        manicureFrame = (LinearLayout) findViewById(R.id.manicureFrame);
        Button searchButton = (Button) findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (category) {
                    case "makeup":
                        startActivity(new Intent(getApplicationContext(), Search.class)
                                .putExtra("from", "makeupFeed")
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        startActivity(new Intent(getApplicationContext(), SearchMakeupMatrix.class)
                                .putExtra("Toolbar", "makeup")
                                .putExtra("Category", "makeup")
                                .putExtra("Request", requestField.getText().toString())
                                .putStringArrayListExtra("Colors", sortMakeupColors(colors))
                                .putExtra("EyeColor", eyeColor)
                                .putExtra("Difficult", difficult)
                                .putExtra("Occasion", "" + occasion.getSelectedItemPosition()));
                        FireAnal.sendString("2", "SearchMakeupRequest", requestField.getText().toString());
                        FireAnal.sendString("2", "SearchMakeupEyeColor", eyeColor);
                        FireAnal.sendString("2", "SearchMakeupDifficult", difficult);
                        FireAnal.sendString("2", "SearchMakeupOccasion", "" + occasion.getSelectedItemPosition());
                        break;
                    case "hairstyle":
                        String[] hairstylesForArray = getResources().getStringArray(R.array.hairstyleFor);
                        startActivity(new Intent(getApplicationContext(), Search.class)
                                .putExtra("from", "hairstyleFeed")
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        startActivity(new Intent(getApplicationContext(), SearchHairstyleMatrix.class)
                                .putExtra("Toolbar", "" + hairstylesForArray[hairstyleFor.getSelectedItemPosition()])
                                .putExtra("Category", "hairstyle")
                                .putExtra("Request", requestField.getText().toString())
                                .putExtra("HairstyleLength", "" + hairstyleLength.getSelectedItemPosition())
                                .putExtra("HairstyleType", "" + hairstyleType.getSelectedItemPosition())
                                .putExtra("HairstyleFor", "" + hairstyleFor.getSelectedItemPosition()));
                        FireAnal.sendString("2", "SearchHairstyleRequest", requestField.getText().toString());
                        FireAnal.sendString("2", "SearchHairstyleLength", "" + hairstyleLength.getSelectedItemPosition());
                        FireAnal.sendString("2", "SearchHairstyleType", "" + hairstyleType.getSelectedItemPosition());
                        FireAnal.sendString("2", "SearchHairstyleFor", "" + hairstyleFor.getSelectedItemPosition());
                        break;
                    case "manicure":
                        startActivity(new Intent(getApplicationContext(), Search.class)
                                .putExtra("from", "manicureFeed")
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        startActivity(new Intent(getApplicationContext(), SearchManicureMatrix.class)
                                .putExtra("Toolbar", "manicure")
                                .putExtra("Category", "manicure")
                                .putExtra("Request", requestField.getText().toString())
                                .putStringArrayListExtra("ManicureColors", sortManicureColors(manicureColors))
                                .putExtra("Shape", "" + shape.getSelectedItemPosition())
                                .putExtra("Design", "" + design.getSelectedItemPosition()));
                        FireAnal.sendString("2", "SearchManicureRequest", requestField.getText().toString());
                        FireAnal.sendString("2", "SearchManicureShape", "" + shape.getSelectedItemPosition());
                        FireAnal.sendString("2", "SearchManicureDesign", "" + design.getSelectedItemPosition());
                        break;
                    /*startActivity(new Intent(getApplicationContext(), SearchManicureFeed.class)
                            .putExtra("Toolbar", "manicure")
                            .putExtra("Category", "manicure")
                            .putExtra("Request", requestField.getText().toString())
                            .putStringArrayListExtra("ManicureColors", sortManicureColors(manicureColors))
                            .putExtra("Shape", "" + shape.getSelectedItemPosition())
                            .putExtra("Design", "" + design.getSelectedItemPosition())
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    FireAnal.sendString("2", "SearchManicureRequest", requestField.getText().toString());
                    FireAnal.sendString("2", "SearchManicureShape", ""+shape.getSelectedItemPosition());
                    FireAnal.sendString("2", "SearchManicureDesign", ""+design.getSelectedItemPosition());
                    break;*/
                }
            }
        });
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.searchToolbar);
        toolbar.setTitle(R.string.toolbar_title_search);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (category) {
                    case "makeup":
                        startActivity(new Intent(getApplicationContext(), Search.class)
                                .putExtra("from", "makeupFeed")
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        startActivity(new Intent(getApplicationContext(), SearchMakeupMatrix.class)
                                .putExtra("Toolbar", "makeup")
                                .putExtra("Category", "makeup")
                                .putExtra("Request", requestField.getText().toString())
                                .putStringArrayListExtra("Colors", sortMakeupColors(colors))
                                .putExtra("EyeColor", eyeColor)
                                .putExtra("Difficult", difficult)
                                .putExtra("Occasion", "" + occasion.getSelectedItemPosition()));
                        break;
                    case "hairstyle":
                        String[] hairstylesForArray = getResources().getStringArray(R.array.hairstyleFor);
                        startActivity(new Intent(getApplicationContext(), Search.class)
                                .putExtra("from", "hairstyleFeed")
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        startActivity(new Intent(getApplicationContext(), SearchHairstyleMatrix.class)
                                .putExtra("Toolbar", "" + hairstylesForArray[hairstyleFor.getSelectedItemPosition()])
                                .putExtra("Category", "hairstyle")
                                .putExtra("Request", requestField.getText().toString())
                                .putExtra("HairstyleLength", "" + hairstyleLength.getSelectedItemPosition())
                                .putExtra("HairstyleType", "" + hairstyleType.getSelectedItemPosition())
                                .putExtra("HairstyleFor", "" + hairstyleFor.getSelectedItemPosition()));
                        break;
                    case "manicure":
                        startActivity(new Intent(getApplicationContext(), Search.class)
                                .putExtra("from", "manicureFeed")
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        startActivity(new Intent(getApplicationContext(), SearchManicureMatrix.class)
                                .putExtra("Toolbar", "manicure")
                                .putExtra("Category", "manicure")
                                .putExtra("Request", requestField.getText().toString())
                                .putStringArrayListExtra("ManicureColors", sortManicureColors(manicureColors))
                                .putExtra("Shape", "" + shape.getSelectedItemPosition())
                                .putExtra("Design", "" + design.getSelectedItemPosition()));
                        break;
                }
                return true;
            }
        });
        toolbar.inflateMenu(R.menu.menu_search);
    }

    private ArrayList<String> sortMakeupColors(ArrayList<String> colors) {
        ArrayList<String> sortedColors = new ArrayList<>();
        String[] colorsCodes = new String[]{
                "BB125B",
                "9210AE",
                "117DAE",
                "3B9670",
                "79BD14",
                "D4B515",
                "D46915",
                "D42415",
                "D2AF7F",
                "B48F58",
                "604E36",
                "555555",
                "000000"
        };
        for (String colorsCode : colorsCodes) {
            for (int j = 0; j < colors.size(); j++) {
                if (colorsCode.equals(colors.get(j)))
                    sortedColors.add(colorsCode);
            }
        }
        return sortedColors;
    }

    private ArrayList<String> sortManicureColors(ArrayList<String> colors) {
        ArrayList<String> sortedColors = new ArrayList<>();
        String[] colorsCodes = new String[]{
                "000000",
                "404040",
                "FF0000",
                "FF6A00",
                "FFD800",
                "B6FF00",
                "4CFF00",
                "00FF21",
                "00FF90",
                "00FFFF",
                "0094FF",
                "0026FF",
                "4800FF",
                "B200FF",
                "FF00DC",
                "FF006E",
                "808080",
                "FFFFFF",
                "F79F49",
                "8733DD",
                "62B922",
                "F9F58D",
                "A50909",
                "1D416F",
                "BCB693",
                "644949",
                "F9CBCB",
                "D6C880"
        };
        for (String colorsCode : colorsCodes) {
            for (int j = 0; j < colors.size(); j++) {
                if (colorsCode.equals(colors.get(j)))
                    sortedColors.add(colorsCode);
            }
        }
        return sortedColors;
    }

    private void initNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.searchDrawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_toggle_open, R.string.drawer_toggle_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) drawerLayout.findViewById(R.id.searchNavigation);
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

        /*menuHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Storage.getString("E-mail", "").equals("")) {
                    startActivity(new Intent(getApplicationContext(), MyProfile.class));
                } else {
                    startActivity(new Intent(getApplicationContext(), SignIn.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
                }
            }
        });*/
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
                switch (inEyeColor) {
                    case "blue":
                        eyeBlueCircle.setAlpha(1.0F);
                        eyeGreenCircle.setAlpha(0.3F);
                        eyeHazelCircle.setAlpha(0.3F);
                        eyeBrownCircle.setAlpha(0.3F);
                        eyeGrayCircle.setAlpha(0.3F);
                        eyeBlackCircle.setAlpha(0.3F);
                        eyeColor = inEyeColor;
                        break;
                    case "green":
                        eyeBlueCircle.setAlpha(0.3F);
                        eyeGreenCircle.setAlpha(1.0F);
                        eyeHazelCircle.setAlpha(0.3F);
                        eyeBrownCircle.setAlpha(0.3F);
                        eyeGrayCircle.setAlpha(0.3F);
                        eyeBlackCircle.setAlpha(0.3F);
                        eyeColor = inEyeColor;
                        break;
                    case "hazel":
                        eyeBlueCircle.setAlpha(0.3F);
                        eyeGreenCircle.setAlpha(0.3F);
                        eyeHazelCircle.setAlpha(1.0F);
                        eyeBrownCircle.setAlpha(0.3F);
                        eyeGrayCircle.setAlpha(0.3F);
                        eyeBlackCircle.setAlpha(0.3F);
                        eyeColor = inEyeColor;
                        break;
                    case "brown":
                        eyeBlueCircle.setAlpha(0.3F);
                        eyeGreenCircle.setAlpha(0.3F);
                        eyeHazelCircle.setAlpha(0.3F);
                        eyeBrownCircle.setAlpha(1.0F);
                        eyeGrayCircle.setAlpha(0.3F);
                        eyeBlackCircle.setAlpha(0.3F);
                        eyeColor = inEyeColor;
                        break;
                    case "gray":
                        eyeBlueCircle.setAlpha(0.3F);
                        eyeGreenCircle.setAlpha(0.3F);
                        eyeHazelCircle.setAlpha(0.3F);
                        eyeBrownCircle.setAlpha(0.3F);
                        eyeGrayCircle.setAlpha(1.0F);
                        eyeBlackCircle.setAlpha(0.3F);
                        eyeColor = inEyeColor;
                        break;
                    case "black":
                        eyeBlueCircle.setAlpha(0.3F);
                        eyeGreenCircle.setAlpha(0.3F);
                        eyeHazelCircle.setAlpha(0.3F);
                        eyeBrownCircle.setAlpha(0.3F);
                        eyeGrayCircle.setAlpha(0.3F);
                        eyeBlackCircle.setAlpha(1.0F);
                        eyeColor = inEyeColor;
                        break;
                }
            }
        });
    }

    private void initDifficult() {
        easyDifficult = (LinearLayout) findViewById(R.id.easyDifficult);
        mediumDifficult = (LinearLayout) findViewById(R.id.mediumDifficult);
        hardDifficult = (LinearLayout) findViewById(R.id.hardDifficult);
        setDifficult(easyDifficult, "easy");
        setDifficult(mediumDifficult, "medium");
        setDifficult(hardDifficult, "hard");
    }

    private void setDifficult(LinearLayout linearLayout, final String inDifficult) {
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (inDifficult) {
                    case "easy":
                        easyDifficult.setAlpha(1.0F);
                        mediumDifficult.setAlpha(0.3F);
                        hardDifficult.setAlpha(0.3F);
                        difficult = inDifficult;
                        break;
                    case "medium":
                        easyDifficult.setAlpha(0.3F);
                        mediumDifficult.setAlpha(1.0F);
                        hardDifficult.setAlpha(0.3F);
                        difficult = inDifficult;
                        break;
                    case "hard":
                        easyDifficult.setAlpha(0.3F);
                        mediumDifficult.setAlpha(0.3F);
                        hardDifficult.setAlpha(1.0F);
                        difficult = inDifficult;
                        break;
                }
            }
        });
    }

    private void initMakeupColors() {
        ImageView cBB125B = (ImageView) findViewById(R.id.cBB125B);
        ImageView c9210AE = (ImageView) findViewById(R.id.c9210AE);
        ImageView c117DAE = (ImageView) findViewById(R.id.c117DAE);
        ImageView c3B9670 = (ImageView) findViewById(R.id.c3B9670);
        ImageView c79BD14 = (ImageView) findViewById(R.id.c79BD14);
        ImageView cD4B515 = (ImageView) findViewById(R.id.cD4B515);
        ImageView cD46915 = (ImageView) findViewById(R.id.cD46915);
        ImageView cD42415 = (ImageView) findViewById(R.id.cD42415);
        ImageView cD2AF7F = (ImageView) findViewById(R.id.cD2AF7F);
        ImageView cB48F58 = (ImageView) findViewById(R.id.cB48F58);
        ImageView c604E36 = (ImageView) findViewById(R.id.c604E36);
        ImageView c555555 = (ImageView) findViewById(R.id.c555555);
        ImageView c000000 = (ImageView) findViewById(R.id.c000000);

        setListener(cBB125B, "BB125B");
        setListener(c9210AE, "9210AE");
        setListener(c117DAE, "117DAE");
        setListener(c3B9670, "3B9670");
        setListener(c79BD14, "79BD14");
        setListener(cD4B515, "D4B515");
        setListener(cD46915, "D46915");
        setListener(cD42415, "D42415");
        setListener(cD2AF7F, "D2AF7F");
        setListener(cB48F58, "B48F58");
        setListener(c604E36, "604E36");
        setListener(c555555, "555555");
        setListener(c000000, "000000");
    }

    private void initManicureColors() {
        ImageView mc000000 = (ImageView) findViewById(R.id.mc000000);
        ImageView mc404040 = (ImageView) findViewById(R.id.mc404040);
        ImageView mcFF0000 = (ImageView) findViewById(R.id.mcFF0000);
        ImageView mcFF6A00 = (ImageView) findViewById(R.id.mcFF6A00);
        ImageView mcFFD800 = (ImageView) findViewById(R.id.mcFFD800);
        ImageView mcB6FF00 = (ImageView) findViewById(R.id.mcB6FF00);
        ImageView mc4CFF00 = (ImageView) findViewById(R.id.mc4CFF00);
        ImageView mc00FF21 = (ImageView) findViewById(R.id.mc00FF21);
        ImageView mc00FF90 = (ImageView) findViewById(R.id.mc00FF90);
        ImageView mc00FFFF = (ImageView) findViewById(R.id.mc00FFFF);
        ImageView mc0094FF = (ImageView) findViewById(R.id.mc0094FF);
        ImageView mc0026FF = (ImageView) findViewById(R.id.mc0026FF);
        ImageView mc4800FF = (ImageView) findViewById(R.id.mc4800FF);
        ImageView mcB200FF = (ImageView) findViewById(R.id.mcB200FF);
        ImageView mcFF00DC = (ImageView) findViewById(R.id.mcFF00DC);
        ImageView mcFF006E = (ImageView) findViewById(R.id.mcFF006E);
        ImageView mc808080 = (ImageView) findViewById(R.id.mc808080);
        ImageView mcFFFFFF = (ImageView) findViewById(R.id.mcFFFFFF);
        ImageView mcF79F49 = (ImageView) findViewById(R.id.mcF79F49);
        ImageView mc8733DD = (ImageView) findViewById(R.id.mc8733DD);
        ImageView mc62B922 = (ImageView) findViewById(R.id.mc62B922);
        ImageView mcF9F58D = (ImageView) findViewById(R.id.mcF9F58D);
        ImageView mcA50909 = (ImageView) findViewById(R.id.mcA50909);
        ImageView mc1D416F = (ImageView) findViewById(R.id.mc1D416F);
        ImageView mcBCB693 = (ImageView) findViewById(R.id.mcBCB693);
        ImageView mc644949 = (ImageView) findViewById(R.id.mc644949);
        ImageView mcF9CBCB = (ImageView) findViewById(R.id.mcF9CBCB);
        ImageView mcD6C880 = (ImageView) findViewById(R.id.mcD6C880);

        setManicureListener(mc000000, "000000");
        setManicureListener(mc404040, "404040");
        setManicureListener(mcFF0000, "FF0000");
        setManicureListener(mcFF6A00, "FF6A00");
        setManicureListener(mcFFD800, "FFD800");
        setManicureListener(mcB6FF00, "B6FF00");
        setManicureListener(mc4CFF00, "4CFF00");
        setManicureListener(mc00FF21, "00FF21");
        setManicureListener(mc00FF90, "00FF90");
        setManicureListener(mc00FFFF, "00FFFF");
        setManicureListener(mc0094FF, "0094FF");
        setManicureListener(mc0026FF, "0026FF");
        setManicureListener(mc4800FF, "4800FF");
        setManicureListener(mcB200FF, "B200FF");
        setManicureListener(mcFF00DC, "FF00DC");
        setManicureListener(mcFF006E, "FF006E");
        setManicureListener(mc808080, "808080");
        setManicureListener(mcFFFFFF, "FFFFFF");
        setManicureListener(mcF79F49, "F79F49");
        setManicureListener(mc8733DD, "8733DD");
        setManicureListener(mc62B922, "62B922");
        setManicureListener(mcF9F58D, "F9F58D");
        setManicureListener(mcA50909, "A50909");
        setManicureListener(mc1D416F, "1D416F");
        setManicureListener(mcBCB693, "BCB693");
        setManicureListener(mc644949, "644949");
        setManicureListener(mcF9CBCB, "F9CBCB");
        setManicureListener(mcD6C880, "D6C880");
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

    private void setManicureListener(final ImageView imageView, final String color) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageView.getAlpha() == 1F) {
                    imageView.setAlpha(0.3F);
                    if (!manicureColors.contains(color))
                        manicureColors.add(color);
                } else if (imageView.getAlpha() == 0.3F) {
                    imageView.setAlpha(1F);
                    if (manicureColors.contains(color))
                        manicureColors.remove(color);
                }
            }
        });
    }
}