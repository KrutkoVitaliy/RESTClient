package appcorp.mmb.list_adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import appcorp.mmb.R;
import appcorp.mmb.activities.user.Authorization;
import appcorp.mmb.activities.other.FullscreenPreview;
import appcorp.mmb.activities.search_feeds.Search;
import appcorp.mmb.activities.feeds.HairstyleFeed;
import appcorp.mmb.activities.search_feeds.SearchHairstyleFeed;
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
    int width, height;

    public HairstyleFeedListAdapter(List<HairstyleDTO> data, Context context) {
        this.data = data;
        this.context = context;

        Storage.init(context);
        initLocalization(Intermediates.convertToString(context, R.string.translation));
        initScreen();
        initFirebase();

        width = Storage.getInt("Width", 480);
        height = width;
        if (!Storage.getString("E-mail", "").equals(""))
            new CheckLikes(Storage.getString("E-mail", "")).execute();
    }

    private void initScreen() {
        Display display;
        int width, height;
        display = ((WindowManager) context
                .getSystemService(context.WINDOW_SERVICE))
                .getDefaultDisplay();
        width = display.getWidth();
        height = (int) (width * 0.75F);
        Storage.addInt("Width", width);
        Storage.addInt("Height", height);
    }

    private void initFirebase() {
        FireAnal.setContext(context);
    }

    private void initLocalization(final String translation) {
        if (translation.equals("English")) {
            Storage.addString("Localization", "English");
        }

        if (translation.equals("Russian")) {
            Storage.addString("Localization", "Russian");
        }
    }

    @Override
    public TapeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tape_item, parent, false);
        return new TapeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TapeViewHolder holder, int position) {
        final HairstyleDTO item = data.get(position);

        if (position == data.size() - 1) {
            if (data.size() - 1 % 100 != 8)
                HairstyleFeed.addFeed(data.size() / 100 + 1);
        }

        final String SHOW = Intermediates.convertToString(context, R.string.show_more_container);
        final String HIDE = Intermediates.convertToString(context, R.string.hide_more_container);

        String[] date = item.getAvailableDate().split("");

        holder.title.setText(item.getAuthorName());
        holder.availableDate.setText(date[1] + date[2] + "-" + date[3] + date[4] + "-" + date[5] + date[6] + " " + date[7] + date[8] + ":" + date[9] + date[10]);
        holder.likesCount.setText("" + item.getLikes());

        if (!likes.contains(item.getId())) {
            holder.addLike.setBackgroundResource(R.mipmap.ic_heart_outline);
        } else {
            holder.addLike.setBackgroundResource(R.mipmap.ic_heart);
            holder.likesCount.setText("" + (item.getLikes() + 1));
        }

        holder.addLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Storage.getString("E-mail", "").equals("")) {
                    if (!likes.contains(item.getId())) {
                        holder.addLike.setBackgroundResource(R.mipmap.ic_heart);
                        likes.add(item.getId());
                        holder.likesCount.setText("" + (item.getLikes() + 1));
                        new GetRequest("http://195.88.209.17/app/in/hairstyleLike.php?id=" + item.getId() + "&email=" + Storage.getString("E-mail", "")).execute();
                    } else if (likes.contains(item.getId())) {
                        holder.addLike.setBackgroundResource(R.mipmap.ic_heart_outline);
                        likes.remove(item.getId());
                        holder.likesCount.setText("" + (new Long(holder.likesCount.getText().toString()) - 1));
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
                            .putExtra("Toolbar", item.getHashTags().get(finalI))
                            .putExtra("Request", item.getHashTags().get(finalI))
                            .putExtra("HairstyleLength", "0")
                            .putExtra("HairstyleType", "0")
                            .putExtra("HairstyleFor", "0")
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            });
            holder.hashTags.addView(hashTag);
        }

        holder.imageViewer.removeAllViews();
        holder.countImages.removeAllViews();
        for (int i = 0; i < item.getImages().size(); i++) {
            ImageView screenShot = new ImageView(context);
            screenShot.setMinimumWidth(width);
            screenShot.setMinimumHeight(height);
            screenShot.setPadding(0, 0, 1, 0);
            screenShot.setBackgroundColor(Color.argb(255, 200, 200, 200));
            Picasso.with(context).load("http://195.88.209.17/storage/images/" + item.getImages().get(i)).resize(width, height).onlyScaleDown().into(screenShot);

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
            holder.imageViewerHorizontal.scrollTo(0,0);

            LinearLayout countLayout = new LinearLayout(context);
            countLayout.setLayoutParams(new ViewGroup.LayoutParams(width, height));
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

                    if (item.getHlenght().equals("short"))
                        moreContainer.addView(createText(Intermediates.convertToString(context, R.string.shortHairstyle), Typeface.DEFAULT_BOLD, 16, "Length", "1"));
                    else if (item.getHlenght().equals("medium"))
                        moreContainer.addView(createText(Intermediates.convertToString(context, R.string.mediumHairstyle), Typeface.DEFAULT_BOLD, 16, "Length", "2"));
                    else if (item.getHlenght().equals("long"))
                        moreContainer.addView(createText(Intermediates.convertToString(context, R.string.longHairstyle), Typeface.DEFAULT_BOLD, 16, "Length", "3"));

                    if (item.getHtype().equals("straight"))
                        moreContainer.addView(createText(Intermediates.convertToString(context, R.string.straightHairstyleType), Typeface.DEFAULT_BOLD, 16, "Type", "1"));
                    else if (item.getHtype().equals("braid"))
                        moreContainer.addView(createText(Intermediates.convertToString(context, R.string.braidHairstyleType), Typeface.DEFAULT_BOLD, 16, "Type", "2"));
                    else if (item.getHtype().equals("tail"))
                        moreContainer.addView(createText(Intermediates.convertToString(context, R.string.tailHairstyleType), Typeface.DEFAULT_BOLD, 16, "Type", "3"));
                    else if (item.getHtype().equals("bunch"))
                        moreContainer.addView(createText(Intermediates.convertToString(context, R.string.bunchHairstyleType), Typeface.DEFAULT_BOLD, 16, "Type", "4"));
                    else if (item.getHtype().equals("netting"))
                        moreContainer.addView(createText(Intermediates.convertToString(context, R.string.nettingHairstyleType), Typeface.DEFAULT_BOLD, 16, "Type", "5"));
                    else if (item.getHtype().equals("curls"))
                        moreContainer.addView(createText(Intermediates.convertToString(context, R.string.curlsHairstyleType), Typeface.DEFAULT_BOLD, 16, "Type", "6"));
                    else if (item.getHtype().equals("unstandart"))
                        moreContainer.addView(createText(Intermediates.convertToString(context, R.string.unstandartHairstyleType), Typeface.DEFAULT_BOLD, 16, "Type", "7"));

                    if (item.getHfor().equals("kids"))
                        moreContainer.addView(createText(Intermediates.convertToString(context, R.string.forKids), Typeface.DEFAULT_BOLD, 16, "For", "1"));
                    else if (item.getHfor().equals("everyday"))
                        moreContainer.addView(createText(Intermediates.convertToString(context, R.string.forEveryday), Typeface.DEFAULT_BOLD, 16, "For", "2"));
                    else if (item.getHfor().equals("wedding"))
                        moreContainer.addView(createText(Intermediates.convertToString(context, R.string.forWedding), Typeface.DEFAULT_BOLD, 16, "For", "3"));
                    else if (item.getHfor().equals("evening"))
                        moreContainer.addView(createText(Intermediates.convertToString(context, R.string.forEvening), Typeface.DEFAULT_BOLD, 16, "For", "4"));
                    else if (item.getHfor().equals("exclusive"))
                        moreContainer.addView(createText(Intermediates.convertToString(context, R.string.forExclusive), Typeface.DEFAULT_BOLD, 16, "For", "5"));

                    holder.moreContainer.addView(moreContainer);
                } else if (holder.showMore.getText().equals(HIDE)) {
                    holder.showMore.setText(SHOW);
                    holder.moreContainer.removeAllViews();
                }
            }
        });
    }

    private TextView createText(String title, Typeface tf, int padding, final String type, final String index) {
        TextView tw = new TextView(context);
        tw.setText("" + title);
        tw.setPadding(0, padding, 0, padding);
        tw.setTextSize(14);
        tw.setTextColor(Color.argb(255, 50, 50, 50));
        tw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type == "Length") {
                    String[] length = context.getResources().getStringArray(R.array.hairstyleLength);
                    Intent intent = new Intent(context, SearchHairstyleFeed.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("Toolbar", "" + length[new Integer(index)]);
                    intent.putExtra("Request", "");
                    intent.putExtra("HairstyleLength", "" + index);
                    intent.putExtra("HairstyleType", "0");
                    intent.putExtra("HairstyleFor", "0");
                    context.startActivity(intent);
                }
                if (type == "Type") {
                    String[] type = context.getResources().getStringArray(R.array.hairstyleType);
                    Intent intent = new Intent(context, SearchHairstyleFeed.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("Toolbar", "" + type[new Integer(index)]);
                    intent.putExtra("Request", "");
                    intent.putExtra("HairstyleLength", "0");
                    intent.putExtra("HairstyleType", "" + index);
                    intent.putExtra("HairstyleFor", "0");
                    context.startActivity(intent);
                }
                if (type == "For") {
                    String[] hfor = context.getResources().getStringArray(R.array.hairstyleFor);
                    Intent intent = new Intent(context, SearchHairstyleFeed.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("Toolbar", "" + hfor[new Integer(index)]);
                    intent.putExtra("Request", "");
                    intent.putExtra("HairstyleLength", "0");
                    intent.putExtra("HairstyleType", "0");
                    intent.putExtra("HairstyleFor", "" + index);
                    context.startActivity(intent);
                }
            }
        });
        //tw.setTypeface(tf);
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

    public class CheckLikes extends AsyncTask<Void, Void, String> {

        HttpURLConnection connection = null;
        BufferedReader reader = null;
        String url = "";
        String result = "";
        String email = "";

        public CheckLikes(String email) {
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
                StringBuffer profileBuffer = new StringBuffer();
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
            for (int i = 0; i < array.length; i++) {
                if (!array[i].equals(""))
                    likes.add(new Long(array[i]));
            }
        }
    }
}