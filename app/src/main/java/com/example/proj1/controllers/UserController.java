package com.example.proj1.controllers;

import android.content.ContentValues;
import com.example.proj1.utils.Database;
import com.example.proj1.utils.DatabaseContents;

import java.util.List;

public class UserController {
    private static Database database;
    private static UserController instance;

    private UserController() {
    }

    public static UserController getInstance() {
        if (instance == null)
            instance = new UserController();
        return instance;
    }

    public static void setDatabase(Database db) {
        database = db;
    }

    public ContentValues getDataByEmail(String email) {
        String queryString = "SELECT * FROM " + DatabaseContents.TABLE_USERS + " WHERE email = '"+
                email +"'";
        List<Object> contents = database.select(queryString);
        if (contents.isEmpty()) {
            return null;
        }
        ContentValues content = (ContentValues) contents.get(0);
        return content;
    }

    public int register(ContentValues content) {
        int id = database.insert(DatabaseContents.TABLE_USERS.toString(), content);
        return id;
    }

}
