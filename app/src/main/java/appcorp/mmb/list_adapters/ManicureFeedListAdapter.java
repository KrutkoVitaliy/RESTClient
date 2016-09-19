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
import appcorp.mmb.activities.feeds.ManicureFeed;
import appcorp.mmb.activities.search_feeds.SearchManicureFeed;
import appcorp.mmb.activities.user.SignIn;
import appcorp.mmb.classes.Intermediates;
import appcorp.mmb.classes.Storage;
import appcorp.mmb.dto.ManicureDTO;
import appcorp.mmb.network.GetRequest;

public class ManicureFeedListAdapter extends RecyclerView.Adapter<ManicureFeedListAdapter.TapeViewHolder> {

    private List<ManicureDTO> data;
    private List<Long> likes = new ArrayList<>();
    private Context context;
    Display display;
    int width, height;

    public ManicureFeedListAdapter(List<ManicureDTO> data, Context context) {
        this.data = data;
        this.context = context;
        display = ((WindowManager) context.getSystemService(context.WINDOW_SERVICE)).getDefaultDisplay();
        width = display.getWidth();
        height = width;
        if (!Storage.getString("E-mail", "").equals(""))
            new CheckLikes(Storage.getString("E-mail", "")).execute();
    }

    @Override
    public TapeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tape_item, parent, false);
        return new TapeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TapeViewHolder holder, int position) {
        final ManicureDTO item = data.get(position);

        if (position == data.size() - 1) {
            if (data.size() - 1 % 100 != 8)
                ManicureFeed.addFeed(data.size() / 100 + 1);
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
                        new GetRequest("http://195.88.209.17/app/in/manicureLike.php?id=" + item.getId() + "&email=" + Storage.getString("E-mail", "")).execute();
                    } else if (likes.contains(item.getId())) {
                        holder.addLike.setBackgroundResource(R.mipmap.ic_heart_outline);
                        likes.remove(item.getId());
                        holder.likesCount.setText("" + (new Long(holder.likesCount.getText().toString()) - 1));
                        new GetRequest("http://195.88.209.17/app/in/manicureDislike.php?id=" + item.getId() + "&email=" + Storage.getString("E-mail", "")).execute();
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
            hashTag.setTextSize(16);
            final int finalI = i;
            hashTag.setText("#" + item.getHashTags().get(i).replace(" ", "") + " ");
            hashTag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(context, SearchManicureFeed.class)
                            .putExtra("Request", item.getHashTags().get(finalI).trim())
                            .putStringArrayListExtra("ManicureColors", new ArrayList<String>())
                            .putExtra("Shape", "" + "0")
                            .putExtra("Design", "" + "0")
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
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
            holder.imageViewerHorizontal.scrollTo(0, 0);

            LinearLayout countLayout = new LinearLayout(context);
            countLayout.setLayoutParams(new ViewGroup.LayoutParams(width, height));
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
                    String[] shapes = context.getResources().getStringArray(R.array.manicureShapes);
                    ArrayList<String> manicureColors = new ArrayList<>();
                    Intent intent = new Intent(context, SearchManicureFeed.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.putStringArrayListExtra("ManicureColors", manicureColors);
                    intent.putExtra("Toolbar", "" + shapes[new Integer(index)]);
                    intent.putExtra("Request", "");
                    intent.putExtra("Shape", "" + index);
                    intent.putExtra("Design", "0");
                    context.startActivity(intent);
                }
                if (type == "Design") {
                    String[] designs = context.getResources().getStringArray(R.array.manicureDesign);
                    ArrayList<String> manicureColors = new ArrayList<>();
                    Intent intent = new Intent(context, SearchManicureFeed.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.putStringArrayListExtra("ManicureColors", manicureColors);
                    intent.putExtra("Toolbar", "" + designs[new Integer(index)]);
                    intent.putExtra("Request", "");
                    intent.putExtra("Shape", "0");
                    intent.putExtra("Design", "" + index);
                    context.startActivity(intent);
                }
            }
        });
        //tw.setTypeface(tf);
        return tw;
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
                ArrayList<String> manicureColors = new ArrayList<>();
                manicureColors.add(searchParameter);
                Intent intent = new Intent(context, SearchManicureFeed.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putStringArrayListExtra("ManicureColors", sortManicureColors(manicureColors));
                intent.putExtra("Request", "");
                intent.putExtra("Shape", "0");
                intent.putExtra("Design", "0");
                context.startActivity(intent);
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
        for (int i = 0; i < colorsCodes.length; i++) {
            for (int j = 0; j < colors.size(); j++) {
                if (colorsCodes[i].equals(colors.get(j)))
                    sortedColors.add(colorsCodes[i]);
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
                URL feedURL = new URL("http://195.88.209.17/app/in/favoritesManicure.php?email=" + email);
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