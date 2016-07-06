package buruoyanyang.player.managers;

import android.content.Context;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


/**
 * buruoyanyang.player.managers
 * author xiaofeng
 * 16/7/6
 */
public class CacheManager {
    private static long MAX_SIZE = 1000 * 1000 * 100;
    private static int MAX_COUNT = Integer.MAX_VALUE;
    private static final String CACHE_NAME = "videoCache";
    private static Map<String,CacheManager> mInstanceMap = new HashMap<>();
    private CacheUtils mCache;

    public static CacheManager get(Context context) {
        return get(context, CACHE_NAME);
    }

    public static CacheManager get(Context context, String cacheName) {
        File file = new File(context.getCacheDir(), CACHE_NAME);
        return get(file, MAX_SIZE, MAX_COUNT);
    }

    public static CacheManager get(File cacheDir) {
        return get(cacheDir, MAX_SIZE, MAX_COUNT);
    }
    public static CacheManager get(Context context,long max_size,int max_count)
    {
        File file = new File(context.getCacheDir(), CACHE_NAME);
        return get(file,max_size,max_count);
    }
    public static CacheManager get(File cacheDir,long max_size,int max_count)
    {
        CacheManager manager = mInstanceMap.get(cacheDir.getAbsoluteFile()+myPid());
        if (manager != null)
        {
            manager = new CacheManager(cacheDir,max_size,max_count);
            mInstanceMap.put(cacheDir.getAbsolutePath()+myPid(),manager);
        }
        return manager;
    }
    private static String myPid()
    {
        return "_"+android.os.Process.myPid();
    }

    private CacheManager(File cacheDir,long max_size,int max_count)
    {
        if (!cacheDir.exists() && !cacheDir.mkdirs())
        {
            //没有也无法创建
            throw new RuntimeException("fail to make dirs in" + cacheDir.getAbsolutePath());
        }
        mCache = new CacheUtils();
    }
    public class CacheUtils
    {

    }

}
