package com.xy.shuhua.ui.mine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.xy.shuhua.R;
import com.xy.shuhua.common_background.Account;
import com.xy.shuhua.common_background.AllActions;
import com.xy.shuhua.ui.CustomApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaoyu on 2016/3/30.
 */
public class MineFragment extends Fragment implements View.OnClickListener {
    private ImageView avatar;
    private TextView name;
    private View allZuoPin;
    private View settingFL;
    private View zuoPinLL;
    private View zuoPinLineView;
    private View descLL;
    private View descLineView;
    private ViewPager viewPager;

    private List<View> views = new ArrayList<>();
    private MinePagerAdapter minePagerAdapter;

    private ZuoPinRecyclerView zuoPinRecyclerView;
    private DescView descView;

    private Account account = CustomApplication.getInstance().getAccount();

    private BroadcastReceiver refreshMyZuopin = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ((ZuoPinRecyclerView)(views.get(0))).refreshData();
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mine_fragment, null);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        avatar = (ImageView) view.findViewById(R.id.avatar);
        name = (TextView) view.findViewById(R.id.name);
        allZuoPin = view.findViewById(R.id.allZuoPin);
        settingFL = view.findViewById(R.id.settingFL);
        zuoPinLL = view.findViewById(R.id.zuoPinLL);
        zuoPinLineView = view.findViewById(R.id.zuoPinLineView);
        descLL = view.findViewById(R.id.descLL);
        descLineView = view.findViewById(R.id.descLineView);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);

        allZuoPin.setOnClickListener(this);
        settingFL.setOnClickListener(this);
        zuoPinLL.setOnClickListener(this);
        descLL.setOnClickListener(this);

        zuoPinRecyclerView = new ZuoPinRecyclerView(getContext(), account.userId);
        views.add(zuoPinRecyclerView);
        descView = new DescView(getContext());
        if(!TextUtils.isEmpty(account.introduce)){
            descView.desc.setText("个人简介："+account.introduce);
        }else{
            descView.desc.setText("这个人还没有个人简介奥");
        }
        views.add(descView);

        minePagerAdapter = new MinePagerAdapter();
        viewPager.setAdapter(minePagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                changedSelectedState(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        initUserData();
        registerReceiver();
    }

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter(AllActions.key_refresh_my_zuopin_list);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        localBroadcastManager.registerReceiver(refreshMyZuopin, intentFilter);
    }

    private void unRegisterReceiver() {
        if (refreshMyZuopin != null) {
            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
            localBroadcastManager.unregisterReceiver(refreshMyZuopin);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initUserData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterReceiver();
    }

    private void initUserData() {
        /**初始化数据**/
        if (account != null) {
            if (!TextUtils.isEmpty(account.avatar)) {
                Glide.with(this).load(account.avatar).error(R.drawable.me_avatar_boy).into(avatar);
            } else {
                avatar.setImageResource(R.drawable.me_avatar_boy);
            }

            if (!TextUtils.isEmpty(account.userName)) {
                name.setText(account.userName);
            } else {
                name.setText("去设置");
            }

            if(!TextUtils.isEmpty(account.introduce)){
                descView.desc.setText("个人简介："+account.introduce);
            }else{
                descView.desc.setText("这个人还没有个人简介奥");
            }
        }
    }

    private void changedSelectedState(int index) {
        switch (index) {
            case 0:
                zuoPinLineView.setVisibility(View.VISIBLE);
                descLineView.setVisibility(View.GONE);
                break;
            case 1:
                zuoPinLineView.setVisibility(View.GONE);
                descLineView.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.allZuoPin:
                ActivityAllZuoPin.open(getActivity(), account.userId);
                break;
            case R.id.settingFL:
                ActivitySetting.open(getActivity());
                break;
            case R.id.zuoPinLL:
                changedSelectedState(0);
                viewPager.setCurrentItem(0);
                break;
            case R.id.descLL:
                changedSelectedState(1);
                viewPager.setCurrentItem(1);
                break;
        }
    }

    private class MinePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views.get(position));
            return views.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(views.get(position));
        }
    }
}
