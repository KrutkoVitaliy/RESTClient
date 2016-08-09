package appcorp.mmb.fragment_adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import appcorp.mmb.dto.ManicureDTO;
import appcorp.mmb.dto.TapeDTO;
import appcorp.mmb.fragments.AbstractTabFragment;
import appcorp.mmb.fragments.FavoritesManicureFeedFragment;
import appcorp.mmb.fragments.GlobalFeedFragment;

public class FavoritesManicureFeedFragmentAdapter extends FragmentPagerAdapter {

    private Map<Integer, AbstractTabFragment> tabs;
    private Context context;
    private List<ManicureDTO> data;
    private FavoritesManicureFeedFragment favoritesManicureFeedFragment;

    public FavoritesManicureFeedFragmentAdapter(Context context, FragmentManager fm, List<ManicureDTO> data) {
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
        favoritesManicureFeedFragment = FavoritesManicureFeedFragment.getInstance(context, data);
        tabs.put(0, favoritesManicureFeedFragment);
        //tabs.put(1, FavoritesManicureFeedFragment.getInstance(context, data));
    }

    public void setData(List<ManicureDTO> data) {
        this.data = data;
        favoritesManicureFeedFragment.refreshData(data);
    }
}