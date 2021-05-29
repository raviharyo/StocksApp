package com.example.proj1.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import com.example.proj1.utils.Model;

public class DatabaseHelper extends SQLiteOpenHelper implements Database {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = DatabaseContents.DATABASE.toString();

    private SQLiteDatabase db;
    private static  final String TABLE_TASKS = "tbl_tasks";
    private static  final String COL_1 = "ID";
    private static  final String COL_2 = "USER_ID";
    private static  final String COL_3 = "TASK";
    private static  final String COL_4 = "STATUS";

    private Context context;

    public static final String my_shared_preferences = "my_shared_preferences";
    public final static String TAG_ID = "_id";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DatabaseContents.TABLE_USERS + "("
                + "_id INTEGER PRIMARY KEY,"
                + "email TEXT(32),"
                + "password TEXT(256),"
                + "name TEXT(100),"
                + "phone TEXT(32)"
                + ");");
        Log.d("CREATE DATABASE", "Create " + DatabaseContents.TABLE_USERS + " Successfully.");


        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_TASKS +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT , USER_ID INTEGER, TASK TEXT , STATUS INTEGER)");


    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
// Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContents.TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
// Create tables again
        onCreate(db);
    }


//    USER HELPER


    @Override
    public List<Object> select(String queryString) {
        try {

            SQLiteDatabase database = this.getWritableDatabase();
            List<Object> list = new ArrayList<>();
            Cursor cursor = database.rawQuery(queryString, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        ContentValues content = new ContentValues();
                        String[] columnNames = cursor.getColumnNames();
                        for (String columnName : columnNames) {
                            content.put(columnName, cursor.getString(cursor
                                    .getColumnIndex(columnName)));

                        }
                        list.add(content);
                    } while (cursor.moveToNext());
                }
            }
            cursor.close();
            database.close();
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int insert(String tableName, Object content) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            int id = (int) database.insert(tableName, null,
                    (ContentValues) content);
            database.close();
            return id;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public boolean update(String tableName, Object content) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues cont = (ContentValues) content;
// this array will always contains only one element.
            String[] array = new String[]{cont.get("_id") + ""};

            database.update(tableName, cont, " _id = ?", array);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String tableName, int id) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            database.delete(tableName, " _id = ?", new String[]{id + ""});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean execute(String queryString) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            database.execSQL(queryString);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

//    END OF USER HELPER



//    TASKS HELPER

    public void insertTask(Model model){
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_2 , model.getUser_id());
        values.put(COL_3 , model.getTask());
        values.put(COL_4 , 0);
        db.insert(TABLE_TASKS , null , values);
}

    public void updateTask(int id , String task){
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_3 , task);
        db.update(TABLE_TASKS , values , "ID=?" , new String[]{String.valueOf(id)});
    }

    public void updateStatus(int id , int status){
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_4 , status);
        db.update(TABLE_TASKS , values , "ID=?" , new String[]{String.valueOf(id)});
    }

    public void deleteTask(int id ){
        db = this.getWritableDatabase();
        db.delete(TABLE_TASKS , "ID=?" , new String[]{String.valueOf(id)});
    }

    public List<Model> getAllTasks(Context context){

        Model model = new Model();
        db = this.getWritableDatabase();
        Cursor cursor = null;
        List<Model> modelList = new ArrayList<>();

        db.beginTransaction();
        try {
            cursor = db.rawQuery("SELECT * FROM tbl_tasks WHERE USER_ID = (?)", new String[]{String.valueOf(
                    context.getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE).getString(TAG_ID, "0")
            )});
//            cursor = db.query(TABLE_TASKS , null , null, null , null , null , null);
            if (cursor !=null){
                if (cursor.moveToFirst()){
                    do {
                        Model task = new Model();
                        task.setId(cursor.getInt(cursor.getColumnIndex(COL_1)));
                        task.setUser_id(cursor.getInt(cursor.getColumnIndex(COL_2)));
                        task.setTask(cursor.getString(cursor.getColumnIndex(COL_3)));
                        task.setStatus(cursor.getInt(cursor.getColumnIndex(COL_4)));
                        modelList.add(task);

                    }while (cursor.moveToNext());
                }
            }
        }finally {
            db.endTransaction();
            cursor.close();
        }
        return modelList;
    }

//    END OF TASKS HELPER

}
