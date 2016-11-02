package appcorp.mmb.fragment_adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import appcorp.mmb.dto.ManicureDTO;
import appcorp.mmb.dto.VideoManicureDTO;
import appcorp.mmb.fragments.AbstractTabFragment;
import appcorp.mmb.fragments.ManicureFeedFragment;

public class ManicureFeedFragmentAdapter extends FragmentPagerAdapter {

    private Map<Integer, AbstractTabFragment> tabs;
    private Context context;
    private List<ManicureDTO> data;
    private List<VideoManicureDTO> videoData;
    private ManicureFeedFragment manicureFeedFragment;

    public ManicureFeedFragmentAdapter(Context context, FragmentManager fm, List<ManicureDTO> data, List<VideoManicureDTO> videoData) {
        super(fm);
        this.data = data;
        this.videoData = videoData;
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
        manicureFeedFragment = ManicureFeedFragment.getInstance(context, data, videoData);
        tabs.put(0, manicureFeedFragment);
    }

    public void setData(List<ManicureDTO> data, List<VideoManicureDTO> videoData) {
        this.data = data;
        this.videoData = videoData;
        manicureFeedFragment.refreshData(data, videoData);
    }
}