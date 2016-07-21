package buruoyanyang.player.activities;

import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import buruoyanyang.player.R;
import buruoyanyang.player.models.VideoModel;
import in.srain.cube.views.GridViewWithHeaderAndFooter;
import in.srain.cube.views.loadmore.LoadMoreContainer;
import in.srain.cube.views.loadmore.LoadMoreGridViewContainer;
import in.srain.cube.views.loadmore.LoadMoreHandler;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

public class VideoListActivity extends BaseActivity {

    String ClickedId = "";
    int currentPageNum = 0;
    List<VideoModel.ContentEntity> mContentEntityList;
    List<VideoModel.ChannelsEntity> mChannelsEntityList;
    Boolean hasNext;
    PtrClassicFrameLayout mRefresh;
    GridViewWithHeaderAndFooter mGridView;
    LoadMoreGridViewContainer mLoadMoreContainer;
    boolean isLoadMore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        ClickedId = getBundleValue();

    }

    public void initClass() {
        mGridView = (GridViewWithHeaderAndFooter) findViewById(R.id.video_List_grid);
        hasNext = true;
        isLoadMore = false;
        mChannelsEntityList = new ArrayList<>();
        mContentEntityList = new ArrayList<>();
        mRefresh = (PtrClassicFrameLayout) findViewById(R.id.video_list_refresh);
        mRefresh.setPtrHandler(new RefreshHandler());
        mRefresh.setLastUpdateTimeRelateObject(this);
        mRefresh.disableWhenHorizontalMove(false);
        mLoadMoreContainer = (LoadMoreGridViewContainer) findViewById(R.id.video_list_grid_container);
        mLoadMoreContainer.setAutoLoadMore(true);
        mLoadMoreContainer.useDefaultHeader();
        mLoadMoreContainer.setLoadMoreHandler(new LoadHandler());

    }

    class LoadHandler implements LoadMoreHandler {
        @Override
        public void onLoadMore(LoadMoreContainer loadMoreContainer) {
            isLoadMore = true;
            currentPageNum++;
            //请求网络
        }
    }

    class RefreshHandler extends PtrDefaultHandler {

        @Override
        public void onRefreshBegin(PtrFrameLayout frame) {
            isLoadMore = false;
            currentPageNum = 0;
            //请求网络
        }

        @Override
        public boolean checkCanDoRefresh(PtrFrameLayout frameLayout, View content, View header) {
            return PtrDefaultHandler.checkContentCanBePulledDown(frameLayout, mGridView, header);
        }
    }

    public String getBundleValue() {
        Bundle bundle = this.getIntent().getExtras();
        return bundle.getString("ClickedId");
    }


    @Override
    public void initParams(Bundle params) {

    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_video_list;
    }

    @Override
    public void initView(View view) {

    }
}
