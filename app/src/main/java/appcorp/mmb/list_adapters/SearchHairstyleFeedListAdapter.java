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
import android.widget.Toast;

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
import appcorp.mmb.activities.search_feeds.Search;
import appcorp.mmb.activities.search_feeds.SearchHairstyleFeed;
import appcorp.mmb.activities.user.SignIn;
import appcorp.mmb.classes.FireAnal;
import appcorp.mmb.classes.Intermediates;
import appcorp.mmb.classes.Storage;
import appcorp.mmb.dto.HairstyleDTO;
import appcorp.mmb.network.GetRequest;

public class SearchHairstyleFeedListAdapter extends RecyclerView.Adapter<SearchHairstyleFeedListAdapter.TapeViewHolder> {

    private List<HairstyleDTO> data;
    private List<Long> likes = new ArrayList<>();
    private Context context;
    private String request = "", hairstyleLength = "", hairstyleType = "", hairstyleFor = "";

    public SearchHairstyleFeedListAdapter(List<HairstyleDTO> data, Context context) {
        this.data = data;
        this.context = context;
        Storage.init(context);
        request = Storage.getString("SearchTempHairstyleRequest", "");
        hairstyleLength = Storage.getString("SearchTempHairstyleLength", "");
        hairstyleType = Storage.getString("SearchTempHairstyleType", "");
        hairstyleFor = Storage.getString("SearchTempHairstyleFor", "");
        switch (hairstyleLength) {
            case "0":
                this.hairstyleLength = "";
                break;
            case "1":
                this.hairstyleLength = "short";
                break;
            case "2":
                this.hairstyleLength = "medium";
                break;
            case "3":
                this.hairstyleLength = "long";
                break;
        }

        switch (hairstyleType) {
            case "0":
                this.hairstyleType = "";
                break;
            case "1":
                this.hairstyleType = "straight";
                break;
            case "2":
                this.hairstyleType = "braid";
                break;
            case "3":
                this.hairstyleType = "tail";
                break;
            case "4":
                this.hairstyleType = "bunch";
                break;
            case "5":
                this.hairstyleType = "netting";
                break;
            case "6":
                this.hairstyleType = "curls";
                break;
            case "7":
                this.hairstyleType = "custom";
                break;
        }

        switch (hairstyleFor) {
            case "0":
                this.hairstyleFor = "";
                break;
            case "1":
                this.hairstyleFor = "kids";
                break;
            case "2":
                this.hairstyleFor = "everyday";
                break;
            case "3":
                this.hairstyleFor = "wedding";
                break;
            case "4":
                this.hairstyleFor = "evening";
                break;
            case "5":
                this.hairstyleFor = "exclusive";
                break;
        }
        Storage.init(context);
        initFirebase();

        if (!Storage.getString("Name", "Make Me Beauty").equals("Make Me Beauty"))
            new CheckLikes(Storage.getString("E-mail", "")).execute();
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
        final HairstyleDTO item = data.get(position);

        if (item.getId() == -1) {
            context.startActivity(new Intent(context, Search.class)
                    .putExtra("from", "hairstyleFeed")
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            Toast.makeText(context, R.string.notFindResult, Toast.LENGTH_LONG).show();
        } else {
            if (position == data.size() - 1) {
                new Load(request, hairstyleLength, hairstyleType, hairstyleFor, data.size() / 100 + 1).execute();
            }

            final String SHOW = Intermediates.convertToString(context, R.string.show_more_container);
            final String HIDE = Intermediates.convertToString(context, R.string.hide_more_container);

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
                    if (!Storage.getString("Name", "Make Me Beauty").equals("Make Me Beauty")) {
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

            holder.hashTags.removeAllViews();
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
                        context.startActivity(new Intent(context, SearchHairstyleFeed.class)
                                .putExtra("Request", item.getHashTags().get(finalI))
                                .putExtra("HairstyleLength", "0")
                                .putExtra("HairstyleType", "0")
                                .putExtra("HairstyleFor", "0")
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        FireAnal.sendString("2", "SearchHairstyleFeedTag", item.getHashTags().get(finalI));
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
                                moreContainer.addView(createText(Intermediates.convertToString(context, R.string.shortHairstyle), 16, "Length", "1"));
                                break;
                            case "medium":
                                moreContainer.addView(createText(Intermediates.convertToString(context, R.string.mediumHairstyle), 16, "Length", "2"));
                                break;
                            case "long":
                                moreContainer.addView(createText(Intermediates.convertToString(context, R.string.longHairstyle), 16, "Length", "3"));
                                break;
                        }

                        switch (item.getHtype()) {
                            case "straight":
                                moreContainer.addView(createText(Intermediates.convertToString(context, R.string.straightHairstyleType), 16, "Type", "1"));
                                break;
                            case "braid":
                                moreContainer.addView(createText(Intermediates.convertToString(context, R.string.braidHairstyleType), 16, "Type", "2"));
                                break;
                            case "tail":
                                moreContainer.addView(createText(Intermediates.convertToString(context, R.string.tailHairstyleType), 16, "Type", "3"));
                                break;
                            case "bunch":
                                moreContainer.addView(createText(Intermediates.convertToString(context, R.string.bunchHairstyleType), 16, "Type", "4"));
                                break;
                            case "netting":
                                moreContainer.addView(createText(Intermediates.convertToString(context, R.string.nettingHairstyleType), 16, "Type", "5"));
                                break;
                            case "curls":
                                moreContainer.addView(createText(Intermediates.convertToString(context, R.string.curlsHairstyleType), 16, "Type", "6"));
                                break;
                            case "unstandart":
                                moreContainer.addView(createText(Intermediates.convertToString(context, R.string.unstandartHairstyleType), 16, "Type", "7"));
                                break;
                        }

                        switch (item.getHfor()) {
                            case "kids":
                                moreContainer.addView(createText(Intermediates.convertToString(context, R.string.forKids), 16, "For", "1"));
                                break;
                            case "everyday":
                                moreContainer.addView(createText(Intermediates.convertToString(context, R.string.forEveryday), 16, "For", "2"));
                                break;
                            case "wedding":
                                moreContainer.addView(createText(Intermediates.convertToString(context, R.string.forWedding), 16, "For", "3"));
                                break;
                            case "evening":
                                moreContainer.addView(createText(Intermediates.convertToString(context, R.string.forEvening), 16, "For", "4"));
                                break;
                            case "exclusive":
                                moreContainer.addView(createText(Intermediates.convertToString(context, R.string.forExclusive), 16, "For", "5"));
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
                    Intent intent = new Intent(context, SearchHairstyleFeed.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("Toolbar", "" + length[Integer.valueOf(index)]);
                    intent.putExtra("Request", "");
                    intent.putExtra("HairstyleLength", "" + index);
                    intent.putExtra("HairstyleType", "0");
                    intent.putExtra("HairstyleFor", "0");
                    context.startActivity(intent);
                    FireAnal.sendString("2", "SearchHairstyleFeedParamLength", index);
                }
                if (Objects.equals(type, "Type")) {
                    String[] type = context.getResources().getStringArray(R.array.hairstyleType);
                    Intent intent = new Intent(context, SearchHairstyleFeed.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("Toolbar", "" + type[Integer.valueOf(index)]);
                    intent.putExtra("Request", "");
                    intent.putExtra("HairstyleLength", "0");
                    intent.putExtra("HairstyleType", "" + index);
                    intent.putExtra("HairstyleFor", "0");
                    context.startActivity(intent);
                    FireAnal.sendString("2", "SearchHairstyleFeedParamType", index);
                }
                if (Objects.equals(type, "For")) {
                    String[] hfor = context.getResources().getStringArray(R.array.hairstyleFor);
                    Intent intent = new Intent(context, SearchHairstyleFeed.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("Toolbar", "" + hfor[Integer.valueOf(index)]);
                    intent.putExtra("Request", "");
                    intent.putExtra("HairstyleLength", "0");
                    intent.putExtra("HairstyleType", "0");
                    intent.putExtra("HairstyleFor", "" + index);
                    context.startActivity(intent);
                    FireAnal.sendString("2", "SearchHairstyleParamFor", index);
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

    public class Load extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlFeedConnection = null;
        BufferedReader reader = null;
        String resultJsonFeed = "";
        int position;
        private String request, hairstyleLength, hairstyleType, hairstyleFor;

        public Load(String request, String hairstyleLength, String hairstyleType, String hairstyleFor, int position) {
            this.request = request;
            this.hairstyleLength = hairstyleLength;
            this.hairstyleType = hairstyleType;
            this.hairstyleFor = hairstyleFor;
            this.position = position;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                if (position == 1) {
                    URL feedURL = new URL("http://195.88.209.17/search/hairstyle.php?request=" + Intermediates.encodeToURL(request) + "&hairstyle_length=" + hairstyleLength + "&hairstyle_type=" + hairstyleType + "&hairstyle_for=" + hairstyleFor + "&position=" + position);
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
                        URL feedURL = new URL("http://195.88.209.17/search/hairstyle.php?request=" + Intermediates.encodeToURL(request) + "&hairstyle_length=" + hairstyleLength + "&hairstyle_type=" + hairstyleType + "&hairstyle_for=" + hairstyleFor + "&position=" + position);
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
                        resultJsonFeed = resultJsonFeed.replace("][", "");
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

                    String[] tempTags = item.getString("tags").split(",");
                    Collections.addAll(hashTags, tempTags);

                    HairstyleDTO hairstyleDTO = new HairstyleDTO(
                            item.getLong("id"),
                            item.getString("uploadDate"),
                            item.getString("authorName"),
                            item.getString("authorPhoto"),
                            item.getString("hairstyleType"),
                            images,
                            hashTags,
                            item.getLong("likes"),
                            item.getString("length"),
                            item.getString("type"),
                            item.getString("for"));
                    data.add(hairstyleDTO);
                    notifyDataSetChanged();

                    FireAnal.sendString("1", "Open", "SearchHairstyleLoaded");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}