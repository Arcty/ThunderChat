package cn.neu.arcty.thunchat.Widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.os.Message;

import cn.neu.arcty.thunchat.Activity.MainActivity;
import cn.neu.arcty.thunchat.Util.Builder;

/**
 * Created by arcty on 16-12-9.
 */

public class P2pInfoReceiver extends BroadcastReceiver {
    //调试用tag
    private static final String TAG = "WifiDirectBroadReceiver";
    //wifi管理器实例
    private WifiP2pManager wifiP2pManager;
    //Channel实例
    private WifiP2pManager.Channel channel;
    //MainActivity实例
    private MainActivity activity;
    //Handler实例，为了传递给FindPeers类
    private Handler handler;

    public P2pInfoReceiver(WifiP2pManager wifiP2pManager, WifiP2pManager.Channel channel, MainActivity activity, Handler handler) {
        this.wifiP2pManager = wifiP2pManager;
        this.channel = channel;
        this.activity = activity;
        this.handler = handler;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action) {
            //p2p状态改变时的广播
            case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION:
                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {

                } else {
                    Builder.BuildToast("请点击右下角按钮开启Wifi", context);
                }
                break;
            //p2p连接状态改变发送的广播
            case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION:
                wifiP2pManager.requestConnectionInfo(channel, new ConnectPeers(handler, activity));
                break;
            //对等体列表信息改变的广播
            case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION:
                if (wifiP2pManager != null) {
                    wifiP2pManager.requestPeers(channel, new FindPeers(handler));
                }
                break;
            //自己的设备状态发生变化的广播
            case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION:
                Message msg = new Message();
                msg.what = 2;
                msg.obj = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
                handler.sendMessage(msg);
                break;
        /*    //断开连接的广播
            case Constants.DISCONNECT_ACTION:
                Message message=new Message();
                message.what=5;
                handler.sendMessage(message);
                break;*/
        }
    }
}

