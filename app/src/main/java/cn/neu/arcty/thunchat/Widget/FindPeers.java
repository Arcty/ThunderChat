package cn.neu.arcty.thunchat.Widget;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;

/**
 * Created by arcty on 16-12-9.
 * 查找对等体的类，主要功能是返回对等体列表。
 */

public class FindPeers implements WifiP2pManager.PeerListListener {
    private static ArrayList<WifiP2pDevice> list_wifiP2pDevices = new ArrayList<>();
    private Handler handler;

    public FindPeers(Handler handler) {
        this.handler = handler;

    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
        //先清除所有的旧信息，否则会导致列表数据重复
        list_wifiP2pDevices.clear();
        //获取到消息列表之后，使用handler发送消息给主线程，告知主线程来取设备列表。
        list_wifiP2pDevices.addAll(wifiP2pDeviceList.getDeviceList());
        Message message = new Message();
        message.what = 1;
        handler.sendMessage(message);
    }

    /**
     * 对外开放接口
     *
     * @return 返回设备列表
     */
    public static ArrayList<WifiP2pDevice> getDeviceList() {
        return list_wifiP2pDevices;
    }
}
