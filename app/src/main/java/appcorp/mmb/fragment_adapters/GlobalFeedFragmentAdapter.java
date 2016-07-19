package appcorp.mmb.fragment_adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import appcorp.mmb.dto.TapeDTO;
import appcorp.mmb.fragments.AbstractTabFragment;
import appcorp.mmb.fragments.GlobalFeedFragment;

public class GlobalFeedFragmentAdapter extends FragmentPagerAdapter {

    private Map<Integer, AbstractTabFragment> tabs;
    private Context context;
    private List<TapeDTO> data;
    private GlobalFeedFragment globalFeedFragment;

    public GlobalFeedFragmentAdapter(Context context, FragmentManager fm, List<TapeDTO> data) {
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
        globalFeedFragment = GlobalFeedFragment.getInstance(context, data);
        tabs.put(0, globalFeedFragment);
    }

    public void setData(List<TapeDTO> data) {
        this.data = data;
        globalFeedFragment.refreshData(data);
    }
}