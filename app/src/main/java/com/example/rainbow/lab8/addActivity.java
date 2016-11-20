package com.example.rainbow.lab8;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class addActivity extends AppCompatActivity {

    myDB db = new myDB(this);
    private Button add_button;
    private EditText name_edit, bd_edit, gift_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_info);

        //findView
        add_button = (Button)findViewById(R.id.add_button);
        name_edit = (EditText)findViewById(R.id.name_edit);
        bd_edit = (EditText)findViewById(R.id.bd_edit);
        gift_edit = (EditText)findViewById(R.id.gift_edti);

        //button绑定监听器
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = name_edit.getText().toString();
                String bd = bd_edit.getText().toString();
                String gift = gift_edit.getText().toString();

                //插入
                int result = db.insert2DB(name, bd, gift);
                //成功插入时返回0
                if(result==0){
                    Intent intent = getIntent();
                    setResult(1, intent);
                    finish();
                }
                //名字为空时返回1
                else if(result==1){
                    Toast.makeText(addActivity.this, "名字为空，请完善",
                            Toast.LENGTH_LONG).show();
                }
                //名字重复时返回2
                else if(result==2){
                    Toast.makeText(addActivity.this, "名字重复啦，请核查",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}