package appcorp.mmb.list_adapters;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;

import java.util.List;

import appcorp.mmb.R;
import appcorp.mmb.classes.FireAnal;
import appcorp.mmb.classes.Storage;
import appcorp.mmb.dto.VideoMakeupDTO;

public class FavoriteVideosMakeupFeedListAdapter extends RecyclerView.Adapter<FavoriteVideosMakeupFeedListAdapter.TapeViewHolder> {

    private List<VideoMakeupDTO> videoMakeupData;
    private Context context;
    boolean loaded = false;

    public FavoriteVideosMakeupFeedListAdapter(List<VideoMakeupDTO> videoMakeupData, Context context) {
        this.videoMakeupData = videoMakeupData;
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
        final VideoMakeupDTO video = videoMakeupData.get(position / 10);

        final VideoView videoView = new VideoView(context);
        videoView.setLayoutParams(new ViewGroup.LayoutParams(Storage.getInt("Width", 480), Storage.getInt("Width", 480)));
        videoView.setVideoURI(Uri.parse("http://195.88.209.17/storage/videos/makeup/previews/" + video.getSource()));
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

        holder.user_avatar.setVisibility(View.INVISIBLE);
        holder.showMore.setVisibility(View.INVISIBLE);
        holder.imageViewer.addView(videoView);

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
            holder.addLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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
        return videoMakeupData.size();
    }

    public void setData(List<VideoMakeupDTO> videoMakeupData) {
        this.videoMakeupData = videoMakeupData;
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