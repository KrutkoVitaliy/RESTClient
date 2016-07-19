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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import appcorp.mmb.R;
import appcorp.mmb.activities.FullscreenPreview;
import appcorp.mmb.activities.Profile;
import appcorp.mmb.activities.Search;
import appcorp.mmb.dto.SearchDTO;

public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.SearchViewHolder> {

    private List<SearchDTO> searchData;
    private Context context;
    Display display;
    int width, height;

    public SearchListAdapter(List<SearchDTO> searchData, Context context) {
        this.searchData = searchData;
        this.context = context;
        display = ((WindowManager) context.getSystemService(context.WINDOW_SERVICE)).getDefaultDisplay();
        width = display.getWidth();
        height = (int) (width * 0.75F);
    }

    @Override
    public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false);
        return new SearchViewHolder(view);
    }



    @Override
    public void onBindViewHolder(final SearchViewHolder holder, int position) {
        final SearchDTO item = searchData.get(position);

        holder.title.setText(item.getAuthor());
        String s = item.getAvailableDate().toString();
        holder.availableDate.setText(s);
        holder.likesCount.setText("" + item.getLikes());

        Picasso.with(context).load("http://195.88.209.17:8080/mmbcontent/Storage/Screenshots/"+item.getAuthorPhoto()).into(holder.user_avatar);
        holder.user_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Profile.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("sid", item.getSid());
                new Profile(item.getSid());
                context.startActivity(intent);
            }
        });

        /*holder.hashTags.removeAllViews();
        for (int i = 0; i < item.getHashTags().size(); i++) {
            TextView hashTag = new TextView(context);
            hashTag.setTextColor(Color.argb(255, 51, 102, 153));
            hashTag.setTextSize(14);
            final int finalI = i;
            hashTag.setText("#" + item.getHashTags().get(i) + " ");
            hashTag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, Search.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("hashTag", item.getHashTags().get(finalI));
                    context.startActivity(intent);
                }
            });
            holder.hashTags.addView(hashTag);
        }*/

        holder.imageViewer.removeAllViews();
        for (int i = 0; i < item.getImages().size(); i++) {
            ImageView screenShot = new ImageView(context);
            screenShot.setMinimumWidth(width);
            screenShot.setMinimumHeight(height);
            screenShot.setPadding(0, 0, 1, 0);
            String image = item.getImages().get(i);
            Picasso.with(context).load("http://195.88.209.17:8080/mmbcontent/Storage/Screenshots/"+image).resize(width, height).centerCrop().into(screenShot);
            screenShot.setScaleType(ImageView.ScaleType.CENTER_CROP);
            final int finalI = i;
            screenShot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, FullscreenPreview.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("screenshot", item.getImages().get(finalI));
                    context.startActivity(intent);
                }
            });
            holder.imageViewer.addView(screenShot);
        }

        final Animation show = AnimationUtils.loadAnimation(context, R.anim.show_more);
        final Animation hide = AnimationUtils.loadAnimation(context, R.anim.hide_more);

        holder.showMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout transparentLayer = new LinearLayout(context);
                transparentLayer.setMinimumWidth(width/3);
                transparentLayer.setBackgroundColor(Color.argb(0, 255, 255, 255));
                holder.moreContainer.addView(transparentLayer);

                LinearLayout more = new LinearLayout(context);
                more.setOrientation(LinearLayout.VERTICAL);
                more.setPadding(16, 0, 16, 16);
                more.setMinimumWidth(width);
                more.setMinimumHeight(height);
                more.setBackgroundColor(Color.argb(255, 255, 255, 255));
                holder.moreContainer.addView(more);

                if (holder.showMore.getText().equals("Подробнее")) {
                    //holder.imageViewer.setVisibility(View.INVISIBLE);
                    more.startAnimation(show);
                    more.addView(createText("Цвет глаз", Typeface.DEFAULT_BOLD, 8));
                    more.addView(createImage(item.getEye_color()));
                    more.addView(createText("Используемые цвета", Typeface.DEFAULT_BOLD, 8));
                    LinearLayout colors = new LinearLayout(context);
                    colors.setOrientation(LinearLayout.HORIZONTAL);
                    if (item.getColors().contains("pink")) colors.addView(createCircle("#bb125b", "pink"));
                    if (item.getColors().contains("purple"))colors.addView(createCircle("#9210ae", "purple"));
                    if (item.getColors().contains("blue")) colors.addView(createCircle("#117dae", "blue"));
                    if (item.getColors().contains("teal")) colors.addView(createCircle("#3b9670", "teal"));
                    if (item.getColors().contains("green")) colors.addView(createCircle("#79bd14", "green"));
                    if (item.getColors().contains("yellow"))colors.addView(createCircle("#d4b515", "yellow"));
                    if (item.getColors().contains("orange"))colors.addView(createCircle("#d46915", "orange"));
                    if (item.getColors().contains("red")) colors.addView(createCircle("#d42415", "red"));
                    if (item.getColors().contains("neutral"))colors.addView(createCircle("#d2af7f", "neutral"));
                    if (item.getColors().contains("copper"))colors.addView(createCircle("#b48f58", "copper"));
                    if (item.getColors().contains("brown")) colors.addView(createCircle("#604e36", "brown"));
                    if (item.getColors().contains("hazel")) colors.addView(createCircle("#70653f", "hazel"));
                    if (item.getColors().contains("gray")) colors.addView(createCircle("#555555", "gray"));
                    if (item.getColors().contains("black")) colors.addView(createCircle("#000000", "black"));
                    more.addView(colors);
                    more.addView(createText("Сложность макияжа", Typeface.DEFAULT_BOLD, 8));
                    more.addView(difficult(item.getDifficult()));
                    more.addView(createText("Тип макияжа", Typeface.DEFAULT_BOLD, 8));
                    TextView occasion = createText(item.getOccasion(), Typeface.DEFAULT, 0);
                    occasion.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, Search.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("hashTag", item.getOccasion());
                            context.startActivity(intent);
                        }
                    });
                    more.addView(occasion);

                    holder.showMore.setText("Скрыть");
                } else if (holder.showMore.getText().equals("Скрыть")) {
                    //holder.imageViewer.setVisibility(View.VISIBLE);
                    //holder.imageViewer.startAnimation(show);
                    more.startAnimation(hide);
                    holder.moreContainer.removeAllViews();
                    holder.showMore.setText("Подробнее");
                }
            }
        });
    }

    private TextView createText(String title, Typeface tf, int padding) {
        TextView tw = new TextView(context);
        tw.setText("" + title);
        tw.setPadding(0, padding, 0, padding);
        tw.setTextSize(12);
        tw.setTextColor(Color.argb(255, 100, 100, 100));
        tw.setTypeface(tf);
        return tw;
    }

    private LinearLayout difficult(String difficult) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        if (difficult.equals("Easy")) {
            ImageView imageView = new ImageView(context);
            imageView.setImageResource(R.mipmap.easy);
        }
        if (difficult.equals("Medium")) {
            ImageView imageView = new ImageView(context);
            imageView.setImageResource(R.mipmap.medium);
        }
        if (difficult.equals("Hard")) {
            ImageView imageView = new ImageView(context);
            imageView.setImageResource(R.mipmap.hard);
        }
        return layout;
    }

    private ImageView createCircle(String color, final String searchParameter) {
        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new ViewGroup.LayoutParams((int)(width*0.075F),(int)(width*0.075F)));
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

    private LinearLayout createImage(String color) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setVerticalGravity(Gravity.CENTER_VERTICAL);

        ImageView imageView = new ImageView(context);
        TextView title = new TextView(context);
        title.setTextSize(20);
        title.setTextColor(Color.argb(255, 100, 100, 100));
        title.setPadding(16, 0, 0, 0);
        if (color.equals("Black")) {
            imageView.setImageResource(R.mipmap.eye_black);
            title.setText("Черные глаза");
        }
        if (color.equals("Blue")) {
            imageView.setImageResource(R.mipmap.eye_blue);
            title.setText("Голубые глаза");
        }
        if (color.equals("Brown")) {
            imageView.setImageResource(R.mipmap.eye_brown);
            title.setText("Карие глаза");
        }
        if (color.equals("Gray")) {
            imageView.setImageResource(R.mipmap.eye_gray);
            title.setText("Серые глаза");
        }
        if (color.equals("Green")) {
            imageView.setImageResource(R.mipmap.eye_green);
            title.setText("Зеленые глаза");
        }
        if (color.equals("Hazel")) {
            imageView.setImageResource(R.mipmap.eye_hazel);
            title.setText("Болотный цвет глаз");
        }

        layout.addView(imageView);
        layout.addView(title);
        return layout;
    }

    @Override
    public int getItemCount() {
        return searchData.size();
    }

    public void setData(List<SearchDTO> searchData) {
        this.searchData = searchData;
    }

    public static class SearchViewHolder extends RecyclerView.ViewHolder {
        TextView title, availableDate, author, showMore, likesCount;
        LinearLayout imageViewer, hashTags, moreContainer;
        ImageView user_avatar;

        public SearchViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.searchTitle);
            availableDate = (TextView) itemView.findViewById(R.id.searchAvailableDate);
            showMore = (TextView) itemView.findViewById(R.id.searchShow_more);
            imageViewer = (LinearLayout) itemView.findViewById(R.id.searchImageViewer);
            hashTags = (LinearLayout) itemView.findViewById(R.id.searchHashTags);
            likesCount = (TextView) itemView.findViewById(R.id.searchLikesCount);
            user_avatar = (ImageView) itemView.findViewById(R.id.searchUserAvatar);
            moreContainer = (LinearLayout) itemView.findViewById(R.id.searchMoreContainer);
        }
    }

}
