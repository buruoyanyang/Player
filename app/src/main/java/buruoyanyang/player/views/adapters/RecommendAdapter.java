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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

import buruoyanyang.player.R;
import buruoyanyang.player.models.RecommendModel;
import buruoyanyang.player.utils.ImageLoadUtils;

import static buruoyanyang.player.models.RecommendModel.RecommendsEntity;

/**
 * buruoyanyang.player.views.adapters
 * author xiaofeng
 * 16/7/14
 */
public class RecommendAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Context mContext;
    private List<String> mJsonList;
    private List<String> mNameList;
    private boolean mIsRefresh;
    ImageLoadUtils mLoader;
    int mWidth;
    int mHeight;
    BitmapDrawable holdBD;

    @SuppressWarnings("deprecation")
    public RecommendAdapter(LayoutInflater layoutInflater, Context context, List<String> jsonList, List<String> nameList, boolean isRefresh, int oWidth, int oHeight) {
        this.mInflater = layoutInflater;
        this.mContext = context;
        this.mJsonList = jsonList;
        this.mNameList = nameList;
        this.mIsRefresh = isRefresh;
        this.mWidth = oWidth;
        this.mHeight = oHeight;
        mLoader = ImageLoadUtils.newImageLoader();
        Bitmap bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.item_bg), oWidth, oHeight, false);
        holdBD = new BitmapDrawable(bitmap);
    }

    @Override
    public int getCount() {
        return mJsonList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mJsonList.size() == 0) {
            return null;
        }
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.recommend_adapter, parent, false);
        }
        int[] ivIds = new int[]{R.id.recommend_image1, R.id.recommend_image2, R.id.recommend_image3};
        int[] tvIds = new int[]{R.id.recommend_video_name_tv1, R.id.recommend_video_name_tv2, R.id.recommend_video_name_tv3};
        TextView recommendName = ViewHolder.get(convertView, R.id.recommend_name_tv);
        recommendName.setText(mNameList.get(position));
        Gson gson = new Gson();
        String json = mJsonList.get(position);
        ImageView imageView;
        TextView textView;
        RecommendModel model = gson.fromJson(json, RecommendModel.class);
        if (model != null) {
            List<RecommendsEntity> list = model.getRecommends();
            for (int i = 0; i < list.size(); i++) {
                imageView = ViewHolder.get(convertView, ivIds[i % 3]);
                textView = ViewHolder.get(convertView, tvIds[i % 3]);
                int loop = i;
                if (mIsRefresh) {
                    loop = (loop + 3) % 6;
                }
                //加载图片
                textView.setText(list.get(loop).getName());
                mLoader.load(mContext.getApplicationContext(), list.get(loop).getCover(), mWidth, mHeight, holdBD, imageView);
            }
        }

        return convertView;
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
