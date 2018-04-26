package com.example.acorona.earthquakemonitor;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "earthquakes.db";
    private static final int DATABASE_VERSION = 1;
    public DbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String EARTHQUAKES_DATABASE = "CREATE TABLE " + EarthquakeContract.EarthquakeColumns.TABLE_NAME + " (" +
                EarthquakeContract.EarthquakeColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                EarthquakeContract.EarthquakeColumns.MAGNITUDE + " REAL NOT NULL," +
                EarthquakeContract.EarthquakeColumns.PLACE + " TEXT NOT NULL," +
                EarthquakeContract.EarthquakeColumns.LONGITUDE + " TEXT NOT NULL," +
                EarthquakeContract.EarthquakeColumns.LATITUDE + " TEXT NOT NULL, " +
                EarthquakeContract.EarthquakeColumns.TIMESTAMP + " TEXT NOT NULL" +
                ")";
        db.execSQL(EARTHQUAKES_DATABASE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
        onCreate(db);
    }
}
