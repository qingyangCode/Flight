package demo.binea.com.pulltorefreshlib;

import android.view.View;

public interface PtrHandler {

    /**
     * Check can do refresh or not. For example the content is empty or the first child is in view.
     * <p/>
     * {@link demo.binea.com.pulltorefreshlib.PtrDefaultHandler#checkContentCanBePulledDown}
     */
    public boolean checkCanDoRefresh(final PtrFrameLayout frame, final View content,
            final View header);

    /**
     * When refresh begin
     *
     * @param frame
     */
    public void onRefreshBegin(final PtrFrameLayout frame);
}