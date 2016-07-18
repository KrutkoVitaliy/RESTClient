package appcorp.mmb.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import appcorp.mmb.dto.SearchDTO;
import appcorp.mmb.fragment.AbstractTabFragment;
import appcorp.mmb.fragment.SearchFeedFragment;

public class SearchFragmentAdapter extends FragmentPagerAdapter {

    private Map<Integer, AbstractTabFragment> tabs;
    private Context context;
    private List<SearchDTO> searchData;
    private SearchFeedFragment shortTimeTapeFragment;

    public SearchFragmentAdapter(Context context, FragmentManager fm, List<SearchDTO> searchData) {
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
        shortTimeTapeFragment = SearchFeedFragment.getInstance(context, searchData);
        tabs.put(0, shortTimeTapeFragment);
    }

    public void setData(List<SearchDTO> searchData) {
        this.searchData = searchData;
        shortTimeTapeFragment.refreshData(searchData);
    }
}
