package buruoyanyang.player.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.gson.Gson;
import com.rey.material.widget.ProgressView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import buruoyanyang.player.R;
import buruoyanyang.player.interfaces.OnAdapterClickListener;
import buruoyanyang.player.messages.HotBeginMsg;
import buruoyanyang.player.messages.HotOkMsg;
import buruoyanyang.player.models.HotsModel;
import buruoyanyang.player.models.HotsModel.HomeEntity;
import buruoyanyang.player.network.BaseNetwork;
import buruoyanyang.player.views.adapters.RecommendAdapter;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * buruoyanyang.player.fragments
 * author xiaofeng
 * 16/7/14
 */
public class RecommendFragment extends BaseFragment implements OnAdapterClickListener ,View.OnClickListener{
    ListView recommendList;
    PtrClassicFrameLayout mPtrClassicFrameLayout;
    Context superContext;
    String homeList = "";
    boolean isRefresh = false;
    List<String> jsonList;
    BaseNetwork mNetWork;
    List<HomeEntity> entityList;
    LayoutInflater inflater;
    List<String> nameList;
    ProgressView mProgressView;
    int width;
    int height;
    private OnAdapterClickListener mListener;
    ImageButton titleSearch;
    ImageButton titleDownload;
    ImageButton titleHistory;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        prepareFetchData(true);
        View contentView = inflater.inflate(R.layout.recommend_fragment_layout, container, false);
        superContext = container.getContext();
        EventBus.getDefault().register(this);
        Bundle bundle = getArguments();
        homeList = bundle.getString("homeList");
        width = bundle.getInt("screenWidth");
        height = bundle.getInt("screenHeight");
        initClass(contentView);
        Log.d("visible", getUserVisibleHint() + "");
        return contentView;
    }

    public void setListener(OnAdapterClickListener listener) {
        mListener = listener;
    }

    private void initClass(View contentView) {
        titleSearch = (ImageButton) contentView.findViewById(R.id.title_app_search);
        titleDownload = (ImageButton) contentView.findViewById(R.id.title_app_download);
        titleHistory = (ImageButton) contentView.findViewById(R.id.title_app_history);
        titleDownload.setVisibility(View.INVISIBLE);
        titleSearch.setVisibility(View.INVISIBLE);
        titleHistory.setVisibility(View.INVISIBLE);
        jsonList = new ArrayList<>();
        nameList = new ArrayList<>();
        mNetWork = BaseNetwork.newNetWork();
        recommendList = (ListView) contentView.findViewById(R.id.recommend_list);
        mProgressView = (ProgressView) contentView.findViewById(R.id.recommend_loading_on);
        inflater = (LayoutInflater) superContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mPtrClassicFrameLayout = (PtrClassicFrameLayout) contentView.findViewById(R.id.recommend_refresh);
        mPtrClassicFrameLayout.setLastUpdateTimeRelateObject(superContext);
        mPtrClassicFrameLayout.disableWhenHorizontalMove(false);
        mPtrClassicFrameLayout.setPtrHandler(new RefreshHandler());
    }

    @Override
    public void onClick(String msg,String where) {
        mListener.onClick(msg,where);
    }

    @Override
    public void onClick(View v) {

    }

    class RefreshHandler extends PtrDefaultHandler {

        @Override
        public void onRefreshBegin(PtrFrameLayout frame) {
            isRefresh = !isRefresh;
            EventBus.getDefault().post(new HotBeginMsg());

        }

        @Override
        public boolean checkCanDoRefresh(PtrFrameLayout frameLayout, View content, View header) {
            return PtrDefaultHandler.checkContentCanBePulledDown(frameLayout, content, header);
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void getHots(HotBeginMsg message) {
        jsonList.clear();
        Gson gson = new Gson();
        HotsModel hotsModel = gson.fromJson(homeList, HotsModel.class);
        entityList = hotsModel.getHome();
        for (HomeEntity hot : entityList) {
            String json = mNetWork.getInfoWithDataFormat("http://115.29.190.54:99/Recommends.aspx?hotid="
                    + hot.getId()
                    + "&appid=" + superContext.getString(R.string.app_id)
                    + "&version=" + superContext.getString(R.string.app_version), superContext.getString(R.string.ase_key));
            if (json.length() < 10) {

            } else {
                nameList.add(hot.getName());
                jsonList.add(json);
            }
        }
        EventBus.getDefault().post(new HotOkMsg());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setAdapter(HotOkMsg message) {
        RecommendAdapter adapter = new RecommendAdapter(inflater, superContext, jsonList, nameList, isRefresh, height / 6, width * 2 / 5);
        adapter.setListener(this);
        recommendList.setAdapter(adapter);
        mPtrClassicFrameLayout.refreshComplete();
        mProgressView.setVisibility(View.GONE);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void fetchData() {
        mProgressView.setVisibility(View.VISIBLE);
        //处理数据
        EventBus.getDefault().post(new HotBeginMsg());
    }
}
