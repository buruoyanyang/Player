package buruoyanyang.player.views.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import buruoyanyang.player.R;
import buruoyanyang.player.utils.ImageLoadUtils;


/**
 * buruoyanyang.player.views.adapters
 * author xiaofeng
 * 16/7/21
 */
public class VideosGridViewAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context mContext;
    private List<String> mJsonList;
    ImageLoadUtils mLoader;
    int mWidth;
    int mHeight;
    BitmapDrawable holdBD;

    @SuppressWarnings("deprecation")
    public VideosGridViewAdapter(LayoutInflater inflater, Context context, List<String> jsonList, int oWidth, int oHeight) {
        this.mInflater = inflater;
        this.mContext = context;
        this.mJsonList = jsonList;
        this.mWidth = oWidth;
        this.mHeight = oHeight;
        Bitmap bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.item_bg), oWidth, oHeight, false);
        holdBD = new BitmapDrawable(bitmap);
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
    @SuppressWarnings("unchecked")
    static class ViewHolder {
        public static <T extends View> T get(View view, int id) {
            SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
            if (viewHolder == null) {
                viewHolder = new SparseArray<>();
                view.setTag(viewHolder);
            }
            View childView = viewHolder.get(id);
            if (childView == null) {
                childView = view.findViewById(id);
                viewHolder.put(id, childView);
            }
            return (T) childView;
        }
    }
}
