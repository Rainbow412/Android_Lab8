package com.example.rainbow.lab8;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class myDB extends SQLiteOpenHelper{
    private static final String DB_NAME = "reminder.db";
    private static final String TABLE_NAME = "birthday";
    private static final int DB_VERSION = 1;


    public myDB(Context context){
        super(context, DB_NAME,null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase){

        String CREATE_TABLE = "CREATE TABLE if not exists "
                +TABLE_NAME
                +" (name TEXT PRIMARY KEY, birth TEXT, gift TEXT)";
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1){

    }

    private ArrayList<Map<String, String>> cursor2list(Cursor cursor) {
        ArrayList<Map<String, String>> list = new ArrayList<Map<String,String>>();

        //遍历Cursor
        while(cursor.moveToNext()){
            Map<String, String> map = new HashMap<String, String>();
            map.put("name", cursor.getString(0));
            map.put("birth", cursor.getString(1));
            map.put("gift", cursor.getString(2));
            list.add(map);
        }
        return list;
    }

    //插入
    //成功插入时返回0
    //名字为空时返回1
    //名字重复时返回2
    public int insert2DB(String name, String bd, String gift){
        if(name.equals(""))
            return 1;

        SQLiteDatabase db1 = getReadableDatabase();
        Cursor cr = db1.query(TABLE_NAME,
                new String[]{"name"},
                null, null, null, null, null);
        while (cr.moveToNext()){
            if(cr.getString(0).equals(name))
                return 2;
        }

        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("birth", bd);
        cv.put("gift", gift);
        db.insert(TABLE_NAME,null,cv);
        db.close();
        return 0;
    }

    public void updateDB(String name, String bd, String gift){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("birth", bd);
        cv.put("gift", gift);
        String whereClause = "name=? ";
        String[] whereArgs = {name};
        db.update(TABLE_NAME, cv, whereClause, whereArgs);
        db.close();
    }

    public void deleteDB(String name){
        SQLiteDatabase db = getWritableDatabase();

        String whereClause = "name=? ";
        String[] whereArgs = {name};

        db.delete(TABLE_NAME, whereClause, whereArgs);
        db.close();
    }

    public ArrayList<Map<String, String>> queryArrayList(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cr = db.query(TABLE_NAME,
                new String[]{"name", "birth", "gift"},
                null, null, null, null, null);

        return cursor2list(cr);
    }

}
