package com.zebdar.tom;

import android.view.View;

import com.zebdar.tom.bean.Msg;

/**
 * Created by Administrator on 2017/6/23.
 */

public interface MessageUiDelegate {
    View getView(Msg m);
    void onClick(View itemView, Msg m);
    void onLongClick(View itemView, Msg m);
    void getShortContent(Msg m);
}
