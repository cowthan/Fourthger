package com.zebdar.tom.speech;

/**
 * Created by qiaoliang on 2017/6/24.
 */

public interface SpeechCallback {

    void onRecognizedOk(String textResult, String audioPath, boolean isLast);
    void onRecognizedFail(String reason);

}
