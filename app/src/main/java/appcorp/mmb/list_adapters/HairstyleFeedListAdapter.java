package appcorp.mmb.list_adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.NativeExpressAdView;
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

import appcorp.mmb.R;
import appcorp.mmb.activities.other.FullscreenPreview;
import appcorp.mmb.activities.search_feeds.SearchHairstyleMatrix;
import appcorp.mmb.activities.user.SignIn;
import appcorp.mmb.classes.FireAnal;
import appcorp.mmb.classes.Intermediates;
import appcorp.mmb.classes.Storage;
import appcorp.mmb.dto.HairstyleDTO;
import appcorp.mmb.network.GetRequest;

public class HairstyleFeedListAdapter extends RecyclerView.Adapter<HairstyleFeedListAdapter.TapeViewHolder> {

    private List<HairstyleDTO> data;
    private List<Long> likes = new ArrayList<>();
    private Context context;

    public HairstyleFeedListAdapter(List<HairstyleDTO> data, Context context) {
        this.data = data;
        this.context = context;

        Storage.init(context);

        if (!Storage.getString("E-mail", "").equals(""))
            new CheckLikes(Storage.getString("E-mail", "")).execute();
    }

    public static String convertToString(Context context, int r) {
        TextView textView = new TextView(context);
        textView.setText(r);
        return textView.getText().toString();
    }

    @Override
    public TapeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tape_item, parent, false);
        return new TapeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TapeViewHolder holder, int position) {
        holder.imageViewer.removeAllViews();
        holder.hashTags.removeAllViews();
        if (position % 8 == 0 && position != 0) {
            holder.post.removeAllViews();
            NativeExpressAdView nativeExpressAdView = new NativeExpressAdView(context);
            nativeExpressAdView.setAdUnitId("ca-app-pub-4151792091524133/1939808891");
            nativeExpressAdView.setAdSize(AdSize.MEDIUM_RECTANGLE);
            nativeExpressAdView.loadAd(new AdRequest.Builder().build());
            holder.post.addView(nativeExpressAdView);
        } else {
            final HairstyleDTO item = data.get(position);

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
                            new GetRequest("http://195.88.209.17/app/in/hairstyleLike.php?id=" + item.getId() + "&email=" + Storage.getString("E-mail", "")).execute();
                        } else if (likes.contains(item.getId())) {
                            holder.addLike.setBackgroundResource(R.mipmap.ic_heart_outline);
                            likes.remove(item.getId());
                            holder.likesCount.setText(String.valueOf(holder.likesCount.getText()));
                            new GetRequest("http://195.88.209.17/app/in/hairstyleDislike.php?id=" + item.getId() + "&email=" + Storage.getString("E-mail", "")).execute();
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

            for (int i = 0; i < item.getHashTags().size(); i++) {
                TextView hashTag = new TextView(context);
                hashTag.setTextColor(Color.argb(255, 51, 102, 153));
                hashTag.setTextSize(14);
                final int finalI = i;
                if (!item.getHashTags().get(i).toLowerCase().equals("")) {
                    hashTag.setText("#" + item.getHashTags().get(i).toLowerCase() + " ");
                }
                hashTag.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        context.startActivity(new Intent(context, SearchHairstyleMatrix.class)
                                .putExtra("Toolbar", item.getHashTags().get(finalI))
                                .putExtra("Request", item.getHashTags().get(finalI))
                                .putExtra("HairstyleLength", "0")
                                .putExtra("HairstyleType", "0")
                                .putExtra("HairstyleFor", "0")
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        FireAnal.sendString("2", "HairstyleFeedTag", item.getHashTags().get(finalI));
                    }
                });
                holder.hashTags.addView(hashTag);
            }

            holder.countImages.removeAllViews();
            for (int i = 0; i < item.getImages().size(); i++) {
                ImageView screenShot = new ImageView(context);
                screenShot.setMinimumWidth(Storage.getInt("Width", 480));
                screenShot.setMinimumHeight(Storage.getInt("Width", 480));
                screenShot.setPadding(0, 0, 1, 0);
                screenShot.setBackgroundColor(Color.argb(255, 200, 200, 200));
                Picasso.with(context).load("http://195.88.209.17/storage/images/" + item.getImages().get(i)).resize(Storage.getInt("Width", 480), Storage.getInt("Width", 480)).onlyScaleDown().into(screenShot);

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

                        switch (item.getHlenght()) {
                            case "short":
                                moreContainer.addView(createText(convertToString(context, R.string.shortHairstyle), 16, "Length", "1"));
                                break;
                            case "medium":
                                moreContainer.addView(createText(convertToString(context, R.string.mediumHairstyle), 16, "Length", "2"));
                                break;
                            case "long":
                                moreContainer.addView(createText(convertToString(context, R.string.longHairstyle), 16, "Length", "3"));
                                break;
                        }

                        switch (item.getHtype()) {
                            case "straight":
                                moreContainer.addView(createText(convertToString(context, R.string.straightHairstyleType), 16, "Type", "1"));
                                break;
                            case "braid":
                                moreContainer.addView(createText(convertToString(context, R.string.braidHairstyleType), 16, "Type", "2"));
                                break;
                            case "tail":
                                moreContainer.addView(createText(convertToString(context, R.string.tailHairstyleType), 16, "Type", "3"));
                                break;
                            case "bunch":
                                moreContainer.addView(createText(convertToString(context, R.string.bunchHairstyleType), 16, "Type", "4"));
                                break;
                            case "netting":
                                moreContainer.addView(createText(convertToString(context, R.string.nettingHairstyleType), 16, "Type", "5"));
                                break;
                            case "curls":
                                moreContainer.addView(createText(convertToString(context, R.string.curlsHairstyleType), 16, "Type", "6"));
                                break;
                            case "unstandart":
                                moreContainer.addView(createText(convertToString(context, R.string.unstandartHairstyleType), 16, "Type", "7"));
                                break;
                        }

                        switch (item.getHfor()) {
                            case "kids":
                                moreContainer.addView(createText(convertToString(context, R.string.forKids), 16, "For", "1"));
                                break;
                            case "everyday":
                                moreContainer.addView(createText(convertToString(context, R.string.forEveryday), 16, "For", "2"));
                                break;
                            case "wedding":
                                moreContainer.addView(createText(convertToString(context, R.string.forWedding), 16, "For", "3"));
                                break;
                            case "evening":
                                moreContainer.addView(createText(convertToString(context, R.string.forEvening), 16, "For", "4"));
                                break;
                            case "exclusive":
                                moreContainer.addView(createText(convertToString(context, R.string.forExclusive), 16, "For", "5"));
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
                if (Objects.equals(type, "Length")) {
                    String[] length = context.getResources().getStringArray(R.array.hairstyleLength);
                    Intent intent = new Intent(context, SearchHairstyleMatrix.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("Toolbar", "" + length[Integer.valueOf(index)]);
                    intent.putExtra("Request", "");
                    intent.putExtra("HairstyleLength", "" + index);
                    intent.putExtra("HairstyleType", "0");
                    intent.putExtra("HairstyleFor", "0");
                    context.startActivity(intent);
                    FireAnal.sendString("2", "HairstyleFeedParamLength", index);
                }
                if (Objects.equals(type, "Type")) {
                    String[] type = context.getResources().getStringArray(R.array.hairstyleType);
                    Intent intent = new Intent(context, SearchHairstyleMatrix.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("Toolbar", "" + type[Integer.valueOf(index)]);
                    intent.putExtra("Request", "");
                    intent.putExtra("HairstyleLength", "0");
                    intent.putExtra("HairstyleType", "" + index);
                    intent.putExtra("HairstyleFor", "0");
                    context.startActivity(intent);
                    FireAnal.sendString("2", "HairstyleFeedParamType", index);
                }
                if (Objects.equals(type, "For")) {
                    String[] hfor = context.getResources().getStringArray(R.array.hairstyleFor);
                    Intent intent = new Intent(context, SearchHairstyleMatrix.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("Toolbar", "" + hfor[Integer.valueOf(index)]);
                    intent.putExtra("Request", "");
                    intent.putExtra("HairstyleLength", "0");
                    intent.putExtra("HairstyleType", "0");
                    intent.putExtra("HairstyleFor", "" + index);
                    context.startActivity(intent);
                    FireAnal.sendString("2", "HairstyleFeedParamFor", index);
                }
            }
        });
        return tw;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<HairstyleDTO> data) {
        this.data = data;
    }

    public static class TapeViewHolder extends RecyclerView.ViewHolder {
        TextView title, availableDate, showMore, likesCount;
        LinearLayout imageViewer, countImages, hashTags, moreContainer, post, postFrame;
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
            post = (LinearLayout) itemView.findViewById(R.id.post);
            postFrame = (LinearLayout) itemView.findViewById(R.id.postFrame);
        }
    }

    private class CheckLikes extends AsyncTask<Void, Void, String> {

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

    public class Load extends AsyncTask<Void, Void, List<JSONArray>> {

        private HttpURLConnection urlFeedConnection = null;
        private BufferedReader reader = null;
        private int position;
        private List<JSONArray> dataArrays = new ArrayList<>();

        Load(int position) {
            this.position = position;
        }

        @Override
        protected List<JSONArray> doInBackground(Void... params) {
            try {
                URL feedURL = new URL("http://195.88.209.17/app/static/hairstyle" + position + ".html");
                urlFeedConnection = (HttpURLConnection) feedURL.openConnection();
                urlFeedConnection.setRequestMethod("GET");
                urlFeedConnection.connect();
                InputStream inputStream = urlFeedConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer buffer;
                buffer = new StringBuffer();
                String line;
                while ((line = reader.readLine()) != null)
                    buffer.append(line);
                dataArrays.add(new JSONArray(String.valueOf(buffer)));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return dataArrays;
        }

        @Override
        protected void onPostExecute(List<JSONArray> dataArrays) {
            super.onPostExecute(dataArrays);

            List<JSONObject> items = new ArrayList<>();
            List<HairstyleDTO> exportData = new ArrayList<>();

            try {
                for (int i = 0; i < dataArrays.get(0).length(); i++) {
                    if (dataArrays.get(0).getJSONObject(i) != null)
                        items.add(dataArrays.get(0).getJSONObject(i));
                }
                for (int i = 0; i < items.size(); i++) {
                    List<String> images = new ArrayList<>();

                    if (!items.get(i).has("videoSource")) {
                        for (int j = 0; j < 10; j++)
                            if (!items.get(i).getString("screen" + j).equals("empty.jpg"))
                                images.add(items.get(i).getString("screen" + j));

                        List<String> tags = new ArrayList<>();
                        if (Storage.getString("Localization", "").equals("English")) {
                            Collections.addAll(tags, items.get(i).getString("tags").split(","));
                        } else if (Storage.getString("Localization", "").equals("Russian")) {
                            Collections.addAll(tags, items.get(i).getString("tagsRu").split(","));
                        }

                        HairstyleDTO post = new HairstyleDTO(
                                items.get(i).getLong("id"),
                                "content",
                                items.get(i).getString("uploadDate"),
                                items.get(i).getString("authorName"),
                                items.get(i).getString("authorPhoto"),
                                items.get(i).getString("hairstyleType"),
                                images,
                                tags,
                                items.get(i).getLong("likes"),
                                items.get(i).getString("length"),
                                items.get(i).getString("type"),
                                items.get(i).getString("for"),
                                0,
                                "",
                                "",
                                "",
                                "",
                                0,
                                "");
                        exportData.add(post);
                    }
                }

                FireAnal.sendString("1", "Open", "HairstyleFeedLoaded");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}