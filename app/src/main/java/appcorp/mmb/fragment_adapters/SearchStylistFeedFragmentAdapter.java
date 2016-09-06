package appcorp.mmb.fragment_adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import appcorp.mmb.dto.StylistDTO;
import appcorp.mmb.fragments.AbstractTabFragment;
import appcorp.mmb.fragments.SearchHairstyleFeedFragment;
import appcorp.mmb.fragments.SearchStylistFeedFragment;

public class SearchStylistFeedFragmentAdapter extends FragmentPagerAdapter {

    private Map<Integer, AbstractTabFragment> tabs;
    private Context context;
    private List<StylistDTO> data;
    private SearchStylistFeedFragment searchStylistFeedFragment;

    public SearchStylistFeedFragmentAdapter(Context context, FragmentManager fm, List<StylistDTO> data) {
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
        searchStylistFeedFragment = SearchStylistFeedFragment.getInstance(context, data);
        tabs.put(0, searchStylistFeedFragment);
    }

    public void setData(List<StylistDTO> data) {
        this.data = data;
        searchStylistFeedFragment.refreshData(data);
    }
}