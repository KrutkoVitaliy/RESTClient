package appcorp.mmb.activities;

import android.content.Intent;
import android.os.Bundle;
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
import appcorp.mmb.activities.feeds.HairstyleFeed;
import appcorp.mmb.activities.feeds.LipsFeed;
import appcorp.mmb.activities.feeds.MakeupFeed;
import appcorp.mmb.activities.feeds.ManicureFeed;
import appcorp.mmb.activities.search_feeds.SearchHairstyleFeed;
import appcorp.mmb.activities.search_feeds.SearchMakeupFeed;
import appcorp.mmb.activities.search_feeds.SearchManicureFeed;
import appcorp.mmb.classes.Storage;

public class Search extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private String hashTag = "", request = "", eyeColor = "", difficult = "";
    private String arrayColors = "", category = "";
    private EditText requestField, hairstyleType;
    private Button search;
    private ImageView eyeBlueCircle, eyeGreenCircle, eyeHazelCircle, eyeBrownCircle,
            eyeGrayCircle, eyeBlackCircle;
    private LinearLayout easyDifficult, mediumDifficult, hardDifficult;
    private Spinner occasion, shape, design;
    private ImageView cBB125B, c9210AE, c117DAE, c3B9670, c79BD14, cD4B515, cD46915, cD42415, cD2AF7F, cB48F58, c604E36, c555555, c000000;
    private ImageView mc000000, mc404040, mcFF0000, mcFF6A00, mcFFD800, mcB6FF00, mc4CFF00, mc00FF21, mc00FF90,
            mc00FFFF, mc0094FF, mc0026FF, mc4800FF, mcB200FF, mcFF00DC, mcFF006E, mc808080, mcFFFFFF, mcF79F49,
            mc8733DD, mc62B922, mcF9F58D, mcA50909, mc1D416F, mcBCB693, mc644949, mcF9CBCB, mcD6C880;
    private ArrayList<String> colors = new ArrayList<>();
    private ArrayList<String> manicureColors = new ArrayList<>();
    private LinearLayout catMakeup, catManicure, catHairstyle, makeupFrame, hairstyleFrame, manicureFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


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

    public void initCategorySelector() {
        catMakeup = (LinearLayout) findViewById(R.id.catMakeup);
        catManicure = (LinearLayout) findViewById(R.id.catManicure);
        //catHairstyle = (LinearLayout) findViewById(R.id.catHairstyle);

        catMakeup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = "makeup";
                setUI("makeup");
            }
        });
        /*catHairstyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = "hairstyle";
                setUI("hairstyle");
            }
        });*/
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
                //findViewById(R.id.catHairstyle).setAlpha(0.2F);
                findViewById(R.id.catManicure).setAlpha(0.2F);
                makeupFrame.setVisibility(View.VISIBLE);
                //hairstyleFrame.setVisibility(View.INVISIBLE);
                manicureFrame.setVisibility(View.INVISIBLE);
                break;
            /*case "hairstyle":
                findViewById(R.id.catMakeup).setAlpha(0.2F);
                findViewById(R.id.catHairstyle).setAlpha(1.0F);
                findViewById(R.id.catManicure).setAlpha(0.2F);
                makeupFrame.setVisibility(View.INVISIBLE);
                hairstyleFrame.setVisibility(View.VISIBLE);
                manicureFrame.setVisibility(View.INVISIBLE);
                break;*/
            case "manicure":
                findViewById(R.id.catMakeup).setAlpha(0.2F);
                //findViewById(R.id.catHairstyle).setAlpha(0.2F);
                findViewById(R.id.catManicure).setAlpha(1.0F);
                makeupFrame.setVisibility(View.INVISIBLE);
                //hairstyleFrame.setVisibility(View.INVISIBLE);
                manicureFrame.setVisibility(View.VISIBLE);
                break;
            case "":
                findViewById(R.id.catMakeup).setAlpha(1.0F);
                //findViewById(R.id.catHairstyle).setAlpha(1.0F);
                findViewById(R.id.catManicure).setAlpha(1.0F);
                makeupFrame.setVisibility(View.INVISIBLE);
                //hairstyleFrame.setVisibility(View.INVISIBLE);
                manicureFrame.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void checkLocation() {
        if (getIntent().getStringExtra("from") != null)
            switch (getIntent().getStringExtra("from").toString()) {
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
        //hairstyleType = (EditText) findViewById(R.id.hairstyleType);
        makeupFrame = (LinearLayout) findViewById(R.id.makeupFrame);
        //hairstyleFrame = (LinearLayout) findViewById(R.id.hairstyleFrame);
        manicureFrame = (LinearLayout) findViewById(R.id.manicureFrame);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.searchToolbar);
        toolbar.setTitle(R.string.toolbar_title_search);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (category) {
                    case "makeup":
                        startActivity(new Intent(getApplicationContext(), SearchMakeupFeed.class)
                                .putExtra("Category", "makeup")
                                .putExtra("Request", requestField.getText().toString())
                                .putStringArrayListExtra("Colors", sortMakeupColors(colors))
                                .putExtra("EyeColor", eyeColor)
                                .putExtra("Difficult", difficult)
                                .putExtra("Occasion", "" + occasion.getSelectedItemPosition())
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        break;
                    case "hairstyle":
                        startActivity(new Intent(getApplicationContext(), SearchHairstyleFeed.class)
                                .putExtra("Category", "hairstyle")
                                .putExtra("Request", requestField.getText().toString())
                                .putExtra("HairstyleType", hairstyleType.getText().toString())
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        break;
                    case "manicure":
                        startActivity(new Intent(getApplicationContext(), SearchManicureFeed.class)
                                .putExtra("Category", "manicure")
                                .putExtra("Request", requestField.getText().toString())
                                .putStringArrayListExtra("ManicureColors", sortManicureColors(manicureColors))
                                .putExtra("Shape", "" + shape.getSelectedItemPosition())
                                .putExtra("Design", "" + design.getSelectedItemPosition())
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
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
        for (int i = 0; i < colorsCodes.length; i++) {
            for (int j = 0; j < colors.size(); j++) {
                if (colorsCodes[i].equals(colors.get(j)))
                    sortedColors.add(colorsCodes[i]);
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
        for (int i = 0; i < colorsCodes.length; i++) {
            for (int j = 0; j < colors.size(); j++) {
                if (colorsCodes[i].equals(colors.get(j)))
                    sortedColors.add(colorsCodes[i]);
            }
        }
        return sortedColors;
    }

    private void initNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.searchDrawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_toggle_open, R.string.drawer_toggle_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) drawerLayout.findViewById(R.id.searchNavigation);
        initHeaderLayout(navigationView);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                drawerLayout.closeDrawers();
                switch (item.getItemId()) {
                    /*case R.id.navMenuGlobalFeed:
                        startActivity(new Intent(getApplicationContext(), SelectCategory.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        break;*/
                    case R.id.navMenuSearch:
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

    private void initHeaderLayout(NavigationView navigationView) {
        View menuHeader = navigationView.getHeaderView(0);
        ImageView avatar = (ImageView) menuHeader.findViewById(R.id.accountPhoto);
        TextView switcherHint = (TextView) menuHeader.findViewById(R.id.accountHint);
        if (!Storage.getString("PhotoURL", "").equals("")) {
            Picasso.with(getApplicationContext()).load(Storage.getString("PhotoURL", "")).into(avatar);
            switcherHint.setText(R.string.account_hint_signed);
        } else {
            avatar.setImageResource(R.mipmap.icon);
            switcherHint.setText(R.string.account_hint_unsigned);
        }
        TextView accountName = (TextView) menuHeader.findViewById(R.id.accountName);
        accountName.setText(Storage.getString("Name", "Make Me Beauty"));

        menuHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Storage.getString("PhotoURL", "").equals("")) {
                    startActivity(new Intent(getApplicationContext(), MyProfile.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                } else {
                    startActivity(new Intent(getApplicationContext(), Authorization.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
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

    private void initMakeupColors() {
        cBB125B = (ImageView) findViewById(R.id.cBB125B);
        c9210AE = (ImageView) findViewById(R.id.c9210AE);
        c117DAE = (ImageView) findViewById(R.id.c117DAE);
        c3B9670 = (ImageView) findViewById(R.id.c3B9670);
        c79BD14 = (ImageView) findViewById(R.id.c79BD14);
        cD4B515 = (ImageView) findViewById(R.id.cD4B515);
        cD46915 = (ImageView) findViewById(R.id.cD46915);
        cD42415 = (ImageView) findViewById(R.id.cD42415);
        cD2AF7F = (ImageView) findViewById(R.id.cD2AF7F);
        cB48F58 = (ImageView) findViewById(R.id.cB48F58);
        c604E36 = (ImageView) findViewById(R.id.c604E36);
        c555555 = (ImageView) findViewById(R.id.c555555);
        c000000 = (ImageView) findViewById(R.id.c000000);

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
        mc000000 = (ImageView) findViewById(R.id.mc000000);
        mc404040 = (ImageView) findViewById(R.id.mc404040);
        mcFF0000 = (ImageView) findViewById(R.id.mcFF0000);
        mcFF6A00 = (ImageView) findViewById(R.id.mcFF6A00);
        mcFFD800 = (ImageView) findViewById(R.id.mcFFD800);
        mcB6FF00 = (ImageView) findViewById(R.id.mcB6FF00);
        mc4CFF00 = (ImageView) findViewById(R.id.mc4CFF00);
        mc00FF21 = (ImageView) findViewById(R.id.mc00FF21);
        mc00FF90 = (ImageView) findViewById(R.id.mc00FF90);
        mc00FFFF = (ImageView) findViewById(R.id.mc00FFFF);
        mc0094FF = (ImageView) findViewById(R.id.mc0094FF);
        mc0026FF = (ImageView) findViewById(R.id.mc0026FF);
        mc4800FF = (ImageView) findViewById(R.id.mc4800FF);
        mcB200FF = (ImageView) findViewById(R.id.mcB200FF);
        mcFF00DC = (ImageView) findViewById(R.id.mcFF00DC);
        mcFF006E = (ImageView) findViewById(R.id.mcFF006E);
        mc808080 = (ImageView) findViewById(R.id.mc808080);
        mcFFFFFF = (ImageView) findViewById(R.id.mcFFFFFF);
        mcF79F49 = (ImageView) findViewById(R.id.mcF79F49);
        mc8733DD = (ImageView) findViewById(R.id.mc8733DD);
        mc62B922 = (ImageView) findViewById(R.id.mc62B922);
        mcF9F58D = (ImageView) findViewById(R.id.mcF9F58D);
        mcA50909 = (ImageView) findViewById(R.id.mcA50909);
        mc1D416F = (ImageView) findViewById(R.id.mc1D416F);
        mcBCB693 = (ImageView) findViewById(R.id.mcBCB693);
        mc644949 = (ImageView) findViewById(R.id.mc644949);
        mcF9CBCB = (ImageView) findViewById(R.id.mcF9CBCB);
        mcD6C880 = (ImageView) findViewById(R.id.mcD6C880);

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