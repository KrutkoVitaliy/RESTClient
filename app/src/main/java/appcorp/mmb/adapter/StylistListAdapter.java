package appcorp.mmb.adapter;

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
import appcorp.mmb.dto.StylistDTO;

public class StylistListAdapter extends RecyclerView.Adapter<StylistListAdapter.StylistViewHolder> {

    private List<StylistDTO> data;
    private Context context;
    Display display;
    int width, height;

    public StylistListAdapter(List<StylistDTO> data, Context context) {
        this.data = data;
        this.context = context;
        display = ((WindowManager) context.getSystemService(context.WINDOW_SERVICE)).getDefaultDisplay();
        width = display.getWidth();
        height = (int) (width * 0.75F);
    }

    @Override
    public StylistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stylist_item, parent, false);
        return new StylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final StylistViewHolder holder, int position) {
        final StylistDTO item = data.get(position);

        holder.name.setText(item.getAuthorName());
        holder.location.setText(item.getAuthorLocation());
        holder.likes.setText(""+item.getLikes());
        holder.followers.setText(""+item.getFollowers());
        Picasso.with(context).load(item.getAuthorPhoto()).into(holder.photo);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<StylistDTO> data) {
        this.data = data;
    }

    public static class StylistViewHolder extends RecyclerView.ViewHolder {
        TextView name, location, likes, followers;
        ImageView photo;

        public StylistViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.stylistName);
            location = (TextView) itemView.findViewById(R.id.stylistLocation);
            likes = (TextView) itemView.findViewById(R.id.stylistLikes);
            followers = (TextView) itemView.findViewById(R.id.stylistFollowers);
            photo = (ImageView) itemView.findViewById(R.id.stylistPhoto);
        }
    }
}