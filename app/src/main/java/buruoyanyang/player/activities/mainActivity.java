package buruoyanyang.player.activities;

import android.os.Bundle;
import android.view.View;

import buruoyanyang.player.R;

public class mainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAllowFullScreen(true);
        setSteepStatusBar(true);
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
}
