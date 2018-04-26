package com.example.acorona.earthquakemonitor;

import android.provider.BaseColumns;

public class EarthquakeContract {

    public class EarthquakeColumns implements BaseColumns{
        public static final String TABLE_NAME = "earthquakes";

        public static final String MAGNITUDE = "magnitude";
        public static final String PLACE = "place";
        public static final String LONGITUDE = "longitude";
        public static final String LATITUDE = "latitude";
        public static final String TIMESTAMP = "timestamp";
    }
}
