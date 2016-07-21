package buruoyanyang.player.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;


import buruoyanyang.player.R;

/**
 * buruoyanyang.player.fragments
 * author xiaofeng
 * 16/7/20
 */
public class MyFragment extends BaseFragment {
    Context superContext;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contextView = inflater.inflate(R.layout.my_fragment_layout, container, false);
        superContext = container.getContext();
        return contextView;
    }


    @Override
    public void fetchData() {

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d("myFragment", isVisibleToUser + "");
    }
}
