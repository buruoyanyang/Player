package buruoyanyang.player.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;

import buruoyanyang.player.R;
import buruoyanyang.player.interfaces.OnAdapterClickListener;
import buruoyanyang.player.views.adapters.CateGridViewAdapter;

/**
 * buruoyanyang.player.fragments
 * author xhf1
 * 16/7/14
 */
public class CateListFragment extends BaseFragment implements OnAdapterClickListener,View.OnClickListener {
    Context superContext;
    GridView mGridView;
    LayoutInflater mInflater;
    String cateString;
    private int width;
    private int height;
    View contextView;
    private OnAdapterClickListener mListener;
    ImageButton titleSearch;
    ImageButton titleDownload;
    ImageButton titleHistory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contextView = inflater.inflate(R.layout.catelist_fragment_layout, container, false);
        superContext = container.getContext();
        Bundle bundle = getArguments();
        cateString = bundle.getString("cateList");
        width = bundle.getInt("screenWidth");
        height = bundle.getInt("screenHeight");
        initFragment();
        return contextView;
    }


    public void setListener(OnAdapterClickListener listener) {
        mListener = listener;
    }

    private void initFragment() {
        mGridView = (GridView) contextView.findViewById(R.id.cate_grid);
        mInflater = (LayoutInflater) superContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        titleSearch = (ImageButton) contextView.findViewById(R.id.title_app_search);
        titleDownload = (ImageButton) contextView.findViewById(R.id.title_app_download);
        titleHistory = (ImageButton) contextView.findViewById(R.id.title_app_history);
        titleSearch.setOnClickListener(this);
        titleHistory.setOnClickListener(this);
        titleDownload.setOnClickListener(this);
    }

    private void initCateGrid() {
        CateGridViewAdapter adapter = new CateGridViewAdapter(mInflater, superContext, cateString, height / 6, width * 2 / 5);
        adapter.setListener(this);
        mGridView.setAdapter(adapter);
    }


    @Override
    public void fetchData() {
        initCateGrid();
    }

    @Override
    public void onClick(String msg, String where) {
        Log.d("Fragment", msg);
        mListener.onClick(msg, where);

    }

    @Override
    public void onClick(View v) {

    }
}



























































