package buruoyanyang.player.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import buruoyanyang.player.R;
import buruoyanyang.player.interfaces.OnAdapterClickListener;
import buruoyanyang.player.managers.CacheManager;
import buruoyanyang.player.messages.VideoListMsg;
import buruoyanyang.player.messages.VideoListOkMsg;
import buruoyanyang.player.models.VideoModel;
import buruoyanyang.player.network.BaseNetwork;
import buruoyanyang.player.views.adapters.VideosGridViewAdapter;
import in.srain.cube.views.GridViewWithHeaderAndFooter;
import in.srain.cube.views.loadmore.LoadMoreContainer;
import in.srain.cube.views.loadmore.LoadMoreGridViewContainer;
import in.srain.cube.views.loadmore.LoadMoreHandler;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

public class VideoListActivity extends BaseAppCompatActivity implements OnAdapterClickListener {

    String ClickedId = "";
    String lastView = "";
    int currentPageNum = 0;
    int defaultPageSize = 30;
    int defaultOrder = 2;
    int defaultDistrict = 0;
    int defaultKind = 0;
    List<VideoModel.ContentEntity> mContentEntityList;
    List<VideoModel.ChannelsEntity> mChannelsEntityList;
    Boolean hasNext;
    PtrClassicFrameLayout mRefresh;
    GridViewWithHeaderAndFooter mGridView;
    LoadMoreGridViewContainer mLoadMoreContainer;
    boolean isLoadMore;
    CacheManager mCacheManager;
    BaseNetwork mNetwork;
    LayoutInflater mInflater;
    List<String> videoList;
    int screenHeight;
    int screenWidth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        EventBus.getDefault().register(this);
        initClass();
        initData();
    }

    public void initClass() {
        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mGridView = (GridViewWithHeaderAndFooter) findViewById(R.id.video_List_grid);
        hasNext = true;
        isLoadMore = false;
        mChannelsEntityList = new ArrayList<>();
        mContentEntityList = new ArrayList<>();
        videoList = new ArrayList<>();
        mRefresh = (PtrClassicFrameLayout) findViewById(R.id.video_list_refresh);
        mRefresh.setLastUpdateTimeRelateObject(this);
        mRefresh.disableWhenHorizontalMove(false);
        mLoadMoreContainer = (LoadMoreGridViewContainer) findViewById(R.id.video_list_grid_container);
        mLoadMoreContainer.setAutoLoadMore(true);
        mLoadMoreContainer.useDefaultHeader();
        mCacheManager = CacheManager.get(getApplicationContext());
        mNetwork = BaseNetwork.newNetWork();
    }
    public void initChannel()
    {

    }

    public void initData() {
        ClickedId = getBundleValue();
        if (ClickedId == "-1") {
            //说明是没有传参，可能是在缓存
            ClickedId = mCacheManager.getAsString("ClickedId");
            lastView = mCacheManager.getAsString("where");
        } else {
            Bundle bundle = this.getIntent().getExtras();
            lastView = bundle.getString("where");
        }
        screenHeight = (int) mCacheManager.getAsObject("height");
        screenWidth = (int) mCacheManager.getAsObject("width");
        mLoadMoreContainer.setLoadMoreHandler(new LoadHandler());
        mRefresh.setPtrHandler(new RefreshHandler());
        //初始化完成
        //请求网络
        EventBus.getDefault().post(new VideoListMsg());
    }


    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void getVideoList(VideoListMsg msg) {
        if (lastView != "CateList") {
            String Url = "http://www.biezhi360.cn:99/Videos.aspx?page=" + currentPageNum
                    + "&cat=" + ClickedId
                    + "&size=" + defaultPageSize
                    + "&order=" + defaultOrder
                    + "&district=" + defaultDistrict
                    + "&kind=" + defaultKind
                    + "&appid=" + getString(R.string.app_id)
                    + "&version=" + getString(R.string.app_version);
            String videoJson = mNetwork.getInfoWithDataFormat(Url, getString(R.string.ase_key));
            if (isLoadMore) {
                if (videoList.size() < 6) {
                    videoList.add(videoJson);
                } else {
                    videoList.clear();
                    videoList.set(videoList.size() - 1, videoJson);
                }
            } else {
                videoList.clear();
                videoList.add(videoJson);
            }
            mContentEntityList.clear();
            for (String json : videoList) {
                Gson gson = new Gson();
                VideoModel model = gson.fromJson(json, VideoModel.class);
                mContentEntityList.addAll(model.getContent());
                hasNext = Boolean.valueOf(model.getHas_next());
                mChannelsEntityList.addAll(model.getChannels());
            }
            //通知准备修改UI
            EventBus.getDefault().post(new VideoListOkMsg());
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void VideoListOK(VideoListOkMsg msg) {
        initChannel();
        VideosGridViewAdapter adapter = new VideosGridViewAdapter(mInflater, VideoListActivity.this, mContentEntityList, mChannelsEntityList, screenHeight / 6, screenWidth * 2 / 5, lastView);
        adapter.setListener(this);
        mGridView.setAdapter(adapter);
        if (isLoadMore) {
            mLoadMoreContainer.loadMoreFinish(false, hasNext);
        } else {
            mRefresh.refreshComplete();
        }
    }

    @Override
    public void onClick(String msg, String where) {

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
            EventBus.getDefault().post(new VideoListMsg());
        }

        @Override
        public boolean checkCanDoRefresh(PtrFrameLayout frameLayout, View content, View header) {
            return PtrDefaultHandler.checkContentCanBePulledDown(frameLayout, mGridView, header);
        }
    }

    public String getBundleValue() {
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null && bundle.getString("ClickedId") != null) {
            return bundle.getString("ClickedId");
        } else {
            return "-1";
        }
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
