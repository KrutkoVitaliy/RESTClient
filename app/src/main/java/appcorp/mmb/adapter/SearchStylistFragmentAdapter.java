package appcorp.mmb.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import appcorp.mmb.dto.SearchDTO;
import appcorp.mmb.dto.StylistDTO;
import appcorp.mmb.fragment.AbstractTabFragment;
import appcorp.mmb.fragment.SearchFeedFragment;
import appcorp.mmb.fragment.SearchStylistFeedFragment;

public class SearchStylistFragmentAdapter extends FragmentPagerAdapter {

    private Map<Integer, AbstractTabFragment> tabs;
    private Context context;
    private List<StylistDTO> searchData;
    private SearchStylistFeedFragment searchStylistFeedFragment;

    public SearchStylistFragmentAdapter(Context context, FragmentManager fm, List<StylistDTO> searchData) {
        super(fm);
        this.searchData = searchData;
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
        searchStylistFeedFragment = SearchStylistFeedFragment.getInstance(context, searchData);
        tabs.put(0, searchStylistFeedFragment);
    }

    public void setData(List<StylistDTO> searchData) {
        this.searchData = searchData;
        searchStylistFeedFragment.refreshData(searchData);
    }
}
