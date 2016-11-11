package appcorp.mmb.list_adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import appcorp.mmb.R;
import appcorp.mmb.activities.other.FullscreenPreview;
import appcorp.mmb.activities.other.FullscreenVideoPreview;
import appcorp.mmb.activities.search_feeds.Search;
import appcorp.mmb.activities.search_feeds.SearchManicureMatrix;
import appcorp.mmb.activities.user.SignIn;
import appcorp.mmb.classes.FireAnal;
import appcorp.mmb.classes.Intermediates;
import appcorp.mmb.classes.Storage;
import appcorp.mmb.dto.VideoManicureDTO;
import appcorp.mmb.network.GetRequest;

public class FavoriteVideosManicureFeedListAdapter extends RecyclerView.Adapter<FavoriteVideosManicureFeedListAdapter.TapeViewHolder> {

    private List<VideoManicureDTO> videoManicureData;
    private Context context;

    public FavoriteVideosManicureFeedListAdapter(List<VideoManicureDTO> videoManicureData, Context context) {
        this.videoManicureData = videoManicureData;
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
        final VideoManicureDTO video = videoManicureData.get(position);

        FrameLayout frameLayout = new FrameLayout(context);

        ImageView preview = new ImageView(context);
        preview.setLayoutParams(new ViewGroup.LayoutParams(Storage.getInt("Width", 480), Storage.getInt("Width", 480)));
        Picasso
                .with(context)
                .load("http://195.88.209.17/storage/videos/previews/" + video.getPreview())
                .resize(Storage.getInt("Width", 480), Storage.getInt("Width", 480))
                .centerCrop()
                .into(preview);
        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, FullscreenVideoPreview.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra("Source", video.getSource()));
            }
        });

        ImageView play = new ImageView(context);
        play.setImageResource(R.mipmap.play);
        play.setScaleX(0.3F);
        play.setScaleY(0.3F);

        frameLayout.addView(preview);
        frameLayout.addView(play);
        holder.imageViewer.addView(frameLayout);

        Picasso
                .with(context)
                .load("http://195.88.209.17/storage/photos/mmbuser.jpg")
                .resize(Storage.getInt("Width", 480), Storage.getInt("Width", 480))
                .centerCrop()
                .into(holder.user_avatar);

        holder.showMore.setVisibility(View.INVISIBLE);
        holder.title.setText(video.getTitle());
        String[] date = String.valueOf(video.getAvailableDate()).split("");
        holder.availableDate.setText(date[1] + date[2] + "-" + date[3] + date[4] + "-" + date[5] + date[6] + " " + date[7] + date[8] + ":" + date[9] + date[10]);

        holder.hashTags.removeAllViews();
        for (int i = 0; i < video.getTags().size(); i++) {
            TextView hashTag = new TextView(context);
            hashTag.setTextColor(Color.argb(255, 51, 102, 153));
            hashTag.setTextSize(16);
            final int finalI = i;
            hashTag.setText("#" + video.getTags().get(i).replace(" ", "") + " ");
            hashTag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        /*context.startActivity(new Intent(context, SearchManicureVideoMatrix.class)
                                .putExtra("Request", video.getTags().get(finalI).trim())
                                .putStringArrayListExtra("ManicureColors", new ArrayList<String>())
                                .putExtra("Shape", "" + "0")
                                .putExtra("Design", "" + "0")
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        FireAnal.sendString("2", "ManicureFeedTag", video.getTags().get(finalI));*/
                }
            });
            holder.hashTags.addView(hashTag);
            holder.likesCount.setText(String.valueOf(video.getLikes()));

            holder.addLike.setBackgroundResource(R.mipmap.ic_heart);
            holder.likesCount.setVisibility(View.INVISIBLE);
            holder.addLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new GetRequest("http://195.88.209.17/app/in/manicureVideoDislike.php?id=" + video.getId() + "&email=" + Storage.getString("E-mail", "")).execute();
                    holder.post.removeAllViews();
                    /*if (!Storage.getString("E-mail", "").equals("")) {
                        if (!videoLikes.contains(video.getId())) {
                            holder.addLike.setBackgroundResource(R.mipmap.ic_heart);
                            videoLikes.add(video.getId());
                            holder.likesCount.setText(String.valueOf(video.getLikes() + 1));
                            new GetRequest("http://195.88.209.17/app/in/manicureVideoLike.php?id=" + video.getId() + "&email=" + Storage.getString("E-mail", "")).execute();
                        } else if (videoLikes.contains(video.getId())) {
                            holder.addLike.setBackgroundResource(R.mipmap.ic_heart_outline);
                            videoLikes.remove(video.getId());
                            holder.likesCount.setText(String.valueOf(holder.likesCount.getText()));
                            new GetRequest("http://195.88.209.17/app/in/manicureVideoDislike.php?id=" + video.getId() + "&email=" + Storage.getString("E-mail", "")).execute();
                        }
                    } else {
                        context.startActivity(new Intent(context, SignIn.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    }
                    new ManicureFeedListAdapter.CheckLikes(Storage.getString("E-mail", "")).execute();
                    new ManicureFeedListAdapter.CheckVideoLikes(Storage.getString("E-mail", "")).execute();*/
                }
            });

            Picasso.with(context).load("http://195.88.209.17/storage/photos/mmbuser.jpg").into(holder.user_avatar);
        }
    }

    @Override
    public int getItemCount() {
        return videoManicureData.size();
    }

    public void setData(List<VideoManicureDTO> videoManicureData) {
        this.videoManicureData = videoManicureData;
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