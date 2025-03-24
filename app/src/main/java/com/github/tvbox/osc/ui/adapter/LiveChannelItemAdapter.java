package com.github.tvbox.osc.ui.adapter;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.tvbox.osc.R;
import com.github.tvbox.osc.bean.LiveChannelItem;

import java.util.ArrayList;

/**
 * @author pj567
 * @date :2021/1/12
 * @description:
 */

public class LiveChannelItemAdapter extends BaseQuickAdapter < LiveChannelItem, BaseViewHolder > {
    private int selectedChannelIndex = -1;
    private int focusedChannelIndex = -1;

    public LiveChannelItemAdapter() {
        super(R.layout.item_live_channel, new ArrayList < > ());
    }

    @Override
    protected void convert(BaseViewHolder holder, LiveChannelItem item) {
        // guhill1
        // fetch itemView
        View itemView = holder.itemView;
        // set itemView touchable and focusable
        itemView.setFocusableInTouchMode(true);

        TextView tvChannelNum = holder.getView(R.id.tvChannelNum);
        TextView tvChannel = holder.getView(R.id.tvChannelName);

        tvChannelNum.setText(String.format("%s", item.getChannelNum()));
        tvChannel.setText(item.getChannelName());

        int channelIndex = item.getChannelIndex();

        //  渲染焦点和选中状态
        if (channelIndex == selectedChannelIndex) {
            if (channelIndex == focusedChannelIndex) {
                tvChannelNum.setTextColor(mContext.getResources().getColor(R.color.color_FFDF00)); // 红色表示焦点
                tvChannel.setTextColor(mContext.getResources().getColor(R.color.color_FFDF00));
            } else {
                tvChannelNum.setTextColor(mContext.getResources().getColor(R.color.color_1890FF)); // 蓝色表示选中
                tvChannel.setTextColor(mContext.getResources().getColor(R.color.color_0CADE2));
            }
        } else {
            tvChannelNum.setTextColor(Color.WHITE);
            tvChannel.setTextColor(Color.WHITE);
        }
    }

    //  设置选中状态
    public void setSelectedChannelIndex(int selectedChannelIndex) {
        if (selectedChannelIndex == this.selectedChannelIndex) return;

        int preSelectedChannelIndex = this.selectedChannelIndex;
        this.selectedChannelIndex = selectedChannelIndex;

        if (preSelectedChannelIndex != -1) {
            notifyItemChanged(preSelectedChannelIndex);
        }
        if (this.selectedChannelIndex != -1) {
            notifyItemChanged(this.selectedChannelIndex);
        }
    }

    //  设置焦点状态
    public void setFocusedChannelIndex(int focusedChannelIndex) {
        int preFocusedChannelIndex = this.focusedChannelIndex;
        this.focusedChannelIndex = focusedChannelIndex;

        if (preFocusedChannelIndex != -1) {
            notifyItemChanged(preFocusedChannelIndex);
        }
        if (this.focusedChannelIndex != -1) {
            notifyItemChanged(this.focusedChannelIndex);
        } else if (this.selectedChannelIndex != -1) {
            notifyItemChanged(this.selectedChannelIndex);
        }
    }
}