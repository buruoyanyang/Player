package buruoyanyang.player.utils;

/**
 * buruoyanyang.player.utils
 * author xiaofeng
 * 16/7/6
 */
public class TimeUtils {
    private static final char mSeparator = ' ';
    private static boolean isDue(String str)
    {
        return isDue(str.getBytes());
    }
    private static boolean isDue(byte[] data)
    {
        String[] strs = getDateInfoFormDate(data);
        if (strs != null && strs.length == 2)
        {
            String saveTimeStr = strs[0];
            while (saveTimeStr.startsWith("0"))
            {
                
            }
        }
    }
    private static boolean hasDateInfo(byte[] data)
    {
        return data != null && data.length > 15 && data[13] == '-' && indexOf(data,mSeparator)> 14;
    }
    private static int indexOf(byte[] data,char c)
    {
        for (int i = 0;i< data.length;i++)
        {
            if (data[i] == c)
            {
                return i;
            }
        }
        return -1;
    }
    private static String[] getDateInfoFormDate(byte[] data)
    {
        if (hasDateInfo(data))
        {
            String saveDate = new String(copyOfRange(data,0,13));
            String deleteAfter = new String(copyOfRange(data,14,indexOf(data,mSeparator)));
            return new String[]{saveDate,deleteAfter};
        }
        return null;
    }
    private static byte[] copyOfRange(byte[] original,int from,int to) {
        int newLength = to - from;
        if (newLength < 0)
        {
            throw new IllegalArgumentException(from + ">" + to);
        }
        byte[] copy = new byte[newLength];
        System.arraycopy(original,from,copy,0,Math.min(original.length -from, newLength));
        return copy;
    }
}
