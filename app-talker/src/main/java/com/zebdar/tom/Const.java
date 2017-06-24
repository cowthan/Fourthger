package com.zebdar.tom;

import android.os.Environment;

/**
 * Created by Administrator on 2017/6/23.
 */

public class Const {

    /**
     * 分隔符
     */
    public final static String  SPILT = "☆";


    public static final String XF_VOICE_APPID="573945a6";//讯飞语音appid
    public static final String XF_AD_APPID="573a6ddc";//讯飞广告appid
    public static final String XF_AD_FULLSCREEN_ID="D5B0845FF3FCF739CF88AF2FB45723F5";//讯飞广告位id
    public static final String XF_AD_BANNER_ID="78154B642F559C48E2BB53C2E46E83A3";//讯飞广告位id
    public static final String XF_AD_BANNER2_ID="3A072782D7257046E8F13FDDCBD031EF";//讯飞广告位id
    public final static String XF_SET_VOICE_RECORD="VOICE_RECORD";//录音语言
    public final static String XF_SET_VOICE_READ="XF_SET_VOICE_READ";//朗读语言

    public final static String IM_VOICE_TPPE="IM_VOICE_TPPE";//语音聊天形式
    public final static String IM_SPEECH_TPPE="IM_SPEECH_TPPE";//聊天回复是否直接朗读


    public static final String FILE_IMG_CACHE = Environment.getExternalStorageDirectory() + "/qrobot/images/cache/";
    public static final String FILE_VOICE_CACHE = Environment.getExternalStorageDirectory() + "/qrobot/voice/";
    public static final String FILE_DOWNLOAD = Environment.getExternalStorageDirectory() + "/qrobot/download/";


    //机器人api，注意key为本人所有，使用时请到图灵机器人官网注册http://www.tuling123.com
    public static final String ROBOT_URL="http://www.tuling123.com/openapi/api";
    public static final String ROBOT_KEY="24cf362cd4b88f7b8ef3cdf207c8765f";


    //静态地图API
    public static  final String LOCATION_URL_S = "http://api.map.baidu.com/staticimage?width=320&height=240&zoom=17&center=";
    public static  final String LOCATION_URL_L = "http://api.map.baidu.com/staticimage?width=480&height=800&zoom=17&center=";

    /**
     * 默认横坐标
     */
    public final static double LOC_LONGITUDE = 116.403119;
    /**
     * 默认纵坐标
     */
    public final static double LOC_LATITUDE = 39.915378;
    /**
     * 实时定位地址
     */
    public final static String ADDRESS = "ADDRESS";
    /**
    /**
     * 实时定位坐标
     */
    public final static String LOCTION = "LOCTION";

    /**
     * 实时定位城市
     */
    public final static String CITY = "CITY";
}
