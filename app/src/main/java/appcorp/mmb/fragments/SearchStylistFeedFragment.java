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
import appcorp.mmb.list_adapters.SearchStylistListAdapter;
import appcorp.mmb.dto.StylistDTO;

public class SearchStylistFeedFragment extends AbstractTabFragment{

    private List<StylistDTO> searchData;
    private SearchStylistListAdapter searchStylistListAdapter;

    public static SearchStylistFeedFragment getInstance(Context context, List<StylistDTO> data) {
        Bundle args = new Bundle();
        SearchStylistFeedFragment stylistFeedFragment = new SearchStylistFeedFragment();
        stylistFeedFragment.setArguments(args);
        stylistFeedFragment.setContext(context);
        stylistFeedFragment.setData(data);
        return stylistFeedFragment;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setData(List<StylistDTO> data) {
        this.searchData = data;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search_stylist, container, false);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.searchStylistRecyclerView);
        rv.setLayoutManager(new LinearLayoutManager(context));
        searchStylistListAdapter = new SearchStylistListAdapter(searchData, context);
        rv.setAdapter(searchStylistListAdapter);
        return view;
    }

    public void refreshData(List<StylistDTO> data) {
        searchStylistListAdapter.setData(data);
        searchStylistListAdapter.notifyDataSetChanged();
    }
}