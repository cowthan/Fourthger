package com.zebdar.tom.ai;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by qiaoliang on 2017/6/25.
 *
 * 人工智能转发，接收聊天页来的文本，或者推送，或者http请求，转发到具体功能模块
 */

public class AiDispatcher {

    private static Map<String, AiWorker> workers = new HashMap<>();

    public static void addAiWoker(String key, AiWorker w){
        workers.put(key, w);
    }

    public static void dispatch(String input, OnAiCallback callback){

        for(String keyword: workers.keySet()){
            if(keyMatch(input, keyword)){
                Log.e("ai--work", input + "---matchs---" + keyword);
                AiWorker worker = workers.get(keyword);
                worker.handle(input, callback);
                return;
            }
        }

        ///没匹配到，后期得改成尝试发给服务器处理，服务器再没有，才onNoResponse
        Log.e("ai--work", input + "---doesn't match any keyword---");
        callback.onNoResponse(input);
    }

    private static boolean keyMatch(String userInput, String keyword){
        return userInput.equalsIgnoreCase(keyword);
    }

}
