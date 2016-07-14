package buruoyanyang.player.views.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shizhefei.view.indicator.IndicatorViewPager.IndicatorFragmentPagerAdapter;

import buruoyanyang.player.R;
import buruoyanyang.player.fragments.NoNetFragment;
import buruoyanyang.player.fragments.RecommendFragment;
import buruoyanyang.player.utils.ImageLoadUtils;

/**
 * buruoyanyang.player.views.adapters
 * author xiaofeng
 * 16/7/14
 */
public class FragmentAdapter extends IndicatorFragmentPagerAdapter {
    private int[] tabIcons =
            {
                    R.drawable.main_tab_recommend_selector,
                    R.drawable.main_tab_chanel_selector,
                    R.drawable.main_tab_my_selector
            };
    private String[] tabNames = {"推荐", "频道", "我的"};
    private LayoutInflater mInflater;
    private Context mContext;
    private SparseArray<Object> infoArray;

    public FragmentAdapter(Context context, FragmentManager fragmentManager, SparseArray<Object> infoArray) {
        super(fragmentManager);
        mInflater = LayoutInflater.from(context);
        mContext = context;
        this.infoArray = infoArray;
    }

    public FragmentAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public int getCount() {
        return tabIcons.length;
    }

    @Override
    public View getViewForTab(int position, View convertView, ViewGroup container) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.tabhost_layout, container, false);
        }
        TextView tabNameTv = ViewHolder.get(convertView, R.id.tab_name);
        ImageView tabImage = ViewHolder.get(convertView, R.id.tab_image);
        tabNameTv.setText(tabNames[position]);
        tabImage.setImageResource(tabIcons[position]);
        return convertView;
    }

    @Override
    public Fragment getFragmentForPage(int i) {
        Bundle bundle = new Bundle();
        bundle.putString("homeList", (String) infoArray.get(3));
        bundle.putInt("screenWidth", (int) infoArray.get(8));
        bundle.putInt("screenHeight", (int) infoArray.get(9));
        Fragment fragment = new RecommendFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @SuppressWarnings("unchecked")
    static class ViewHolder {
        public static <T extends View> T get(View view, int id) {
            SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
            if (viewHolder == null) {
                viewHolder = new SparseArray<>();
                view.setTag(viewHolder);
            }
            View childView = viewHolder.get(id);
            if (childView == null) {
                childView = view.findViewById(id);
                viewHolder.put(id, childView);
            }
            return (T) childView;
        }
    }
}
