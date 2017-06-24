package com.zebdar.tom;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import java.io.File;

/**
 * Created by Administrator on 2017/6/23.
 */

public class SysUtils {
    public static void startActivity(Activity a, Class<?> c, Bundle b){
        Toast.makeText(a, "打开页面", Toast.LENGTH_SHORT).show();
    }
    public static final boolean extraUse() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    /**
     * 创建文件夹
     */
    public static void initFiles() {
        File file = new File(Environment.getExternalStorageDirectory(), "qrobot/data");
        if (!file.exists())
            file.mkdirs();
        file = new File(Environment.getExternalStorageDirectory(), "qrobot/images/upload");
        if (!file.exists())
            file.mkdirs();
        file = new File(Environment.getExternalStorageDirectory(), "qrobot/images/cache");
        if (!file.exists())
            file.mkdirs();
        file = new File(Environment.getExternalStorageDirectory(), "qrobot/download");
        if (!file.exists())
            file.mkdirs();
        file = new File(Environment.getExternalStorageDirectory(), "qrobot/voice");
        if (!file.exists())
            file.mkdirs();
    }

    /**
     * 移动文件
     * @param oldPath
     * @param newPath
     * @return
     */
    public static boolean copyFile(String oldPath, String newPath) {
        File oldFile = new File(oldPath);
        if (!oldFile.exists()) {
            return false;
        }
        if(oldFile.renameTo(new File(newPath))){
            return true;
        }else{
            return false;
        }
    }


    /**
     * 防止滑动Scrollview到顶部或底部时出现蓝边现象
     *
     * @param scrollView
     */
    public static void setOverScrollMode(ScrollView scrollView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO)
            scrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    /**
     * 防止滑动listView到顶部或底部时出现蓝边现象
     *
     * @param listView
     */
    public static void setOverScrollMode(ListView listView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO)
            listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
    }
}
