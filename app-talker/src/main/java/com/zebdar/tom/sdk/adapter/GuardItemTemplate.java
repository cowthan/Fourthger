package com.zebdar.tom.sdk.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * 后备，放在template列表的最后，防止有一个bean找不到对应的模板，这里会显示bean信息，让你能找到是哪个bean
 *
 * 这个就相当于原先的fallback功能，但没整的那么复杂，其实就是想要个哨兵
 */
public class GuardItemTemplate extends AyoItemTemplate {

    public GuardItemTemplate(Activity activity) {
        super(activity);
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @NonNull
    @Override
    public AyoViewHolder onCreateViewHolder(ViewGroup parent) {
        TextView tv = new TextView(mActivity);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200);
        tv.setLayoutParams(lp);
        tv.setGravity(Gravity.CENTER);
        tv.setBackgroundColor(Color.parseColor("#88000000"));
        tv.setTextColor(Color.GREEN);
        tv.setTextSize(10);
        return AyoViewHolder.bind(tv);
    }

    @Override
    public boolean isForViewType(ItemBean bean, int position) {
        return true;
    }

    @Override
    public void onBindViewHolder(ItemBean itemBean, int position, AyoViewHolder holder) {
        TextView tv_info = (TextView) holder.root();
        tv_info.setText(itemBean.getClass().getSimpleName() + "未注册样式模板");
    }


}
