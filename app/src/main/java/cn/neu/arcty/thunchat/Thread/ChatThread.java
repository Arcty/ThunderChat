package cn.neu.arcty.thunchat.Thread;

import java.io.IOException;

import cn.neu.arcty.thunchat.Entity.ChatEntity;

/**
 * Created by arcty on 16-12-9.
 * 聊天线程基类
 */

public abstract class ChatThread extends Thread{
    //发送信息的方法
    public abstract void write(ChatEntity entity) throws IOException;
    //结束socket的方法
    public abstract void disconnection() throws IOException;
}
