package buruoyanyang.player.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import buruoyanyang.player.R;
import buruoyanyang.player.views.adapters.CateGridViewAdapter;

/**
 * buruoyanyang.player.fragments
 * author xhf1
 * 16/7/14
 */
public class CateListFragment extends BaseFragment {
    Context superContext;
    GridView mGridView;
    LayoutInflater mInflater;
    String cateString;
    private int width;
    private int height;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.cate_list_fragment, container, false);
        superContext = container.getContext();
        Bundle bundle = getArguments();
        cateString = bundle.getString("cateList");
        width = bundle.getInt("screenWidth");
        height = bundle.getInt("screenHeight");
        initFragment(contentView);
        initCateGrid();
        return contentView;
    }

    private void initFragment(View contextView) {
        mGridView = (GridView) contextView.findViewById(R.id.cate_grid);
        mInflater = (LayoutInflater) superContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private void initCateGrid() {
        CateGridViewAdapter adapter = new CateGridViewAdapter(mInflater, superContext, cateString, height / 6, width * 2 / 5);
        mGridView.setAdapter(adapter);
    }

    @Override
    public void fetchData() {

    }
}



























































