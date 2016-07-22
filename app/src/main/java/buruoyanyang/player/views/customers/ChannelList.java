package buruoyanyang.player.views.customers;


import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ScrollingView;
import android.support.v4.widget.EdgeEffectCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Scroller;

import java.net.FileNameMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * buruoyanyang.player.views.customers
 * author xiaofeng
 * 16/7/22
 */
public class ChannelList extends AdapterView<ListAdapter> {

    //设置插入item的位置
    private static final int INSERT_AT_END_OF_LIST = -1;
    private static final int INSERT_AT_START_OF_LIST = 0;

    //滑动list item的速度
    private float ABSORB_VELOCITY = 30f;

    //滑动的阻尼系数
    private float FLING_FRICTION = 0.009f;

    //在循环滑动之后恢复list的状态的标记
    private static final String BUNDLE_ID_CURRENT_X = "BUNDLE_ID_CURRENT_X";

    //在循环滑动之后恢复父类状态的标记
    private static final String BUNDLE_ID_PARENT_STATE = "BUNDLE_ID_PARENT_STATE";

    //不间断的滑动轨迹
    protected Scroller mFlingTracker = new Scroller(getContext());

    //手势listener

    //识别手势
    private GestureDetector mGestureDetector;

    //最左边的显示的item的index
    private int mDisplayOffset;

    //adapter
    protected ListAdapter mAdapter;

    //保存可能被重用的item
    protected List<Queue<View>> mRemovedViewsCache = new ArrayList<>();

    //FLAG:adapter的数据是否发生改变
    private boolean mDataChanged = false;

    //rect
    private Rect mRec = new Rect();

    //当前被触摸的item，用于代理
    private View mViewBeingTouched = null;

    //两个item之间的间距
    private int mDividerWidth = 0;

    //用来被当做间距的drawable
    private Drawable mDivider = null;

    //当前被渲染的item的position
    protected int mCurrentX;

    //下一个要被渲染的item的position
    protected int mNextX;

    //滑动位置
    private Integer mRestoreX = null;

    //X的最大值
    private int mMax = Integer.MAX_VALUE;

    //显示在最左边的item的index
    private int mLeftViewAdapterIndex;

    //显示在左右边的item的index
    private int mRightViewAdapterIndex;

    //当前选中item的的index
    private int mCurrentSelectedAdapterIndex;

    //回调接口：当用户滑到data的最后几个时触发

    //用户定义的，还有多少个item会被认为是超出data
    private int mRunningOutOfDataThreshold = 0;

    //是否已经通知listener处于low on data状态
    private boolean mHasNotifiedRunningLowOnData = false;

    //回调接口：当滑动状态改变的时候被触发

    //返回当前滑动状态，这样我们就可以通知scroll listener


    //左边缘的反馈效果
    private EdgeEffectCompat mEdgeGlowLeft;

    //右边缘的反馈效果
    private EdgeEffectCompat mEdgeGlowRight;

    //空间的高度，用来帮助设置子控件的高度（item的高度）
    private int mHeightMeasureSpec;

    //touch动作是否应该被阻塞
    private boolean mBlockTouchAction = false;

    //是否拦截父类view的垂直滑动事件
    private boolean mIsParentVerticiallyScrollableViewDisallowingInterceptTouchEvent = false;

    //view的点击 listener
    private OnClickListener mOnClickListener;


    public ChannelList(Context context, AttributeSet attrs) {
        super(context, attrs);
        mEdgeGlowLeft = new EdgeEffectCompat(context);
        mEdgeGlowRight = new EdgeEffectCompat(context);
        //初始化手势
        setWillNotDraw(false);
    }

    /**
     * 绑定手势
     */
    private void bindGestureDetector() {
        final View.OnTouchListener onTouchListener = new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mGestureDetector.onTouchEvent(event);
            }
        };
    }

    /**
     * 当这个list被包含在一个垂直滑动的view的时候，当用户在滑动的时候，必须去禁用父类view拦截任何touch事件。
     *
     * @param disallowIntercept
     */
    private void requestParentListViewToNotInterceptTouchEvents(boolean disallowIntercept) {
        if (mIsParentVerticiallyScrollableViewDisallowingInterceptTouchEvent != disallowIntercept) {
            View view = this;
            while (view.getParent() instanceof View) {
                if (view.getParent() instanceof ListView || view.getParent() instanceof ScrollingView) {
                    view.getParent().requestDisallowInterceptTouchEvent(disallowIntercept);
                    mIsParentVerticiallyScrollableViewDisallowingInterceptTouchEvent = disallowIntercept;
                    return;
                }
                view = (View) view.getParent();
            }
        }
    }


    @Override
    public ListAdapter getAdapter() {
        return null;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {

    }

    @Override
    public View getSelectedView() {
        return null;
    }

    @Override
    public void setSelection(int position) {

    }
}
