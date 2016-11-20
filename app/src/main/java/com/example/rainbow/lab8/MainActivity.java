package com.example.rainbow.lab8;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    myDB db = new myDB(this);
    private List<Map<String, String>> item;
    private SimpleAdapter simpleAdapter;
    private ListView listView;
    private Button add_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //findView
        add_button = (Button)findViewById(R.id.add_item);
        listView = (ListView)findViewById(R.id.list);

        //查询数据并更新list
        item = db.queryArrayList();
        refreshList();

        //点击add_button跳转至addActivity
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, addActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        //短按item弹出更新数据对话框
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long idp) {
                //自定义对话框布局
                final LayoutInflater factory = LayoutInflater.from(MainActivity.this);
                View contentView = factory.inflate(R.layout.dialoglayout, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setView(contentView);

                //获取点击的item的数据
                final String this_name = item.get(position).get("name");
                String this_bd = item.get(position).get("birth");
                String this_gift = item.get(position).get("gift");
                //findView
                TextView name_text = (TextView)contentView.findViewById(R.id.name_show);
                final EditText bd_edit = (EditText)contentView.findViewById(R.id.bd_edit);
                final EditText gift_edit = (EditText)contentView.findViewById(R.id.gift_edti);
                TextView tel_text = (TextView)contentView.findViewById(R.id.tel_text);
                //显示数据
                name_text.setText(this_name);
                bd_edit.setText(this_bd);
                gift_edit.setText(this_gift);

                String number = new String();
                //使用getContentResolver读取联系人列表
                Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                        null,null,null,null);
                while (cursor.moveToNext()){
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cursor.getString(
                            cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    if(!name.equals(this_name))
                        continue;
                    //该行联系人姓名与item姓名相同时
                    //先判断有没有电话号码
                    int isHas = Integer.parseInt(cursor.getString(
                            cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                    //若有电话号码
                    //根据该id查询电话号码
                    if(isHas>0){
                        Cursor c = getContentResolver().query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"="+id,
                                null, null);
                        //可能有多个电话号码，逐个遍历
                        while (c.moveToNext()){
                            number += c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))+" ";
                        }
                        c.close();
                        break;
                    }
                }
                //如果有找到电话号码，则显示电话号码
                //没有找到，则显示无
                if(!number.equals(""))
                    tel_text.setText(number);
                else
                    tel_text.setText("无");

                //设置对话框下方的两个按钮并显示对话框
                builder.setTitle("<(￣︶￣)>").setPositiveButton("保存修改",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //保存修改
                                String new_bd = bd_edit.getText().toString();
                                String new_gift = gift_edit.getText().toString();
                                db.updateDB(this_name, new_bd, new_gift); //更新数据库
                                refreshList(); //刷新listView
                            }
                        }).setNegativeButton("放弃修改", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //放弃修改
                            }
                }).create().show();
            }
        });

        //长按item弹出询问是否删除的对话框
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final String this_name = item.get(position).get("name");
                dialog.setMessage("是否删除").setPositiveButton("是",
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.deleteDB(this_name); //删除该行数据
                        refreshList(); //更新listView
                    }
                }).setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //不删除
                    }
                }).create().show();
                return true;
            }
        });
    }

    //当从addActivity返回时，更新listView
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        refreshList();
    }

    //更新listView
    private void refreshList(){
        item = db.queryArrayList();
        simpleAdapter = new SimpleAdapter(getApplicationContext(), item, R.layout.item,
                new String[]{"name","birth","gift"},
                new int[]{R.id.name_text, R.id.bd_text, R.id.gift_text});
        listView.setAdapter(simpleAdapter);
    }




}
