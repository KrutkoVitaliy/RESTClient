package appcorp.mmb.fragment_adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import appcorp.mmb.dto.HairstyleDTO;
import appcorp.mmb.fragments.AbstractTabFragment;
import appcorp.mmb.fragments.HairstyleFeedFragment;
import appcorp.mmb.fragments.SearchHairstyleFeedFragment;

public class SearchHairstyleFeedFragmentAdapter extends FragmentPagerAdapter {

    private Map<Integer, AbstractTabFragment> tabs;
    private Context context;
    private List<HairstyleDTO> data;
    private SearchHairstyleFeedFragment hairstyleFeedFragment;

    public SearchHairstyleFeedFragmentAdapter(Context context, FragmentManager fm, List<HairstyleDTO> data) {
        super(fm);
        this.data = data;
        this.context = context;
        initTabsMap(context);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs.get(position).getTitle();
    }

    @Override
    public Fragment getItem(int position) {
        return tabs.get(position);
    }

    @Override
    public int getCount() {
        return tabs.size();
    }

    private void initTabsMap(Context context) {
        tabs = new HashMap<>();
        hairstyleFeedFragment = SearchHairstyleFeedFragment.getInstance(context, data);
        tabs.put(0, hairstyleFeedFragment);
    }

    public void setData(List<HairstyleDTO> data) {
        this.data = data;
        hairstyleFeedFragment.refreshData(data);
    }
}