package cn.neu.yoyo.namecarddesign;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import cn.neu.arcty.thunchat.R;

public class Editcardinfo extends AppCompatActivity {

    private Button info_edit;
    private Button info_ensure;
    private Button info_preview;
    private EditText name;
    private EditText phone;
    private EditText age;
    private EditText company;
    private EditText position;
    private EditText email;
    private EditText mailcode;
    private String string;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editcardinfo);

        info_edit = (Button)findViewById(R.id.userinfo_Edit);
        info_ensure = (Button)findViewById(R.id.userinfo_ensure);
        info_preview = (Button)findViewById(R.id.userinfo_preview);
        name = (EditText)findViewById(R.id.editName);
        phone = (EditText)findViewById(R.id.editPhone);
        age = (EditText)findViewById(R.id.editAge);
        company = (EditText)findViewById(R.id.editCompany);
        position = (EditText)findViewById(R.id.editPosition);
        email = (EditText)findViewById(R.id.editEmail);
        mailcode = (EditText)findViewById(R.id.editMailcode);

        //设置控件不可编辑
        name.setEnabled(false);
        phone.setEnabled(false);
        age.setEnabled(false);
        company.setEnabled(false);
        position.setEnabled(false);
        email.setEnabled(false);
        mailcode.setEnabled(false);

        //监听编辑button，使EditText控件可以编辑，并对第一个控件提供focused
        info_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name.setEnabled(true);
                name.requestFocus();
                phone.setEnabled(true);
                age.setEnabled(true);
                company.setEnabled(true);
                position.setEnabled(true);
                email.setEnabled(true);
                mailcode.setEnabled(true);
            }
        });

        //监听完成button，使EditText控件恢复不可编辑
        info_ensure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name.setEnabled(false);
                phone.setEnabled(false);
                age.setEnabled(false);
                company.setEnabled(false);
                position.setEnabled(false);
                email.setEnabled(false);
                mailcode.setEnabled(false);
                finish();
            }
        });



        info_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Editcardinfo.this,Demo.class);
                Bundle bundle = new Bundle();
                bundle.putString("name",name.getText().toString());
                bundle.putString("phone",phone.getText().toString());
                bundle.putString("email",email.getText().toString());
                bundle.putString("company",company.getText().toString());
                bundle.putString("position",position.getText().toString());
                bundle.putString("mailcode",mailcode.getText().toString());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}
