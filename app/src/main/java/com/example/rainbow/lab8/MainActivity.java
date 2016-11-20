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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button add_button = (Button)findViewById(R.id.add_item);
        listView = (ListView)findViewById(R.id.list);

        item = db.queryArrayList();
        refreshList();

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, addActivity.class);
                startActivityForResult(intent, 1);

            }
        });
        //短按item
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long idp) {
                final LayoutInflater factory = LayoutInflater.from(MainActivity.this);
                View contentView = factory.inflate(R.layout.dialoglayout, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setView(contentView);

                final String this_name = item.get(position).get("name");
                String this_bd = item.get(position).get("birth");
                String this_gift = item.get(position).get("gift");
                TextView name_text = (TextView)contentView.findViewById(R.id.name_show);
                final EditText bd_edit = (EditText)contentView.findViewById(R.id.bd_edit);
                final EditText gift_edit = (EditText)contentView.findViewById(R.id.gift_edti);
                name_text.setText(this_name);
                bd_edit.setText(this_bd);
                gift_edit.setText(this_gift);

                String number = new String();
                TextView tel_text = (TextView)contentView.findViewById(R.id.tel_text);
                Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                        null,null,null,null);
                while (cursor.moveToNext()){
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cursor.getString(
                            cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    Log.d("name", name);
                    if(!name.equals(this_name))
                        continue;
                    int isHas = Integer.parseInt(cursor.getString(
                            cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                    Log.d("isHas", ""+isHas);
                    if(isHas<0){
                        tel_text.setText("无");
                    }
                    else{
                        Log.d("id", ""+id);
                        Cursor c = getContentResolver().query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"="+id,
                                null, null);
                        Log.d("count", ""+c.getCount());
                        while (c.moveToNext()){
                            number += c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))+" ";
                            Log.d("number", c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                            Log.d("test", "hhh");
                        }
                        c.close();
                        break;
                    }
                }
                if(!number.equals(""))
                    tel_text.setText(number);
                else
                    tel_text.setText("无");


                builder.setTitle("<(￣︶￣)>").setPositiveButton("保存修改",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //保存修改
                                String new_bd = bd_edit.getText().toString();
                                String new_gift = gift_edit.getText().toString();

                                db.updateDB(this_name, new_bd, new_gift);
                                refreshList();
                            }
                        }).setNegativeButton("放弃修改", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //放弃修改
                    }
                }).create().show();
            }
        });

        //长按item
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final String this_name = item.get(position).get("name");
                dialog.setMessage("是否删除").setPositiveButton("是",
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //删除item
                        db.deleteDB(this_name);
                        refreshList();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        refreshList();
    }

    private void refreshList(){
        item = db.queryArrayList();
        simpleAdapter = new SimpleAdapter(getApplicationContext(), item, R.layout.item,
                new String[]{"name","birth","gift"},
                new int[]{R.id.name_text, R.id.bd_text, R.id.gift_text});
        listView.setAdapter(simpleAdapter);
    }




}
