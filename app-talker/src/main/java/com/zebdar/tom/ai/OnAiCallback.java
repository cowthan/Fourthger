package com.zebdar.tom.ai;

/**
 * Created by qiaoliang on 2017/6/25.
 */

public interface OnAiCallback {

    void onResponse(String input, String output);
    void onNoResponse(String input);

}
