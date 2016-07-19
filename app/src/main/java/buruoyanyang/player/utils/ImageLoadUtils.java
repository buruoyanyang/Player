package buruoyanyang.player.utils;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * buruoyanyang.player.utils
 * author xiaofeng
 * 16/7/14
 */
public class ImageLoadUtils {
    private static class Holder {
        public static ImageLoadUtils sImageLoadUtils = new ImageLoadUtils();
    }

    private ImageLoadUtils() {

    }

    public static ImageLoadUtils newImageLoader() {
        return Holder.sImageLoadUtils;
    }

    public void load(Context context, String url, int oWidth, int oHeight, BitmapDrawable errorDrawable, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .override(oWidth, oHeight)
                .error(errorDrawable)
                .placeholder(errorDrawable)
                .centerCrop()
                .into(imageView);
    }

}
