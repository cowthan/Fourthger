package com.zebdar.tom.chat.callback;

import com.zebdar.tom.chat.model.IMMsg;

/**
 * Created by qiaoliang on 2017/6/25.
 *
 * 我就认为loading状态不是一个有必要存进数据库的状态，所以回调也单独给个回调
 */

public interface OnMessageChangedListener {
    void onAdd(IMMsg m);
    void onDelete(IMMsg m);
    void onUpdate(IMMsg m);
    void onLoading(IMMsg m, boolean isFinish, int progress);
}