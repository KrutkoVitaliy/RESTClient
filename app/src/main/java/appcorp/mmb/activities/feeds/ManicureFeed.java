package appcorp.mmb.activities.feeds;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import appcorp.mmb.R;
import appcorp.mmb.activities.search_feeds.Search;
import appcorp.mmb.activities.search_feeds.SearchStylist;
import appcorp.mmb.activities.user.Authorization;
import appcorp.mmb.activities.user.Favorites;
import appcorp.mmb.activities.user.MyProfile;
import appcorp.mmb.activities.user.SignIn;
import appcorp.mmb.classes.Intermediates;
import appcorp.mmb.classes.Storage;
import appcorp.mmb.dto.ManicureDTO;
import appcorp.mmb.fragment_adapters.ManicureFeedFragmentAdapter;
import appcorp.mmb.loaders.ManicureFeedLoader;

public class ManicureFeed extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ViewPager viewPager;
    private static ManicureFeedFragmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manicure_feed);

        initToolbar();
        initNavigationView();
        initViewPager();

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(Intermediates.convertToString(getApplicationContext(), R.string.loading));
        progressDialog.show();

        new ManicureFeedLoader(adapter, 1, progressDialog).execute();
    }

    public static void addFeed(int position) {
        new ManicureFeedLoader(adapter, position).execute();
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.manicureToolbar);
        toolbar.setTitle(R.string.menu_item_manicure);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent intent = new Intent(getApplicationContext(), Search.class);
                intent.putExtra("from", "manicureFeed");
                startActivity(intent);
                return true;
            }
        });
        toolbar.inflateMenu(R.menu.menu);
    }

    private void initNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.manicureDrawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_toggle_open, R.string.drawer_toggle_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) drawerLayout.findViewById(R.id.manicureNavigation);
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
            Picasso.with(getApplicationContext()).load("http://195.88.209.17/storage/photos/"+Storage.getString("PhotoURL", "")).resize(100,100).centerCrop().into(avatar);
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

    private void initViewPager() {
        viewPager = (ViewPager) findViewById(R.id.manicureViewPager);
        adapter = new ManicureFeedFragmentAdapter(getApplicationContext(), getSupportFragmentManager(), new ArrayList<ManicureDTO>());
        viewPager.setAdapter(adapter);
    }
}