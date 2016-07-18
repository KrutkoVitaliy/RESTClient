package appcorp.mmb.fragment;

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
import appcorp.mmb.adapter.SearchListAdapter;
import appcorp.mmb.dto.SearchDTO;

public class SearchFeedFragment extends AbstractTabFragment{

    private List<SearchDTO> searchData;
    private SearchListAdapter searchAdapter;

    public static SearchFeedFragment getInstance(Context context, List<SearchDTO> data) {
        Bundle args = new Bundle();
        SearchFeedFragment searchFragment = new SearchFeedFragment();
        searchFragment.setArguments(args);
        searchFragment.setContext(context);
        searchFragment.setData(data);
        return searchFragment;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setData(List<SearchDTO> data) {
        this.searchData = data;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.searchRecyclerView);
        rv.setLayoutManager(new LinearLayoutManager(context));
        searchAdapter = new SearchListAdapter(searchData, context);
        rv.setAdapter(searchAdapter);
        return view;
    }

    public void refreshData(List<SearchDTO> data) {
        searchAdapter.setData(data);
        searchAdapter.notifyDataSetChanged();
    }
}