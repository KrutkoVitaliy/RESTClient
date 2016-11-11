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
import appcorp.mmb.list_adapters.MakeupFeedListAdapter;

public class MakeupFeedFragment extends AbstractTabFragment{

    private List<MakeupDTO> data;
    private List<VideoMakeupDTO> videoData;
    private MakeupFeedListAdapter adapter;

    public static MakeupFeedFragment getInstance(Context context, List<MakeupDTO> data, List<VideoMakeupDTO> videoData) {
        Bundle args = new Bundle();
        MakeupFeedFragment fragment = new MakeupFeedFragment();
        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setData(data, videoData);
        return fragment;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setData(List<MakeupDTO> data, List<VideoMakeupDTO> videoData) {
        this.data = data;
        this.videoData = videoData;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_weekly, container, false);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(context));
        adapter = new MakeupFeedListAdapter(data, videoData, context);
        rv.setAdapter(adapter);
        return view;
    }

    public void refreshData(List<MakeupDTO> data, List<VideoMakeupDTO> videoData) {
        adapter.setData(data, videoData);
        adapter.notifyDataSetChanged();
    }
}