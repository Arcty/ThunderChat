package cn.neu.yoyo.namecarddesign;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import cn.neu.arcty.thunchat.R;

public class Demo extends AppCompatActivity {

    private TextView showname;
    private TextView showphone;
    private TextView showemail;
    private TextView showcompany;
    private TextView showposition;
    private TextView showmailcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo1);

        showname = (TextView)findViewById(R.id.show_name);
        showphone = (TextView)findViewById(R.id.show_phone);
        showemail = (TextView)findViewById(R.id.show_email);
        showcompany = (TextView)findViewById(R.id.show_company);
        showposition = (TextView)findViewById(R.id.show_position);
        showmailcode = (TextView)findViewById(R.id.show_mailcode);

        Bundle bundle = this.getIntent().getExtras();
        String getname = bundle.getString("name");
        String getphone = bundle.getString("phone");
        String getemail = bundle.getString("email");
        String getcompany = bundle.getString("company");
        String getposition = bundle.getString("position");
        String getmailcode = bundle.getString("mailcode");

        showname.setText(getname);
        showphone.setText(getphone);
        showemail.setText(getemail);
        showcompany.setText(getcompany);
        showposition.setText(getposition);
        showmailcode.setText(getmailcode);
    }
}
