package buruoyanyang.player.utils;

import android.util.Log;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * buruoyanyang.player.utils
 * author xiaofeng
 * 16/7/6
 */
public class CacheUtils {
    private final AtomicLong cacheSize;
    private final AtomicInteger cacheCount;
    private final long sizeLimit;
    private final int countLimit;
    private final Map<File, Long> lastUsageDates = Collections.synchronizedMap(new HashMap<File, Long>());
    protected File cacheDir;

    /**
     * @param cacheDir
     * @param sizeLimit
     * @param countLimit
     */
    private CacheUtils(File cacheDir, long sizeLimit, int countLimit) {
        this.cacheDir = cacheDir;
        this.sizeLimit = sizeLimit;
        this.countLimit = countLimit;
        //防止多线程操作导致的数据不一致
        cacheSize = new AtomicLong();
        cacheCount = new AtomicInteger();
        getSizeAndCount();
    }

    private void getSizeAndCount() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int size = 0;
                int count = 0;
                File[] cacheFiles = cacheDir.listFiles();
                if (cacheFiles != null) {
                    for (File file : cacheFiles) {
                        size += file.length();
                        count += 1;
                        lastUsageDates.put(file, file.lastModified());
                    }
                    cacheSize.set(size);
                    cacheCount.set(count);
                }
            }
        }).start();
    }

    /**
     * 存放文件
     *
     * @param file
     */
    private void put(File file) {
        long currentCount = cacheCount.get();
        while (currentCount + 1 > countLimit) {
            long freedSize = removeNext();
            currentCount = cacheSize.addAndGet(-freedSize);
        }
        cacheSize.addAndGet(file.length());
        Long currentTime = System.currentTimeMillis();
        if (file.setLastModified(currentTime)) {
            lastUsageDates.put(file, currentTime);
        }
    }

    /**
     * 获取文件
     *
     * @param key
     * @return
     */
    private File get(String key) {
        File file = new File(cacheDir, key.hashCode() + "");
        Long currentTime = System.currentTimeMillis();
        if (file.setLastModified(currentTime)) {
            lastUsageDates.put(file, currentTime);
        }
        return file;
    }

    private boolean remove(String key) {
        File image = get(key);
        return image.delete();
    }

    /**
     * 清空缓存
     */
    private boolean clear() {
        lastUsageDates.clear();
        cacheSize.set(0);
        boolean isCleared = true;
        File[] files = cacheDir.listFiles();
        if (files != null) {
            for (File _file : files) {
                if (isCleared) {
                    isCleared = _file.delete();
                }
            }
        }
        return isCleared;
    }

    /**
     * 移除最少使用的那个缓存
     *
     * @return
     */
    private long removeNext() {
        if (lastUsageDates.isEmpty()) {
            return 0;
        }
        Long oldestUsage = null;
        File mostLongUsedFile = null;
        Set<Map.Entry<File, Long>> entries = lastUsageDates.entrySet();
        synchronized (lastUsageDates) {
            for (Map.Entry<File, Long> entry : entries) {
                if (mostLongUsedFile == null) {
                    mostLongUsedFile = entry.getKey();
                    oldestUsage = entry.getValue();
                } else {
                    Long lastValueUsage = entry.getValue();
                    if (lastValueUsage < oldestUsage) {
                        oldestUsage = lastValueUsage;
                        mostLongUsedFile = entry.getKey();
                    }
                }
            }
        }
        long fileSize = mostLongUsedFile.length();
        if ((mostLongUsedFile.delete())) {
            lastUsageDates.remove(mostLongUsedFile);
        }
        return fileSize;
    }
}
