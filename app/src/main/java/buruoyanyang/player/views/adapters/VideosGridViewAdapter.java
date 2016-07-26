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


import java.util.List;

import buruoyanyang.player.R;
import buruoyanyang.player.interfaces.OnAdapterClickListener;
import buruoyanyang.player.models.VideoModel;
import buruoyanyang.player.utils.ImageLoadUtils;


/**
 * buruoyanyang.player.views.adapters
 * author xiaofeng
 * 16/7/21
 */
public class VideosGridViewAdapter extends BaseAdapter implements View.OnClickListener {

    private LayoutInflater mInflater;
    private Context mContext;
    int mWidth;
    int mHeight;
    BitmapDrawable holdBD;
    String mLastView;
    List<VideoModel.ContentEntity> mContentEntityList;
    List<VideoModel.ChannelsEntity> mChannelsEntityList;
    ImageLoadUtils mLoader;
    private OnAdapterClickListener mListener;

    @SuppressWarnings("deprecation")
    public VideosGridViewAdapter(LayoutInflater inflater, Context context, List<VideoModel.ContentEntity> contentEntityList, List<VideoModel.ChannelsEntity> channelsEntityList, int oWidth, int oHeight, String lastView) {
        this.mInflater = inflater;
        this.mContext = context;
        this.mContentEntityList = contentEntityList;
        this.mChannelsEntityList = channelsEntityList;
        this.mWidth = oWidth;
        this.mHeight = oHeight;
        this.mLastView = lastView;
        Bitmap bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.item_bg), oWidth, oHeight, false);
        holdBD = new BitmapDrawable(bitmap);
        mLoader = ImageLoadUtils.newImageLoader();
    }

    public void setListener(OnAdapterClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public int getCount() {
        return mContentEntityList.size();
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
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.video_adapter, parent, false);
        }
        ImageView imageView = ViewHolder.get(convertView, R.id.video_image);
        TextView textView = ViewHolder.get(convertView, R.id.video_name);
        mLoader.load(mContext, mContentEntityList.get(position).getCover(), mWidth, mHeight, holdBD, imageView);
        textView.setText(mContentEntityList.get(position).getName());
        return convertView;
    }

    @Override
    public void onClick(View v) {
        mListener.onClick(v.getTag(R.id.image_tag) + "", "VideoList");
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
