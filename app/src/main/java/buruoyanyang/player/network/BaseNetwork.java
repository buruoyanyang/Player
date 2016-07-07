package buruoyanyang.player.network;

/**
 * Created by xhf on 16/7/7.
 */


import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import buruoyanyang.player.utils.AESDecodeUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 错误代码：
 * 0 服务器掉线
 * 1 读写失败
 * 2 解密失败
 * 5 未知错误
 */
//使用EventBus统一管理线程
public class BaseNetwork {
    public static final String SERVER_IS_TIMEOUT = "0";
    public static final String IO_FAIL = "1";
    public static final String UNKNOWN_ERROR = "5";

    public static String httpGetBase(String url) {
        if (url.isEmpty()) {
            return null;
        }
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        Request request = new Request
                .Builder()
                .url(url)
                .build();
        Response response;
        try {
            response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                return SERVER_IS_TIMEOUT;
            } else {
                return response.body().string();
            }
        } catch (IOException e) {
            return UNKNOWN_ERROR;
        }
    }

    public static String getInfoWithAES(String url, String key) {
        OkHttpClient client = new OkHttpClient
                .Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        Request request = new Request
                .Builder()
                .url(url)
                .build();
        Response response;
        try {
            response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                return SERVER_IS_TIMEOUT;
            } else {
                //解密
                return AESDecodeUtils.Decrypt(response.body().string(), key);
            }
        } catch (Exception ex) {
            return IO_FAIL;
        }
    }

    public static String getInfoWithDataFormat(String url, String key) {
        OkHttpClient client = new OkHttpClient
                .Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        Request request = new Request
                .Builder()
                .url(url)
                .build();
        Response response;
        try {
            response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                return SERVER_IS_TIMEOUT;
            } else {
                Gson gson = new Gson();
                Data resultData = gson.fromJson(response.body().string(), Data.class);
                return AESDecodeUtils.Decrypt(resultData.data, key);
            }
        } catch (IOException io) {
            return IO_FAIL;
        }
    }

    static class Data {
        String data;
    }
}
