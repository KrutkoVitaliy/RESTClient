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
import appcorp.mmb.list_adapters.HairstyleFeedListAdapter;

public class HairstyleFeedFragment extends AbstractTabFragment{

    private List<HairstyleDTO> data;
    private HairstyleFeedListAdapter adapter;

    public static HairstyleFeedFragment getInstance(Context context, List<HairstyleDTO> data) {
        Bundle args = new Bundle();
        HairstyleFeedFragment fragment = new HairstyleFeedFragment();
        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setData(data);
        return fragment;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setData(List<HairstyleDTO> data) {
        this.data = data;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_weekly, container, false);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(context));
        adapter = new HairstyleFeedListAdapter(data, context);
        rv.setAdapter(adapter);
        return view;
    }

    public void refreshData(List<HairstyleDTO> data) {
        adapter.setData(data);
        adapter.notifyDataSetChanged();
    }
}