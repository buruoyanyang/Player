package buruoyanyang.player.views.customers;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.ScrollingView;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.EdgeEffectCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Scroller;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import buruoyanyang.player.R;

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
    private Rect mRect = new Rect();

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
    private int mMaxX = Integer.MAX_VALUE;

    //显示在最左边的item的index
    private int mLeftViewAdapterIndex;

    //显示在左右边的item的index
    private int mRightViewAdapterIndex;

    //当前选中item的的index
    private int mCurrentSelectedAdapterIndex;

    //回调接口：当用户滑到data的最后几个时触发
    private RunningOutOfDataListener mRunningOutOfDataListener = null;

    //用户定义的，还有多少个item会被认为是超出data
    private int mRunningOutOfDataThreshold = 0;

    //是否已经通知listener处于low on data状态
    private boolean mHasNotifiedRunningLowOnData = false;

    //回调接口：当滑动状态改变的时候被触发
    private OnScrollStateChangedListener mOnScrollStateChangedListener = null;

    //返回当前滑动状态，这样我们就可以通知scroll listener

    private OnScrollStateChangedListener.ScrollState mCurrentScrollState = OnScrollStateChangedListener.ScrollState.SCROLL_STATE_IDLE;

    //左边缘的反馈效果
    private EdgeEffectCompat mEdgeGlowLeft;

    //右边缘的反馈效果
    private EdgeEffectCompat mEdgeGlowRight;

    //空间的高度，用来帮助设置子控件的高度（item的高度）
    private int mHeightMeasureSpec;

    //touch动作是否应该被阻塞
    private boolean mBlockTouchAction = false;

    //是否拦截父类view的垂直滑动事件
    private boolean mIsParentVerticallyScrollableViewDisallowingInterceptTouchEvent = false;

    //view的点击 listener
    private OnClickListener mOnClickListener;


    public ChannelList(Context context, AttributeSet attrs) {
        super(context, attrs);
        mEdgeGlowLeft = new EdgeEffectCompat(context);
        mEdgeGlowRight = new EdgeEffectCompat(context);
        //初始化手势
        bindGestureDetector();
        initView();
        retrieveXmlConfiguration(context, attrs);
        setWillNotDraw(false);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            HoneycombPlus.setFriction(mFlingTracker, FLING_FRICTION);
        }
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
        setOnTouchListener(onTouchListener);
    }

    /**
     * 当这个list被包含在一个垂直滑动的view的时候，当用户在滑动的时候，必须去禁用父类view拦截任何touch事件。
     *
     * @param disallowIntercept
     */
    private void requestParentListViewToNotInterceptTouchEvents(boolean disallowIntercept) {
        if (mIsParentVerticallyScrollableViewDisallowingInterceptTouchEvent != disallowIntercept) {
            View view = this;
            while (view.getParent() instanceof View) {
                if (view.getParent() instanceof ListView || view.getParent() instanceof ScrollingView) {
                    view.getParent().requestDisallowInterceptTouchEvent(disallowIntercept);
                    mIsParentVerticallyScrollableViewDisallowingInterceptTouchEvent = disallowIntercept;
                    return;
                }
                view = (View) view.getParent();
            }
        }
    }

    /**
     * 解析xml设置
     *
     * @param context 上下文
     * @param attrs   参数
     */
    private void retrieveXmlConfiguration(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ChannelList);
            //从xml中获取drawable
            final Drawable d = a.getDrawable(R.styleable.ChannelList_android_divider);
            if (d != null) {
                //如果获取到的drawable不为null。则可以用来规定间距
                setDivider(d);
            }
            //如果width是有详细值得，则用来设宽度
            final int dividerWidth = a.getDimensionPixelSize(R.styleable.ChannelList_dividerWidth, 0);
            if (dividerWidth != 0) {
                setDividerWidth(dividerWidth);
            }
            a.recycle();
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        //把父类的状态加入bundle
        bundle.putParcelable(BUNDLE_ID_PARENT_STATE, super.onSaveInstanceState());
        //把我们自己的状态加入bundle
        bundle.putInt(BUNDLE_ID_CURRENT_X, mCurrentX);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            //从bundle中恢复我们自己的状态
            mRestoreX = bundle.getInt(BUNDLE_ID_CURRENT_X);
            //从bundle中回复父类的状态
            super.onRestoreInstanceState(bundle.getParcelable(BUNDLE_ID_PARENT_STATE));
        }
    }

    /**
     * 设置两个item之间的间距
     * 如果没有确定的而间距，那就调用setDividerWidth(int) 方法
     *
     * @param divider
     */
    public void setDivider(Drawable divider) {
        mDivider = divider;
        if (divider != null) {
            setDividerWidth(divider.getIntrinsicWidth());
        } else {
            setDividerWidth(0);
        }
    }

    /**
     * 设置item之间的间距
     * 如果调用了这个方法，那么setDivider的效果将被覆盖
     *
     * @param width
     */
    public void setDividerWidth(int width) {
        mDividerWidth = width;
        requestLayout();
        invalidate();
    }

    private void initView() {
        mLeftViewAdapterIndex = -1;
        mRightViewAdapterIndex = -1;
        mDisplayOffset = 0;
        mCurrentX = 0;
        mNextX = 0;
        mMaxX = Integer.MAX_VALUE;
        setCurrentScrollState(OnScrollStateChangedListener.ScrollState.SCROLL_STATE_IDLE);
    }

    //重新初始化这个view，移除所有的item，还原到最初始状态
    private void reset() {
        initView();
        removeAllViewsInLayout();
        requestLayout();
    }

    /**
     * 数据集观察者，用来捕获adapter的数据改变事件
     */
    private DataSetObserver mAdapterDataObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            mDataChanged = true;

            //清除标记，以便再次通知当我们快要移出data
            mHasNotifiedRunningLowOnData = false;

            unpressTouchedChild();

            //强制当前view重绘他自己
            invalidate();
            requestLayout();
        }

        @Override
        public void onInvalidated() {
            //清除标记，以便再次通知当我们快要移出data
            mHasNotifiedRunningLowOnData = false;

            unpressTouchedChild();
            reset();

            invalidate();
            requestLayout();
        }
    };

    @Override
    public void setSelection(int position) {
        mCurrentSelectedAdapterIndex = position;
    }

    @Override
    public View getSelectedView() {
        return getChild(mCurrentSelectedAdapterIndex);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mAdapterDataObserver);
        }
        if (adapter != null) {
            mHasNotifiedRunningLowOnData = false;
            mAdapter = adapter;
            mAdapter.registerDataSetObserver(mAdapterDataObserver);
        }
        initializeRecycledViewCache(mAdapter != null ? mAdapter.getViewTypeCount() : 0);
        reset();
    }

    @Override
    public ListAdapter getAdapter() {
        return mAdapter;
    }

    /**
     * 创建并初始化一个缓存，用来保存数量一定的不同类型的view
     *
     * @param viewTypeCount
     */
    private void initializeRecycledViewCache(int viewTypeCount) {
        //创建当前adapter中item的缓存
        mRemovedViewsCache.clear();
        for (int i = 0; i < viewTypeCount; i++) {
            mRemovedViewsCache.add(new LinkedList<View>());
        }
    }

    /**
     * 返回一个可以被再利用的view
     * 如果是无效的，就返回null
     *
     * @param adapterIndex
     * @return
     */
    private View getRecycledView(int adapterIndex) {
        int itemViewType = mAdapter.getItemViewType(adapterIndex);
        if (isItemViewTypeValid(itemViewType)) {
            //获取并移除view
            return mRemovedViewsCache.get(itemViewType).poll();
        }
        return null;
    }


    /**
     * 把提供的view添加到缓存中
     *
     * @param adapterIndex
     * @param view
     */
    private void recycleView(int adapterIndex, View view) {
        //用一个队列来保存所有不同类型的view
        //将相同种类的view添加到各自的堆里面去
        //添加和移除的顺序并没有什么关系
        int itemViewType = mAdapter.getItemViewType(adapterIndex);
        if (isItemViewTypeValid(itemViewType)) {
            //加入view
            mRemovedViewsCache.get(itemViewType).offer(view);
        }
    }

    /**
     * 判断item的类型是否有效
     *
     * @param itemViewType
     * @return
     */
    private boolean isItemViewTypeValid(int itemViewType) {
        return itemViewType < mRemovedViewsCache.size();
    }

    /**
     * 将item添加到ViewGroup
     * 同时获取到他的正确的大小
     *
     * @param child
     * @param viewPos
     */
    private void addAndMeasureChild(final View child, int viewPos) {
        LayoutParams params = getLayoutParams(child);
        addViewInLayout(child, viewPos, params, true);
        measureChild(child);
    }

    /**
     * 测量子类
     *
     * @param child
     */
    private void measureChild(View child) {
        ViewGroup.LayoutParams childLayoutParams = getLayoutParams(child);
        int childHeightSpec = ViewGroup.getChildMeasureSpec(mHeightMeasureSpec, getPaddingTop() + getPaddingBottom(), childLayoutParams.height);
        int childWidthSpec;
        if (childLayoutParams.width > 0) {
            childWidthSpec = MeasureSpec.makeMeasureSpec(childLayoutParams.width, MeasureSpec.EXACTLY);
        } else {
            childWidthSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    /**
     * 获取item的LayoutParams
     * 如果是无效的，那就返回一个默认的
     *
     * @param child
     * @return
     */
    private ViewGroup.LayoutParams getLayoutParams(View child) {
        ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        }
        return layoutParams;
    }

    @SuppressLint("WrongCall")
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mAdapter == null) {
            return;
        }
        //强制系统重绘view
        invalidate();

        //如果数据改变，则还原所有设置，同时渲染到原来的位置
        if (mDataChanged) {
            int oldCurrentX = mCurrentX;
            initView();
            removeAllViewsInLayout();
            mNextX = oldCurrentX;
            mDataChanged = false;
        }

        //如果从屏幕旋转恢复
        if (mRestoreX != null) {
            mNextX = mRestoreX;
            mRestoreX = null;
        }


        if (mFlingTracker.computeScrollOffset()) {
            //计算下一个位置
            mNextX = mFlingTracker.getCurrX();
        }

        //一紧是最左边了，不能向左滑动了
        if (mNextX < 0) {
            mNextX = 0;
            if (mEdgeGlowLeft.isFinished()) {
                mEdgeGlowLeft.onAbsorb((int) determineFlingAbsorbVelocity());
            }
            mFlingTracker.forceFinished(true);
            setCurrentScrollState(OnScrollStateChangedListener.ScrollState.SCROLL_STATE_IDLE);
        } else if (mNextX > mMaxX) {
            //已经是最右边了
            mNextX = mMaxX;
            if (mEdgeGlowRight.isFinished()) {
                mEdgeGlowRight.onAbsorb((int) determineFlingAbsorbVelocity());
            }

            mFlingTracker.forceFinished(true);
            setCurrentScrollState(OnScrollStateChangedListener.ScrollState.SCROLL_STATE_IDLE);
        }

        //计算上一次重绘view的距离
        int dx = mCurrentX - mNextX;
        removeNonVisibleChildren(dx);
        fillList(dx);
        positionChildren(dx);

        //当view被重绘了，更新当前的位置
        mCurrentX = mNextX;

        //如果足够放下所有的item，那就计算现在最大的滑动位置
        if (determineMaxX()) {
            onLayout(changed, left, top, right, bottom);
            return;
        }

        //如果滑动动作结束了
        if (mFlingTracker.isFinished()) {
            if (mCurrentScrollState == OnScrollStateChangedListener.ScrollState.SCROLL_STATE_FLING) {
                setCurrentScrollState(OnScrollStateChangedListener.ScrollState.SCROLL_STATE_IDLE);
            } else {
                ViewCompat.postOnAnimation(this, mDelayedLayout);
            }
        }

    }

    @Override
    protected float getLeftFadingEdgeStrength() {
        int horizontalFadingEdgeLength = getHorizontalFadingEdgeLength();
        //刚好在左边第一个 关闭边缘效果
        if (mCurrentX == 0) {
            return 0;
        }
        //已经非常靠近边缘了，enable边缘效果
        else if (mCurrentX < horizontalFadingEdgeLength) {
            //按一定比例显示边缘效果
            return (float) mCurrentX / horizontalFadingEdgeLength;
        } else {
            //完全消失边缘效果
            return 1;
        }
    }

    @Override
    protected float getRightFadingEdgeStrength() {
        //和左边处理的方法一样
        int horizontalFadingEdgeLength = getHorizontalFadingEdgeLength();
        //刚好在右边最后一个
        if (mCurrentX == mMaxX) {
            return 0;
        } else if ((mMaxX - mCurrentX) < horizontalFadingEdgeLength) {
            return (float) (mMaxX - mCurrentX) / horizontalFadingEdgeLength;
        } else {
            return 1;
        }
    }

    private float determineFlingAbsorbVelocity() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            //冰淇淋版本以上可以直接获取真实速度
            return IceCreamSandwichPlus.getCurrVelocity(mFlingTracker);
        } else {
            //无法获取真实速度，所以就返回一个默认的速度
            return ABSORB_VELOCITY;
        }
    }

    //通过runnable计划requestLayout
    private Runnable mDelayedLayout = new Runnable() {
        @Override
        public void run() {
            requestLayout();
        }
    };

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //缓存高度
        mHeightMeasureSpec = heightMeasureSpec;
    }

    /**
     * 计算最大的X值，获取用户可以滑动的最远距离
     * 直到最后一个item被layout了，才能计算
     * 当最后一个item出现的时候，将会发生计算，同时决定是否需要重绘和重新
     *
     * @return
     */
    private boolean determineMaxX() {
        if (isLastItemAdapter(mRightViewAdapterIndex)) {
            View rightView = getRightmostChild();
            if (rightView != null) {
                int oldMaxX = mMaxX;


                mMaxX = mCurrentX + (rightView.getRight() - getPaddingLeft() - getRenderWidth());

                //处理无法充满一个屏幕的情况
                if (mMaxX < 0) {
                    mMaxX = 0;
                }
                if (mMaxX != oldMaxX) {
                    return true;
                }
            }
        }
        return false;
    }

    //将子view添加到左右两边，直到屏幕充满
    private void fillList(final int dx) {
        //获取最右边的子view，同时计算最右边的边界
        int edge = 0;
        View child = getRightmostChild();
        if (child != null) {
            edge = child.getRight();
        }

        //将新的view添加到右边，直到放满一个屏幕
        fillListRight(edge, dx);

        edge = 0;
        child = getLeftmostChild();
        if (child != null) {
            edge = child.getLeft();
        }
        fillListLeft(edge, dx);

    }

    /**
     * 移除所有不显示的子view
     *
     * @param dx
     */
    private void removeNonVisibleChildren(final int dx) {
        View child = getLeftmostChild();
        //循环移除最左边的view，直到所有view都在一个屏幕上
        while (child != null && child.getRight() + dx <= 0) {
            //整个子view都要被移除，所有我们需要移除他自己的宽度，同时也要移除他的间距（如果有的话）
            mDisplayOffset += isLastItemAdapter(mLeftViewAdapterIndex) ? child.getMeasuredWidth() : mDividerWidth + child.getMeasuredWidth();

            //把移除的view放到缓存
            recycleView(mLeftViewAdapterIndex, child);

            //移除view
            removeViewInLayout(child);

            mLeftViewAdapterIndex++;
            child = getLeftmostChild();
        }
        //右边也是一样的
        child = getRightmostChild();
        while (child != null && child.getLeft() + dx >= getWidth()) {
            recycleView(mRightViewAdapterIndex, child);
            removeViewInLayout(child);
            mRightViewAdapterIndex--;
            child = getRightmostChild();
        }

    }

    private void fillListRight(int rightEdge, final int dx) {
        //循环添加view到最右边直到屏幕充满
        while (rightEdge + dx + mDividerWidth < getWidth() && mRightViewAdapterIndex + 1 < mAdapter.getCount()) {
            mRightViewAdapterIndex++;

            //如果小于0，则说明是第一次添加，同时left == right
            if (mLeftViewAdapterIndex < 0) {
                mLeftViewAdapterIndex = mRightViewAdapterIndex;
            }
            //从adapter中获取view，如果缓存可用的话，就利用缓存
            View child = mAdapter.getView(mRightViewAdapterIndex, getRecycledView(mRightViewAdapterIndex), this);
            addAndMeasureChild(child, INSERT_AT_END_OF_LIST);

            //如果是第一个view，那么他左边是没有间距的，否则就添加间距
            rightEdge += (mRightViewAdapterIndex == 0 ? 0 : mDividerWidth) + child.getMeasuredWidth();

            //我们已经在data的低位时，就通知listeners去获取更多的data
            determineIfLowOnData();
        }
    }

    private void fillListLeft(int leftEdge, final int dx) {
        //添加方法和右边一样
        while (leftEdge + dx - mDividerWidth > 0 && mLeftViewAdapterIndex >= 1) {
            mLeftViewAdapterIndex--;
            View child = mAdapter.getView(mLeftViewAdapterIndex, getRecycledView(mLeftViewAdapterIndex), this);
            addAndMeasureChild(child, INSERT_AT_START_OF_LIST);
            //如果是第一个，他的左边也没有间距
            leftEdge -= mLeftViewAdapterIndex == 0 ? child.getMeasuredWidth() : mDividerWidth + child.getMeasuredWidth();

            //如果是擦边的，那只需要移除子view，否则连间距也要一起移除
            mDisplayOffset -= leftEdge + dx == 0 ? child.getMeasuredWidth() : mDividerWidth + child.getMeasuredWidth();
        }
    }

    /**
     * 依次把所有的子view放到屏幕上
     *
     * @param dx
     */
    private void positionChildren(final int dx) {
        int childCount = getChildCount();
        if (childCount > 0) {
            mDisplayOffset += dx;
            int leftOffset = mDisplayOffset;

            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                int left = leftOffset + getPaddingLeft();
                int right = left + child.getMeasuredWidth();
                int top = getPaddingTop();
                int bottom = top + child.getMeasuredHeight();

                child.layout(left, top, right, bottom);

                leftOffset += child.getMeasuredWidth() + mDividerWidth;
            }
        }
    }

    /**
     * 获取最左边的子view
     *
     * @return
     */
    private View getLeftmostChild() {
        return getChildAt(0);
    }

    /**
     * 获取最右边的子view
     *
     * @return
     */
    private View getRightmostChild() {
        return getChildAt(getChildCount() - 1);
    }

    /**
     * 获取指定位置的子view
     * 如果没有，就返回null
     *
     * @param adapterIndex
     * @return
     */
    private View getChild(int adapterIndex) {
        if (adapterIndex >= mLeftViewAdapterIndex && adapterIndex <= mRightViewAdapterIndex) {
            return getChildAt(adapterIndex - mLeftViewAdapterIndex);
        }
        return null;
    }

    /**
     * 返回指定左边的子view的index
     * 用于点击事件，找到被点击的view
     *
     * @param x
     * @param y
     * @return
     */
    private int getChildIndex(final int x, final int y) {
        int childCount = getChildCount();
        for (int index = 0; index < childCount; index++) {
            getChildAt(index).getHitRect(mRect);
            if (mRect.contains(x, y)) {
                return index;
            }
        }
        return -1;
    }

    private boolean isLastItemAdapter(int index) {
        return index == mAdapter.getCount() - 1;
    }

    /**
     * 获取已被渲染的width
     *
     * @return
     */
    private int getRenderWidth() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    /**
     * 获取已被渲染的height
     *
     * @return
     */
    private int getRenderHeight() {
        return getHeight() - getPaddingBottom() - getPaddingTop();
    }

    /**
     * 移动的指定位置
     *
     * @param x
     */
    public void scrollTo(int x) {
        mFlingTracker.startScroll(mNextX, 0, x - mNextX, 0);
        setCurrentScrollState(OnScrollStateChangedListener.ScrollState.SCROLL_STATE_FLING);
        requestLayout();
    }

    @Override
    public int getFirstVisiblePosition() {
        return mLeftViewAdapterIndex;
    }

    @Override
    public int getLastVisiblePosition() {
        return mRightViewAdapterIndex;
    }

    /**
     * 绘制边界效果
     *
     * @param canvas
     */
    private void drawEdgeGlow(Canvas canvas) {
        if (mEdgeGlowLeft != null && !mEdgeGlowLeft.isFinished() && isEdgeGlowEnabled()) {

            //边界效果是从top位置来的，所以要旋转绘制在左边界
            final int restoreCount = canvas.save();
            final int height = getHeight();
            canvas.rotate(-90, 0, 0);

            canvas.translate(-height + getPaddingBottom(), 0);

            mEdgeGlowLeft.setSize(getRenderHeight(), getRenderWidth());
            if (mEdgeGlowLeft.draw(canvas)) {
                invalidate();
            }
            canvas.restoreToCount(restoreCount);
        } else if (mEdgeGlowRight != null && !mEdgeGlowRight.isFinished() && isEdgeGlowEnabled()) {
            //同理，旋转到右边界
            final int restoreCount = canvas.save();
            final int width = getWidth();

            canvas.rotate(90, 0, 0);
            canvas.translate(getPaddingTop(), -width);
            mEdgeGlowRight.setSize(getRenderHeight(), getRenderWidth());
            if (mEdgeGlowRight.draw(canvas)) {
                invalidate();
            }
            canvas.restoreToCount(restoreCount);
        }
    }

    /**
     * 绘制间距
     *
     * @param canvas
     */
    private void drawDivider(Canvas canvas) {
        final int count = getChildCount();
        //只设置左右两边，上下一只都是一样的
        final Rect bounds = mRect;
        mRect.top = getPaddingTop();
        mRect.bottom = mRect.top + getRenderHeight();

        //绘制list的间距
        for (int i = 0; i < count; i++) {
            //最左边和最右边不需要间距
            if (!(i == count - 1) && isLastItemAdapter(mRightViewAdapterIndex)) {
                View child = getChildAt(i);

                bounds.left = child.getRight();
                bounds.right = child.getRight() + mDividerWidth;

                //处理屏幕左边界
                if (bounds.left < getPaddingLeft()) {
                    bounds.left = getPaddingLeft();
                }
                if (bounds.right > getWidth() - getPaddingRight()) {
                    bounds.right = getWidth() - getPaddingRight();
                }

                //绘制间距
                drawDivider(canvas, bounds);

                //如果是第一个view，需要决定间距是否被显示
                //如果左边的view没有将屏幕的左边界填充满？那么就需要显示
                if (i == 0 && child.getLeft() > getPaddingLeft()) {
                    bounds.left = getPaddingLeft();
                    bounds.right = child.getLeft();
                    drawDivider(canvas, bounds);
                }
            }
        }
    }

    /**
     * 通过给定的bounds来绘制间距
     *
     * @param canvas
     * @param bounds
     */
    private void drawDivider(Canvas canvas, Rect bounds) {
        if (mDivider != null) {
            mDivider.setBounds(bounds);
            mDivider.draw(canvas);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawDivider(canvas);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        drawEdgeGlow(canvas);
    }

    @Override
    protected void dispatchSetPressed(boolean pressed) {
        //不要分发setPressed到我们的子类中，我们自己调用setPressed，来获取正确的selector
    }

    protected boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        mFlingTracker.fling(mNextX, 0, (int) -velocityX, 0, 0, mMaxX, 0, 0);
        setCurrentScrollState(OnScrollStateChangedListener.ScrollState.SCROLL_STATE_FLING);
        requestLayout();
        return true;
    }

    protected boolean onDown(MotionEvent e) {
        //如果用户处于按下状态，那么需要禁用所有触摸事件，直到他放开为止
        mBlockTouchAction = !mFlingTracker.isFinished();

        //允许一个手指按下事件去获取一个按下动作
        mFlingTracker.forceFinished(true);

        setCurrentScrollState(OnScrollStateChangedListener.ScrollState.SCROLL_STATE_IDLE);

        //设置view处于不被按下状态
        unpressTouchedChild();
        if (!mBlockTouchAction) {
            //找到被按下的子view的index
            final int index = getChildIndex((int) e.getX(), (int) e.getY());
            if (index >= 0) {
                //把被按下的view保存下来，以便于之后释放
                mViewBeingTouched = getChildAt(index);
                if (mViewBeingTouched != null) {
                    //设置view被按下状态
                    mViewBeingTouched.setPressed(true);
                    refreshDrawableState();
                }
            }
        }
        return true;
    }

    /**
     * 如果一个view正在处于被按下状态，那么让他变成不被按下状态
     */
    private void unpressTouchedChild() {
        if (mViewBeingTouched != null) {
            //设置view没有被按下
            mViewBeingTouched.setPressed(false);
            refreshDrawableState();

            //设置为null，防止泄露。非常重要！！！！！！！！！！
            mViewBeingTouched = null;
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return ChannelList.this.onDown(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return ChannelList.this.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            //不允许父类ListView截获touch事件
            requestParentListViewToNotInterceptTouchEvents(true);
            unpressTouchedChild();
            mNextX += (int) distanceX;
            updateOverscrollAnimation(Math.round(distanceX));
            requestLayout();

            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            unpressTouchedChild();
            OnItemClickListener onItemClickListener = getOnItemClickListener();
            final int index = getChildIndex((int) e.getX(), (int) e.getY());
            //如果tap在子view里面，同时不阻塞touch事件
            if (index > 0 && !mBlockTouchAction) {
                View child = getChildAt(index);
                int adapterIndex = mLeftViewAdapterIndex + index;
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(ChannelList.this, child, adapterIndex, mAdapter.getItemId(adapterIndex));
                    return true;
                }
            }
            if (mOnClickListener != null && !mBlockTouchAction) {
                mOnClickListener.onClick(ChannelList.this);
            }
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            unpressTouchedChild();
            final int index = getChildIndex((int) e.getX(), (int) e.getY());
            if (index >= 0 && !mBlockTouchAction) {
                View child = getChildAt(index);
                OnItemLongClickListener onItemLongClickListener = getOnItemLongClickListener();
                if (onItemLongClickListener != null) {
                    int adapterIndex = mLeftViewAdapterIndex + index;
                    boolean handled = onItemLongClickListener.onItemLongClick(ChannelList.this, child, adapterIndex, mAdapter.getItemId(adapterIndex));
                    if (handled) {
                        //BZZZTT!!1!  what 鬼？
                        performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                    }
                }
            }
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {

            if (mFlingTracker == null || mFlingTracker.isFinished()) {
                setCurrentScrollState(OnScrollStateChangedListener.ScrollState.SCROLL_STATE_IDLE);
            }
            //允许父类view继续拦截touch事件
            requestParentListViewToNotInterceptTouchEvents(false);

            releaseEdgeGlow();
        } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
            unpressTouchedChild();

            requestParentListViewToNotInterceptTouchEvents(false);
        }
        return super.onTouchEvent(event);
    }

    private void releaseEdgeGlow() {
        if (mEdgeGlowLeft != null) {
            mEdgeGlowLeft.onRelease();
        }
        if (mEdgeGlowRight != null) {
            mEdgeGlowRight.onRelease();
        }
    }

    /**
     * 设置一个监听，用来监听我们是否已经滑动到了data的最后几个
     * 可以用来提前下载数据
     *
     * @param listener
     * @param numberOfItemsLeftConsideredLow
     */
    public void setRunningOutofDataListener(RunningOutOfDataListener listener, int numberOfItemsLeftConsideredLow) {
        mRunningOutOfDataListener = listener;
        mRunningOutOfDataThreshold = numberOfItemsLeftConsideredLow;
    }

    public static interface RunningOutOfDataListener {
        void onRunningOutOfData();
    }

    private void determineIfLowOnData() {
        if (mRunningOutOfDataListener != null && mAdapter != null && mAdapter.getCount() - (mRightViewAdapterIndex + 1) < mRunningOutOfDataThreshold) {
            if (mHasNotifiedRunningLowOnData) {
                mHasNotifiedRunningLowOnData = true;
                mRunningOutOfDataListener.onRunningOutOfData();
            }
        }
    }


    @Override
    public void setOnClickListener(OnClickListener listener) {
        mOnClickListener = listener;
    }

    public interface OnScrollStateChangedListener {
        public enum ScrollState {

            /**
             * view处于不滑动状态
             */
            SCROLL_STATE_IDLE,

            /**
             * 正在滑动状态，手指也还在屏幕上
             */
            SCROLL_STATE_TOUCH_SCROLL,

            /**
             * 惯性滑动
             */
            SCROLL_STATE_FLING
        }

        /**
         * 监听滑动状态的改变
         *
         * @param scrollState
         */
        public void onScrollStateChanged(ScrollState scrollState);
    }

    /**
     * 设置滑动状态监听
     *
     * @param listener
     */
    public void setOnScrollStateChangedListener(OnScrollStateChangedListener listener) {
        mOnScrollStateChangedListener = listener;
    }


    /**
     * 当滑动状态改变的时候，被调用设置新的状态
     *
     * @param newScrollState
     */
    private void setCurrentScrollState(OnScrollStateChangedListener.ScrollState newScrollState) {
        if (mCurrentScrollState != newScrollState && mOnScrollStateChangedListener != null) {
            mOnScrollStateChangedListener.onScrollStateChanged(newScrollState);
        }
        mCurrentScrollState = newScrollState;
    }

    /**
     * 设置滑动动画
     *
     * @param scrolledOffset
     */
    private void updateOverscrollAnimation(final int scrolledOffset) {
        if (mEdgeGlowLeft == null || mEdgeGlowRight == null) {
            return;
        }
        //计算下一个view的position
        int nextScrollPosition = mCurrentX + scrolledOffset;

        //当前没有手指动作
        if (mFlingTracker == null || mFlingTracker.isFinished()) {
            //已经滑到最左边了，且adapter不是空的
            if (nextScrollPosition < 0) {
                //计算上一帧到现在已经滑动的总数
                int overscroll = Math.abs(scrolledOffset);

                //通知边界效果重绘他自己
                mEdgeGlowLeft.onPull((float) overscroll / getRenderWidth());

                //取消右边界的边界效果
                if (!mEdgeGlowRight.isFinished()) {
                    mEdgeGlowRight.onRelease();
                }
            } else if (nextScrollPosition > mMaxX) {
                //滑动到最右边
                int overscroll = Math.abs(scrolledOffset);
                mEdgeGlowRight.onPull((float) overscroll / getRenderWidth());

                if (!mEdgeGlowLeft.isFinished()) {
                    mEdgeGlowLeft.onRelease();
                }
            }
        }
    }

    /**
     * 检测边界效果是否应该被开启
     *
     * @return
     */
    private boolean isEdgeGlowEnabled() {
        return !(mAdapter == null || mAdapter.isEmpty()) && mMaxX > 0;
    }

    @TargetApi(11)
    private static final class HoneycombPlus {
        static {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                throw new RuntimeException("Should not get this class(HoneycombPlus) unless sdk is >= 11");
            }
        }

        public static void setFriction(Scroller scroller, float friction) {
            if (scroller != null) {
                scroller.setFriction(friction);
            }
        }
    }

    @TargetApi(14)
    private static final class IceCreamSandwichPlus {
        static {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                throw new RuntimeException("Should not get this class(IceCreamSandwichPlus) unless sdk is >= 14");
            }
        }

        public static float getCurrVelocity(Scroller scroller) {
            return scroller.getCurrVelocity();
        }
    }

}
