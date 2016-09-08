package appcorp.mmb.list_adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
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
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import appcorp.mmb.R;
import appcorp.mmb.activities.feeds.ManicureFeed;
import appcorp.mmb.activities.other.FullscreenPreview;
import appcorp.mmb.activities.search_feeds.Search;
import appcorp.mmb.activities.search_feeds.SearchManicureFeed;
import appcorp.mmb.activities.user.Authorization;
import appcorp.mmb.activities.user.Profile;
import appcorp.mmb.classes.Intermediates;
import appcorp.mmb.classes.Storage;
import appcorp.mmb.dto.ManicureDTO;
import appcorp.mmb.dto.StylistDTO;
import appcorp.mmb.network.GetRequest;

public class SearchStylistFeedListAdapter extends RecyclerView.Adapter<SearchStylistFeedListAdapter.TapeViewHolder> {

    private List<StylistDTO> data;
    private List<Long> likes = new ArrayList<>();
    private Context context;
    Display display;
    int width, height;

    public SearchStylistFeedListAdapter(List<StylistDTO> data, Context context) {
        this.data = data;
        this.context = context;
        display = ((WindowManager) context.getSystemService(context.WINDOW_SERVICE)).getDefaultDisplay();
        width = display.getWidth();
        height = width;
    }

    @Override
    public TapeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stylist_item, parent, false);
        return new TapeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TapeViewHolder holder, int position) {
        final StylistDTO item = data.get(position);

        if (position == data.size() - 1) {
            if (data.size() - 1 % 100 != 8)
                ManicureFeed.addFeed(data.size() / 100 + 1);
        }

        Picasso.with(context).load("http://195.88.209.17/storage/photos/" + item.getAuthorPhoto()).into(holder.photo);
        holder.name.setText(item.getAuthorName() + " " + item.getAuthorLastname());
        holder.location.setText(item.getAuthorCity() + ", " + item.getAuthorAddress());
        holder.phone.setText(item.getAuthorPhoneNumber());

        if (item.getGplus().equals(""))
            holder.gplus.setAlpha(0.4F);
        if (item.getFb().equals(""))
            holder.fb.setAlpha(0.4F);
        if (item.getVk().equals(""))
            holder.vk.setAlpha(0.4F);
        if (item.getInstagram().equals(""))
            holder.instagram.setAlpha(0.4F);
        if (item.getOk().equals(""))
            holder.ok.setAlpha(0.4F);

        holder.stylistPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, Profile.class)
                        .putExtra("ID", ""+item.getId())
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<StylistDTO> data) {
        this.data = data;
    }

    public static class TapeViewHolder extends RecyclerView.ViewHolder {
        TextView name, location, phone;
        ImageView photo, gplus, fb, vk, instagram, ok;
        LinearLayout stylistPost;

        public TapeViewHolder(View itemView) {
            super(itemView);
            stylistPost = (LinearLayout) itemView.findViewById(R.id.stylistPost);
            name = (TextView) itemView.findViewById(R.id.name);
            location = (TextView) itemView.findViewById(R.id.location);
            phone = (TextView) itemView.findViewById(R.id.phone);
            photo = (ImageView) itemView.findViewById(R.id.user_avatar);
            gplus = (ImageView) itemView.findViewById(R.id.gplusButton);
            fb = (ImageView) itemView.findViewById(R.id.fbButton);
            vk = (ImageView) itemView.findViewById(R.id.vkButton);
            instagram = (ImageView) itemView.findViewById(R.id.instagramButton);
            ok = (ImageView) itemView.findViewById(R.id.okButton);
        }
    }
}