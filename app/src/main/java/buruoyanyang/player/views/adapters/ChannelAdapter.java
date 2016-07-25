package buruoyanyang.player.views.adapters;


import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;


import buruoyanyang.player.R;
import buruoyanyang.player.views.customers.ChannelData;

/**
 * buruoyanyang.player.views.adapters
 * author xiaofeng
 * 16/7/25
 */
public class ChannelAdapter extends ArrayAdapter<ChannelData> {

    private LayoutInflater mInflater;

    public ChannelAdapter(Context context, ChannelData[] channels) {
        super(context, R.layout.channel_adapter, channels);
        this.mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

    }

    static class ViewHolder {
        @SuppressWarnings("unchecked")
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
