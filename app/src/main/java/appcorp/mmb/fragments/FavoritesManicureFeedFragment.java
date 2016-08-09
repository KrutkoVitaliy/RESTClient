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
import appcorp.mmb.dto.TapeDTO;
import appcorp.mmb.list_adapters.FavoritesManicureFeedListAdapter;
import appcorp.mmb.list_adapters.GlobalFeedListAdapter;

public class FavoritesManicureFeedFragment extends AbstractTabFragment{

    private List<ManicureDTO> data;
    private FavoritesManicureFeedListAdapter adapter;

    public static FavoritesManicureFeedFragment getInstance(Context context, List<ManicureDTO> data) {
        Bundle args = new Bundle();
        FavoritesManicureFeedFragment fragment = new FavoritesManicureFeedFragment();
        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setData(data);
        fragment.setTitle("Manicure");
        return fragment;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setData(List<ManicureDTO> data) {
        this.data = data;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_weekly, container, false);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(context));
        adapter = new FavoritesManicureFeedListAdapter(data, context);
        rv.setAdapter(adapter);
        return view;
    }

    public void refreshData(List<ManicureDTO> data) {
        adapter.setData(data);
        adapter.notifyDataSetChanged();
    }
}