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
import appcorp.mmb.list_adapters.FavoritesMakeupFeedListAdapter;

public class FavoritesMakeupFeedFragment extends AbstractTabFragment{

    private List<MakeupDTO> makeupData;
    private FavoritesMakeupFeedListAdapter adapter;

    public static FavoritesMakeupFeedFragment getInstance(Context context, List<MakeupDTO> makeupData) {
        Bundle args = new Bundle();
        FavoritesMakeupFeedFragment fragment = new FavoritesMakeupFeedFragment();
        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setData(makeupData);
        fragment.setTitle("Makeup");
        return fragment;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setData(List<MakeupDTO> makeupData) {
        this.makeupData = makeupData;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_weekly, container, false);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(context));
        adapter = new FavoritesMakeupFeedListAdapter(makeupData, context);
        rv.setAdapter(adapter);
        return view;
    }

    public void refreshData(List<MakeupDTO> makeupData) {
        adapter.setData(makeupData);
        adapter.notifyDataSetChanged();
    }
}