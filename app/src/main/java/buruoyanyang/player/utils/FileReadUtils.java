package buruoyanyang.player.utils;


import android.content.Context;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import buruoyanyang.player.R;


/**
 * buruoyanyang.player.utils
 * author xiaofeng
 * 16/7/13
 */
public class FileReadUtils {
    private static class Holder {
        public static FileReadUtils sReadUtils = new FileReadUtils();
    }

    private FileReadUtils() {
    }

    public static FileReadUtils newReadUtils() {
        return Holder.sReadUtils;
    }

    public String readUI(File file, Context context) {
        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file), "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(reader);
            String str = "";
            String mineTypeLine;
            while ((mineTypeLine = bufferedReader.readLine()) != null) {
                str += mineTypeLine;
            }
            JSONObject jsonObject = new JSONObject(str);
            String responseData = jsonObject.optString("responseData");
            responseData = AESDecodeUtils.Decrypt(responseData, context.getString(R.string.ase_key));
            if (responseData.length() > 200) {
                responseData = responseData.split("\\},\\{")[0];
                responseData += "}";
            }
            return responseData;

        } catch (IOException | JSONException ignored) {
            return "";
        }
    }
}
