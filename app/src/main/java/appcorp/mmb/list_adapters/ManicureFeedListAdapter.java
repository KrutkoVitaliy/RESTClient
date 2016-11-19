package appcorp.mmb.list_adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

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
import appcorp.mmb.activities.search_feeds.SearchManicureMatrix;
import appcorp.mmb.activities.user.SignIn;
import appcorp.mmb.classes.FireAnal;
import appcorp.mmb.classes.Intermediates;
import appcorp.mmb.classes.Storage;
import appcorp.mmb.dto.MakeupDTO;
import appcorp.mmb.sharing.vkontakte.GetToken;
import appcorp.mmb.dto.ManicureDTO;
import appcorp.mmb.dto.VideoManicureDTO;
import appcorp.mmb.network.GetRequest;
import appcorp.mmb.sharing.vkontakte.WallPost;

public class ManicureFeedListAdapter extends RecyclerView.Adapter<ManicureFeedListAdapter.TapeViewHolder> {

    private List<ManicureDTO> data;
    private List<Long> likes = new ArrayList<>();
    private List<Long> videoLikes = new ArrayList<>();
    private Context context;

    public ManicureFeedListAdapter(List<ManicureDTO> data, Context context) {
        this.data = data;
        this.context = context;

        Storage.init(context);
        initFirebase();

        if (!Storage.getString("E-mail", "").equals("")) {
            new CheckLikes(Storage.getString("E-mail", "")).execute();
            new CheckVideoLikes(Storage.getString("E-mail", "")).execute();
        }
    }

    public String convertToString(int r) {
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
        if (position % 10 == 0 && position != 0) {
            /*holder.postHeader.removeAllViews();
            holder.postFooter.removeAllViews();
            holder.postTagsFrame.removeAllViews();
            holder.imageViewer.removeAllViews();
            holder.hashTags.removeAllViews();*/
            holder.post.removeAllViews();
            NativeExpressAdView nativeExpressAdView = new NativeExpressAdView(context);
            nativeExpressAdView.setAdUnitId(Intermediates.convertToString(context, R.string.adUnitId));
            nativeExpressAdView.setAdSize(AdSize.MEDIUM_RECTANGLE);
            nativeExpressAdView.loadAd(new AdRequest.Builder().build());
            holder.post.addView(nativeExpressAdView);

            holder.post.setMinimumWidth(Storage.getInt("Width", 480));
            holder.post.setMinimumHeight(Storage.getInt("Width", 480));
        } else {
            final ManicureDTO post = data.get(position);

            if (post.getDataType().equals("video")) {
                holder.title.setText(post.getAuthorName());
                String[] date = post.getVideoAvailableDate().split("");
                holder.availableDate.setText(date[1] + date[2] + "-" + date[3] + date[4] + "-" + date[5] + date[6] + " " + date[7] + date[8] + ":" + date[9] + date[10]);
                Picasso.with(context).load("http://195.88.209.17/storage/photos/mmbuser.jpg").resize(100, 100).centerCrop().into(holder.user_avatar);
                final VideoView videoView = new VideoView(context);
                videoView.setLayoutParams(new ViewGroup.LayoutParams(Storage.getInt("Width", 480), Storage.getInt("Width", 480)));
                videoView.setVideoURI(Uri.parse("http://195.88.209.17/storage/videos/" + post.getVideoSource()));
                videoView.setBackgroundColor(Color.parseColor("#336699FF"));
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        videoView.setBackgroundColor(Color.parseColor("#33669900"));
                        videoView.start();
                    }
                });
                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        videoView.start();
                    }
                });
                videoView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (videoView.isPlaying()) {
                            videoView.pause();
                        } else {
                            videoView.start();
                        }
                        return false;
                    }
                });
                holder.showMore.setVisibility(View.INVISIBLE);
                holder.postFrame.removeView(holder.hashTags);
                holder.hashTags.removeAllViews();
                holder.imageViewer.removeAllViews();
                holder.imageViewer.addView(videoView);

                holder.likesCount.setText(String.valueOf(post.getVideoLikes()));
                holder.addLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!Storage.getString("E-mail", "").equals("")) {
                            if (!videoLikes.contains(post.getVideoId())) {
                                holder.addLike.setBackgroundResource(R.mipmap.ic_heart);
                                videoLikes.add(post.getVideoId());
                                holder.likesCount.setText(String.valueOf(post.getVideoLikes() + 1));
                                new GetRequest("http://195.88.209.17/app/in/manicureVideoLike.php?id=" + post.getId() + "&email=" + Storage.getString("E-mail", "")).execute();
                            } else if (videoLikes.contains(post.getVideoId())) {
                                holder.addLike.setBackgroundResource(R.mipmap.ic_heart_outline);
                                videoLikes.remove(post.getVideoId());
                                holder.likesCount.setText(String.valueOf(holder.likesCount.getText()));
                                new GetRequest("http://195.88.209.17/app/in/manicureVideoDislike.php?id=" + post.getId() + "&email=" + Storage.getString("E-mail", "")).execute();
                            }
                        } else {
                            context.startActivity(new Intent(context, SignIn.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        }
                        new CheckVideoLikes(Storage.getString("E-mail", "")).execute();
                    }
                });
            }
            if (post.getDataType().equals("content")) {
                final ManicureDTO item = data.get(position);

                if (position == data.size() - data.size() / 10) {
                    if (data.size() - 1 % 100 != 8)
                        new Load(data.size() / 100 + 1).execute();
                }

                final String SHOW = convertToString(R.string.show_more_container);
                final String HIDE = convertToString(R.string.hide_more_container);

                holder.title.setText(item.getVideoTitle());
                String[] date = item.getAvailableDate().split("");
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
                                new GetRequest("http://195.88.209.17/app/in/manicureVideoLike.php?id=" + item.getId() + "&email=" + Storage.getString("E-mail", "")).execute();
                            } else if (likes.contains(item.getId())) {
                                holder.addLike.setBackgroundResource(R.mipmap.ic_heart_outline);
                                likes.remove(item.getId());
                                holder.likesCount.setText(String.valueOf(holder.likesCount.getText()));
                                new GetRequest("http://195.88.209.17/app/in/manicureVideoDislike.php?id=" + item.getId() + "&email=" + Storage.getString("E-mail", "")).execute();
                            }
                        } else {
                            context.startActivity(new Intent(context, SignIn.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        }
                    }
                });

                holder.share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (Objects.equals(Storage.getString("VKAccessToken", ""), ""))
                            context.startActivity(new Intent(context, GetToken.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        else
                            context.startActivity(new Intent(context, WallPost.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
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
                    hashTag.setTextSize(16);
                    final int finalI = i;
                    hashTag.setText("#" + item.getHashTags().get(i).replace(" ", "") + " ");
                    hashTag.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            context.startActivity(new Intent(context, SearchManicureMatrix.class)
                                    .putExtra("Request", item.getHashTags().get(finalI).trim())
                                    .putStringArrayListExtra("ManicureColors", new ArrayList<String>())
                                    .putExtra("Shape", "" + "0")
                                    .putExtra("Design", "" + "0")
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            FireAnal.sendString("2", "ManicureFeedTag", item.getHashTags().get(finalI));
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
                    count.setTextSize(24);
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

                            moreContainer.addView(createText(convertToString(R.string.title_used_colors), 16, "", ""));
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
                            switch (item.getShape()) {
                                case "square":
                                    moreContainer.addView(createText(convertToString(R.string.squareShape), 16, "Shape", "1"));
                                    break;
                                case "oval":
                                    moreContainer.addView(createText(convertToString(R.string.ovalShape), 16, "Shape", "2"));
                                    break;
                                case "stiletto":
                                    moreContainer.addView(createText(convertToString(R.string.stilettoShape), 16, "Shape", "3"));
                                    break;
                            }

                            switch (item.getDesign()) {
                                case "french_classic":
                                    moreContainer.addView(createText(convertToString(R.string.french_classicDesign), 16, "Design", "1"));
                                    break;
                                case "french_chevron":
                                    moreContainer.addView(createText(convertToString(R.string.french_chevronDesign), 16, "Design", "2"));
                                    break;
                                case "french_millennium":
                                    moreContainer.addView(createText(convertToString(R.string.french_millenniumDesign), 16, "Design", "3"));
                                    break;
                                case "french_fun":
                                    moreContainer.addView(createText(convertToString(R.string.french_funDesign), 16, "Design", "4"));
                                    break;
                                case "french_crystal":
                                    moreContainer.addView(createText(convertToString(R.string.french_crystalDesign), 16, "Design", "5"));
                                    break;
                                case "french_colorful":
                                    moreContainer.addView(createText(convertToString(R.string.french_colorfulDesign), 16, "Design", "6"));
                                    break;
                                case "french_designer":
                                    moreContainer.addView(createText(convertToString(R.string.french_designerDesign), 16, "Design", "7"));
                                    break;
                                case "french_spa":
                                    moreContainer.addView(createText(convertToString(R.string.french_spaDesign), 16, "Design", "8"));
                                    break;
                                case "french_moon":
                                    moreContainer.addView(createText(convertToString(R.string.french_moonDesign), 16, "Design", "9"));
                                    break;
                                case "art":
                                    moreContainer.addView(createText(convertToString(R.string.artDesign), 16, "Design", "10"));
                                    break;
                                case "designer":
                                    moreContainer.addView(createText(convertToString(R.string.designerDesign), 16, "Design", "11"));
                                    break;
                                case "volume":
                                    moreContainer.addView(createText(convertToString(R.string.volumeDesign), 16, "Design", "12"));
                                    break;
                                case "aqua":
                                    moreContainer.addView(createText(convertToString(R.string.aquaDesign), 16, "Design", "13"));
                                    break;
                                case "american":
                                    moreContainer.addView(createText(convertToString(R.string.americanDesign), 16, "Design", "14"));
                                    break;
                                case "photo":
                                    moreContainer.addView(createText(convertToString(R.string.photoDesign), 16, "Design", "15"));
                                    break;
                            }

                            holder.moreContainer.addView(moreContainer);
                        } else if (holder.showMore.getText().equals(HIDE)) {
                            holder.showMore.setText(SHOW);
                            holder.moreContainer.removeAllViews();
                        }
                    }
                });
                holder.showMore.setVisibility(View.VISIBLE);
                holder.hashTags.setVisibility(View.VISIBLE);
            }
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
                if (Objects.equals(type, "Shape")) {
                    String[] shapes = context.getResources().getStringArray(R.array.manicureShapes);
                    ArrayList<String> manicureColors = new ArrayList<>();
                    Intent intent = new Intent(context, SearchManicureMatrix.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.putStringArrayListExtra("ManicureColors", manicureColors);
                    intent.putExtra("Toolbar", "" + shapes[Integer.valueOf(index)]);
                    intent.putExtra("Request", "");
                    intent.putExtra("Shape", "" + index);
                    intent.putExtra("Design", "0");
                    context.startActivity(intent);
                    FireAnal.sendString("2", "ManicureFeedParamShape", index);
                }
                if (Objects.equals(type, "Design")) {
                    String[] designs = context.getResources().getStringArray(R.array.manicureDesign);
                    ArrayList<String> manicureColors = new ArrayList<>();
                    Intent intent = new Intent(context, SearchManicureMatrix.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.putStringArrayListExtra("ManicureColors", manicureColors);
                    intent.putExtra("Toolbar", "" + designs[Integer.valueOf(index)]);
                    intent.putExtra("Request", "");
                    intent.putExtra("Shape", "0");
                    intent.putExtra("Design", "" + index);
                    context.startActivity(intent);
                    FireAnal.sendString("2", "ManicureFeedParamDesign", index);
                }
            }
        });
        return tw;
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
                ArrayList<String> manicureColors = new ArrayList<>();
                manicureColors.add(searchParameter);
                Intent intent = new Intent(context, SearchManicureMatrix.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putStringArrayListExtra("ManicureColors", sortManicureColors(manicureColors));
                intent.putExtra("Request", "");
                intent.putExtra("Shape", "0");
                intent.putExtra("Design", "0");
                context.startActivity(intent);
                FireAnal.sendString("2", "ManicureFeedParamColor", color);
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

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<ManicureDTO> data) {
        this.data = data;
    }

    public static class TapeViewHolder extends RecyclerView.ViewHolder {
        TextView title, availableDate, showMore, likesCount;
        LinearLayout imageViewer, countImages, hashTags, moreContainer, post, postFrame;
        ImageView user_avatar, addLike, share;
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
            share = (ImageView) itemView.findViewById(R.id.share);
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

    private class CheckVideoLikes extends AsyncTask<Void, Void, String> {

        private HttpURLConnection connection = null;
        private BufferedReader reader = null;
        private String result = "";
        private String email = "";

        CheckVideoLikes(String email) {
            this.email = email;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL feedURL = new URL("http://195.88.209.17/app/in/favoriteVideosManicure.php?email=" + email);
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
                    videoLikes.add(Long.valueOf(anArray));
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
                URL feedURL = new URL("http://195.88.209.17/app/static/manicure" + position + ".html");
                urlFeedConnection = (HttpURLConnection) feedURL.openConnection();
                urlFeedConnection.setRequestMethod("GET");
                urlFeedConnection.connect();
                InputStream inputStream = urlFeedConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder buffer = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null)
                    buffer.append(line);
                dataArrays.add(new JSONArray(String.valueOf(buffer)));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                URL feedURL = new URL("http://195.88.209.17/app/static/videoManicure" + position + ".html");
                urlFeedConnection = (HttpURLConnection) feedURL.openConnection();
                urlFeedConnection.setRequestMethod("GET");
                urlFeedConnection.connect();
                InputStream inputStream = urlFeedConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder buffer = new StringBuilder();
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
            List<ManicureDTO> exportData = new ArrayList<>();

            try {
                for (int i = 0; i < Math.max(dataArrays.get(0).length(), dataArrays.get(1).length()); i++) {
                    if (dataArrays.get(0).getJSONObject(i) != null)
                        items.add(dataArrays.get(0).getJSONObject(i));
                    if (dataArrays.get(1).getJSONObject(i) != null)
                        items.add(dataArrays.get(1).getJSONObject(i));
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

                        ManicureDTO post = new ManicureDTO(
                                items.get(i).getLong("id"),
                                "content",
                                items.get(i).getString("uploadDate"),
                                items.get(i).getString("authorName"),
                                items.get(i).getString("authorPhoto"),
                                items.get(i).getString("shape"),
                                items.get(i).getString("design"),
                                images,
                                items.get(i).getString("colors"),
                                tags,
                                items.get(i).getLong("likes"),
                                0,
                                "",
                                "",
                                "",
                                "",
                                0,
                                "");
                        exportData.add(post);
                    }
                    if (!items.get(i).has("id")) {
                        ManicureDTO post = new ManicureDTO(
                                0,
                                "video",
                                "",
                                "",
                                "",
                                "",
                                "",
                                new ArrayList<String>(),
                                "",
                                new ArrayList<String>(),
                                0,
                                items.get(i).getLong("videoId"),
                                items.get(i).getString("videoTitle"),
                                items.get(i).getString("videoPreview"),
                                items.get(i).getString("videoSource"),
                                items.get(i).getString("videoTags"),
                                items.get(i).getLong("videoLikes"),
                                items.get(i).getString("videoUploadDate"));
                        exportData.add(post);
                    }
                }
                FireAnal.sendString("1", "Open", "ManicureFeedLoaded");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}