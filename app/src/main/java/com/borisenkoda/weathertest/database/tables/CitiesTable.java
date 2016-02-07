package com.borisenkoda.weathertest.database.tables;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.queries.Query;

public class CitiesTable {


    public static final String TABLE = "cities";

    @NonNull
    public static final String COLUMN_ID = "COLUMN_ID";

    public static final String NAME = "NAME";

    public static final String COUNTRY = "COUNTRY";

    public static final String DESCRIPTION_WEATHER = "DESCRIPTION_WEATHER";

    public static final String WEATHER_TEMP = "WEATHER_TEMP";

    public static final String ICON = "ICON";

    public static final String FORECAST = "FORECAST";


    @NonNull
    public static final Query QUERY_ALL = Query.builder()
            .table(TABLE)
            .build();

    private CitiesTable() {
        throw new IllegalStateException("No instances please");
    }

    @NonNull
    public static String getCreateTableQuery() {
        return "CREATE TABLE " + TABLE + "("
                + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY, "
                + NAME + " TEXT, "
                + COUNTRY + " TEXT, "
                + DESCRIPTION_WEATHER + " TEXT, "
                + WEATHER_TEMP + " DOUBLE, "
                + ICON + " TEXT, "
                + FORECAST + " TEXT "
                + ");";
    }
}
