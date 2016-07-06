package buruoyanyang.player.activities;

import android.os.Bundle;
import android.view.View;

import buruoyanyang.player.R;
import buruoyanyang.player.managers.CacheManager;

public class InitActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        setAllowFullScreen(false);
        setSteepStatusBar(false);
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
