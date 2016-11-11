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
import appcorp.mmb.dto.HairstyleDTO;
import appcorp.mmb.dto.VideoHairstyleDTO;
import appcorp.mmb.list_adapters.FavoriteVideosHairstyleFeedListAdapter;
import appcorp.mmb.list_adapters.FavoritesHairstyleFeedListAdapter;

public class FavoriteVideosHairstyleFeedFragment extends AbstractTabFragment{

    private List<VideoHairstyleDTO> videoHairstyleData;
    private FavoriteVideosHairstyleFeedListAdapter adapter;

    public static FavoriteVideosHairstyleFeedFragment getInstance(Context context, List<VideoHairstyleDTO> hairstyleData) {
        Bundle args = new Bundle();
        FavoriteVideosHairstyleFeedFragment fragment = new FavoriteVideosHairstyleFeedFragment();
        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setData(hairstyleData);
        fragment.setTitle("Hairstyle");
        return fragment;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setData(List<VideoHairstyleDTO> videoHairstyleData) {
        this.videoHairstyleData = videoHairstyleData;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_weekly, container, false);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(context));
        adapter = new FavoriteVideosHairstyleFeedListAdapter(videoHairstyleData, context);
        rv.setAdapter(adapter);
        return view;
    }

    public void refreshData(List<VideoHairstyleDTO> hairstyleData) {
        adapter.setData(hairstyleData);
        adapter.notifyDataSetChanged();
    }
}