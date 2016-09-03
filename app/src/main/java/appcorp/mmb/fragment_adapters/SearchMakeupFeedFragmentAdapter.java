package appcorp.mmb.fragment_adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import appcorp.mmb.dto.MakeupDTO;
import appcorp.mmb.fragments.AbstractTabFragment;
import appcorp.mmb.fragments.MakeupFeedFragment;
import appcorp.mmb.fragments.SearchMakeupFeedFragment;

public class SearchMakeupFeedFragmentAdapter extends FragmentPagerAdapter {

    private Map<Integer, AbstractTabFragment> tabs;
    private Context context;
    private List<MakeupDTO> data;
    private SearchMakeupFeedFragment makeupFeedFragment;

    public SearchMakeupFeedFragmentAdapter(Context context, FragmentManager fm, List<MakeupDTO> data) {
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
        makeupFeedFragment = SearchMakeupFeedFragment.getInstance(context, data);
        tabs.put(0, makeupFeedFragment);
    }

    public void setData(List<MakeupDTO> data) {
        this.data = data;
        makeupFeedFragment.refreshData(data);
    }
}