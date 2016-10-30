package appcorp.mmb.activities.search_feeds;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.GridView;
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
import java.util.ArrayList;

import appcorp.mmb.R;
import appcorp.mmb.activities.adapters.SearchHairstyleMatrixAdapter;
import appcorp.mmb.activities.feeds.HairstyleFeed;
import appcorp.mmb.activities.feeds.MakeupFeed;
import appcorp.mmb.activities.feeds.ManicureFeed;
import appcorp.mmb.activities.user.Favorites;
import appcorp.mmb.activities.user.MyProfile;
import appcorp.mmb.activities.user.SignIn;
import appcorp.mmb.classes.Intermediates;
import appcorp.mmb.classes.Storage;

public class SearchHairstyleMatrix extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private SearchHairstyleMatrixAdapter adapter;
    private ArrayList<String> thumbs = new ArrayList<>();
    private static String request, hairstyleLength, hairstyleType, hairstyleFor, toolbarTitle;
    private ArrayList<String> colors = new ArrayList<>();
    private ProgressDialog progressDialog;

    private int visibleThreshold = 5;
    private int currentPage = 0;
    private int previousTotal = 0;
    private boolean loading = true;
    int run = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_manicure_matrix);

        this.toolbarTitle = getIntent().getStringExtra("Toolbar");
        this.request = getIntent().getStringExtra("Request");
        this.hairstyleLength = getIntent().getStringExtra("HairstyleLength");
        this.hairstyleType = getIntent().getStringExtra("HairstyleType");
        this.hairstyleFor = getIntent().getStringExtra("HairstyleFor");

        initToolbar();
        initNavigationView();

        final GridView gridView = (GridView) findViewById(R.id.gridView);
        adapter = new SearchHairstyleMatrixAdapter(getApplicationContext());
        gridView.setAdapter(adapter);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(Intermediates.getInstance().convertToString(getApplicationContext(), R.string.loading));
        progressDialog.show();
        new Load(request, hairstyleLength, hairstyleType, hairstyleFor, run).execute();

        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                        currentPage++;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                    run++;
                    new Load(request, hairstyleLength, hairstyleType, hairstyleFor, run).execute();
                    loading = true;
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
        });
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.manicureMatrixToolbar);
        toolbar.setTitle(R.string.toolbar_title_search);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                startActivity(new Intent(getApplicationContext(), SearchHairstyleFeed.class)
                        .putExtra("Request", "" + request)
                        .putExtra("HairstyleLength", "" + hairstyleLength)
                        .putExtra("HairstyleType", "" + hairstyleType)
                        .putExtra("HairstyleFor", "" + hairstyleFor));
                return true;
            }
        });
        toolbar.inflateMenu(R.menu.menu_search_list);
    }

    private void initNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.manicureMatrixDrawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_toggle_open, R.string.drawer_toggle_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) drawerLayout.findViewById(R.id.manicureMatrixNavigation);
        initHeaderLayout(navigationView);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                drawerLayout.closeDrawers();
                switch (item.getItemId()) {
                    /*case R.id.navMenuGlobalFeed:
                        startActivity(new Intent(getApplicationContext(), GlobalFeed.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
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

    public class Load extends AsyncTask<Void, Void, String> {
        private HttpURLConnection connection = null;
        private BufferedReader reader = null;
        private String result = "";
        private int position;
        private String request, hairstyleLength, hairstyleType, hairstyleFor;
        private String colorsStr = "";

        public Load(String request, String hairstyleLength, String hairstyleType, String hairstyleFor, int position) {
            this.request = request;
            this.position = position;

            if (hairstyleLength.equals("0"))
                this.hairstyleLength = "";
            else if (hairstyleLength.equals("1"))
                this.hairstyleLength = "short";
            else if (hairstyleLength.equals("2"))
                this.hairstyleLength = "medium";
            else if (hairstyleLength.equals("3"))
                this.hairstyleLength = "long";

            if (hairstyleType.equals("0"))
                this.hairstyleType = "";
            else if (hairstyleType.equals("1"))
                this.hairstyleType = "straight";
            else if (hairstyleType.equals("2"))
                this.hairstyleType = "braid";
            else if (hairstyleType.equals("3"))
                this.hairstyleType = "tail";
            else if (hairstyleType.equals("4"))
                this.hairstyleType = "bunch";
            else if (hairstyleType.equals("5"))
                this.hairstyleType = "netting";
            else if (hairstyleType.equals("6"))
                this.hairstyleType = "curls";
            else if (hairstyleType.equals("7"))
                this.hairstyleType = "custom";

            if (hairstyleFor.equals("0"))
                this.hairstyleFor = "";
            else if (hairstyleFor.equals("1"))
                this.hairstyleFor = "kids";
            else if (hairstyleFor.equals("2"))
                this.hairstyleFor = "everyday";
            else if (hairstyleFor.equals("3"))
                this.hairstyleFor = "wedding";
            else if (hairstyleFor.equals("4"))
                this.hairstyleFor = "evening";
            else if (hairstyleFor.equals("5"))
                this.hairstyleFor = "exclusive";
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL feedURL = new URL("http://195.88.209.17/search/hairstyle.php?request=" + Intermediates.getInstance().encodeToURL(request) + "&hairstyle_length=" + hairstyleLength + "&hairstyle_type=" + hairstyleType + "&hairstyle_for=" + hairstyleFor + "&position=" + position);
                connection = (HttpURLConnection) feedURL.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer buffer = new StringBuffer();
                String line;
                while ((line = reader.readLine()) != null)
                    buffer.append(line);
                result += buffer.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONArray items = new JSONArray(result);
                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);

                    //id = item.getLong("id");
                    if (!request.equals(""))
                        toolbar.setTitle("#" + request + " - " + item.getString("count"));
                    thumbs.add(item.getString("screen1"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            adapter.setData(thumbs);
            adapter.notifyDataSetChanged();
            progressDialog.dismiss();
        }
    }
}