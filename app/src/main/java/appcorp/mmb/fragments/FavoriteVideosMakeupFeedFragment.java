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
import appcorp.mmb.dto.MakeupDTO;
import appcorp.mmb.dto.VideoMakeupDTO;
import appcorp.mmb.list_adapters.FavoriteVideosMakeupFeedListAdapter;
import appcorp.mmb.list_adapters.FavoritesMakeupFeedListAdapter;

public class FavoriteVideosMakeupFeedFragment extends AbstractTabFragment{

    private List<VideoMakeupDTO> videoMakeupData;
    private FavoriteVideosMakeupFeedListAdapter adapter;

    public static FavoriteVideosMakeupFeedFragment getInstance(Context context, List<VideoMakeupDTO> makeupData) {
        Bundle args = new Bundle();
        FavoriteVideosMakeupFeedFragment fragment = new FavoriteVideosMakeupFeedFragment();
        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setData(makeupData);
        fragment.setTitle("Makeup");
        return fragment;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setData(List<VideoMakeupDTO> makeupData) {
        this.videoMakeupData = makeupData;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_weekly, container, false);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(context));
        adapter = new FavoriteVideosMakeupFeedListAdapter(videoMakeupData, context);
        rv.setAdapter(adapter);
        return view;
    }

    public void refreshData(List<VideoMakeupDTO> makeupData) {
        adapter.setData(makeupData);
        adapter.notifyDataSetChanged();
    }
}