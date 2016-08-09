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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import appcorp.mmb.R;
import appcorp.mmb.activities.FullscreenPreview;
import appcorp.mmb.activities.Search;
import appcorp.mmb.activities.feeds.MakeupFeed;
import appcorp.mmb.activities.search_feeds.SearchFeed;
import appcorp.mmb.classes.Intermediates;
import appcorp.mmb.dto.MakeupDTO;

public class MakeupFeedListAdapter extends RecyclerView.Adapter<MakeupFeedListAdapter.TapeViewHolder> {

    private List<MakeupDTO> data;
    private Context context;
    Display display;
    int width, height;

    public MakeupFeedListAdapter(List<MakeupDTO> data, Context context) {
        this.data = data;
        this.context = context;
        display = ((WindowManager) context.getSystemService(context.WINDOW_SERVICE)).getDefaultDisplay();
        width = display.getWidth();
        height = (int) (width * 0.75F);
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
            if (data.size()-1 % 100 != 8)
                MakeupFeed.addFeed(data.size() / 100 + 1);
        }

        final String SHOW = Intermediates.convertToString(context, R.string.show_more_container);
        final String HIDE = Intermediates.convertToString(context, R.string.hide_more_container);

        String[] date = item.getAvailableDate().split("");

        holder.title.setText(item.getAuthorName());
        holder.availableDate.setText(date[1]+date[2]+"-"+date[3]+date[4]+"-"+date[5]+date[6]+" "+date[7]+date[8]+":"+date[9]+date[10]);
        holder.likesCount.setText("" + item.getLikes());

        Picasso.with(context).load("http://195.88.209.17/storage/images/" + item.getAuthorPhoto()).into(holder.user_avatar);
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
            hashTag.setText("#" + item.getHashTags().get(i) + " ");
            hashTag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, SearchFeed.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("Request", item.getHashTags().get(finalI));
                    context.startActivity(intent);
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
            Picasso.with(context).load("http://195.88.209.17/storage/images/" + item.getImages().get(i)).resize(width, height).centerCrop().into(screenShot);

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

            LinearLayout countLayout = new LinearLayout(context);
            countLayout.setLayoutParams(new ViewGroup.LayoutParams(width, height));
            TextView count = new TextView(context);
            count.setText((i + 1) + "/" + item.getImages().size());
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

                    moreContainer.addView(createText(Intermediates.convertToString(context, R.string.title_eye_color), Typeface.DEFAULT_BOLD, 16));
                    moreContainer.addView(createImage(item.getEye_color()));
                    moreContainer.addView(createText(Intermediates.convertToString(context, R.string.title_used_colors), Typeface.DEFAULT_BOLD, 16));
                    LinearLayout colors = new LinearLayout(context);
                    colors.setOrientation(LinearLayout.HORIZONTAL);
                    if (item.getColors().contains("pink"))
                        colors.addView(createCircle("#bb125b", "pink"));
                    if (item.getColors().contains("purple"))
                        colors.addView(createCircle("#9210ae", "purple"));
                    if (item.getColors().contains("blue"))
                        colors.addView(createCircle("#117dae", "blue"));
                    if (item.getColors().contains("teal"))
                        colors.addView(createCircle("#3b9670", "teal"));
                    if (item.getColors().contains("green"))
                        colors.addView(createCircle("#79bd14", "green"));
                    if (item.getColors().contains("yellow"))
                        colors.addView(createCircle("#d4b515", "yellow"));
                    if (item.getColors().contains("orange"))
                        colors.addView(createCircle("#d46915", "orange"));
                    if (item.getColors().contains("red"))
                        colors.addView(createCircle("#d42415", "red"));
                    if (item.getColors().contains("neutral"))
                        colors.addView(createCircle("#d2af7f", "neutral"));
                    if (item.getColors().contains("copper"))
                        colors.addView(createCircle("#b48f58", "copper"));
                    if (item.getColors().contains("brown"))
                        colors.addView(createCircle("#604e36", "brown"));
                    if (item.getColors().contains("hazel"))
                        colors.addView(createCircle("#70653f", "hazel"));
                    if (item.getColors().contains("gray"))
                        colors.addView(createCircle("#555555", "gray"));
                    if (item.getColors().contains("black"))
                        colors.addView(createCircle("#000000", "black"));
                    moreContainer.addView(colors);

                    moreContainer.addView(createText(Intermediates.convertToString(context, R.string.title_difficult), Typeface.DEFAULT_BOLD, 16));
                    moreContainer.addView(difficult(item.getDifficult()));
                    TextView occasion = createText(item.getOccasion(), Typeface.DEFAULT, 16);
                    if (item.getOccasion().equals("everyday"))
                        occasion.setText(R.string.occasion_everyday);
                    else if (item.getOccasion().equals("celebrity"))
                        occasion.setText(R.string.occasion_celebrity);
                    else if (item.getOccasion().equals("dramatic"))
                        occasion.setText(R.string.occasion_dramatic);
                    else if (item.getOccasion().equals("holiday"))
                        occasion.setText(R.string.occasion_holiday);

                    occasion.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, Search.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("hashTag", item.getOccasion());
                            context.startActivity(intent);
                        }
                    });
                    moreContainer.addView(occasion);

                    holder.moreContainer.addView(moreContainer);
                } else if (holder.showMore.getText().equals(HIDE)) {
                    holder.showMore.setText(SHOW);
                    holder.moreContainer.removeAllViews();
                }
            }
        });
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
        imageView.setLayoutParams(new ViewGroup.LayoutParams((int) (width * 0.075F), (int) (width * 0.075F)));
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
        return data.size();
    }

    public void setData(List<MakeupDTO> data) {
        this.data = data;
    }

    public static class TapeViewHolder extends RecyclerView.ViewHolder {
        TextView title, availableDate, showMore, likesCount;
        LinearLayout imageViewer, countImages, hashTags, moreContainer;
        ImageView user_avatar, addLike;

        public TapeViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            availableDate = (TextView) itemView.findViewById(R.id.available_date);
            showMore = (TextView) itemView.findViewById(R.id.show_more);
            imageViewer = (LinearLayout) itemView.findViewById(R.id.imageViewer);
            countImages = (LinearLayout) itemView.findViewById(R.id.countImages);
            hashTags = (LinearLayout) itemView.findViewById(R.id.hash_tags);
            likesCount = (TextView) itemView.findViewById(R.id.likesCount);
            user_avatar = (ImageView) itemView.findViewById(R.id.user_avatar);
            moreContainer = (LinearLayout) itemView.findViewById(R.id.moreContainer);
            addLike = (ImageView) itemView.findViewById(R.id.addLike);
        }
    }
}