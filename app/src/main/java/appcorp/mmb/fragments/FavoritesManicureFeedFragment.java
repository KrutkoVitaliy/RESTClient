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
import appcorp.mmb.list_adapters.FavoritesManicureFeedListAdapter;

public class FavoritesManicureFeedFragment extends AbstractTabFragment{

    private List<ManicureDTO> manicureData;
    private FavoritesManicureFeedListAdapter adapter;

    public static FavoritesManicureFeedFragment getInstance(Context context, List<ManicureDTO> manicureData) {
        Bundle args = new Bundle();
        FavoritesManicureFeedFragment fragment = new FavoritesManicureFeedFragment();
        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setData(manicureData);
        fragment.setTitle("Manicure");
        return fragment;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setData(List<ManicureDTO> manicureData) {
        this.manicureData = manicureData;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_weekly, container, false);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(context));
        adapter = new FavoritesManicureFeedListAdapter(manicureData, context);
        rv.setAdapter(adapter);
        return view;
    }

    public void refreshData(List<ManicureDTO> manicureData) {
        adapter.setData(manicureData);
        adapter.notifyDataSetChanged();
    }
}