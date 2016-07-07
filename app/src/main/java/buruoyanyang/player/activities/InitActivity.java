package buruoyanyang.player.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import java.io.File;

import buruoyanyang.player.R;
import buruoyanyang.player.managers.CacheManager;
import buruoyanyang.player.utils.NetworkUtils;

//需要在initActivity做的事情
//获取cateList
//获取recommendList
//获取当前账号信息
//获取版本升级情况
//获取轮播列表
//建立文件夹 准备存放登陆信息等需要永久保存的信息
//使用缓存保存其他信息
public class InitActivity extends BaseActivity implements DialogInterface.OnClickListener {

    CacheManager mCacheManager;
    private boolean isFirstInit;
    private boolean isNotNet = false;
    private String appDirPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFirstInfo();
        setContentView(R.layout.activity_init);
        initClass();
//        initData();

    }

    private void initClass() {
        setAllowFullScreen(true);
        setSteepStatusBar(true);
        mCacheManager = CacheManager.get(this);
        getNetInfo();
        if (isNotNet) {
            //准备离线启动
            if (isFirstInit) {
                //首次启动必须有网
                Toast.makeText(this, R.string.first_init_nonet_warning, Toast.LENGTH_SHORT).show();
            } else {
                //提示是否以离线模式启动
                showDialog();
            }
        }
        else
        {
            //请求数据
            initData();
        }
    }

    private void getOtherInfo()
    {
        //获取屏幕大小，获取手机信息

    }
    private void initData() {

    }

    private void showDialog() {
        AlertDialog dialog = new AlertDialog
                .Builder(this)
                .setTitle(getString(R.string.nonet_title))
                .setMessage(getString(R.string.open_wifi))
                .setNegativeButton("好", this)
                .setPositiveButton("离线启动", this)
                .create();
        dialog.show();
    }

    private void getNetInfo() {
        String netInfoStr = NetworkUtils.checkNetWork(this);
        switch (netInfoStr) {
            case "WIFI":
                isNotNet = false;
                break;
            case "MOBILE":
                isNotNet = false;
                Toast.makeText(this, R.string.mobile_warning, Toast.LENGTH_SHORT).show();
                break;
            case "NO":
                isNotNet = true;
                Toast.makeText(this, R.string.notnet_warning, Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }


    private void getFirstInfo() {
        File appDir = new File(Environment.getExternalStorageDirectory(), "biezhi");
        isFirstInit = !appDir.exists();
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

    @Override
    public void onClick(DialogInterface dialog, int which) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {

        }
        if (which != DialogInterface.BUTTON_POSITIVE) {
            //跳转
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            //todo 退出程序
        } else {
            //离线启动
            isNotNet = true;
            dialog.dismiss();
        }

    }
}
