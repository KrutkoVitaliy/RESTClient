package appcorp.mmb.fragment_adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import appcorp.mmb.dto.HairstyleDTO;
import appcorp.mmb.dto.MakeupDTO;
import appcorp.mmb.dto.ManicureDTO;
import appcorp.mmb.dto.VideoHairstyleDTO;
import appcorp.mmb.dto.VideoMakeupDTO;
import appcorp.mmb.dto.VideoManicureDTO;
import appcorp.mmb.fragments.AbstractTabFragment;
import appcorp.mmb.fragments.FavoriteVideosHairstyleFeedFragment;
import appcorp.mmb.fragments.FavoriteVideosMakeupFeedFragment;
import appcorp.mmb.fragments.FavoriteVideosManicureFeedFragment;
import appcorp.mmb.fragments.FavoritesHairstyleFeedFragment;
import appcorp.mmb.fragments.FavoritesMakeupFeedFragment;
import appcorp.mmb.fragments.FavoritesManicureFeedFragment;

public class FavoriteVideosFragmentAdapter extends FragmentPagerAdapter {

    private Map<Integer, AbstractTabFragment> tabs;
    private Context context;
    private List<VideoManicureDTO> manicureVideoData;
    private List<VideoMakeupDTO> makeupVideoData;
    private List<VideoHairstyleDTO> hairstyleVideoData;
    private FavoriteVideosManicureFeedFragment favoriteVideosManicureFeedFragment;
    private FavoriteVideosMakeupFeedFragment favoriteVideosMakeupFeedFragment;
    private FavoriteVideosHairstyleFeedFragment favoriteVideosHairstyleFeedFragment;

    public FavoriteVideosFragmentAdapter(Context context, FragmentManager fm, List<VideoManicureDTO> manicureVideoData, List<VideoMakeupDTO> makeupVideoData, List<VideoHairstyleDTO> hairstyleVideoData) {
        super(fm);
        this.context = context;
        this.manicureVideoData = manicureVideoData;
        this.makeupVideoData = makeupVideoData;
        this.hairstyleVideoData = hairstyleVideoData;
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
        favoriteVideosManicureFeedFragment = FavoriteVideosManicureFeedFragment.getInstance(context, manicureVideoData);
        favoriteVideosMakeupFeedFragment = FavoriteVideosMakeupFeedFragment.getInstance(context, makeupVideoData);
        favoriteVideosHairstyleFeedFragment = FavoriteVideosHairstyleFeedFragment.getInstance(context, hairstyleVideoData);
        tabs.put(0, favoriteVideosManicureFeedFragment);
        tabs.put(1, favoriteVideosMakeupFeedFragment);
        tabs.put(2, favoriteVideosHairstyleFeedFragment);
    }

    public void setManicureData(List<VideoManicureDTO> manicureVideoData) {
        this.manicureVideoData = manicureVideoData;
        favoriteVideosManicureFeedFragment.setData(manicureVideoData);
        favoriteVideosManicureFeedFragment.refreshData(manicureVideoData);
    }
    public void setMakeupData(List<VideoMakeupDTO> makeupVideoData) {
        this.makeupVideoData = makeupVideoData;
        favoriteVideosMakeupFeedFragment.setData(makeupVideoData);
        favoriteVideosMakeupFeedFragment.refreshData(makeupVideoData);
    }
    public void setHairstyleData(List<VideoHairstyleDTO> hairstyleVideoData) {
        this.hairstyleVideoData = hairstyleVideoData;
        favoriteVideosHairstyleFeedFragment.setData(hairstyleVideoData);
    }
}