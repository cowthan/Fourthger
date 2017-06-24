package com.zebdar.tom.speech;

/**
 * Created by qiaoliang on 2017/6/24.
 */

public interface SpeechInitCallback {
    void onInitOk();
    void onInitFail(int code);
}
