package buruoyanyang.player.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;


import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import buruoyanyang.player.R;
import buruoyanyang.player.managers.CacheManager;
import buruoyanyang.player.messages.InfoMsg;
import buruoyanyang.player.messages.InitOKMsg;
import buruoyanyang.player.models.LoginModel;
import buruoyanyang.player.models.WeChatModel;
import buruoyanyang.player.network.BaseNetwork;
import buruoyanyang.player.utils.FileReadUtils;
import buruoyanyang.player.utils.NetworkUtils;

//需要在initActivity做的事情
//获取cateList
//获取recommendList
//获取当前账号信息
//获取版本升级情况
//获取轮播列表
//建立文件夹 准备存放登陆信息等需要永久保存的信息
//使用缓存保存其他信息
public class InitActivity extends BaseActivity {

    CacheManager mCacheManager;
    private boolean isFirstInit;
    private boolean isNotNet = false;
    private String appDirPath = "";
    private String appVersion = "1.0";
    private String appId = "74";
    private int screenHeight = 0;
    private int screenWidth = 0;
    private String deviceId = "";
    private String telNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFirstInfo();
        setContentView(R.layout.activity_init);
        EventBus.getDefault().register(this);
        initClass();
        getOtherInfo();

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
        } else {
            //请求数据
            if (isFirstInit) {
                //添加文件夹，准备跳转
                addDir();
            } else {
                //读取账号信息
                getUIFromSD();
            }
            initData();
        }
    }


    private void addDir() {
        File appDir = new File(Environment.getExternalStorageDirectory(), "bzvideo");
        if (!appDir.exists()) {
            if (appDir.mkdir()) {
                Toast.makeText(InitActivity.this, R.string.fail_mkdir, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getUIFromSD() {
        File appDir = new File(Environment.getExternalStorageDirectory(), "bzvideo");
        if (appDir.exists()) {
            File file = new File(appDir, "UI");
            if (file.exists()) {
                FileReadUtils readUtils = FileReadUtils.newReadUtils();
                String responseData = readUtils.readUI(file, getApplicationContext());
                Gson gson = new Gson();
                LoginModel loginModel = gson.fromJson(responseData, LoginModel.class);
                String tel = loginModel.getTel();
                String password = loginModel.getPassword();
                boolean vip = loginModel.isVip();
                //缓存
                mCacheManager.put("tel", tel);
                mCacheManager.put("password", password);
                mCacheManager.put("vip", vip);
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void getOtherInfo() {
        //获取屏幕大小，获取手机信息
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenHeight = size.y;
            screenWidth = size.x;
        } else {
            screenWidth = getWindowManager().getDefaultDisplay().getWidth();
            screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        }
        //获取手机其他IME
        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (tm.getDeviceId() != "") {
            deviceId = tm.getDeviceId();
        }
        if (tm.getLine1Number() != "") {
            telNumber = tm.getLine1Number();
        }
        if (deviceId.replace("0", "") == "" || deviceId.isEmpty()) {
            deviceId = telNumber;
        }

    }

    private void initData() {
        EventBus.getDefault().post(new InfoMsg());
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void getInfoFromServer(InfoMsg im) {
        Gson gson;
        BaseNetwork baseNetwork = BaseNetwork.newNetWork();
        String result = baseNetwork.getInfoWithDataFormat("http://115.29.190.54:99/category.aspx?appid=" + appId + "&version=" + appVersion, getString(R.string.ase_key));
        if (result.length() < 10) {
            //异常
            Log.d(getPackageName(), result + " 异常");
        } else {
            mCacheManager.put("cateList", result);
            Log.d(getPackageName(), result);
        }
        result = baseNetwork.httpGetBase("http://115.29.190.54:99/idfa.aspx?idfa=" + deviceId);
        assert result != null;
        if (result.length() < 10) {
            Log.d(getPackageName(), result + " 异常");
            //异常
        } else {
            //处理
            gson = new Gson();
            WeChatModel model = gson.fromJson(result, WeChatModel.class);
            mCacheManager.put("weChatId", model.getWx());
            mCacheManager.put("weChatBanner", model.getBanner());
            Log.d(getPackageName(), result);
        }
        result = baseNetwork.getInfoWithDataFormat("http://115.29.190.54:99/Home.aspx?appid=" + appId + "&version=" + appVersion, getString(R.string.ase_key));
        if (result.length() < 10) {
            Log.d(getPackageName(), result + " 异常");
        } else {
            mCacheManager.put("homeList", result);
            Log.d(getPackageName(), result);
        }
        EventBus.getDefault().post(new InitOKMsg());

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void startNextActivity(InitOKMsg im) {
        //跳转
        Toast.makeText(InitActivity.this, "准备跳转", Toast.LENGTH_SHORT).show();
        startActivity(mainActivity.class);
    }

    @SuppressLint("InflateParams")
    private void showDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        View view = getLayoutInflater().inflate(R.layout.offline_dialog_layout, null);
        dialog.setView(view, 0, 0, 0, 0);
        dialog.show();
        int width = getWindowManager().getDefaultDisplay().getWidth();//得到当前显示设备的宽度，单位是像素
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = width - (width / 6);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(params);
        Button openWifiBtn = (Button) dialog.getWindow().findViewById(R.id.open_net_ok);
        Button offlineBtn = (Button) dialog.getWindow().findViewById(R.id.start_offline);
        openWifiBtn.setTag(1);
        offlineBtn.setTag(2);
        offlineBtn.setOnClickListener(this);
        openWifiBtn.setOnClickListener(this);
        dialog.setCanceledOnTouchOutside(false);
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
        File appDir = new File(Environment.getExternalStorageDirectory(), "bzvideo");
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
    public void onClick(View view) {
        switch ((int) view.getTag()) {
            case 1:
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                break;
            case 2:
                isNotNet = true;
                //跳转
//                dialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
