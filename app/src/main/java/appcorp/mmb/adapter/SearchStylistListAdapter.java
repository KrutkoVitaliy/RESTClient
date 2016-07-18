package appcorp.mmb.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
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
import appcorp.mmb.dto.StylistDTO;

public class SearchStylistListAdapter extends RecyclerView.Adapter<SearchStylistListAdapter.SearchStylistViewHolder> {

    private List<StylistDTO> searchData;
    private Context context;
    Display display;
    int width, height;

    public SearchStylistListAdapter(List<StylistDTO> searchData, Context context) {
        this.searchData = searchData;
        this.context = context;
        display = ((WindowManager) context.getSystemService(context.WINDOW_SERVICE)).getDefaultDisplay();
        width = display.getWidth();
        height = (int) (width * 0.75F);
    }

    @Override
    public SearchStylistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stylist_item, parent, false);
        return new SearchStylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SearchStylistViewHolder holder, int position) {
        final StylistDTO item = searchData.get(position);


        holder.name.setText(item.getAuthorName());
        holder.location.setText(item.getAuthorLocation());
        holder.likes.setText("" + item.getLikes());
        holder.followers.setText("" + item.getFollowers());
        Picasso.with(context).load(item.getAuthorPhoto()).into(holder.photo);
    }

    @Override
    public int getItemCount() {
        return searchData.size();
    }

    public void setData(List<StylistDTO> searchData) {
        this.searchData = searchData;
    }

    public static class SearchStylistViewHolder extends RecyclerView.ViewHolder {
        TextView name, location, likes, followers;
        ImageView photo;

        public SearchStylistViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.stylistName);
            location = (TextView) itemView.findViewById(R.id.stylistLocation);
            likes = (TextView) itemView.findViewById(R.id.stylistLikes);
            followers = (TextView) itemView.findViewById(R.id.stylistFollowers);
            photo = (ImageView) itemView.findViewById(R.id.stylistPhoto);
        }
    }

}
