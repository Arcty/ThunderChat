package cn.neu.arcty.thunchat.Thread;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import cn.neu.arcty.thunchat.Entity.ChatEntity;
import cn.neu.arcty.thunchat.Util.Constants;
import cn.neu.arcty.thunchat.Util.Record;

/**
 * Created by arcty on 16-12-9.
 */

public class ClientThread extends ChatThread {
    //上下文对象
    private Context context;
    //服务器IP地址
    private InetAddress ADDRESS;
    //服务器端口号
    private int PORT;
    //客户端socket
    private Socket socket;
    //输入输出流
    private static ObjectInputStream inputStream;
    private static ObjectOutputStream outputStream;


    public ClientThread(InetAddress IP, int PORT, Context context) {
        this.ADDRESS = IP;
        this.PORT = PORT;
        this.context = context;
        Log.e("Connect", "Service" + IP);
    }

    @Override
    public void run() {
        try {
            //初始化socket
            socket = new Socket();
            socket.bind(null);
            //设置5秒超时时间
            socket.connect(new InetSocketAddress(ADDRESS, PORT), 10000);
            //获取数据输入输出流
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
            while (true) {
                try {
                    //将标志位置为false
                    Constants.DISCONNECT = false;
                    ChatEntity chatEntity = (ChatEntity) inputStream.readObject();
                    if (chatEntity.getName().equals("cutt")) {
                        Log.e("client收到信号", chatEntity.getWord());
                        break;
                    } else {
                        //将接收到的消息体加入消息记录列表
                        Record.CHAT_RECORD.add(chatEntity);
                        //发送广播通知列表刷新数据
                        Intent i = new Intent();
                        i.setAction(Constants.UPDATE_LIST_ACTION);
                        context.sendBroadcast(i);
                    }
                } catch (IOException e) {
                    if(inputStream != null) inputStream.close();
                    if (outputStream != null) outputStream.close();
                    if(socket != null) socket.close();
                }
            }
        } catch (IOException e) {
            try {
                if(inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
                if(socket != null) socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if(inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
                if(socket != null) socket.close();
            } catch (IOException e) {
            }
        }
    }


    @Override
    public void write(ChatEntity entity) throws IOException {
        if (outputStream!=null) {
            outputStream.writeObject(entity);
            outputStream.flush();
        }
    }

    @Override
    public void disconnection() throws IOException {
        Log.e("ClientThread", "第3步");
        if(inputStream != null) inputStream.close();
        if (outputStream != null) outputStream.close();
        if(socket != null) {
            socket.close();
            if(socket.isClosed()){
                Log.e("socket已经关闭","第4步");
            }
        }
    }
}
