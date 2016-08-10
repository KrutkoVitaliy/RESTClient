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
import appcorp.mmb.list_adapters.FavoritesHairstyleFeedListAdapter;

public class FavoritesHairstyleFeedFragment extends AbstractTabFragment{

    private List<HairstyleDTO> hairstyleData;
    private FavoritesHairstyleFeedListAdapter adapter;

    public static FavoritesHairstyleFeedFragment getInstance(Context context, List<HairstyleDTO> hairstyleData) {
        Bundle args = new Bundle();
        FavoritesHairstyleFeedFragment fragment = new FavoritesHairstyleFeedFragment();
        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setData(hairstyleData);
        fragment.setTitle("Hairstyle");
        return fragment;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setData(List<HairstyleDTO> hairstyleData) {
        this.hairstyleData = hairstyleData;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_weekly, container, false);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(context));
        adapter = new FavoritesHairstyleFeedListAdapter(hairstyleData, context);
        rv.setAdapter(adapter);
        return view;
    }

    public void refreshData(List<HairstyleDTO> hairstyleData) {
        adapter.setData(hairstyleData);
        adapter.notifyDataSetChanged();
    }
}