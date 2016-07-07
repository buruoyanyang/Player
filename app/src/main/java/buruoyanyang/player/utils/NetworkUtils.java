package buruoyanyang.player.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

/**
 * buruoyanyang.player.utils
 * author xiaofeng
 * 16/7/7
 */
public class NetworkUtils {
    @SuppressWarnings("deprecation")
    public static String checkNetWork(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return "WIFI";
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                return "MOBILE";
            } else {
                return "NO";
            }
        } else {
            return "NO";
        }
    }
}
