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
import appcorp.mmb.dto.StylistDTO;
import appcorp.mmb.list_adapters.SearchStylistFeedListAdapter;

public class SearchStylistFeedFragment extends AbstractTabFragment {

    private List<StylistDTO> data;
    private SearchStylistFeedListAdapter adapter;

    public static SearchStylistFeedFragment getInstance(Context context, List<StylistDTO> data) {
        Bundle args = new Bundle();
        SearchStylistFeedFragment fragment = new SearchStylistFeedFragment();
        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setData(data);
        return fragment;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setData(List<StylistDTO> data) {
        this.data = data;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_weekly, container, false);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(context));
        adapter = new SearchStylistFeedListAdapter(data, context);
        rv.setAdapter(adapter);
        return view;
    }

    public void refreshData(List<StylistDTO> data) {
        adapter.setData(data);
        adapter.notifyDataSetChanged();
    }
}