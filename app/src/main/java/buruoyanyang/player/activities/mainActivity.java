package buruoyanyang.player.activities;


import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageButton;

import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.viewpager.SViewPager;


import buruoyanyang.player.R;
import buruoyanyang.player.managers.CacheManager;
import buruoyanyang.player.views.adapters.FragmentAdapter;


//初始化主界面
//请求cateList，加载图片
public class MainActivity extends BaseAppCompatActivity implements View.OnClickListener {

    ImageButton titleSearch;
    ImageButton titleDownload;
    ImageButton titleHistory;
    CacheManager mCacheManager;
    String tel = "";
    String password = "";
    boolean isVipUser = false;
    String homeList = "";
    String weChatId = "";
    String weChatBanner = "";
    String UI = "";
    String cateList = "";
    SparseArray<Object> infoArray;
    int screenHeight;
    int screenWidth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCacheManager = CacheManager.get(getApplicationContext());
        getInfoFromCache();
        initClass();

    }
    private void getInfoFromCache() {

        tel = mCacheManager.getAsString("tel");
        password = mCacheManager.getAsString("password");
        if (mCacheManager.getAsObject("vip") != null) {
            isVipUser = (boolean) mCacheManager.getAsObject("vip");
        }
        homeList = mCacheManager.getAsString("homeList");
        weChatId = mCacheManager.getAsString("weChatId");
        weChatBanner = mCacheManager.getAsString("weChatBanner");
        UI = mCacheManager.getAsString("UI");
        cateList = mCacheManager.getAsString("cateList");
        screenHeight = (int)mCacheManager.getAsObject("height");
        screenWidth = (int)mCacheManager.getAsObject("width");
        infoArray = new SparseArray<>();
        infoArray.put(0, tel);
        infoArray.put(1, password);
        infoArray.put(2, isVipUser);
        infoArray.put(3, homeList);
        infoArray.put(4, weChatId);
        infoArray.put(5, weChatBanner);
        infoArray.put(6, UI);
        infoArray.put(7, cateList);
        infoArray.put(8,screenWidth);
        infoArray.put(9,screenHeight);
    }

    private void initClass() {
        setAllowFullScreen(true);
        setSteepStatusBar(true);
        //按键
        titleSearch = (ImageButton) findViewById(R.id.title_app_search);
        titleDownload = (ImageButton) findViewById(R.id.title_app_download);
        titleHistory = (ImageButton) findViewById(R.id.title_app_history);
        titleSearch.setOnClickListener(this);
        titleHistory.setOnClickListener(this);
        titleDownload.setOnClickListener(this);
        initTab();
    }

    @SuppressWarnings("ConstantConditions")
    private void initTab() {
        SViewPager sViewPager = (SViewPager) findViewById(R.id.pager);
        Indicator indicator = (Indicator) findViewById(R.id.indicator);
        IndicatorViewPager viewPager = new IndicatorViewPager(indicator, sViewPager);
        viewPager.setAdapter(new FragmentAdapter(MainActivity.this, getSupportFragmentManager(), infoArray));
        sViewPager.setCanScroll(true);
        sViewPager.setOffscreenPageLimit(2);
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
        return R.layout.activity_main;
    }

    @Override
    public void initView(View view) {

    }

    @Override
    public void onClick(View view) {

    }
}
