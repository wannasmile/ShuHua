package com.xy.shuhua.ui.mine;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by xiaoyu on 2016/3/31.
 */
public class ZuoPinItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private Context context;
    private View rootView;

    public ZuoPinItemViewHolder(Context context, View itemView) {
        super(itemView);
        this.context = context;
        this.rootView = itemView;

        rootView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
    }
}
