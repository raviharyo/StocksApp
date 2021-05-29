package com.example.proj1.utils;

public enum DatabaseContents {
    DATABASE("todoApp.db"),
    TABLE_USERS("tbl_user");
    private String name;
    private DatabaseContents(String name) {
        this.name = name;
    }
    @Override
    public String toString() {
        return name;
    }
}