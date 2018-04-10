package cn.neu.arcty.thunchat.Activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

import cn.neu.arcty.thunchat.Adapter.ListAdapter;
import cn.neu.arcty.thunchat.R;
import cn.neu.arcty.thunchat.Util.Builder;
import cn.neu.arcty.thunchat.Util.Constants;
import cn.neu.arcty.thunchat.Widget.ConnectService;
import cn.neu.arcty.thunchat.Widget.FindPeers;
import cn.neu.arcty.thunchat.Widget.P2pInfoReceiver;
import cn.neu.yoyo.namecarddesign.Editcardinfo;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //自定义的广播接收器对象
    private P2pInfoReceiver p2pInfoReceiver;
    //wifi管理类对象
    private WifiP2pManager wifiP2pManager;
    private WifiManager wifiManager;
    //获取channel对象
    private WifiP2pManager.Channel channel;
    //设备信息文本框
    private TextView user_name_text, status_text;
    //下拉搜索布局
    private SwipeRefreshLayout device_swipe_layout;
    //列表实例
    private ListView lv_peers;
    //适配器实例
    private ListAdapter adapter;
    //设备列表
    private ArrayList<WifiP2pDevice> mPeerslist;

    //判断谁是组长
    private boolean isGroupOwner;
    //组长的地址
    private InetAddress groupOwnerAddress;
    //设备在当前列表中的位置
    private int position;
    //自己的设备信息
    private WifiP2pDevice device;
    //service类实例
    private ConnectService connectService;
    //serviceConnection实例
    private ServiceConnection serviceConnection;
    //fab按钮
    private FloatingActionButton fab;
    /**
     * case 1:获取到数据
     * case 2:自己的设备发生变化
     * case 3:选中一个设备并连接
     * case 4:判断是否是组长.
     * case 5:断开连接的信号
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    mPeerslist = FindPeers.getDeviceList();
                    presentData(mPeerslist);
                    device_swipe_layout.setRefreshing(false);
                    break;
                case 2:
                    device = ((WifiP2pDevice) msg.obj);
                    updateDevice(device);
                    break;
                case 3:
                    position = msg.arg1;
                    connectDevice(mPeerslist.get(position), position);
                    break;
                case 4:
                    groupOwnerAddress = (InetAddress) msg.obj;
                    if (msg.arg1 == 1) {
                        isGroupOwner = true;
                    } else {
                        isGroupOwner = false;
                    }
                    initService(isGroupOwner, groupOwnerAddress);
                    break;
                case 5:
                    disconnect();
                    updateDevice(device);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        if (wifiManager.isWifiEnabled()){
            fab.setImageResource(R.drawable.close_wifi);
        } else {
            fab.setImageResource(R.drawable.start_wifi);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "改变wifi状态...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                if (wifiManager.isWifiEnabled()){
                    if (wifiManager.setWifiEnabled(false)) fab.setImageResource(R.drawable.start_wifi);
                } else {
                    if (wifiManager.setWifiEnabled(true)) fab.setImageResource(R.drawable.close_wifi);
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //建立过滤器
        IntentFilter filter = getIntentFilter(
                WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION,
                WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION,
                WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION,
                WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        //注册广播接收器
        registerReceiver(p2pInfoReceiver, filter);
    }

    @Override
    protected void onStart(){
        super.onStart();
        device_swipe_layout.setRefreshing(true);
        discover();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.editcard) {
            Intent intent = new Intent(MainActivity.this,Editcardinfo.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * 初始化资源的方法
     */
    private void init() {
        //实例化视图
        device_swipe_layout = (SwipeRefreshLayout) findViewById(R.id.device_swipe_layout);
        lv_peers = (ListView) findViewById(R.id.listview_peers);
        //实例化数组
        mPeerslist = new ArrayList();
        //获取wifi管理类
        wifiP2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //获取channel对象
        channel = wifiP2pManager.initialize(this, getMainLooper(), null);
        //实例化receiver
        p2pInfoReceiver = new P2pInfoReceiver(wifiP2pManager, channel, this, handler);
        //为下拉搜索设置监听
        device_swipe_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                discover();
            }
        });
        //为列表项设置监听器
        lv_peers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                jumpToChat(isGroupOwner, device.deviceName, mPeerslist.get(i).deviceName);
            }
        });
    }


    /**
     * 扫描对等体的方法
     */
    private void discover() {
        //开启了扫描过程并立即返回，仅仅表示初始化过程是否正确
        wifiP2pManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
            }
            @Override
            public void onFailure(int i) {
                Builder.BuildToast("扫描失败", MainActivity.this);
            }
        });
    }

    /**
     * 呈现列表数据
     * @param mPeerslist 获取到的设备列表
     */
    private void presentData(ArrayList<WifiP2pDevice> mPeerslist) {
        adapter = new ListAdapter(mPeerslist, MainActivity.this, handler);
        lv_peers.setAdapter(adapter);
    }

    /**
     * 更新设备状态的方法
     *
     * @param device
     */
    private void updateDevice(WifiP2pDevice device) {
        user_name_text = (TextView) findViewById(R.id.user_name_text);
        status_text = (TextView) findViewById(R.id.status_text);
        if (user_name_text == null || status_text == null){
            return;
        }
        user_name_text.setText(device.deviceName);
        String str_status;

        switch (device.status) {
            case WifiP2pDevice.AVAILABLE:
                str_status = "空闲";
                break;
            case WifiP2pDevice.CONNECTED:
                str_status = "已连接";
                break;
            case WifiP2pDevice.FAILED:
                str_status = "连接失败";
                break;
            case WifiP2pDevice.INVITED:
                str_status = "已邀请";
                break;
            case WifiP2pDevice.UNAVAILABLE:
                str_status = "设备不可用";
                break;
            default:
                str_status = "未知状况";
        }
        status_text.setText(str_status);
    }

    /**
     * 与指定设备建立连接
     *
     * @param device 要连接的设备
     */
    private void connectDevice(WifiP2pDevice device, final int position) {

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        //该接口仅仅通知连接是否成功
        wifiP2pManager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {}
            @Override
            public void onFailure(int i) {
                Builder.BuildToast("搭讪失败，请重试(>_<)", MainActivity.this);
            }
        });
    }

    /**
     * 开启后台线程
     *
     * @param isOwner      是否是组长
     * @param OwnerAddress 组长的ip地址
     */
    private void initService(final Boolean isOwner, final InetAddress OwnerAddress) {
        //如果连接成功，则开启service准备接收信息
        Intent intent = new Intent(MainActivity.this, ConnectService.class);
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                //获取到service实例
                connectService = ((ConnectService.MyBinder) iBinder).getService();
                //初始化socket流，准备传输数据
                connectService.initSocket(isOwner, OwnerAddress);
            }
            @Override
            public void onServiceDisconnected(ComponentName componentName) {
            }
        };
        //开启服务
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    /**
     *  设置过滤器的方法
     *
     * @param kvs 需要过滤的动作
     * @return 返回设置好的过滤器
     */
    private IntentFilter getIntentFilter(String... kvs) {
        IntentFilter filter = new IntentFilter();
        for (int i = 0; i < kvs.length; i++) {
            filter.addAction(kvs[i]);
        }
        return filter;
    }

    /**
     * 跳转到聊天界面
     *
     * @param isGroupOwner 是否是组长
     * @name 自己的名字
     * @p2pname 对方的名字
     */
    private void jumpToChat(boolean isGroupOwner, String name, String p2pname) {
        Intent intent = new Intent();
        intent.putExtra("devicename", name);
        intent.putExtra("p2pname", p2pname);
        intent.setClass(MainActivity.this, ChatActivity.class);
        startActivity(intent);
    }

    /**
     * 断开连接的方法
     */
    private void disconnect() {
        try {
            Log.e("MainActivity", "第一步");
            //结束后台线程
            connectService.cutConnection();
            //终止service
            connectService.stopSelf();
            //解除绑定
            unbindService(serviceConnection);
            //标志位回归
            Constants.DISCONNECT = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        wifiP2pManager.removeGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Builder.BuildToast("已断开连接", MainActivity.this);
                discover();
            }

            @Override
            public void onFailure(int i) {
                Builder.BuildToast("结束失败", MainActivity.this);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(p2pInfoReceiver);
    }
}
