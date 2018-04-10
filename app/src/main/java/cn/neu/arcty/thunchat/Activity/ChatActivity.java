package cn.neu.arcty.thunchat.Activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import cn.neu.arcty.thunchat.Adapter.RecordListAdapter;
import cn.neu.arcty.thunchat.Entity.ChatEntity;
import cn.neu.arcty.thunchat.R;
import cn.neu.arcty.thunchat.Util.Builder;
import cn.neu.arcty.thunchat.Util.Constants;
import cn.neu.arcty.thunchat.Util.Record;

/**
 * Created by arcty on 16-12-9.
 */

public class ChatActivity extends Activity {
    //View
    private EditText editText_message;
    private Button btn_send;
    private ListView lv_chatRecord;
    private TextView tv_head;
    private Button btn_back;
    //自己的名字
    private String name;
    //对方的名字
    private String p2pname;
    //适配器实例
    private RecordListAdapter adapter;
    //过滤器实例
    private IntentFilter intentFilter;
    //监听列表状态变化的广播接收器
    private updateListReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);
    }

    /**
     * 初始化界面元素
     */
    private void initView() {
        //初始化View
        editText_message = (EditText) findViewById(R.id.tv_message);
        btn_send = (Button) findViewById(R.id.btn_sendMessage);
        lv_chatRecord = (ListView) findViewById(R.id.lv_chatRecord);
        tv_head = (TextView) findViewById(R.id.tv_head);
        btn_back = (Button) findViewById(R.id.btn_back);
        //初始化广播
        receiver = new updateListReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.UPDATE_LIST_ACTION);
        //获取到Intent信息
        Intent in = getIntent();
        p2pname = in.getExtras().get("p2pname").toString();
        name = in.getExtras().get("devicename").toString();
        //将对方名字显示在head
        tv_head.setText(p2pname);
        adapter = new RecordListAdapter(Record.CHAT_RECORD, this);
        lv_chatRecord.setAdapter(adapter);
        //为发送按钮设置监听器
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String word = editText_message.getText().toString();
                if (word.length() == 0) {
                    Builder.BuildToast("请输入文字", ChatActivity.this);
                } else {
                    if (Constants.DISCONNECT) {
                        Builder.BuildToast("已断开连接", ChatActivity.this);
                    } else {
                        ChatEntity chatEntity = new ChatEntity();
                        chatEntity.setName(name + ":");
                        chatEntity.setWord(editText_message.getText().toString());
                        //将自己发送的信息加入消息记录列表
                        Record.CHAT_RECORD.add(chatEntity);
                        //刷新数据
                        adapter.notifyDataSetChanged();
                        //将输入框清空
                        editText_message.setText("");
                        //向线程发送广播，请求发送消息
                        Intent intent = new Intent();
                        intent.setAction(Constants.NEW_MESSAGE_ACTION);
                        intent.putExtra("message", chatEntity);
                        sendBroadcast(intent);
                    }
                }
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * 接收列表聊天记录更新的接收器
     */
    class updateListReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Constants.UPDATE_LIST_ACTION:
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}