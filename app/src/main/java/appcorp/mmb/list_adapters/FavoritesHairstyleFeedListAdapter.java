package appcorp.mmb.list_adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
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

import java.util.ArrayList;
import java.util.List;

import appcorp.mmb.R;
import appcorp.mmb.activities.user.Favorites;
import appcorp.mmb.activities.other.FullscreenPreview;
import appcorp.mmb.activities.search_feeds.Search;
import appcorp.mmb.activities.search_feeds.SearchHairstyleMatrix;
import appcorp.mmb.classes.FireAnal;
import appcorp.mmb.classes.Intermediates;
import appcorp.mmb.classes.Storage;
import appcorp.mmb.dto.HairstyleDTO;
import appcorp.mmb.network.GetRequest;

public class FavoritesHairstyleFeedListAdapter extends RecyclerView.Adapter<FavoritesHairstyleFeedListAdapter.TapeViewHolder> {

    private List<HairstyleDTO> hairstyleData;
    private Context context;
    private List<Long> likesId = new ArrayList<>();
    Display display;
    int width, height;
    boolean loaded = false;

    public FavoritesHairstyleFeedListAdapter(List<HairstyleDTO> hairstyleData, Context context) {
        this.hairstyleData = hairstyleData;
        this.context = context;

        Storage.init(context);
        initLocalization(Intermediates.getInstance().convertToString(context, R.string.translation));
        initScreen();
        initFirebase();

        display = ((WindowManager) context.getSystemService(context.WINDOW_SERVICE)).getDefaultDisplay();
        width = display.getWidth();
        height = width;
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
        if (position == getItemCount() - 1) {
            loaded = true;
        }
        if (position < getItemCount()) {
            final HairstyleDTO item = hairstyleData.get(position);

            if (position == hairstyleData.size() - 1) {
                if (hairstyleData.size() - 1 % 100 != 8)
                    Favorites.addManicureFeed(hairstyleData.size() / 100 + 1);
            }

            final String SHOW = Intermediates.getInstance().convertToString(context, R.string.show_more_container);
            final String HIDE = Intermediates.getInstance().convertToString(context, R.string.hide_more_container);

            String[] date = item.getAvailableDate().split("");

            holder.title.setText(item.getAuthorName());
            holder.availableDate.setText(date[1] + date[2] + "-" + date[3] + date[4] + "-" + date[5] + date[6] + " " + date[7] + date[8] + ":" + date[9] + date[10]);
            holder.likesCount.setText("" + item.getLikes());

            holder.likesCount.setText("");
            holder.addLike.setBackgroundResource(R.mipmap.ic_heart);

            holder.addLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new GetRequest("http://195.88.209.17/app/in/hairstyleDislike.php?id=" + item.getId() + "&email=" + Storage.getString("E-mail", "")).execute();
                    holder.post.removeAllViews();
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
                if (!item.getHashTags().get(i).toLowerCase().equals(""))
                    hashTag.setText("#" + item.getHashTags().get(i).trim() + " ");
                hashTag.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        context.startActivity(new Intent(context, SearchHairstyleMatrix.class)
                                .putExtra("Request", item.getHashTags().get(finalI))
                                .putExtra("HairstyleType", "")
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        FireAnal.sendString("2", "FavoritesHairstyleFeedTag", item.getHashTags().get(finalI));
                    }
                });
                holder.hashTags.addView(hashTag);
            }

            holder.imageViewer.removeAllViews();
            holder.countImages.removeAllViews();
            for (int i = 0; i < item.getImages().size(); i++) {
                ImageView screenShot = new ImageView(context);
                screenShot.setMinimumWidth(Storage.getInt("Width", 480));
                screenShot.setMinimumHeight(Storage.getInt("Height", 854));
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
                countLayout.setLayoutParams(new ViewGroup.LayoutParams(Storage.getInt("Width", 480), Storage.getInt("Height", 854)));
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
                            moreContainer.addView(createText(Intermediates.getInstance().convertToString(context, R.string.shortHairstyle), Typeface.DEFAULT_BOLD, 16, "Length", "1"));
                        else if (item.getHlenght().equals("medium"))
                            moreContainer.addView(createText(Intermediates.getInstance().convertToString(context, R.string.mediumHairstyle), Typeface.DEFAULT_BOLD, 16, "Length", "2"));
                        else if (item.getHlenght().equals("long"))
                            moreContainer.addView(createText(Intermediates.getInstance().convertToString(context, R.string.longHairstyle), Typeface.DEFAULT_BOLD, 16, "Length", "3"));

                        if (item.getHtype().equals("straight"))
                            moreContainer.addView(createText(Intermediates.getInstance().convertToString(context, R.string.straightHairstyleType), Typeface.DEFAULT_BOLD, 16, "Type", "1"));
                        else if (item.getHtype().equals("braid"))
                            moreContainer.addView(createText(Intermediates.getInstance().convertToString(context, R.string.braidHairstyleType), Typeface.DEFAULT_BOLD, 16, "Type", "2"));
                        else if (item.getHtype().equals("tail"))
                            moreContainer.addView(createText(Intermediates.getInstance().convertToString(context, R.string.tailHairstyleType), Typeface.DEFAULT_BOLD, 16, "Type", "3"));
                        else if (item.getHtype().equals("bunch"))
                            moreContainer.addView(createText(Intermediates.getInstance().convertToString(context, R.string.bunchHairstyleType), Typeface.DEFAULT_BOLD, 16, "Type", "4"));
                        else if (item.getHtype().equals("netting"))
                            moreContainer.addView(createText(Intermediates.getInstance().convertToString(context, R.string.nettingHairstyleType), Typeface.DEFAULT_BOLD, 16, "Type", "5"));
                        else if (item.getHtype().equals("curls"))
                            moreContainer.addView(createText(Intermediates.getInstance().convertToString(context, R.string.curlsHairstyleType), Typeface.DEFAULT_BOLD, 16, "Type", "6"));
                        else if (item.getHtype().equals("unstandart"))
                            moreContainer.addView(createText(Intermediates.getInstance().convertToString(context, R.string.unstandartHairstyleType), Typeface.DEFAULT_BOLD, 16, "Type", "7"));

                        if (item.getHfor().equals("kids"))
                            moreContainer.addView(createText(Intermediates.getInstance().convertToString(context, R.string.forKids), Typeface.DEFAULT_BOLD, 16, "For", "1"));
                        else if (item.getHfor().equals("everyday"))
                            moreContainer.addView(createText(Intermediates.getInstance().convertToString(context, R.string.forEveryday), Typeface.DEFAULT_BOLD, 16, "For", "2"));
                        else if (item.getHfor().equals("wedding"))
                            moreContainer.addView(createText(Intermediates.getInstance().convertToString(context, R.string.forWedding), Typeface.DEFAULT_BOLD, 16, "For", "3"));
                        else if (item.getHfor().equals("evening"))
                            moreContainer.addView(createText(Intermediates.getInstance().convertToString(context, R.string.forEvening), Typeface.DEFAULT_BOLD, 16, "For", "4"));
                        else if (item.getHfor().equals("exclusive"))
                            moreContainer.addView(createText(Intermediates.getInstance().convertToString(context, R.string.forExclusive), Typeface.DEFAULT_BOLD, 16, "For", "5"));

                        holder.moreContainer.addView(moreContainer);
                    } else if (holder.showMore.getText().equals(HIDE)) {
                        holder.showMore.setText(SHOW);
                        holder.moreContainer.removeAllViews();
                    }
                }
            });
        }
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
                    Intent intent = new Intent(context, SearchHairstyleMatrix.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("Toolbar", "" + length[new Integer(index)]);
                    intent.putExtra("Request", "");
                    intent.putExtra("HairstyleLength", "" + index);
                    intent.putExtra("HairstyleType", "0");
                    intent.putExtra("HairstyleFor", "0");
                    context.startActivity(intent);
                    FireAnal.sendString("2", "FavoritesHairstyleFeedParamLength", index);
                }
                if (type == "Type") {
                    String[] type = context.getResources().getStringArray(R.array.hairstyleType);
                    Intent intent = new Intent(context, SearchHairstyleMatrix.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("Toolbar", "" + type[new Integer(index)]);
                    intent.putExtra("Request", "");
                    intent.putExtra("HairstyleLength", "0");
                    intent.putExtra("HairstyleType", "" + index);
                    intent.putExtra("HairstyleFor", "0");
                    context.startActivity(intent);
                    FireAnal.sendString("2", "FavoritesHairstyleFeedParamType", index);
                }
                if (type == "For") {
                    String[] hfor = context.getResources().getStringArray(R.array.hairstyleFor);
                    Intent intent = new Intent(context, SearchHairstyleMatrix.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("Toolbar", "" + hfor[new Integer(index)]);
                    intent.putExtra("Request", "");
                    intent.putExtra("HairstyleLength", "0");
                    intent.putExtra("HairstyleType", "0");
                    intent.putExtra("HairstyleFor", "" + index);
                    context.startActivity(intent);
                    FireAnal.sendString("2", "FavoritesHairstyleFeedParamFor", index);
                }
            }
        });
        //tw.setTypeface(tf);
        return tw;
    }

    private TextView createText(String title, Typeface tf, int padding) {
        TextView tw = new TextView(context);
        tw.setText("" + title);
        tw.setPadding(0, padding, 0, padding);
        tw.setTextSize(14);
        tw.setTextColor(Color.argb(255, 50, 50, 50));
        //tw.setTypeface(tf);
        return tw;
    }

    private LinearLayout difficult(String difficult) {
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
        }
        if (difficult.equals("medium")) {
            imageView.setImageResource(R.mipmap.medium);
            text.setText(R.string.difficult_medium);
        }
        if (difficult.equals("hard")) {
            imageView.setImageResource(R.mipmap.hard);
            text.setText(R.string.difficult_hard);
        }
        layout.addView(imageView);
        layout.addView(text);
        return layout;
    }

    private ImageView createCircle(String color, final String searchParameter) {
        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new ViewGroup.LayoutParams((int) (Storage.getInt("Width", 480) * 0.075F), (int) (Storage.getInt("Width", 480) * 0.075F)));
        imageView.setScaleX(0.9F);
        imageView.setScaleY(0.9F);
        imageView.setBackgroundColor(Color.parseColor(color));
        imageView.setImageResource(R.mipmap.photo_layer);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Search.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("hashTag", searchParameter);
                context.startActivity(intent);
            }
        });
        return imageView;
    }


    String colorName;

    private LinearLayout createImage(String color) {
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
                Intent intent = new Intent(context, Search.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("hashTag", colorName);
                context.startActivity(intent);
            }
        });
        layout.addView(imageView);
        layout.addView(title);
        return layout;
    }

    @Override
    public int getItemCount() {
        return hairstyleData.size();
    }

    public void setData(List<HairstyleDTO> hairstyleData) {
        this.hairstyleData = hairstyleData;
    }

    public static class TapeViewHolder extends RecyclerView.ViewHolder {
        TextView title, availableDate, showMore, likesCount;
        LinearLayout imageViewer, countImages, hashTags, moreContainer, post;
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
            post = (LinearLayout) itemView.findViewById(R.id.post);
            addLike = (ImageView) itemView.findViewById(R.id.addLike);
        }
    }
}