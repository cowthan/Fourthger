package com.zebdar.tom.sdk;

import android.app.Application;

/**
 * Created by qiaoliang on 2017/6/13.
 */

public class DqdCore {

    private static Application app;
    private static boolean DEBUG;

    public static void init(Application a, boolean isDebug){
        app = a;
        DEBUG = isDebug;
    }

    public static Application app(){
        return app;
    }

    public static boolean isDbug(){
        return DEBUG;
    }

}
