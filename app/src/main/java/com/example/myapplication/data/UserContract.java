package com.example.myapplication.data;

import android.provider.BaseColumns;

public class UserContract {
    public static final class UserEntry implements BaseColumns {
        public static final String TABLE = "users";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_YEAR = "year";
    }
}
