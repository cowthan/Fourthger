package com.zebdar.tom.sdk.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 模板，负责处理一个类型的Bean的显示，依赖于
 */
public abstract class AyoItemTemplate<T extends ItemBean>{  // implements AdapterDelegate<List<ItemBean>> {

    public abstract boolean isForViewType(T itemBean, int position);
    public abstract void onBindViewHolder(T itemBean, int position, AyoViewHolder holder);

    protected Activity mActivity;

    public AyoItemTemplate(Activity activity) {
        this.mActivity = activity;
    }

    public AyoItemTemplate(){
        this.mActivity = null;
    }

    public void attachToActivity(Activity a){
        this.mActivity = a;
    }

    protected abstract int getLayoutId();

    @NonNull
    public AyoViewHolder onCreateViewHolder(ViewGroup parent) {
        View v = null;
        if(parent == null){
            v = View.inflate(mActivity, getLayoutId(), null);
        }else{
            v = LayoutInflater.from(mActivity).inflate(getLayoutId(), parent, false);
        }
        AyoViewHolder h = AyoViewHolder.bind(v);
        return h;
    }


}
