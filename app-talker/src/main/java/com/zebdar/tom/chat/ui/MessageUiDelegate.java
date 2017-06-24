package com.zebdar.tom.chat.ui;

import android.view.View;

import com.zebdar.tom.chat.model.IMMsg;

/**
 * Created by Administrator on 2017/6/23.
 */

public interface MessageUiDelegate {
    View getView(IMMsg m);
    void onClick(View itemView, IMMsg m);
    void onLongClick(View itemView, IMMsg m);
    void getShortContent(IMMsg m);
}
