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
import java.util.Objects;

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
    private boolean loaded = false;

    public FavoritesHairstyleFeedListAdapter(List<HairstyleDTO> hairstyleData, Context context) {
        this.hairstyleData = hairstyleData;
        this.context = context;

        Storage.init(context);
        initFirebase();
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
        if (position == getItemCount() - 1) {
            loaded = true;
        }
        if (position < getItemCount()) {
            final HairstyleDTO item = hairstyleData.get(position);

            /*if (position == hairstyleData.size() - 1) {
                if (hairstyleData.size() - 1 % 100 != 8)
                    Favorites.addManicureFeed(hairstyleData.size() / 100 + 1);
            }*/

            final String SHOW = Intermediates.convertToString(context, R.string.show_more_container);
            final String HIDE = Intermediates.convertToString(context, R.string.hide_more_container);

            String[] date = item.getAvailableDate().split("");

            holder.title.setText(item.getAuthorName());
            holder.availableDate.setText(date[1] + date[2] + "-" + date[3] + date[4] + "-" + date[5] + date[6] + " " + date[7] + date[8] + ":" + date[9] + date[10]);
            holder.likesCount.setText(String.valueOf(item.getLikes()));

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

                        switch (item.getHlenght()) {
                            case "short":
                                moreContainer.addView(createText(Intermediates.convertToString(context, R.string.shortHairstyle),  16, "Length", "1"));
                                break;
                            case "medium":
                                moreContainer.addView(createText(Intermediates.convertToString(context, R.string.mediumHairstyle),  16, "Length", "2"));
                                break;
                            case "long":
                                moreContainer.addView(createText(Intermediates.convertToString(context, R.string.longHairstyle),  16, "Length", "3"));
                                break;
                        }

                        switch (item.getHtype()) {
                            case "straight":
                                moreContainer.addView(createText(Intermediates.convertToString(context, R.string.straightHairstyleType),  16, "Type", "1"));
                                break;
                            case "braid":
                                moreContainer.addView(createText(Intermediates.convertToString(context, R.string.braidHairstyleType),  16, "Type", "2"));
                                break;
                            case "tail":
                                moreContainer.addView(createText(Intermediates.convertToString(context, R.string.tailHairstyleType),  16, "Type", "3"));
                                break;
                            case "bunch":
                                moreContainer.addView(createText(Intermediates.convertToString(context, R.string.bunchHairstyleType),  16, "Type", "4"));
                                break;
                            case "netting":
                                moreContainer.addView(createText(Intermediates.convertToString(context, R.string.nettingHairstyleType),  16, "Type", "5"));
                                break;
                            case "curls":
                                moreContainer.addView(createText(Intermediates.convertToString(context, R.string.curlsHairstyleType),  16, "Type", "6"));
                                break;
                            case "unstandart":
                                moreContainer.addView(createText(Intermediates.convertToString(context, R.string.unstandartHairstyleType),  16, "Type", "7"));
                                break;
                        }

                        switch (item.getHfor()) {
                            case "kids":
                                moreContainer.addView(createText(Intermediates.convertToString(context, R.string.forKids),  16, "For", "1"));
                                break;
                            case "everyday":
                                moreContainer.addView(createText(Intermediates.convertToString(context, R.string.forEveryday),  16, "For", "2"));
                                break;
                            case "wedding":
                                moreContainer.addView(createText(Intermediates.convertToString(context, R.string.forWedding),  16, "For", "3"));
                                break;
                            case "evening":
                                moreContainer.addView(createText(Intermediates.convertToString(context, R.string.forEvening),  16, "For", "4"));
                                break;
                            case "exclusive":
                                moreContainer.addView(createText(Intermediates.convertToString(context, R.string.forExclusive),  16, "For", "5"));
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
                    FireAnal.sendString("2", "FavoritesHairstyleFeedParamLength", index);
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
                    FireAnal.sendString("2", "FavoritesHairstyleFeedParamType", index);
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
                    FireAnal.sendString("2", "FavoritesHairstyleFeedParamFor", index);
                }
            }
        });
        return tw;
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