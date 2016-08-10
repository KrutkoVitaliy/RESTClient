package appcorp.mmb.fragment_adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import appcorp.mmb.activities.Favorites;
import appcorp.mmb.dto.HairstyleDTO;
import appcorp.mmb.dto.MakeupDTO;
import appcorp.mmb.dto.ManicureDTO;
import appcorp.mmb.fragments.AbstractTabFragment;
import appcorp.mmb.fragments.FavoritesHairstyleFeedFragment;
import appcorp.mmb.fragments.FavoritesMakeupFeedFragment;
import appcorp.mmb.fragments.FavoritesManicureFeedFragment;

public class FavoritesManicureFeedFragmentAdapter extends FragmentPagerAdapter {

    private Map<Integer, AbstractTabFragment> tabs;
    private Context context;
    private List<ManicureDTO> manicureData;
    private List<MakeupDTO> makeupData;
    private List<HairstyleDTO> hairstyleData;
    private FavoritesManicureFeedFragment favoritesManicureFeedFragment;
    private FavoritesMakeupFeedFragment favoritesMakeupFeedFragment;
    private FavoritesHairstyleFeedFragment favoritesHairstyleFeedFragment;

    public FavoritesManicureFeedFragmentAdapter(Context context, FragmentManager fm, List<ManicureDTO> manicureData, List<MakeupDTO> makeupData, List<HairstyleDTO> hairstyleData) {
        super(fm);
        this.manicureData = manicureData;
        this.makeupData = makeupData;
        this.hairstyleData = hairstyleData;
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
        favoritesManicureFeedFragment = FavoritesManicureFeedFragment.getInstance(context, manicureData);
        favoritesMakeupFeedFragment = FavoritesMakeupFeedFragment.getInstance(context, makeupData);
        favoritesHairstyleFeedFragment = FavoritesHairstyleFeedFragment.getInstance(context, hairstyleData);
        tabs.put(0, favoritesManicureFeedFragment);
        tabs.put(1, favoritesMakeupFeedFragment);
        tabs.put(2, favoritesHairstyleFeedFragment);
    }

    public void setManicureData(List<ManicureDTO> manicureData) {
        this.manicureData = manicureData;
        favoritesManicureFeedFragment.setData(manicureData);
        favoritesManicureFeedFragment.refreshData(manicureData);
    }
    public void setMakeupData(List<MakeupDTO> makeupData) {
        this.makeupData = makeupData;
        favoritesMakeupFeedFragment.setData(makeupData);
        favoritesMakeupFeedFragment.refreshData(makeupData);
    }
    public void setHairstyleData(List<HairstyleDTO> hairstyleData) {
        this.hairstyleData = hairstyleData;
        favoritesHairstyleFeedFragment.setData(hairstyleData);
    }
}