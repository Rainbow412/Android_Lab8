package com.example.rainbow.lab8;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class addActivity extends AppCompatActivity {

    private static final String DB_NAME = "reminder.db";
    private static final String TABLE_NAME = "birthday";
    private static final int DB_VERSION = 1;

    myDB db = new myDB(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_info);

        Button add_button = (Button)findViewById(R.id.add_button);
        final EditText name_edit = (EditText)findViewById(R.id.name_edit);
        final EditText bd_edit = (EditText)findViewById(R.id.bd_edit);
        final EditText gift_edit = (EditText)findViewById(R.id.gift_edti);

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = name_edit.getText().toString();
                String bd = bd_edit.getText().toString();
                String gift = gift_edit.getText().toString();

                if(name.equals("")){
                    Toast.makeText(addActivity.this, "名字为空，请完善",
                            Toast.LENGTH_LONG).show();
                }else{
                    db.insert2DB(name, bd, gift);
                    Intent intent = getIntent();
                    setResult(1, intent);
                    finish();
                }

            }
        });


    }
}