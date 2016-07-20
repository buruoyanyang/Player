package buruoyanyang.player.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import buruoyanyang.player.R;

/**
 * buruoyanyang.player.fragments
 * author xiaofeng
 * 16/7/20
 */
public class MyFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.my_fragment_layout, container, false);
    }
    @Override
    public void fetchData() {

    }
}
