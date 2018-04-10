package cn.neu.arcty.thunchat.Adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import cn.neu.arcty.thunchat.Entity.ChatEntity;
import cn.neu.arcty.thunchat.R;

/**
 * Created by arcty on 16-12-9.
 */

public class RecordListAdapter extends BaseAdapter {
    //设备列表
    private ArrayList<ChatEntity> recordlist;
    //上下文对象
    private Context context;
    //handler对象
    private Handler handler;

    public RecordListAdapter(ArrayList<ChatEntity> recordlist, Context context) {
        this.recordlist = recordlist;
        this.context = context;
    }

    @Override
    public int getCount() {
        return recordlist.size();
    }

    @Override
    public Object getItem(int i) {
        return recordlist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        Holder Holder = null;
        if (convertView == null) {
            convertView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.item_message, null);
            Holder = new Holder((TextView) convertView.findViewById(R.id.tv_recordname), (TextView) convertView.findViewById(R.id.tv_recordcontent));
            convertView.setTag(Holder);
        } else {
            Holder = (Holder) convertView.getTag();
        }

        Holder.tv_recordname.setText(recordlist.get(i).getName());
        Holder.tv_recordcontent.setText(recordlist.get(i).getWord());

        return convertView;
    }

    class Holder {
        private TextView tv_recordname;
        private TextView tv_recordcontent;

        public Holder(TextView tv_recordname, TextView tv_recordcontent) {
            this.tv_recordname = tv_recordname;
            this.tv_recordcontent = tv_recordcontent;
        }
    }
}
