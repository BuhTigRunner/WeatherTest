package com.borisenkoda.weathertest.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.borisenkoda.weathertest.R;
import com.borisenkoda.weathertest.database.tables.CitiesTable;

/**
 * Created by BDA on 05.02.2016.
 */
public class DBOpenHelper extends SQLiteOpenHelper {
    public DBOpenHelper(Context context) {
        super(context, "weather", null, context.getResources().getInteger(R.integer.db_version));
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CitiesTable.getCreateTableQuery());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
