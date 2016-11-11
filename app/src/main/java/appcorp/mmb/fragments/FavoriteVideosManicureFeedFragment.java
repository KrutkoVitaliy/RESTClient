package appcorp.mmb.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import appcorp.mmb.R;
import appcorp.mmb.dto.VideoManicureDTO;
import appcorp.mmb.list_adapters.FavoriteVideosManicureFeedListAdapter;
import appcorp.mmb.list_adapters.FavoritesManicureFeedListAdapter;

public class FavoriteVideosManicureFeedFragment extends AbstractTabFragment{

    private List<VideoManicureDTO> videoManicureData;
    private FavoriteVideosManicureFeedListAdapter adapter;

    public static FavoriteVideosManicureFeedFragment getInstance(Context context, List<VideoManicureDTO> manicureData) {
        Bundle args = new Bundle();
        FavoriteVideosManicureFeedFragment fragment = new FavoriteVideosManicureFeedFragment();
        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setData(manicureData);
        fragment.setTitle("Manicure");
        return fragment;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setData(List<VideoManicureDTO> manicureData) {
        this.videoManicureData = manicureData;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_weekly, container, false);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(context));
        adapter = new FavoriteVideosManicureFeedListAdapter(videoManicureData, context);
        rv.setAdapter(adapter);
        return view;
    }

    public void refreshData(List<VideoManicureDTO> manicureData) {
        adapter.setData(manicureData);
        adapter.notifyDataSetChanged();
    }
}