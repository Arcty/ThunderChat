package cn.neu.arcty.thunchat.Widget;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.os.Message;

/**
 * Created by arcty on 16-12-9.
 * 检测连接信息变化
 */

public class ConnectPeers implements WifiP2pManager.ConnectionInfoListener {
    private Handler handler;
    private Context context;


    public ConnectPeers(Handler handler, Context context) {
        this.handler = handler;
        this.context = context;
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
        //判断是否建立了组
        if (wifiP2pInfo.groupFormed) {
            Message msg = new Message();
            msg.what = 4;
            msg.obj = wifiP2pInfo.groupOwnerAddress;
            if (wifiP2pInfo.isGroupOwner) {
                msg.arg1 = 1;
            } else {
                msg.arg1 = 2;
            }
            handler.sendMessage(msg);
        }


    }
}
