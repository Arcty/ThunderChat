package cn.neu.arcty.thunchat.Adapter;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import cn.neu.arcty.thunchat.R;

/**
 * Created by arcty on 16-12-9.
 * 设备列表的适配器.
 */

public class ListAdapter extends BaseAdapter {
    //设备列表
    private ArrayList<WifiP2pDevice> deviceArrayList;
    //上下文对象
    private Context context;
    //handler对象
    private Handler handler;

    public ListAdapter(ArrayList<WifiP2pDevice> deviceArrayList, Context context, Handler handler) {
        this.deviceArrayList = deviceArrayList;
        this.context = context;
        this.handler = handler;
    }

    @Override
    public int getCount() {
        return deviceArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return deviceArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        MyHolder holder = null;
        if (convertView == null) {
            convertView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.item_device, null);
            holder = new MyHolder((TextView) convertView.findViewById(R.id.tv_device_name), (Button) convertView.findViewById(R.id.btn_connect));
            convertView.setTag(holder);
        } else {
            holder = (MyHolder) convertView.getTag();
        }
        //为设备名称设置监听器
        holder.tv_name.setText(deviceArrayList.get(i).deviceName);
        //获取设备状态码
        final int state = deviceArrayList.get(i).status;
        //为按钮设置监听器的时候，也要按照状态指定监听器
        holder.btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message msg = new Message();
                switch (state) {
                    case 3:
                        msg.what = 3;
                        msg.arg1 = i;
                        handler.sendMessage(msg);
                        break;
                    case 0:
                        //断开连接的操作
                        msg.what = 5;
                        handler.sendMessage(msg);
                        break;

                }

            }
        });
        String status;
        switch (state) {
            //不可用状态
            case 4:
                status = "不可用";
                break;
            //未连接状态
            case 3:
                status = "连接";
                break;
            //连接失败状态
            case 2:
                status = "连接失败";
                break;
            //正在连接状态
            case 1:
                status = "正在连接";
                break;
            //连接成功状态
            case 0:
                status = "断开";
                break;
            default:
                status = null;
        }
        //为按钮设置状态
        holder.btn_connect.setText(status);
        return convertView;
    }


    class MyHolder {
        private TextView tv_name;
        private Button btn_connect;

        public MyHolder(TextView tv_name, Button btn_connect) {
            this.tv_name = tv_name;
            this.btn_connect = btn_connect;


        }
    }
}
