package com.github.tvbox.osc.player.controller;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.ProgressBar;

import com.github.tvbox.osc.R;
// import com.github.tvbox.osc.base.App;

import org.jetbrains.annotations.NotNull;

/**
 * 直播控制器
 */
public class LiveController extends BaseController {
    private static final int MIN_FLING_DISTANCE = 100; // 最小识别滑动距离
    private static final int MIN_FLING_VELOCITY = 10;  // 最小识别滑动速度

    protected ProgressBar mLoading;
    private LiveControlListener listener;

    public LiveController(@NotNull Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.player_live_control_view;
    }

    @Override
    protected void initView() {
        super.initView();
        mLoading = findViewById(R.id.loading_progress);
    }

    public interface LiveControlListener {
        boolean singleTap();

        void longPress();

        void playStateChanged(int playState);

        void changeSource(int direction);
    }

    public void setListener(LiveControlListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        if (listener != null && listener.singleTap()) {
            return true;
        }
        return super.onSingleTapConfirmed(e);
    }

    @Override
    public void onLongPress(MotionEvent e) {
        if (listener != null) {
            listener.longPress();
        }
        super.onLongPress(e);
    }

    @Override
    protected void onPlayStateChanged(int playState) {
        super.onPlayStateChanged(playState);
        if (listener != null) {
            listener.playStateChanged(playState);
        }
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (e1 == null || e2 == null) return false; // 避免空指针异常

        float deltaX = e2.getX() - e1.getX();
        float deltaY = e2.getY() - e1.getY();

        if (Math.abs(deltaX) > MIN_FLING_DISTANCE && Math.abs(velocityX) > MIN_FLING_VELOCITY) {
            if (deltaX > 0) {
                // 右滑
                if (listener != null) listener.changeSource(1);
            } else {
                // 左滑
                if (listener != null) listener.changeSource(-1);
            }
            return true;
        }

        // 预留上下滑动逻辑（如果需要可以扩展）
        if (Math.abs(deltaY) > MIN_FLING_DISTANCE && Math.abs(velocityY) > MIN_FLING_VELOCITY) {
            // 可以在这里添加上下滑动的逻辑
            return true;
        }

        return false;
    }
}
