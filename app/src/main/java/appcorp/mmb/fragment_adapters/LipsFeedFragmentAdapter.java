package appcorp.mmb.fragment_adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import appcorp.mmb.dto.LipsDTO;
import appcorp.mmb.fragments.AbstractTabFragment;
import appcorp.mmb.fragments.LipsFeedFragment;

public class LipsFeedFragmentAdapter extends FragmentPagerAdapter {

    private Map<Integer, AbstractTabFragment> tabs;
    private Context context;
    private List<LipsDTO> data;
    private LipsFeedFragment lipsFeedFragment;

    public LipsFeedFragmentAdapter(Context context, FragmentManager fm, List<LipsDTO> data) {
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
        lipsFeedFragment = LipsFeedFragment.getInstance(context, data);
        tabs.put(0, lipsFeedFragment);
    }

    public void setData(List<LipsDTO> data) {
        this.data = data;
        lipsFeedFragment.refreshData(data);
    }
}