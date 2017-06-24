package com.zebdar.tom.sdk.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


/**
 * 这是一个尝试，尝试写一个真正通用的adapter，连继承都不用，唯一的区别就是添加不同的AyoItemTemplate
 *
 * AyoSoloAdapter和ItemTemplate配合使用，这两个才是暴露给用户的
 */
public class AyoSoloAdapter<T extends ItemBean> extends RecyclerView.Adapter<AyoViewHolder>{

    private AdapterDelegatesManager<T> delegatesManager;
    protected List<T> mList;
    protected Activity mActivity;

    public AyoSoloAdapter(Activity activity, List<AyoItemTemplate<? extends T>> templates) {

        this.mActivity = activity;

        // Delegates
        delegatesManager = new AdapterDelegatesManager<>();
        if(templates != null){
            for(AyoItemTemplate template: templates){
                template.attachToActivity(mActivity);
                delegatesManager.addDelegate(template);
            }
        }
        delegatesManager.addDelegate(new GuardItemTemplate(activity));
    }

    @Override
    public int getItemViewType(int position) {
        return delegatesManager.getItemViewType(mList.get(position), position);
    }

    @Override
    public AyoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return delegatesManager.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(AyoViewHolder holder, int position) {
        T t = mList.get(position);
        delegatesManager.onBindViewHolder(t, position, holder);
    }


    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public void notifyDataSetChanged(List<T> list){
        this.mList = list;
        notifyDataSetChanged();
    }

    public static <T> List<T> upgrade(List<? extends T> list){
        List<T> res = new ArrayList<>();
        for(T t: list){
            res.add(t);
        }
        return res;
    }
}

