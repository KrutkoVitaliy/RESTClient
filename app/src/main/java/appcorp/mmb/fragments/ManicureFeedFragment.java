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
import appcorp.mmb.dto.ManicureDTO;
import appcorp.mmb.dto.VideoManicureDTO;
import appcorp.mmb.list_adapters.ManicureFeedListAdapter;

public class ManicureFeedFragment extends AbstractTabFragment{

    private List<ManicureDTO> data;
    private List<VideoManicureDTO> videoData;
    private ManicureFeedListAdapter adapter;

    public static ManicureFeedFragment getInstance(Context context, List<ManicureDTO> data, List<VideoManicureDTO> videoData) {
        Bundle args = new Bundle();
        ManicureFeedFragment fragment = new ManicureFeedFragment();
        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setData(data, videoData);
        return fragment;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setData(List<ManicureDTO> data, List<VideoManicureDTO> videoData) {
        this.data = data;
        this.videoData = videoData;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_weekly, container, false);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(context));
        adapter = new ManicureFeedListAdapter(data, videoData, context);
        rv.setAdapter(adapter);
        return view;
    }

    public void refreshData(List<ManicureDTO> data, List<VideoManicureDTO> videoData) {
        adapter.setData(data, videoData);
        adapter.notifyDataSetChanged();
    }
}