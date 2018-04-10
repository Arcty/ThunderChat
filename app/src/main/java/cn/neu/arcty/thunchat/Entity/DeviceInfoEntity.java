package cn.neu.arcty.thunchat.Entity;

import android.net.wifi.p2p.WifiP2pDevice;

/**
 * Created by arcty on 16-12-9.
 * 存放设备信息及连接状态
 */

public class DeviceInfoEntity {
    //设备状态
    private String  devicestatus;
    //设备信息
    private WifiP2pDevice device;

    public WifiP2pDevice getDevice() {
        return device;
    }

    public void setDevice(WifiP2pDevice device) {
        this.device = device;
    }

    public String getDevicestatus() {
        return devicestatus;
    }

    public void setDevicestatus(String devicestatus) {
        this.devicestatus = devicestatus;
    }
}
