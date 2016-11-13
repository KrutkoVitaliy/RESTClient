package appcorp.mmb.activities.other;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.util.List;
import java.util.Objects;

import appcorp.mmb.R;
import appcorp.mmb.activities.search_feeds.SearchHairstyleMatrix;
import appcorp.mmb.activities.user.SignIn;
import appcorp.mmb.classes.FireAnal;
import appcorp.mmb.classes.Intermediates;
import appcorp.mmb.classes.Storage;
import appcorp.mmb.network.GetRequest;

public class PostHairstyle extends AppCompatActivity {

    private String imageUrl;
    private LinearLayout postHairstyleHashTags, postHairstyleImageViewer, postHairstyleCountImages, postHairstyleMoreContainer;
    private HorizontalScrollView postHairstyleImageViewerHorizontal;
    private ImageView postHairstyleAvatar, postHairstyleAddLike;
    private TextView postHairstyleTitle, postHairstyleAvailableDate, postHairstyleLikesCount;
    private List<Long> likes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_hairstyle);

        initViews();

        imageUrl = getIntent().getStringExtra("hairstyleImageUrl");
        new LoadPost().execute();
        if (!Storage.getString("Name", "Make Me Beauty").equals("Make Me Beauty"))
            new CheckLikes(Storage.getString("E-mail", "")).execute();

        FireAnal.sendString("Post hairstyle", "Open", "Activity");
    }

    public static String convertToString(Context context, int r) {
        TextView textView = new TextView(context);
        textView.setText(r);
        return textView.getText().toString();
    }

    private void initViews() {
        postHairstyleHashTags = (LinearLayout) findViewById(R.id.postHairstyleHashTags);
        postHairstyleImageViewer = (LinearLayout) findViewById(R.id.postHairstyleImageViewer);
        postHairstyleCountImages = (LinearLayout) findViewById(R.id.postHairstyleCountImages);
        postHairstyleMoreContainer = (LinearLayout) findViewById(R.id.postHairstyleMoreContainer);

        postHairstyleImageViewerHorizontal = (HorizontalScrollView) findViewById(R.id.postHairstyleImageViewerHorizontal);

        postHairstyleAvatar = (ImageView) findViewById(R.id.postHairstyleAvatar);
        postHairstyleAddLike = (ImageView) findViewById(R.id.postHairstyleAddLike);

        postHairstyleTitle = (TextView) findViewById(R.id.postHairstyleTitle);
        postHairstyleAvailableDate = (TextView) findViewById(R.id.postHairstyleAvailableDate);
        postHairstyleLikesCount = (TextView) findViewById(R.id.postHairstyleLikesCount);
    }

    public class LoadPost extends AsyncTask<Void, Void, String> {

        private HttpURLConnection connection = null;
        private BufferedReader reader = null;
        private String result = "";

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL feedURL = new URL("http://195.88.209.17/search/simplePost.php?cat=hairstyle&image=" + imageUrl);
                connection = (HttpURLConnection) feedURL.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder buffer = new StringBuilder();
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
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONArray items = new JSONArray(result);
                for (int i = 0; i < items.length(); i++) {
                    final JSONObject item = items.getJSONObject(i);

                    String[] date = item.getString("uploadDate").split("");
                    postHairstyleAvailableDate.setText(date[1] + date[2] + "-" + date[3] + date[4] + "-" + date[5] + date[6] + " " + date[7] + date[8] + ":" + date[9] + date[10]);
                    postHairstyleTitle.setText(item.getString("authorName"));
                    Picasso.with(getApplicationContext()).load("http://195.88.209.17/storage/photos/" + item.getString("authorPhoto")).resize(150, 150).centerCrop().into(postHairstyleAvatar);

                    final ArrayList<String> hashTags = new ArrayList<>();
                    if (Storage.getString("Localization", "").equals("English"))
                        for (int a = 0; a < item.getString("tags").split(",").length; a++)
                            if (!item.getString("tags").split(",")[a].equals(""))
                                hashTags.add(item.getString("tags").split(",")[a]);
                    if (Storage.getString("Localization", "").equals("Russian"))
                        for (int a = 0; a < item.getString("tagsRu").split(",").length; a++)
                            if (!item.getString("tagsRu").split(",")[a].equals(""))
                                hashTags.add(item.getString("tagsRu").split(",")[a]);
                    for (int j = 0; j < hashTags.size(); j++) {
                        TextView hashTag = new TextView(getApplicationContext());
                        hashTag.setTextColor(Color.argb(255, 51, 102, 153));
                        hashTag.setTextSize(16);
                        final int finalI = j;
                        hashTag.setText(String.valueOf("#" + hashTags.get(j).replace(" ", "")));
                        hashTag.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                getApplicationContext().startActivity(new Intent(getApplicationContext(), SearchHairstyleMatrix.class)
                                        .putExtra("Toolbar", hashTags.get(finalI).trim())
                                        .putExtra("Request", hashTags.get(finalI).trim())
                                        .putStringArrayListExtra("HairstyleColors", new ArrayList<String>())
                                        .putExtra("HairstyleLength", "0")
                                        .putExtra("HairstyleType", "0")
                                        .putExtra("HairstyleFor", "0")
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            }
                        });
                        if (!hashTag.getText().toString().equals(""))
                            postHairstyleHashTags.addView(hashTag);
                    }

                    final ArrayList<String> screenshots = new ArrayList<>();
                    for (int j = 0; j < 10; j++)
                        if (!item.getString("screen" + j).equals("empty.jpg"))
                            screenshots.add(item.getString("screen" + j));
                    for (int k = 0; k < screenshots.size(); k++) {
                        ImageView screenShot = new ImageView(getApplicationContext());
                        screenShot.setMinimumWidth(Storage.getInt("Width", 480));
                        screenShot.setMinimumHeight(Storage.getInt("Width", 480));
                        screenShot.setPadding(0, 0, 1, 0);
                        screenShot.setBackgroundColor(Color.argb(255, 200, 200, 200));
                        Picasso.with(getApplicationContext()).load("http://195.88.209.17/storage/images/" + screenshots.get(k)).resize(Storage.getInt("Width", 480), Storage.getInt("Width", 480)).centerCrop().into(screenShot);
                        screenShot.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        final int finalI = k;
                        screenShot.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getApplicationContext(), FullscreenPreview.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("screenshot", "http://195.88.209.17/storage/images/" + screenshots.get(finalI));
                                getApplicationContext().startActivity(intent);
                            }
                        });
                        postHairstyleImageViewer.addView(screenShot);
                        postHairstyleImageViewerHorizontal.scrollTo(0, 0);
                        LinearLayout countLayout = new LinearLayout(getApplicationContext());
                        countLayout.setLayoutParams(new ViewGroup.LayoutParams(Storage.getInt("Width", 480), Storage.getInt("Width", 480)));
                        TextView count = new TextView(getApplicationContext());
                        count.setText("< " + (k + 1) + "/" + screenshots.size() + " >");
                        count.setTextSize(24);
                        count.setTextColor(Color.WHITE);
                        count.setPadding(32, 32, 32, 32);
                        count.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Galada.ttf"));
                        countLayout.addView(count);
                        postHairstyleCountImages.addView(countLayout);
                    }

                    postHairstyleLikesCount.setText(String.valueOf(item.getString("likes")));

                    postHairstyleAddLike.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!Storage.getString("Name", "Make Me Beauty").equals("Make Me Beauty")) {
                                try {
                                    if (!likes.contains(item.getLong("id"))) {
                                        postHairstyleAddLike.setBackgroundResource(R.mipmap.ic_heart);
                                        likes.add(item.getLong("id"));
                                        postHairstyleLikesCount.setText(String.valueOf(item.getString("likes") + 1));
                                        new GetRequest("http://195.88.209.17/app/in/makeupLike.php?id=" + item.getString("id") + "&email=" + Storage.getString("E-mail", "")).execute();
                                    } else if (likes.contains(item.getLong("id"))) {
                                        postHairstyleAddLike.setBackgroundResource(R.mipmap.ic_heart_outline);
                                        likes.remove(item.getLong("id"));
                                        postHairstyleLikesCount.setText(String.valueOf(postHairstyleLikesCount.getText()));
                                        new GetRequest("http://195.88.209.17/app/in/makeupDislike.php?id=" + item.getString("id") + "&email=" + Storage.getString("E-mail", "")).execute();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                startActivity(new Intent(getApplicationContext(), SignIn.class)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            }
                        }
                    });

                    LinearLayout moreContainer = new LinearLayout(getApplicationContext());
                    moreContainer.setLayoutParams(new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    moreContainer.setOrientation(LinearLayout.VERTICAL);

                    if (item.getString("length").equals("short"))
                        moreContainer.addView(createText(convertToString(getApplicationContext(), R.string.shortHairstyle), 16, "Length", "1"));
                    else if (item.getString("length").equals("medium"))
                        moreContainer.addView(createText(convertToString(getApplicationContext(), R.string.mediumHairstyle), 16, "Length", "2"));
                    else if (item.getString("length").equals("long"))
                        moreContainer.addView(createText(convertToString(getApplicationContext(), R.string.longHairstyle), 16, "Length", "3"));

                    if (item.getString("type").equals("straight"))
                        moreContainer.addView(createText(convertToString(getApplicationContext(), R.string.straightHairstyleType), 16, "Type", "1"));
                    else if (item.getString("type").equals("braid"))
                        moreContainer.addView(createText(convertToString(getApplicationContext(), R.string.braidHairstyleType), 16, "Type", "2"));
                    else if (item.getString("type").equals("tail"))
                        moreContainer.addView(createText(convertToString(getApplicationContext(), R.string.tailHairstyleType), 16, "Type", "3"));
                    else if (item.getString("type").equals("bunch"))
                        moreContainer.addView(createText(convertToString(getApplicationContext(), R.string.bunchHairstyleType), 16, "Type", "4"));
                    else if (item.getString("type").equals("netting"))
                        moreContainer.addView(createText(convertToString(getApplicationContext(), R.string.nettingHairstyleType), 16, "Type", "5"));
                    else if (item.getString("type").equals("curls"))
                        moreContainer.addView(createText(convertToString(getApplicationContext(), R.string.curlsHairstyleType), 16, "Type", "6"));
                    else if (item.getString("type").equals("unstandart"))
                        moreContainer.addView(createText(convertToString(getApplicationContext(), R.string.unstandartHairstyleType), 16, "Type", "7"));

                    if (item.getString("for").equals("kids"))
                        moreContainer.addView(createText(convertToString(getApplicationContext(), R.string.forKids), 16, "For", "1"));
                    else if (item.getString("for").equals("everyday"))
                        moreContainer.addView(createText(convertToString(getApplicationContext(), R.string.forEveryday), 16, "For", "2"));
                    else if (item.getString("for").equals("wedding"))
                        moreContainer.addView(createText(convertToString(getApplicationContext(), R.string.forWedding), 16, "For", "3"));
                    else if (item.getString("for").equals("evening"))
                        moreContainer.addView(createText(convertToString(getApplicationContext(), R.string.forEvening), 16, "For", "4"));
                    else if (item.getString("for").equals("exclusive"))
                        moreContainer.addView(createText(convertToString(getApplicationContext(), R.string.forExclusive), 16, "For", "5"));

                    postHairstyleMoreContainer.addView(moreContainer);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private TextView createText(String title, int padding, final String type, final String index) {
        TextView tw = new TextView(getApplicationContext());
        tw.setText(String.valueOf(title));
        tw.setPadding(0, padding, 0, padding);
        tw.setTextSize(14);
        tw.setTextColor(Color.argb(255, 50, 50, 50));
        tw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Objects.equals(type, "Length")) {
                    String[] length = getResources().getStringArray(R.array.hairstyleLength);
                    Intent intent = new Intent(getApplicationContext(), SearchHairstyleMatrix.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("Toolbar", "" + length[Integer.valueOf(index)]);
                    intent.putExtra("Request", "");
                    intent.putExtra("HairstyleLength", "" + index);
                    intent.putExtra("HairstyleType", "0");
                    intent.putExtra("HairstyleFor", "0");
                    startActivity(intent);
                    FireAnal.sendString("2", "SearchHairstyleMatrixParamLength", index);
                }
                if (Objects.equals(type, "Type")) {
                    String[] type = getResources().getStringArray(R.array.hairstyleType);
                    Intent intent = new Intent(getApplicationContext(), SearchHairstyleMatrix.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("Toolbar", "" + type[Integer.valueOf(index)]);
                    intent.putExtra("Request", "");
                    intent.putExtra("HairstyleLength", "0");
                    intent.putExtra("HairstyleType", "" + index);
                    intent.putExtra("HairstyleFor", "0");
                    startActivity(intent);
                    FireAnal.sendString("2", "SearchHairstyleMatrixParamType", index);
                }
                if (Objects.equals(type, "For")) {
                    String[] hfor = getResources().getStringArray(R.array.hairstyleFor);
                    Intent intent = new Intent(getApplicationContext(), SearchHairstyleMatrix.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("Toolbar", "" + hfor[Integer.valueOf(index)]);
                    intent.putExtra("Request", "");
                    intent.putExtra("HairstyleLength", "0");
                    intent.putExtra("HairstyleType", "0");
                    intent.putExtra("HairstyleFor", "" + index);
                    startActivity(intent);
                    FireAnal.sendString("2", "SearchHairstyleParamFor", index);
                }
            }
        });
        return tw;
    }

    public class CheckLikes extends AsyncTask<Void, Void, String> {

        HttpURLConnection connection = null;
        BufferedReader reader = null;
        String result = "";
        String email = "";

        CheckLikes(String email) {
            this.email = email;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL feedURL = new URL("http://195.88.209.17/app/in/favoritesHairstyle.php?email=" + email);
                connection = (HttpURLConnection) feedURL.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder profileBuffer = new StringBuilder();
                String profileLine;
                while ((profileLine = reader.readLine()) != null) {
                    profileBuffer.append(profileLine);
                }
                result = profileBuffer.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            String[] array = s.split(",");
            for (String anArray : array) {
                if (!anArray.equals(""))
                    likes.add(Long.valueOf(anArray));
            }
        }
    }
}
