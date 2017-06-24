package com.zebdar.tom.chat.model;

import java.util.ArrayList;

public interface IMessageDao {
    /**
     * 添加新信息
     *
     * @param msg
     */
    public int insert(IMMsg msg);


    /**
     * 清空所有聊天记录
     */
    public void deleteTableData();

    /**
     * 根据msgid，删除对应聊天记录
     *
     * @return
     */
    public long deleteMsgById(int msgid);

    /**
     * 查询列表,每页返回15条,依据id逆序查询，将时间最早的记录添加进list的最前面
     *
     * @return
     */
    public ArrayList<IMMsg> queryMsg(String from, String to, int offset);

    /**
     * 查询最新一条记录
     *
     * @return
     */
    public IMMsg queryTheLastMsg();

    /**
     * 查询最新一条记录的id
     *
     * @return
     */
    public int queryTheLastMsgId();

    /**
     * 查询所有信息未读数量
     */
    public int queryAllNotReadCount();
    /**
     * 更新所有信息为已读
     */
    public long updateAllMsgToRead(String from, String to);
    public long deleteAllMsg(String from, String to);

}
