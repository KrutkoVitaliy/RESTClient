package appcorp.mmb.activities.other;

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

import appcorp.mmb.R;
import appcorp.mmb.activities.search_feeds.SearchManicureMatrix;
import appcorp.mmb.activities.user.SignIn;
import appcorp.mmb.classes.FireAnal;
import appcorp.mmb.classes.Storage;
import appcorp.mmb.network.GetRequest;

public class PostManicure extends AppCompatActivity {

    String imageUrl;
    LinearLayout postManicureHashTags, postManicureImageViewer, postManicureCountImages, postManicureColorSet;
    HorizontalScrollView postManicureImageViewerHorizontal;
    ImageView postManicureAvatar, postManicureAddLike;
    TextView postManicureTitle, postManicureAvailableDate, postManicureLikesCount, postManicureShape, postManicureDesign, postManicureColors;
    private List<Long> likes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_manicure);
        initViews();
        Storage.init(getApplicationContext());

        imageUrl = getIntent().getStringExtra("manicureImageUrl");
        new LoadPost().execute();
        if (!Storage.getString("Name", "Make Me Beauty").equals("Make Me Beauty"))
            new CheckLikes(Storage.getString("E-mail", "")).execute();
    }

    private void initViews() {
        postManicureHashTags = (LinearLayout) findViewById(R.id.postManicureHashTags);
        postManicureImageViewer = (LinearLayout) findViewById(R.id.postManicureImageViewer);
        postManicureCountImages = (LinearLayout) findViewById(R.id.postManicureCountImages);
        postManicureColorSet = (LinearLayout) findViewById(R.id.postManicureColorSet);

        postManicureImageViewerHorizontal = (HorizontalScrollView) findViewById(R.id.postManicureImageViewerHorizontal);

        postManicureAvatar = (ImageView) findViewById(R.id.postManicureAvatar);
        postManicureAddLike = (ImageView) findViewById(R.id.postManicureAddLike);

        postManicureTitle = (TextView) findViewById(R.id.postManicureTitle);
        postManicureAvailableDate = (TextView) findViewById(R.id.postManicureAvailableDate);
        postManicureLikesCount = (TextView) findViewById(R.id.postManicureLikesCount);
        postManicureShape = (TextView) findViewById(R.id.postManicureShape);
        postManicureDesign = (TextView) findViewById(R.id.postManicureDesign);
        postManicureColors = (TextView) findViewById(R.id.postManicureColors);
    }

    public class LoadPost extends AsyncTask<Void, Void, String> {

        private HttpURLConnection connection = null;
        private BufferedReader reader = null;
        private String result = "";

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL feedURL = new URL("http://195.88.209.17/search/simplePost.php?cat=manicure&image=" + imageUrl);
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
                    postManicureAvailableDate.setText(date[1] + date[2] + "-" + date[3] + date[4] + "-" + date[5] + date[6] + " " + date[7] + date[8] + ":" + date[9] + date[10]);
                    postManicureTitle.setText(item.getString("authorName"));
                    Picasso.with(getApplicationContext()).load("http://195.88.209.17/storage/photos/" + item.getString("authorPhoto")).resize(150, 150).centerCrop().into(postManicureAvatar);

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
                        hashTag.setText("#" + hashTags.get(j).replace(" ", "") + " ");
                        hashTag.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                getApplicationContext().startActivity(new Intent(getApplicationContext(), SearchManicureMatrix.class)
                                        .putExtra("Request", hashTags.get(finalI).trim())
                                        .putStringArrayListExtra("ManicureColors", new ArrayList<String>())
                                        .putExtra("Shape", "" + "0")
                                        .putExtra("Design", "" + "0")
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            }
                        });

                        if (!hashTag.getText().toString().equals(""))
                            postManicureHashTags.addView(hashTag);
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
                        postManicureImageViewer.addView(screenShot);
                        postManicureImageViewerHorizontal.scrollTo(0, 0);
                        LinearLayout countLayout = new LinearLayout(getApplicationContext());
                        countLayout.setLayoutParams(new ViewGroup.LayoutParams(Storage.getInt("Width", 480), Storage.getInt("Width", 480)));
                        TextView count = new TextView(getApplicationContext());
                        count.setText("< " + (k + 1) + "/" + screenshots.size() + " >");
                        count.setTextSize(24);
                        count.setTextColor(Color.WHITE);
                        count.setPadding(32, 32, 32, 32);
                        count.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Galada.ttf"));
                        countLayout.addView(count);
                        postManicureCountImages.addView(countLayout);
                    }

                    postManicureColors.setText(R.string.title_used_colors);
                    final ArrayList<String> colors = new ArrayList<>();
                    for (int j = 0; j < item.getString("colors").split(",").length; j++)
                        if (!item.getString("colors").split(",")[j].equals("")) {
                            colors.add(item.getString("colors").split(",")[j]);
                        }
                    for (int l = 0; l < colors.size(); l++) {
                        if (!colors.get(l).equals("FFFFFF"))
                            postManicureColorSet.addView(createCircle("#" + colors.get(l), colors.get(l)));
                        else
                            postManicureColorSet.addView(createCircle("#EEEEEE", colors.get(l)));
                    }


                    if (item.getString("shape").equals("square"))
                        postManicureShape.setText(R.string.squareShape);
                    else if (item.getString("shape").equals("oval"))
                        postManicureShape.setText(R.string.ovalShape);
                    else if (item.getString("shape").equals("stiletto"))
                        postManicureShape.setText(R.string.stilettoShape);

                    postManicureShape.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                int index = 0;
                                if (item.getString("shape").equals("square"))
                                    index = 1;
                                else if (item.getString("shape").equals("oval"))
                                    index = 2;
                                else if (item.getString("shape").equals("stiletto"))
                                    index = 3;

                                String[] shapes = getResources().getStringArray(R.array.manicureShapes);
                                ArrayList<String> manicureColors = new ArrayList<>();
                                Intent intent = new Intent(getApplicationContext(), SearchManicureMatrix.class);
                                intent.putStringArrayListExtra("ManicureColors", manicureColors);
                                intent.putExtra("Toolbar", "" + shapes[index]);
                                intent.putExtra("Request", "");
                                intent.putExtra("Shape", "" + index);
                                intent.putExtra("Design", "0");
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                FireAnal.sendString("2", "SearchManicureMatrixParamShape", item.getString("shape"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    postManicureDesign.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                int index = 0;
                                if (item.getString("design").equals("french_classic"))
                                    index = 1;
                                else if (item.getString("design").equals("french_chevron"))
                                    index = 2;
                                else if (item.getString("design").equals("french_millennium"))
                                    index = 3;
                                else if (item.getString("design").equals("french_fun"))
                                    index = 4;
                                else if (item.getString("design").equals("french_crystal"))
                                    index = 5;
                                else if (item.getString("design").equals("french_colorful"))
                                    index = 6;
                                else if (item.getString("design").equals("french_designer"))
                                    index = 7;
                                else if (item.getString("design").equals("french_spa"))
                                    index = 8;
                                else if (item.getString("design").equals("french_moon"))
                                    index = 9;
                                else if (item.getString("design").equals("art"))
                                    index = 10;
                                else if (item.getString("design").equals("designer"))
                                    index = 11;
                                else if (item.getString("design").equals("volume"))
                                    index = 12;
                                else if (item.getString("design").equals("aqua"))
                                    index = 13;
                                else if (item.getString("design").equals("american"))
                                    index = 14;
                                else if (item.getString("design").equals("photo"))
                                    index = 15;

                                String[] designs = getResources().getStringArray(R.array.manicureDesign);
                                ArrayList<String> manicureColors = new ArrayList<>();
                                Intent intent = new Intent(getApplicationContext(), SearchManicureMatrix.class);
                                intent.putStringArrayListExtra("ManicureColors", manicureColors);
                                intent.putExtra("Toolbar", "" + designs[index]);
                                intent.putExtra("Request", "");
                                intent.putExtra("Shape", "0");
                                intent.putExtra("Design", "" + index);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                FireAnal.sendString("2", "SearchManicureMatrixParamDesign", item.getString("design"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    if (item.getString("design").equals("french_classic"))
                        postManicureDesign.setText(R.string.french_classicDesign);
                    else if (item.getString("design").equals("french_chevron"))
                        postManicureDesign.setText(R.string.french_chevronDesign);
                    else if (item.getString("design").equals("french_millennium"))
                        postManicureDesign.setText(R.string.french_millenniumDesign);
                    else if (item.getString("design").equals("french_fun"))
                        postManicureDesign.setText(R.string.french_funDesign);
                    else if (item.getString("design").equals("french_crystal"))
                        postManicureDesign.setText(R.string.french_crystalDesign);
                    else if (item.getString("design").equals("french_colorful"))
                        postManicureDesign.setText(R.string.french_colorfulDesign);
                    else if (item.getString("design").equals("french_designer"))
                        postManicureDesign.setText(R.string.french_designerDesign);
                    else if (item.getString("design").equals("french_spa"))
                        postManicureDesign.setText(R.string.french_spaDesign);
                    else if (item.getString("design").equals("french_moon"))
                        postManicureDesign.setText(R.string.french_moonDesign);
                    else if (item.getString("design").equals("art"))
                        postManicureDesign.setText(R.string.artDesign);
                    else if (item.getString("design").equals("designer"))
                        postManicureDesign.setText(R.string.designerDesign);
                    else if (item.getString("design").equals("volume"))
                        postManicureDesign.setText(R.string.volumeDesign);
                    else if (item.getString("design").equals("aqua"))
                        postManicureDesign.setText(R.string.aquaDesign);
                    else if (item.getString("design").equals("american"))
                        postManicureDesign.setText(R.string.americanDesign);
                    else if (item.getString("design").equals("photo"))
                        postManicureDesign.setText(R.string.photoDesign);

                    postManicureLikesCount.setText(String.valueOf(item.getString("likes")));

                    postManicureAddLike.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!Storage.getString("Name", "Make Me Beauty").equals("Make Me Beauty")) {
                                try {
                                    if (!likes.contains(item.getLong("id"))) {
                                        postManicureAddLike.setBackgroundResource(R.mipmap.ic_heart);
                                        likes.add(Long.valueOf(item.getString("id")));
                                        postManicureLikesCount.setText(String.valueOf(item.getString("likes") + 1));
                                        new GetRequest("http://195.88.209.17/app/in/manicureLike.php?id=" + item.getString("id") + "&email=" + Storage.getString("E-mail", "")).execute();
                                    } else if (likes.contains(item.getLong("id"))) {
                                        postManicureAddLike.setBackgroundResource(R.mipmap.ic_heart_outline);
                                        likes.remove(item.getLong("id"));
                                        postManicureLikesCount.setText(String.valueOf(postManicureLikesCount.getText()));
                                        new GetRequest("http://195.88.209.17/app/in/manicureDislike.php?id=" + item.getString("id") + "&email=" + Storage.getString("E-mail", "")).execute();
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

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private ImageView createCircle(final String color, final String searchParameter) {
            ImageView imageView = new ImageView(getApplicationContext());
            imageView.setLayoutParams(new ViewGroup.LayoutParams((int) (Storage.getInt("Width", 480) * 0.075F), (int) (Storage.getInt("Width", 480) * 0.075F)));
            imageView.setScaleX(0.9F);
            imageView.setScaleY(0.9F);
            imageView.setBackgroundColor(Color.parseColor(color));
            imageView.setImageResource(R.mipmap.photo_layer);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<String> manicureColors = new ArrayList<>();
                    manicureColors.add(searchParameter);
                    Intent intent = new Intent(getApplicationContext(), SearchManicureMatrix.class);
                    intent.putStringArrayListExtra("ManicureColors", sortManicureColors(manicureColors));
                    intent.putExtra("Request", "");
                    intent.putExtra("Shape", "0");
                    intent.putExtra("Design", "0");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(intent);
                    FireAnal.sendString("2", "SearchManicureMatrixParamColor", color);
                }
            });
            return imageView;
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
    }

    public class CheckLikes extends AsyncTask<Void, Void, String> {

        private HttpURLConnection connection = null;
        private BufferedReader reader = null;
        private String result = "";
        private String email = "";

        CheckLikes(String email) {
            this.email = email;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL feedURL = new URL("http://195.88.209.17/app/in/favoritesManicure.php?email=" + email);
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