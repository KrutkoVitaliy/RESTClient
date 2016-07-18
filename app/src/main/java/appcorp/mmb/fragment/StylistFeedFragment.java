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
import appcorp.mmb.adapter.StylistListAdapter;
import appcorp.mmb.dto.StylistDTO;

public class StylistFeedFragment extends AbstractTabFragment{

    private List<StylistDTO> searchData;
    private StylistListAdapter stylistListAdapter;

    public static StylistFeedFragment getInstance(Context context, List<StylistDTO> data) {
        Bundle args = new Bundle();
        StylistFeedFragment stylistFeedFragment = new StylistFeedFragment();
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
        view = inflater.inflate(R.layout.fragment_search, container, false);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.searchRecyclerView);
        rv.setLayoutManager(new LinearLayoutManager(context));
        stylistListAdapter = new StylistListAdapter(searchData, context);
        rv.setAdapter(stylistListAdapter);
        return view;
    }

    public void refreshData(List<StylistDTO> data) {
        stylistListAdapter.setData(data);
        stylistListAdapter.notifyDataSetChanged();
    }
}