package com.zebdar.tom.chat.model.memdb;

import com.zebdar.tom.chat.model.IMessageDao;
import com.zebdar.tom.chat.model.IMMsg;
import com.zebdar.tom.sdk.Lang;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by qiaoliang on 2017/6/24.
 */

public class MessageCache implements IMessageDao {

    private static List<IMMsg> msgs = new CopyOnWriteArrayList<>();
    private static int idSequence = 0;

    @Override
    public int insert(IMMsg msg) {
        idSequence++;
        msg.setMsgId(idSequence);
        msgs.add(msg);
        return idSequence;
    }

    @Override
    public void deleteTableData() {
        msgs = new CopyOnWriteArrayList<>();
    }

    @Override
    public long deleteMsgById(int msgid) {
        for(IMMsg msg: msgs){
            if(msg.getMsgId() == msgid){
                msgs.remove(msg);
                return msgid;
            }
        }
        return -1;
    }

    @Override
    public ArrayList<IMMsg> queryMsg(String from, String to, int offset) {
        if(offset >= msgs.size()){
            return new ArrayList<IMMsg>();
        }
        int remain = msgs.size() - offset;
        int start = 0;
        int end = 0;
        if(remain <= 15){
            start = 0;
            end = remain - 1;
        }else{
            end = remain - 1;
            start = end - 15;
        }
        ArrayList<IMMsg> res = new ArrayList<>();
        for(int i = start; i <= end; i++){
            res.add(msgs.get(i));
        }
        return res;
    }

    @Override
    public IMMsg queryTheLastMsg() {
        return Lang.lastElement(msgs);
    }

    @Override
    public int queryTheLastMsgId() {
        IMMsg msg = Lang.lastElement(msgs);
        return msg == null ? 0 : msg.getMsgId();
    }

    @Override
    public int queryAllNotReadCount() {
        List<IMMsg> res = new ArrayList<>();
        for(IMMsg msg: msgs){
            if("0".equals(msg.getIsReaded())){
                res.add(msg);
            }
        }
        return res.size();
    }

    @Override
    public long updateAllMsgToRead(String from, String to) {
        List<IMMsg> res = new ArrayList<>();
        for(IMMsg msg: msgs){
            if("0".equals(msg.getIsReaded())){
                msg.setIsReaded("1");
            }
        }
        return res.size();
    }

    @Override
    public long deleteAllMsg(String from, String to) {
        List<IMMsg> res = new ArrayList<>();
        for(IMMsg msg: msgs){
            if(Lang.isEquals(from, msg.getFromUser()) && Lang.isEquals(to, msg.getToUser())){
                res.add(msg);
                msgs.remove(msg);
            }
        }
        return res.size();
    }
}
