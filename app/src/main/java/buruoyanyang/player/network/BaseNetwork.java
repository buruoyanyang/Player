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
    private OkHttpClient client = new OkHttpClient.Builder().connectTimeout(8L, TimeUnit.SECONDS).readTimeout(30L, TimeUnit.SECONDS).writeTimeout(15L, TimeUnit.SECONDS).build();
    public static final String SERVER_IS_TIMEOUT = "0";
    public static final String IO_FAIL = "1";
    public static final String UNKNOWN_ERROR = "5";

    private static class Holder {
        public static BaseNetwork sNetwork = new BaseNetwork();
    }

    private BaseNetwork() {
    }

    public static BaseNetwork newNetWork() {
        return Holder.sNetwork;
    }


    public String httpGetBase(String url) {
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

    public String getInfoWithAES(String url, String key) {
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
                return AESDecodeUtils.Decrypt(response.body().string(), key);
            }
        } catch (IOException e) {
            return IO_FAIL;
        }
    }

    public String getInfoWithDataFormat(String url, String key) {
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
                String jsonStr = response.body().string();
                Gson gson = new Gson();
                Data resultData = gson.fromJson(jsonStr, Data.class);
                response.close();
                return AESDecodeUtils.Decrypt(resultData.data, key);

            }
        } catch (IOException e) {
            return IO_FAIL;
        } catch (Exception e) {
            return UNKNOWN_ERROR;
        }
    }

    class Data {
        String data;
    }
}





































