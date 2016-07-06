package buruoyanyang.player.activities;

import android.os.Bundle;
import android.view.View;

import buruoyanyang.player.R;
import buruoyanyang.player.managers.CacheManager;

public class InitActivity extends BaseActivity {

    CacheManager mCacheManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        initClass();

    }

    public void initClass() {
        setAllowFullScreen(true);
        setSteepStatusBar(true);
        mCacheManager = CacheManager.get(this);
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
        return R.layout.activity_init;
    }

    @Override
    public void initView(View view) {

    }
}
