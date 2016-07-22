package buruoyanyang.player.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

import buruoyanyang.player.R;
import buruoyanyang.player.activities.InitActivity;
import buruoyanyang.player.activities.MainActivity;
import buruoyanyang.player.interfaces.OnAdapterClickListener;
import buruoyanyang.player.models.CateModel;
import buruoyanyang.player.utils.ImageLoadUtils;

/**
 * buruoyanyang.player.views.adapters
 */
public class CateGridViewAdapter extends BaseAdapter implements View.OnClickListener {

    private LayoutInflater mInflater;
    private Context mContext;
    private int mWidth;
    private int mHeight;
    private List<CateModel.ContentEntity> mEntityList;
    private ImageLoadUtils mLoadUtils;
    BitmapDrawable holdBD;
    private OnAdapterClickListener mListener;

    @SuppressWarnings("deprecation")
    public CateGridViewAdapter(LayoutInflater inflater, Context context, String cateList, int oWidth, int oHeight) {
        this.mInflater = inflater;
        this.mContext = context;
        this.mWidth = oWidth;
        this.mHeight = oHeight;
        Gson gson = new Gson();
        CateModel model = gson.fromJson(cateList, CateModel.class);
        mEntityList = model.getContent();
        mLoadUtils = ImageLoadUtils.newImageLoader();
        Bitmap bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.item_bg), oWidth, oHeight, false);
        holdBD = new BitmapDrawable(bitmap);
    }

    public void setListener(OnAdapterClickListener listener) {
        mListener = listener;
    }

    @Override
    public int getCount() {
        return mEntityList.size();
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
        mLoadUtils.load(mContext.getApplicationContext(), mEntityList.get(position).getCover(), mWidth, mHeight, holdBD, imageView);
        imageView.setTag(R.id.image_tag, mEntityList.get(position).getCateId());
//        mLoadUtils.load(mContext.getApplicationContext(), "http://h.hiphotos.baidu.com/baike/pic/item/a686c9177f3e67092e15a66d3bc79f3df8dc550f.jpg", mWidth, mHeight, holdBD, imageView);
        textView.setText(mEntityList.get(position).getName());
        imageView.setOnClickListener(this);
        return convertView;
    }

    @Override
    public void onClick(View v) {
        Log.d("imageView", v.getTag(R.id.image_tag) + "");
        mListener.onClick(v.getTag(R.id.image_tag) + "","CateList");
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




























































