package appcorp.mmb.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import appcorp.mmb.dto.StylistDTO;
import appcorp.mmb.fragment.AbstractTabFragment;
import appcorp.mmb.fragment.StylistFeedFragment;

public class StylistFragmentAdapter extends FragmentPagerAdapter {

    private Map<Integer, AbstractTabFragment> tabs;
    private Context context;
    private List<StylistDTO> data;
    private StylistFeedFragment stylistFeedFragment;

    public StylistFragmentAdapter(Context context, FragmentManager fm, List<StylistDTO> data) {
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
        stylistFeedFragment = StylistFeedFragment.getInstance(context, data);
        tabs.put(0, stylistFeedFragment);
    }

    public void setData(List<StylistDTO> data) {
        this.data = data;
        stylistFeedFragment.refreshData(data);
    }
}