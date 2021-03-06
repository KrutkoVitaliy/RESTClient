package appcorp.mmb.activities.other;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
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
import appcorp.mmb.activities.search_feeds.SearchMakeupMatrix;
import appcorp.mmb.activities.user.SignIn;
import appcorp.mmb.classes.FireAnal;
import appcorp.mmb.classes.Intermediates;
import appcorp.mmb.classes.Storage;
import appcorp.mmb.network.GetRequest;

public class PostMakeup extends AppCompatActivity {

    private String imageUrl;
    private LinearLayout postMakeupHashTags, postMakeupImageViewer, postMakeupCountImages, postMakeupMoreContainer;
    private HorizontalScrollView postMakeupImageViewerHorizontal;
    private ImageView postMakeupAvatar, postMakeupAddLike;
    private TextView postMakeupTitle, postMakeupAvailableDate, postMakeupLikesCount;
    private List<Long> likes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_makeup);

        initViews();

        imageUrl = getIntent().getStringExtra("makeupImageUrl");
        new LoadPost().execute();
        if (!Storage.getString("Name", "Make Me Beauty").equals("Make Me Beauty"))
            new CheckLikes(Storage.getString("E-mail", "")).execute();

        FireAnal.sendString("Post makeup", "Open", "Activity");
    }

    public static String convertToString(Context context, int r) {
        TextView textView = new TextView(context);
        textView.setText(r);
        return textView.getText().toString();
    }

    private void initViews() {
        postMakeupHashTags = (LinearLayout) findViewById(R.id.postMakeupHashTags);
        postMakeupImageViewer = (LinearLayout) findViewById(R.id.postMakeupImageViewer);
        postMakeupCountImages = (LinearLayout) findViewById(R.id.postMakeupCountImages);
        postMakeupMoreContainer = (LinearLayout) findViewById(R.id.postMakeupMoreContainer);

        postMakeupImageViewerHorizontal = (HorizontalScrollView) findViewById(R.id.postMakeupImageViewerHorizontal);

        postMakeupAvatar = (ImageView) findViewById(R.id.postMakeupAvatar);
        postMakeupAddLike = (ImageView) findViewById(R.id.postMakeupAddLike);

        postMakeupTitle = (TextView) findViewById(R.id.postMakeupTitle);
        postMakeupAvailableDate = (TextView) findViewById(R.id.postMakeupAvailableDate);
        postMakeupLikesCount = (TextView) findViewById(R.id.postMakeupLikesCount);
    }

    public class LoadPost extends AsyncTask<Void, Void, String> {

        private HttpURLConnection connection = null;
        private BufferedReader reader = null;
        private String result = "";

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL feedURL = new URL("http://195.88.209.17/search/simplePost.php?cat=makeup&image=" + imageUrl);
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
                    postMakeupAvailableDate.setText(date[1] + date[2] + "-" + date[3] + date[4] + "-" + date[5] + date[6] + " " + date[7] + date[8] + ":" + date[9] + date[10]);
                    postMakeupTitle.setText(item.getString("authorName"));
                    Picasso.with(getApplicationContext()).load("http://195.88.209.17/storage/photos/" + item.getString("authorPhoto")).resize(150, 150).centerCrop().into(postMakeupAvatar);

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
                                startActivity(new Intent(getApplicationContext(), SearchMakeupMatrix.class)
                                        .putExtra("Toolbar", hashTags.get(finalI).trim())
                                        .putExtra("Request", hashTags.get(finalI).trim())
                                        .putStringArrayListExtra("Colors", new ArrayList<String>())
                                        .putExtra("EyeColor", "")
                                        .putExtra("Difficult", "")
                                        .putExtra("Occasion", "0")
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            }
                        });

                        if (hashTag.getText().toString().equals(""))
                            postMakeupHashTags.addView(hashTag);
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
                        postMakeupImageViewer.addView(screenShot);
                        postMakeupImageViewerHorizontal.scrollTo(0, 0);
                        LinearLayout countLayout = new LinearLayout(getApplicationContext());
                        countLayout.setLayoutParams(new ViewGroup.LayoutParams(Storage.getInt("Width", 480), Storage.getInt("Width", 480)));
                        TextView count = new TextView(getApplicationContext());
                        count.setText("< " + (k + 1) + "/" + screenshots.size() + " >");
                        count.setTextSize(24);
                        count.setTextColor(Color.WHITE);
                        count.setPadding(32, 32, 32, 32);
                        count.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Galada.ttf"));
                        countLayout.addView(count);
                        postMakeupCountImages.addView(countLayout);
                    }

                    postMakeupLikesCount.setText(String.valueOf(item.getString("likes")));

                    postMakeupAddLike.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!Storage.getString("Name", "Make Me Beauty").equals("Make Me Beauty")) {
                                try {
                                    if (!likes.contains(item.getLong("id"))) {
                                        postMakeupAddLike.setBackgroundResource(R.mipmap.ic_heart);
                                        likes.add(item.getLong("id"));
                                        postMakeupLikesCount.setText(String.valueOf(item.getString("likes") + 1));
                                        new GetRequest("http://195.88.209.17/app/in/makeupLike.php?id=" + item.getString("id") + "&email=" + Storage.getString("E-mail", "")).execute();
                                    } else if (likes.contains(item.getLong("id"))) {
                                        postMakeupAddLike.setBackgroundResource(R.mipmap.ic_heart_outline);
                                        likes.remove(item.getLong("id"));
                                        postMakeupLikesCount.setText(String.valueOf((postMakeupLikesCount.getText())));
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

                    moreContainer.addView(createText(convertToString(getApplicationContext(), R.string.title_eye_color), 16, "", ""));
                    moreContainer.addView(createImage(item.getString("eyeColor")));
                    moreContainer.addView(createText(convertToString(getApplicationContext(), R.string.title_used_colors), 16, "", ""));
                    LinearLayout colors = new LinearLayout(getApplicationContext());
                    colors.setOrientation(LinearLayout.HORIZONTAL);
                    String[] mColors = (item.getString("colors").split(","));
                    for (String mColor : mColors) {
                        if (!mColor.equals("FFFFFF"))
                            colors.addView(createCircle("#" + mColor, mColor));
                        else
                            colors.addView(createCircle("#EEEEEE", mColor));
                    }
                    moreContainer.addView(colors);

                    moreContainer.addView(createText(convertToString(getApplicationContext(), R.string.title_difficult), 16, "", ""));
                    moreContainer.addView(difficult(item.getString("difficult")));
                    if (item.getString("occasion").equals("everyday"))
                        moreContainer.addView(createText(convertToString(getApplicationContext(), R.string.occasion_everyday), 16, "Occasion", "1"));
                    else if (item.getString("occasion").equals("celebrity"))
                        moreContainer.addView(createText(convertToString(getApplicationContext(), R.string.occasion_everyday), 16, "Occasion", "2"));
                    else if (item.getString("occasion").equals("dramatic"))
                        moreContainer.addView(createText(convertToString(getApplicationContext(), R.string.occasion_dramatic), 16, "Occasion", "3"));
                    else if (item.getString("occasion").equals("holiday"))
                        moreContainer.addView(createText(convertToString(getApplicationContext(), R.string.occasion_holiday), 16, "Occasion", "4"));
                    postMakeupMoreContainer.addView(moreContainer);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private LinearLayout difficult(final String difficult) {
            ImageView imageView = new ImageView(getApplicationContext());
            LinearLayout layout = new LinearLayout(getApplicationContext());
            layout.setVerticalGravity(Gravity.CENTER_VERTICAL);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            TextView text = new TextView(getApplicationContext());
            text.setTextSize(14);
            text.setTextColor(Color.argb(255, 100, 100, 100));
            text.setPadding(16, 0, 0, 0);
            if (difficult.equals("easy")) {
                imageView.setImageResource(R.mipmap.easy);
                text.setText(R.string.difficult_easy);
            }
            if (difficult.equals("medium")) {
                imageView.setImageResource(R.mipmap.medium);
                text.setText(R.string.difficult_medium);
            }
            if (difficult.equals("hard")) {
                imageView.setImageResource(R.mipmap.hard);
                text.setText(R.string.difficult_hard);
            }
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<String> makeupColors = new ArrayList<>();
                    Intent intent = new Intent(getApplicationContext(), SearchMakeupMatrix.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("Toolbar", "" + difficult);
                    intent.putExtra("Request", "");
                    intent.putStringArrayListExtra("Colors", sortMakeupColors(makeupColors));
                    intent.putExtra("EyeColor", "");
                    intent.putExtra("Difficult", difficult);
                    intent.putExtra("Occasion", "0");
                    startActivity(intent);
                    FireAnal.sendString("2", "SearchMakeupMatrixParamDifficult", difficult);
                }
            });
            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<String> makeupColors = new ArrayList<>();
                    Intent intent = new Intent(getApplicationContext(), SearchMakeupMatrix.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("Toolbar", "" + difficult);
                    intent.putExtra("Request", "");
                    intent.putStringArrayListExtra("Colors", sortMakeupColors(makeupColors));
                    intent.putExtra("EyeColor", "");
                    intent.putExtra("Difficult", difficult);
                    intent.putExtra("Occasion", "0");
                    startActivity(intent);
                    FireAnal.sendString("2", "SearchMakeupMatrixParamDifficult", difficult);
                }
            });
            layout.addView(imageView);
            layout.addView(text);
            return layout;
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
                    if (Objects.equals(type, "Occasion")) {
                        String[] occasion = getResources().getStringArray(R.array.occasions);
                        ArrayList<String> makeupColors = new ArrayList<>();
                        Intent intent = new Intent(getApplicationContext(), SearchMakeupMatrix.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("Toolbar", "" + occasion[Integer.valueOf(index)]);
                        intent.putExtra("Request", "");
                        intent.putStringArrayListExtra("Colors", sortMakeupColors(makeupColors));
                        intent.putExtra("EyeColor", "");
                        intent.putExtra("Difficult", "");
                        intent.putExtra("Occasion", "" + index);
                        startActivity(intent);
                        FireAnal.sendString("2", "SearchMakeupMatrixParamOccasion", index);
                    }
                }
            });
            return tw;
        }

        String colorName;

        private LinearLayout createImage(final String color) {
            LinearLayout layout = new LinearLayout(getApplicationContext());
            layout.setOrientation(LinearLayout.HORIZONTAL);
            layout.setVerticalGravity(Gravity.CENTER_VERTICAL);

            ImageView imageView = new ImageView(getApplicationContext());
            TextView title = new TextView(getApplicationContext());
            title.setTextSize(14);
            title.setTextColor(Color.argb(255, 100, 100, 100));
            title.setPadding(16, 0, 0, 0);
            if (color.equals("black")) {
                colorName = "black";
                imageView.setImageResource(R.mipmap.eye_black);
                title.setText(R.string.black_eyes);
            }
            if (color.equals("blue")) {
                colorName = "blue";
                imageView.setImageResource(R.mipmap.eye_blue);
                title.setText(R.string.blue_eyes);
            }
            if (color.equals("brown")) {
                colorName = "brown";
                imageView.setImageResource(R.mipmap.eye_brown);
                title.setText(R.string.brown_eyes);
            }
            if (color.equals("gray")) {
                colorName = "gray";
                imageView.setImageResource(R.mipmap.eye_gray);
                title.setText(R.string.gray_eyes);
            }
            if (color.equals("green")) {
                colorName = "green";
                imageView.setImageResource(R.mipmap.eye_green);
                title.setText(R.string.green_eyes);
            }
            if (color.equals("hazel")) {
                colorName = "hazel";
                imageView.setImageResource(R.mipmap.eye_hazel);
                title.setText(R.string.hazel_eyes);
            }
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<String> makeupColors = new ArrayList<>();
                    Intent intent = new Intent(getApplicationContext(), SearchMakeupMatrix.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("Toolbar", "" + colorName);
                    intent.putExtra("Request", "");
                    intent.putStringArrayListExtra("Colors", sortMakeupColors(makeupColors));
                    intent.putExtra("EyeColor", color);
                    intent.putExtra("Difficult", "");
                    intent.putExtra("Occasion", "0");
                    startActivity(intent);
                    FireAnal.sendString("2", "SearchMakeupMatrixParamEyeColor", color);
                }
            });
            layout.addView(imageView);
            layout.addView(title);
            return layout;
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
                    ArrayList<String> makeupColors = new ArrayList<>();
                    makeupColors.add(searchParameter);
                    Intent intent = new Intent(getApplicationContext(), SearchMakeupMatrix.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("Request", "");
                    intent.putStringArrayListExtra("Colors", sortMakeupColors(makeupColors));
                    intent.putExtra("EyeColor", "");
                    intent.putExtra("Difficult", "");
                    intent.putExtra("Occasion", "0");
                    startActivity(intent);
                    FireAnal.sendString("2", "SearchMakeupMatrixParamColor", color);
                }
            });

            return imageView;
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
                URL feedURL = new URL("http://195.88.209.17/app/in/favoritesMakeup.php?email=" + email);
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
