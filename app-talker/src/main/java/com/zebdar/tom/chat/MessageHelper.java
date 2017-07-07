package com.zebdar.tom.chat;

import com.zebdar.tom.chat.model.IMMsg;

/**
 * Created by qiaoliang on 2017/6/25.
 */

public class MessageHelper {


    public static IMMsg createTextMessage(String message, String from, String to, boolean isMeSend) {
        String time = System.currentTimeMillis() / 1000 + "";
        IMMsg msg = new IMMsg();
        msg.setFromUser(from);
        msg.setToUser(to);
        msg.setType(MessageTypes.MSG_TYPE_TEXT);
        msg.setIsComing(isMeSend ? 0 : 1);
        msg.setContent(message);
        msg.setDate(time);
        return msg;
    }

    public static IMMsg createVoiceMessage(String message, String from, String to, boolean isMeSend) {
        String time = System.currentTimeMillis() / 1000 + "";
        IMMsg msg = new IMMsg();
        msg.setFromUser(from);
        msg.setToUser(to);
        msg.setType(MessageTypes.MSG_TYPE_VOICE);
        msg.setIsComing(isMeSend ? 0 : 1);
        msg.setContent(message);
        msg.setDate(time);
        return msg;
    }

    public static IMMsg createLocationMessage(String message, String from, String to, boolean isMeSend) {
        String time = System.currentTimeMillis() / 1000 + "";
        IMMsg msg = new IMMsg();
        msg.setFromUser(from);
        msg.setToUser(to);
        msg.setType(MessageTypes.MSG_TYPE_LOCATION);
        msg.setIsComing(isMeSend ? 0 : 1);
        msg.setContent(message);
        msg.setDate(time);
        return msg;
    }

}
