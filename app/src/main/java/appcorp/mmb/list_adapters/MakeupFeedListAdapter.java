package appcorp.mmb.list_adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import appcorp.mmb.R;
import appcorp.mmb.activities.other.FullscreenPreview;
import appcorp.mmb.activities.search_feeds.SearchMakeupMatrix;
import appcorp.mmb.activities.user.SignIn;
import appcorp.mmb.classes.FireAnal;
import appcorp.mmb.classes.Intermediates;
import appcorp.mmb.classes.Storage;
import appcorp.mmb.dto.MakeupDTO;
import appcorp.mmb.network.GetRequest;

public class MakeupFeedListAdapter extends RecyclerView.Adapter<MakeupFeedListAdapter.TapeViewHolder> {

    private List<MakeupDTO> data;
    private List<Long> likes = new ArrayList<>();
    private Context context;

    public MakeupFeedListAdapter(List<MakeupDTO> data, Context context) {
        this.data = data;
        this.context = context;

        Storage.init(context);
        initFirebase();

        if (!Storage.getString("E-mail", "").equals(""))
            new CheckLikes(Storage.getString("E-mail", "")).execute();
    }

    public static String convertToString(Context context, int r) {
        TextView textView = new TextView(context);
        textView.setText(r);
        return textView.getText().toString();
    }

    private void initFirebase() {
        FireAnal.setContext(context);
    }

    @Override
    public TapeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tape_item, parent, false);
        return new TapeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TapeViewHolder holder, int position) {
        final MakeupDTO item = data.get(position);

        if (position == data.size() - 1) {
            if (data.size() - 1 % 100 != 8)
                new Load(data.size() / 100 + 1).execute();
        }

        final String SHOW = convertToString(context, R.string.show_more_container);
        final String HIDE = convertToString(context, R.string.hide_more_container);

        String[] date = item.getAvailableDate().split("");

        holder.title.setText(item.getAuthorName());
        holder.availableDate.setText(date[1] + date[2] + "-" + date[3] + date[4] + "-" + date[5] + date[6] + " " + date[7] + date[8] + ":" + date[9] + date[10]);
        holder.likesCount.setText(String.valueOf(item.getLikes()));
        if (!likes.contains(item.getId())) {
            holder.addLike.setBackgroundResource(R.mipmap.ic_heart_outline);
        } else {
            holder.addLike.setBackgroundResource(R.mipmap.ic_heart);
            holder.likesCount.setText(String.valueOf(item.getLikes() + 1));
        }

        holder.addLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Storage.getString("E-mail", "").equals("")) {
                    if (!likes.contains(item.getId())) {
                        holder.addLike.setBackgroundResource(R.mipmap.ic_heart);
                        likes.add(item.getId());
                        holder.likesCount.setText(String.valueOf(item.getLikes() + 1));
                        new GetRequest("http://195.88.209.17/app/in/makeupLike.php?id=" + item.getId() + "&email=" + Storage.getString("E-mail", "")).execute();
                    } else if (likes.contains(item.getId())) {
                        holder.addLike.setBackgroundResource(R.mipmap.ic_heart_outline);
                        likes.remove(item.getId());
                        holder.likesCount.setText(String.valueOf(holder.likesCount.getText()));
                        new GetRequest("http://195.88.209.17/app/in/makeupDislike.php?id=" + item.getId() + "&email=" + Storage.getString("E-mail", "")).execute();
                    }
                } else {
                    context.startActivity(new Intent(context, SignIn.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            }
        });

        Picasso.with(context).load("http://195.88.209.17/storage/photos/" + item.getAuthorPhoto()).into(holder.user_avatar);
        /*holder.user_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Profile.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("sid", item.getSid());
                new Profile(item.getSid());
                context.startActivity(intent);
            }
        });*/

        holder.hashTags.removeAllViews();
        for (int i = 0; i < item.getHashTags().size(); i++) {
            TextView hashTag = new TextView(context);
            hashTag.setTextColor(Color.argb(255, 51, 102, 153));
            hashTag.setTextSize(14);
            final int finalI = i;
            if (!item.getHashTags().get(i).equals(""))
                hashTag.setText("#" + item.getHashTags().get(i) + " ");
            hashTag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(context, SearchMakeupMatrix.class)
                            .putExtra("Category", "makeup")
                            .putExtra("Toolbar", String.valueOf(item.getHashTags().get(finalI)))
                            .putExtra("Request", String.valueOf(item.getHashTags().get(finalI)))
                            .putStringArrayListExtra("Colors", new ArrayList<String>())
                            .putExtra("EyeColor", "")
                            .putExtra("Difficult", "")
                            .putExtra("Occasion", "0")
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    FireAnal.sendString("2", "MakeupFeedTag", item.getHashTags().get(finalI));
                }
            });
            holder.hashTags.addView(hashTag);
        }

        holder.imageViewer.removeAllViews();
        holder.countImages.removeAllViews();
        for (int i = 0; i < item.getImages().size(); i++) {
            ImageView screenShot = new ImageView(context);
            screenShot.setMinimumWidth(Storage.getInt("Width", 480));
            screenShot.setMinimumHeight(Storage.getInt("Width", 480));
            screenShot.setPadding(0, 0, 1, 0);
            screenShot.setBackgroundColor(Color.argb(255, 200, 200, 200));
            Picasso.with(context).load("http://195.88.209.17/storage/images/" + item.getImages().get(i)).resize(Storage.getInt("Width", 480), Storage.getInt("Width", 480)).centerCrop().into(screenShot);

            screenShot.setScaleType(ImageView.ScaleType.CENTER_CROP);
            final int finalI = i;
            screenShot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.showMore.getText().equals(SHOW)) {
                        Intent intent = new Intent(context, FullscreenPreview.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("screenshot", "http://195.88.209.17/storage/images/" + item.getImages().get(finalI));
                        context.startActivity(intent);
                    }
                }
            });
            holder.imageViewer.addView(screenShot);
            holder.imageViewerHorizontal.scrollTo(0, 0);

            LinearLayout countLayout = new LinearLayout(context);
            countLayout.setLayoutParams(new ViewGroup.LayoutParams(Storage.getInt("Width", 480), Storage.getInt("Width", 480)));
            TextView count = new TextView(context);
            count.setText("< " + (i + 1) + "/" + item.getImages().size() + " >");
            count.setTextSize(20);
            count.setTextColor(Color.WHITE);
            count.setPadding(32, 32, 32, 32);
            count.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Galada.ttf"));
            countLayout.addView(count);
            holder.countImages.addView(countLayout);
        }

        holder.moreContainer.removeAllViews();
        holder.showMore.setText(SHOW);
        holder.showMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (holder.showMore.getText().equals(SHOW)) {
                    holder.showMore.setText(HIDE);
                    LinearLayout moreContainer = new LinearLayout(context);
                    moreContainer.setLayoutParams(new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    moreContainer.setOrientation(LinearLayout.VERTICAL);
                    moreContainer.setPadding(32, 32, 32, 0);

                    moreContainer.addView(createText(convertToString(context, R.string.title_eye_color), 16, "", ""));
                    moreContainer.addView(createImage(item.getEye_color()));
                    moreContainer.addView(createText(convertToString(context, R.string.title_used_colors), 16, "", ""));
                    LinearLayout colors = new LinearLayout(context);
                    colors.setOrientation(LinearLayout.HORIZONTAL);
                    String[] mColors = (item.getColors().split(","));
                    for (String mColor : mColors) {
                        if (!mColor.equals("FFFFFF"))
                            colors.addView(createCircle("#" + mColor, mColor));
                        else
                            colors.addView(createCircle("#EEEEEE", mColor));
                    }
                    moreContainer.addView(colors);

                    moreContainer.addView(createText(convertToString(context, R.string.title_difficult), 16, "", ""));
                    moreContainer.addView(difficult(item.getDifficult()));
                    switch (item.getOccasion()) {
                        case "everyday":
                            moreContainer.addView(createText(convertToString(context, R.string.occasion_everyday), 16, "Occasion", "1"));
                            break;
                        case "celebrity":
                            moreContainer.addView(createText(convertToString(context, R.string.occasion_everyday), 16, "Occasion", "2"));
                            break;
                        case "dramatic":
                            moreContainer.addView(createText(convertToString(context, R.string.occasion_dramatic), 16, "Occasion", "3"));
                            break;
                        case "holiday":
                            moreContainer.addView(createText(convertToString(context, R.string.occasion_holiday), 16, "Occasion", "4"));
                            break;
                    }

                    holder.moreContainer.addView(moreContainer);
                } else if (holder.showMore.getText().equals(HIDE)) {
                    holder.showMore.setText(SHOW);
                    holder.moreContainer.removeAllViews();
                }
            }
        });
    }


    private TextView createText(String title, int padding, final String type, final String index) {
        TextView tw = new TextView(context);
        tw.setText(String.valueOf(title));
        tw.setPadding(0, padding, 0, padding);
        tw.setTextSize(14);
        tw.setTextColor(Color.argb(255, 50, 50, 50));
        tw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Objects.equals(type, "Occasion")) {
                    String[] occasion = context.getResources().getStringArray(R.array.occasions);
                    ArrayList<String> makeupColors = new ArrayList<>();
                    Intent intent = new Intent(context, SearchMakeupMatrix.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("Toolbar", ""+ occasion[Integer.valueOf(index)]);
                    intent.putExtra("Request", "");
                    intent.putStringArrayListExtra("Colors", sortMakeupColors(makeupColors));
                    intent.putExtra("EyeColor", "");
                    intent.putExtra("Difficult", "");
                    intent.putExtra("Occasion", "" + index);
                    context.startActivity(intent);
                    FireAnal.sendString("2", "MakeupFeedParamOccasion", index);
                }
            }
        });
        return tw;
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

    private String diff;
    private LinearLayout difficult(final String difficult) {
        ImageView imageView = new ImageView(context);
        LinearLayout layout = new LinearLayout(context);
        layout.setVerticalGravity(Gravity.CENTER_VERTICAL);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        TextView text = new TextView(context);
        text.setTextSize(14);
        text.setTextColor(Color.argb(255, 100, 100, 100));
        text.setPadding(16, 0, 0, 0);
        if (difficult.equals("easy")) {
            imageView.setImageResource(R.mipmap.easy);
            text.setText(R.string.difficult_easy);
            diff = convertToString(context, R.string.difficult_easy);
        }
        if (difficult.equals("medium")) {
            imageView.setImageResource(R.mipmap.medium);
            text.setText(R.string.difficult_medium);
            diff = convertToString(context, R.string.difficult_medium);
        }
        if (difficult.equals("hard")) {
            imageView.setImageResource(R.mipmap.hard);
            text.setText(R.string.difficult_hard);
            diff = convertToString(context, R.string.difficult_hard);
        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> makeupColors = new ArrayList<>();
                Intent intent = new Intent(context, SearchMakeupMatrix.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("Toolbar", ""+diff);
                intent.putExtra("Request", "");
                intent.putStringArrayListExtra("Colors", sortMakeupColors(makeupColors));
                intent.putExtra("EyeColor", "");
                intent.putExtra("Difficult", difficult);
                intent.putExtra("Occasion", "0");
                context.startActivity(intent);
                FireAnal.sendString("2", "MakeupFeedParamDifficult", difficult);
            }
        });
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> makeupColors = new ArrayList<>();
                Intent intent = new Intent(context, SearchMakeupMatrix.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("Toolbar", ""+diff);
                intent.putExtra("Request", "");
                intent.putStringArrayListExtra("Colors", sortMakeupColors(makeupColors));
                intent.putExtra("EyeColor", "");
                intent.putExtra("Difficult", difficult);
                intent.putExtra("Occasion", "0");
                context.startActivity(intent);
                FireAnal.sendString("2", "MakeupFeedParamDifficult", difficult);
            }
        });
        layout.addView(imageView);
        layout.addView(text);
        return layout;
    }

    private ImageView createCircle(final String color, final String searchParameter) {
        ImageView imageView = new ImageView(context);
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
                Intent intent = new Intent(context, SearchMakeupMatrix.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("Request", "");
                intent.putStringArrayListExtra("Colors", sortMakeupColors(makeupColors));
                intent.putExtra("EyeColor", "");
                intent.putExtra("Difficult", "");
                intent.putExtra("Occasion", "0");
                context.startActivity(intent);
                FireAnal.sendString("2", "MakeupFeedParamColor", color);
            }
        });

        return imageView;
    }


    private String colorName;
    private LinearLayout createImage(final String color) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setVerticalGravity(Gravity.CENTER_VERTICAL);

        ImageView imageView = new ImageView(context);
        TextView title = new TextView(context);
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
                Intent intent = new Intent(context, SearchMakeupMatrix.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("Toolbar", ""+colorName);
                intent.putExtra("Request", "");
                intent.putStringArrayListExtra("Colors", sortMakeupColors(makeupColors));
                intent.putExtra("EyeColor", color);
                intent.putExtra("Difficult", "");
                intent.putExtra("Occasion", "0");
                context.startActivity(intent);
                FireAnal.sendString("2", "MakeupFeedParamEyeColor", color);
            }
        });
        layout.addView(imageView);
        layout.addView(title);
        return layout;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<MakeupDTO> data) {
        this.data = data;
    }

    public static class TapeViewHolder extends RecyclerView.ViewHolder {
        TextView title, availableDate, showMore, likesCount;
        LinearLayout imageViewer, countImages, hashTags, moreContainer;
        ImageView user_avatar, addLike;
        HorizontalScrollView imageViewerHorizontal;

        public TapeViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            availableDate = (TextView) itemView.findViewById(R.id.available_date);
            showMore = (TextView) itemView.findViewById(R.id.show_more);
            imageViewer = (LinearLayout) itemView.findViewById(R.id.imageViewer);
            imageViewerHorizontal = (HorizontalScrollView) itemView.findViewById(R.id.imageViewerHorizontal);
            countImages = (LinearLayout) itemView.findViewById(R.id.countImages);
            hashTags = (LinearLayout) itemView.findViewById(R.id.hash_tags);
            likesCount = (TextView) itemView.findViewById(R.id.likesCount);
            user_avatar = (ImageView) itemView.findViewById(R.id.user_avatar);
            moreContainer = (LinearLayout) itemView.findViewById(R.id.moreContainer);
            addLike = (ImageView) itemView.findViewById(R.id.addLike);
        }
    }

    private class CheckLikes extends AsyncTask<Void, Void, String> {

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
                AtomicReference<StringBuffer> profileBuffer = new AtomicReference<>(new StringBuffer());
                String profileLine;
                while ((profileLine = reader.readLine()) != null) {
                    profileBuffer.get().append(profileLine);
                }
                result = profileBuffer.get().toString();
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

    private class Load extends AsyncTask<Void, Void, String> {

        private HttpURLConnection urlFeedConnection = null;
        private BufferedReader reader = null;
        private String resultJsonFeed = "";
        private int position;

        Load(int position) {
            this.position = position;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                if (position == 1) {
                    URL feedURL = new URL("http://195.88.209.17/app/static/makeup" + position + ".html");
                    urlFeedConnection = (HttpURLConnection) feedURL.openConnection();
                    urlFeedConnection.setRequestMethod("GET");
                    urlFeedConnection.connect();
                    InputStream inputStream = urlFeedConnection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder buffer = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null)
                        buffer.append(line);
                    resultJsonFeed += buffer.toString();
                } else {
                    for (int i = 1; i <= position; i++) {
                        URL feedURL = new URL("http://195.88.209.17/app/static/makeup" + i + ".html");
                        urlFeedConnection = (HttpURLConnection) feedURL.openConnection();
                        urlFeedConnection.setRequestMethod("GET");
                        urlFeedConnection.connect();
                        InputStream inputStream = urlFeedConnection.getInputStream();
                        reader = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder buffer = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null)
                            buffer.append(line);
                        resultJsonFeed += buffer.toString();
                        resultJsonFeed = resultJsonFeed.replace("][", ",");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJsonFeed;
        }

        @Override
        protected void onPostExecute(String resultJsonFeed) {
            super.onPostExecute(resultJsonFeed);

            try {
                JSONArray items = new JSONArray(resultJsonFeed);

                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);
                    List<String> images = new ArrayList<>();
                    List<String> hashTags = new ArrayList<>();

                    for (int j = 0; j < 10; j++)
                        if (!item.getString("screen" + j).equals("empty.jpg"))
                            images.add(item.getString("screen" + j));

                    String[] tempTags;
                    if (Storage.getString("Localization", "").equals("English")) {
                        tempTags = item.getString("tags").split(",");
                        Collections.addAll(hashTags, tempTags);
                    } else if (Storage.getString("Localization", "").equals("Russian")) {
                        tempTags = item.getString("tagsRu").split(",");
                        Collections.addAll(hashTags, tempTags);
                    }

                    if (item.getString("published").equals("t") && !images.isEmpty()) {
                        MakeupDTO makeupDTO = new MakeupDTO(
                                item.getLong("id"),
                                item.getString("uploadDate"),
                                item.getString("authorName"),
                                item.getString("authorPhoto"),
                                images,
                                item.getString("colors"),
                                item.getString("eyeColor"),
                                item.getString("occasion"),
                                item.getString("difficult"),
                                hashTags,
                                item.getLong("likes"));
                        data.add(makeupDTO);
                    }
                }
                FireAnal.sendString("1", "Open", "MakeupFeedLoader");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}