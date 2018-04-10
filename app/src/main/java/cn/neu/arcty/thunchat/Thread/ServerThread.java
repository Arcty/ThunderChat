package cn.neu.arcty.thunchat.Thread;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import cn.neu.arcty.thunchat.Entity.ChatEntity;
import cn.neu.arcty.thunchat.Util.Constants;
import cn.neu.arcty.thunchat.Util.Record;

/**
 * Created by arcty on 16-12-9.
 */

public class ServerThread extends ChatThread {
    //端口号
    private static int PORT;
    //服务器接收端
    private ServerSocket serverSocket;
    //客户端socket
    private Socket client;
    //输入输出流
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    //上下文对象
    private Context context;

    public ServerThread(int PORT, Context context) {
        this.PORT = PORT;
        this.context = context;
        Log.e("Start", "Service");
    }

    @Override
    public void run() {
        try {
            //将标志位置为false
            Constants.DISCONNECT = false;
            serverSocket = new ServerSocket(PORT);
            client = serverSocket.accept();
            Log.e("GET", client.toString());
            //获取数据输入输出流,注意跟客户端对应
            outputStream = new ObjectOutputStream(client.getOutputStream());
            inputStream = new ObjectInputStream(client.getInputStream());
            while (true) {
                try {
                    ChatEntity chatEntity = (ChatEntity) inputStream.readObject();
                    if (chatEntity.getName().equals("cutt")) {
                        Log.e("server收到信号", chatEntity.getWord());
                        break;
                    } else {
                        Log.e("测试是否在循环中", "test");
                        //将接收到的消息体加入消息记录列表
                        Record.CHAT_RECORD.add(chatEntity);
                        //发送广播通知列表刷新数据
                        Intent i = new Intent();
                        i.setAction(Constants.UPDATE_LIST_ACTION);
                        context.sendBroadcast(i);
                    }

                } catch (IOException e) {
                    outputStream.close();
                    inputStream.close();
                    client.close();
                    serverSocket.close();
                }


            }

        } catch (IOException e) {
            try {
                outputStream.close();
                inputStream.close();
                client.close();
                serverSocket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.close();
                inputStream.close();
                client.close();
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void write(ChatEntity entity) throws IOException {
        outputStream.writeObject(entity);
        outputStream.flush();
    }

    @Override
    public void disconnection() throws IOException {
        Log.e("ServerThread", "第3步");
        outputStream.close();
        inputStream.close();
        client.close();
        serverSocket.close();
        if(client.isClosed()&&serverSocket.isClosed()){
            Log.e("serversocket已经关闭","第4步");
        }

    }

}
