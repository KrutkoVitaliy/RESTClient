package appcorp.mmb.activities.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import appcorp.mmb.activities.other.PostHairstyle;
import appcorp.mmb.activities.other.PostManicure;

public class SearchHairstyleMatrixAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> thumbs = new ArrayList<>();
    int width, height;

    public SearchHairstyleMatrixAdapter(Context context){
        this.context = context;
        Display display;
        display = ((WindowManager) context.getSystemService(context.WINDOW_SERVICE)).getDefaultDisplay();
        width = display.getWidth()/3;
        height = width;
    }

    @Override
    public int getCount() {
        return thumbs.size();
    }

    @Override
    public Object getItem(int position) {
        return thumbs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final String imageUrl = thumbs.get(position);

        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(width, height));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(1, 1, 1, 1);
        } else {
            imageView = (ImageView) convertView;
        }
        Picasso.with(context).load("http://195.88.209.17/storage/images/"+thumbs.get(position)).resize(200,200).into(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, PostHairstyle.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra("hairstyleImageUrl",imageUrl));
            }
        });
        return imageView;
    }

    public void setData(ArrayList<String> thumbs) {
        this.thumbs = thumbs;
    }
}