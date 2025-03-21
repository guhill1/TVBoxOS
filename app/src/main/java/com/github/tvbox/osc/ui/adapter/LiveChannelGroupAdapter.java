package com.github.tvbox.osc.ui.adapter;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.tvbox.osc.R;
import com.github.tvbox.osc.bean.LiveChannelGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pj567
 * @date :2021/1/12
 * @description:
 */
public class LiveChannelGroupAdapter extends BaseQuickAdapter<LiveChannelGroup, BaseViewHolder> {
    private int selectedGroupIndex = -1;
    private int focusedGroupIndex = -1;

    private List<LiveChannelGroup> liveChannelGroups= new ArrayList<>();;

    // 构造函数，传入数据源

    public LiveChannelGroupAdapter() {
        super(R.layout.item_live_channel_group, new ArrayList<>());
    }

    @Override
    protected void convert(BaseViewHolder holder, LiveChannelGroup item) {
        TextView tvGroupName = holder.getView(R.id.tvChannelGroupName);
        tvGroupName.setFocusable(true);
        tvGroupName.setFocusableInTouchMode(true);

        tvGroupName.setText(item.getGroupName());
        int groupIndex = item.getGroupIndex();

        //  渲染焦点和选中状态
        if (groupIndex == selectedGroupIndex && groupIndex == focusedGroupIndex) {
            tvGroupName.setTextColor(mContext.getResources().getColor(R.color.color_FF0000));
        }
        else if (groupIndex == selectedGroupIndex) {
            tvGroupName.setTextColor(mContext.getResources().getColor(R.color.color_1890FF));
        }else {
            tvGroupName.setTextColor(Color.WHITE);
        }
    }
    public void setSelectedGroupIndex(int selectedGroupIndex) {
        if (selectedGroupIndex == this.selectedGroupIndex) return;

        int preSelectedGroupIndex = this.selectedGroupIndex;
        this.selectedGroupIndex = selectedGroupIndex;

        if (preSelectedGroupIndex != -1) {
            notifyItemChanged(preSelectedGroupIndex);
        }
        if (this.selectedGroupIndex != -1) {
            notifyItemChanged(this.selectedGroupIndex);
        }
    }

    public int getSelectedGroupIndex() {
        return selectedGroupIndex;
    }

    public void setFocusedGroupIndex(int focusedGroupIndex) {
        this.focusedGroupIndex = focusedGroupIndex;
        if (this.focusedGroupIndex != -1)
            notifyItemChanged(this.focusedGroupIndex);
        else if (this.selectedGroupIndex != -1)
            notifyItemChanged(this.selectedGroupIndex);
    }
    public int getFocusedGroupIndex() {
        return this.focusedGroupIndex; // 返回当前焦点所在的组索引
    }
}