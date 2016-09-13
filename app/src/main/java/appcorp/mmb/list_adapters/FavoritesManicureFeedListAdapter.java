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
import appcorp.mmb.activities.search_feeds.SearchManicureFeed;
import appcorp.mmb.classes.Intermediates;
import appcorp.mmb.classes.Storage;
import appcorp.mmb.dto.ManicureDTO;
import appcorp.mmb.network.GetRequest;

public class FavoritesManicureFeedListAdapter extends RecyclerView.Adapter<FavoritesManicureFeedListAdapter.TapeViewHolder> {

    private List<ManicureDTO> manicureData;
    private Context context;
    private List<Long> likesId = new ArrayList<>();
    Display display;
    int width, height;
    boolean loaded = false;

    public FavoritesManicureFeedListAdapter(List<ManicureDTO> manicureData, Context context) {
        this.manicureData = manicureData;
        this.context = context;
        display = ((WindowManager) context.getSystemService(context.WINDOW_SERVICE)).getDefaultDisplay();
        width = display.getWidth();
        height = width;
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
            final ManicureDTO item = manicureData.get(position);

            if (position == manicureData.size() - 1) {
                if (manicureData.size() - 1 % 100 != 8)
                    Favorites.addManicureFeed(manicureData.size() / 100 + 1);
            }

            final String SHOW = Intermediates.convertToString(context, R.string.show_more_container);
            final String HIDE = Intermediates.convertToString(context, R.string.hide_more_container);

            String[] date = item.getAvailableDate().split("");

            holder.title.setText(item.getAuthorName());
            holder.availableDate.setText(date[1] + date[2] + "-" + date[3] + date[4] + "-" + date[5] + date[6] + " " + date[7] + date[8] + ":" + date[9] + date[10]);
            holder.likesCount.setText("");
            holder.addLike.setBackgroundResource(R.mipmap.ic_heart);

            holder.addLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new GetRequest("http://195.88.209.17/app/in/manicureDislike.php?id=" + item.getId() + "&email=" + Storage.getString("E-mail", "")).execute();
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
                        context.startActivity(new Intent(context, SearchManicureFeed.class)
                                .putExtra("Request", item.getHashTags().get(finalI))
                                .putStringArrayListExtra("ManicureColors", new ArrayList<String>())
                                .putExtra("Shape", "" + "0")
                                .putExtra("Design", "" + "0")
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

                        moreContainer.addView(createText(Intermediates.convertToString(context, R.string.title_used_colors), Typeface.DEFAULT_BOLD, 16, "", ""));
                        LinearLayout colors = new LinearLayout(context);
                        colors.setOrientation(LinearLayout.HORIZONTAL);
                        String[] mColors = (item.getColors().split(","));
                        for (int i = 0; i < mColors.length; i++) {
                            if (!mColors[i].equals("FFFFFF"))
                                colors.addView(createCircle("#" + mColors[i], mColors[i]));
                            else
                                colors.addView(createCircle("#EEEEEE", mColors[i]));
                        }
                        moreContainer.addView(colors);
                        if (item.getShape().equals("square"))
                            moreContainer.addView(createText("Форма ногтей: Квадратная", Typeface.DEFAULT_BOLD, 16, "Shape", "1"));
                        else if (item.getShape().equals("oval"))
                            moreContainer.addView(createText("Форма ногтей: Овальная", Typeface.DEFAULT_BOLD, 16, "Shape", "2"));
                        else if (item.getShape().equals("stiletto"))
                            moreContainer.addView(createText("Форма ногтей: Стилеты", Typeface.DEFAULT_BOLD, 16, "Shape", "3"));

                        if (item.getDesign().equals("french_classic"))
                            moreContainer.addView(createText("Дизайн ногтей: Классический", Typeface.DEFAULT_BOLD, 16, "Design", "1"));
                        else if (item.getDesign().equals("french_chevron"))
                            moreContainer.addView(createText("Дизайн ногтей: Шеврон", Typeface.DEFAULT_BOLD, 16, "Design", "2"));
                        else if (item.getDesign().equals("french_millennium"))
                            moreContainer.addView(createText("Дизайн ногтей: Миллениум", Typeface.DEFAULT_BOLD, 16, "Design", "3"));
                        else if (item.getDesign().equals("french_fun"))
                            moreContainer.addView(createText("Дизайн ногтей: Фан", Typeface.DEFAULT_BOLD, 16, "Design", "4"));
                        else if (item.getDesign().equals("french_crystal"))
                            moreContainer.addView(createText("Дизайн ногтей: Хрустальный", Typeface.DEFAULT_BOLD, 16, "Design", "5"));
                        else if (item.getDesign().equals("french_colorful"))
                            moreContainer.addView(createText("Дизайн ногтей: Цветной", Typeface.DEFAULT_BOLD, 16, "Design", "6"));
                        else if (item.getDesign().equals("french_designer"))
                            moreContainer.addView(createText("Дизайн ногтей: Дизайнерский", Typeface.DEFAULT_BOLD, 16, "Design", "7"));
                        else if (item.getDesign().equals("french_spa"))
                            moreContainer.addView(createText("Дизайн ногтей: Спа", Typeface.DEFAULT_BOLD, 16, "Design", "8"));
                        else if (item.getDesign().equals("french_moon"))
                            moreContainer.addView(createText("Дизайн ногтей: Лунный", Typeface.DEFAULT_BOLD, 16, "Design", "9"));
                        else if (item.getDesign().equals("art"))
                            moreContainer.addView(createText("Дизайн ногтей: Художественная роспись", Typeface.DEFAULT_BOLD, 16, "Design", "10"));
                        else if (item.getDesign().equals("designer"))
                            moreContainer.addView(createText("Дизайн ногтей: Дизайнерский", Typeface.DEFAULT_BOLD, 16, "Design", "11"));
                        else if (item.getDesign().equals("volume"))
                            moreContainer.addView(createText("Дизайн ногтей: Объемный дизайн", Typeface.DEFAULT_BOLD, 16, "Design", "12"));
                        else if (item.getDesign().equals("aqua"))
                            moreContainer.addView(createText("Дизайн ногтей: Аквариумный дизайн", Typeface.DEFAULT_BOLD, 16, "Design", "13"));
                        else if (item.getDesign().equals("american"))
                            moreContainer.addView(createText("Дизайн ногтей: Американский дизайн", Typeface.DEFAULT_BOLD, 16, "Design", "14"));
                        else if (item.getDesign().equals("photo"))
                            moreContainer.addView(createText("Дизайн ногтей: Фотодизайн", Typeface.DEFAULT_BOLD, 16, "Design", "15"));

                        holder.moreContainer.addView(moreContainer);
                    } else if (holder.showMore.getText().equals(HIDE)) {
                        holder.showMore.setText(SHOW);
                        holder.moreContainer.removeAllViews();
                    }
                }
            });
        }
    }

    private TextView createText(String title, final Typeface tf, int padding, final String type, final String index) {
        TextView tw = new TextView(context);
        tw.setText("" + title);
        tw.setPadding(0, padding, 0, padding);
        tw.setTextSize(14);
        tw.setTextColor(Color.argb(255, 50, 50, 50));

        tw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type == "Shape") {
                    ArrayList<String> manicureColors = new ArrayList<>();
                    Intent intent = new Intent(context, SearchManicureFeed.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putStringArrayListExtra("ManicureColors", manicureColors);
                    intent.putExtra("Request", "");
                    intent.putExtra("Shape", ""+index);
                    intent.putExtra("Design", "0");
                    context.startActivity(intent);
                }
                if (type == "Design") {
                    ArrayList<String> manicureColors = new ArrayList<>();
                    Intent intent = new Intent(context, SearchManicureFeed.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putStringArrayListExtra("ManicureColors", manicureColors);
                    intent.putExtra("Request", "");
                    intent.putExtra("Shape", "0");
                    intent.putExtra("Design", ""+index);
                    context.startActivity(intent);
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
        return manicureData.size();
    }

    public void setData(List<ManicureDTO> manicureData) {
        this.manicureData = manicureData;
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