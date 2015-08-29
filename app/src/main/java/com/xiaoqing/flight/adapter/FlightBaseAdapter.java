package com.xiaoqing.flight.adapter;

import com.xiaoqing.flight.R;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public abstract class FlightBaseAdapter<T> extends android.widget.BaseAdapter {
    // 这个类不支持移除操作
    public static final int VIEWTYPE_ID_DATA = 0;
    public static final int VIEWTYPE_ID_LOAD = 1;
    public static final int VIEWTYPE_ID_MORE = 2;
    public static final int VIEWTYPE_ID_COUNT = 3;
    /** 是否还有剩余 */
    public boolean mHasLeft = true;
    /** 页面大小 */
    private final int mPageSize;
    /** 结果集 */
    private List<T> mResult = null;
    /** 系统上下文 */
    protected final Context mContext;
    /** 布局映射器 */
    private final LayoutInflater mInflater;
    /** 列表项布局ID */
    private final int mRes;
    /** “载入中”布局ID */
    private final int mLoadingRes;
    /** 加载更多的起始索引 */
    // private int mStart = 0;
    /** 加载索引为页码 */
    private int mPageNum = 1;
    /** 临时的加载数据 */
    // private List<T> tmps;
    /** 判断是否正在加载数据，防止重复加载 */
    private boolean isLoadingData = false;
    /** 标识, 加载本页数据失败 */
    private boolean mIsFailLoad;

    /**
     * 如果没有调用setActivity初始化，则为null
     */
    private Activity mActivity;

    public FlightBaseAdapter(Context context, List<T> iniData, int pageSize, int res,
            int loadingRes) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mPageSize = pageSize;
        mRes = res;
        mLoadingRes = loadingRes;
        setInitData(iniData);
    }

    public FlightBaseAdapter(Context context, List<T> iniData, int initPageNum, int pageSize,
            int res, int loadingRes) {
        this.mPageNum = initPageNum;
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mPageSize = pageSize;
        mRes = res;
        mLoadingRes = loadingRes;
        setInitData(iniData);
    }

    public void resetPageNum(int pageNum) {
        this.mPageNum = pageNum;
    }

    public void resetList() {
        if (mResult != null) mResult.clear();
    }

    public void setActivity(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public Activity getBaseActivity() {
        if (mActivity == null) {
            throw new RuntimeException("setActivity() is not called");
        }
        return mActivity;
    }

    /**
     * 检查第一页
     */
    public boolean isCheckFirstPage() {
        return true;
    }

	/*
     * public FlightBaseAdapter(Context context, List<T> iniData, int pageSize,
	 * int res, int loadingRes, int pageNum) { mContext = context; mInflater =
	 * LayoutInflater.from(mContext); mPageSize = pageSize; mRes = res;
	 * mLoadingRes = loadingRes; setInitData(iniData); this.mPageNum = 2; }
	 */

    public List<T> getResult() {
        return mResult;
    }

    public void setInitData(List<T> iniData) {
        mResult = iniData;
        mPageNum = 1;
        // mStart += iniData.size();
        if (mResult.size() < mPageSize && isCheckFirstPage()) {
            mHasLeft = false;
        } else {
            mHasLeft = true;
        }
    }

    // public void reSetDataStart() {
    // mStart = 0;
    // }

    @Override public int getCount() {
        // return 3;
        if (mResult == null) {
            return 0;
        }
        return mHasLeft ? mResult.size() + 1 : mResult.size();
    }

    /**
     * 返回数据item的个数
     *
     * @return count
     */
    protected int getDataCount() {
        return null == mResult ? 0 : mResult.size();
    }

    @Override public int getViewTypeCount() {
        return VIEWTYPE_ID_COUNT;
    }

    @Override public int getItemViewType(int position) {
        if (position >= getDataCount()) {
            if (mIsFailLoad) {
                return VIEWTYPE_ID_MORE;
            } else {
                return VIEWTYPE_ID_LOAD;
            }
        } else {
            return VIEWTYPE_ID_DATA;
        }
    }

    @Override public Object getItem(int position) {
        return position < mResult.size() ? mResult.get(position) : null;
    }

    @Override public long getItemId(int position) {
        return position;
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        // 初始化convertView
        if (null == convertView) {
            if (position == getDataCount()) {
                if (mIsFailLoad) {

                    convertView = mInflater.inflate(R.layout.item_failed, parent, false);
                } else {
                    convertView = mInflater.inflate(mLoadingRes, parent, false);
                }
            } else {
                if (convertView == null && mRes != 0) {
                    convertView = mInflater.inflate(mRes, parent, false);
                }
            }
        }

        // 显示更多视图时, 添加监听事件
        if (position == getDataCount() && mIsFailLoad) {
            convertView = mInflater.inflate(R.layout.item_failed, null);
            final View loadFail = convertView.findViewById(R.id.loadFail);
            final View loading = convertView.findViewById(R.id.loading);
            convertView.setOnClickListener(new OnClickListener() {

                @Override public void onClick(View v) {
                    if (mIsFailLoad && !isLoadingData) {
                        loadFail.setVisibility(View.GONE);
                        loading.setVisibility(View.VISIBLE);
                        mIsFailLoad = false;
                        isLoadingData = true;
                        loadNextPage();
                    }
                }
            });
            // Button btn = (Button)
            // convertView.findViewById(R.id.fullscreen_listitem_more_button);
            // btn.setOnClickListener(mMoreClickListener);
        } else if (position == getDataCount()) {
            if (!mIsFailLoad && !isLoadingData) {
                isLoadingData = true;
                loadNextPage();
            }
        } else {
            convertView = getView(position, convertView, parent, mResult.get(position));
        }
        return convertView;
    }

    @Override public boolean isEnabled(int position) {

        // 加载视图或者更多视图, 都不可点击
        if (position == getDataCount()) {
            return false;
        }
        return super.isEnabled(position);
    }

    /**
     * 是否还有下一页的数据
     *
     * @return true 有下一页
     */
    protected boolean hasNextPage() {
        return mHasLeft;
    }

    /**
     * 加载指定start和size区域的数据
     *
     * @param start 起始点
     * @param size 数量
     * @return null 加载失败; list.size < size 没有下一页数据; list.size == size 有下一页数据.
     */
    public void nextPage(int start, int size, ILoadNextPageData<T> iLoadNextPageData) {
    }

    public abstract View getView(int position, View convertView, ViewGroup parent, T value);

    private void loadNextPage() {
        nextPage(++mPageNum, mPageSize, new ILoadNextPageData<T>() {
            @Override public void loadNextPageData(List<T> t) {
                handleNextPage(t);
            }
        });
    }

    public interface ILoadNextPageData<T> {
        public void loadNextPageData(List<T> t);
    }

    /*
     * 处理下一页数据
     */
    private void handleNextPage(List<T> pageData) {
        if (null == pageData) {
            mHasLeft = true;
            mIsFailLoad = true;
            mPageNum--;
        } else if (mPageSize > pageData.size()) {
            mIsFailLoad = false;
            mHasLeft = false;
            // addUnrepeatData(pageData);
            mResult.addAll(parseResult(pageData));
            // mPageNum++;
            // mStart += tmps.size();
        } else {
            mHasLeft = true;
            mResult.addAll(parseResult(pageData));
            // mResult.addAll(pageData);//不可如此添加
            // mPageNum++;
            // mStart += tmps.size();
        }
        isLoadingData = false;
        notifyDataSetChanged();
    }

    /**
     * 处理下一页结果,用于去重,需要去重时,子类需要重写
     */
    protected List<T> parseResult(List<T> list) {
        return list;
    }

    // 数据去重操作 不用
    private void addUnrepeatData(List<T> list) {
        for (int i = 0; i < list.size(); i++) {
            if (!mResult.contains(list.get(i))) {
                mResult.add(list.get(i));
            }
        }
    }
    // private final View.OnClickListener mMoreClickListener = new
    // View.OnClickListener() {
    //
    // @Override
    // public void onClick(View v) {
    // if (!isLoadingData) {
    // isLoadingData = true;
    // mIsFailLoad = false;
    // loadNextPage();
    // notifyDataSetChanged();
    // }
    // }
    // };
}
