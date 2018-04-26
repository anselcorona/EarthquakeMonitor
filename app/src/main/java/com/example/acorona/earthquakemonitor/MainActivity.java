package com.example.acorona.earthquakemonitor;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

public class MainActivity extends AppCompatActivity implements DownloadEarthquakeDataAsyncTask.DownloadEarthquakeInterface {
    private ListView eList;
    SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                    if (Utils.isNetworkAvailable(MainActivity.this)) {
                        try {
                            URL url = new URL("https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_month.geojson");
                            Refresh(url);

                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }else{
                        Toast.makeText(MainActivity.this, "Error de la conexión", Toast.LENGTH_LONG).show();
                    }

            }
        });
        eList = findViewById(R.id.e_list);

        FabSpeedDial fabSpeedDial = findViewById(R.id.speed_dial);
        fabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                if (Utils.isNetworkAvailable(MainActivity.this)) {
                        downloadData(menuItem);
                }else{
                    try{
                        getEarthquakesFromDatabase(menuItem);
                    }catch(Exception ex){
                        ex.printStackTrace();
                    }
                }
                return false;
            }
        });
    }

    private void getEarthquakesFromDatabase(MenuItem menuItem){
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        long result;
        ArrayList<Earthquake> eq = new ArrayList<>();
        Cursor cursor;

        switch (menuItem.getItemId()) {
            case R.id.refresh:
                Toast.makeText(MainActivity.this, "Error de la conexión", Toast.LENGTH_LONG).show();
                break;
            case R.id.last_month:
                    c.add(Calendar.MONTH, -1);
                    result = c.getTimeInMillis();
                    cursor = db.query(EarthquakeContract.EarthquakeColumns.TABLE_NAME, null, "timestamp > " + String.valueOf(result), null, null, null, null);
                    while(cursor.moveToNext()){
                        String location = cursor.getString(EarthquakeContract.EarthquakeColumns.PLACE_COLUMN_INDEX);
                        Double longitude = cursor.getDouble(EarthquakeContract.EarthquakeColumns.LONGITUDE_COLUMN_INDEX);
                        Double latitude = cursor.getDouble(EarthquakeContract.EarthquakeColumns.LATITUDE_COLUMN_INDEX);
                        Double magnitude = cursor.getDouble(EarthquakeContract.EarthquakeColumns.MAGNITUDE_COLUMN_INDEX);
                        Long date = cursor.getLong(EarthquakeContract.EarthquakeColumns.TIMESTAMP_COLUMN_INDEX);
                        eq.add(new Earthquake(location, longitude, latitude, magnitude, date));
                    }
                    cursor.close();
                break;
            case R.id.last_week:
                    c.add(Calendar.DATE, -7);
                    result = c.getTimeInMillis();
                    cursor = db.query(EarthquakeContract.EarthquakeColumns.TABLE_NAME, null, "timestamp > " + String.valueOf(result), null, null, null, null);
                    while(cursor.moveToNext()){
                        String location = cursor.getString(EarthquakeContract.EarthquakeColumns.PLACE_COLUMN_INDEX);
                        Double longitude = cursor.getDouble(EarthquakeContract.EarthquakeColumns.LONGITUDE_COLUMN_INDEX);
                        Double latitude = cursor.getDouble(EarthquakeContract.EarthquakeColumns.LATITUDE_COLUMN_INDEX);
                        Double magnitude = cursor.getDouble(EarthquakeContract.EarthquakeColumns.MAGNITUDE_COLUMN_INDEX);
                        Long date = cursor.getLong(EarthquakeContract.EarthquakeColumns.TIMESTAMP_COLUMN_INDEX);
                        eq.add(new Earthquake(location, longitude, latitude, magnitude, date));
                    }
                    cursor.close();
                break;
            case R.id.today:
                c.add(Calendar.DATE, -1);
                result = c.getTimeInMillis();
                cursor = db.query(EarthquakeContract.EarthquakeColumns.TABLE_NAME, null, "timestamp > " + String.valueOf(result), null, null, null, null);
                while(cursor.moveToNext()){
                    String location = cursor.getString(EarthquakeContract.EarthquakeColumns.PLACE_COLUMN_INDEX);
                    Double longitude = cursor.getDouble(EarthquakeContract.EarthquakeColumns.LONGITUDE_COLUMN_INDEX);
                    Double latitude = cursor.getDouble(EarthquakeContract.EarthquakeColumns.LATITUDE_COLUMN_INDEX);
                    Double magnitude = cursor.getDouble(EarthquakeContract.EarthquakeColumns.MAGNITUDE_COLUMN_INDEX);
                    Long date = cursor.getLong(EarthquakeContract.EarthquakeColumns.TIMESTAMP_COLUMN_INDEX);
                    eq.add(new Earthquake(location, longitude, latitude, magnitude, date));
                }
                cursor.close();
                break;
        }
        fillEarthquakeList(eq);
    }

    private void fillEarthquakeList(ArrayList<Earthquake> eq) {
        final EarthquakeAdapter earthquakeAdapter = new EarthquakeAdapter(this, R.layout.eq_listitem, eq);
        eList.setAdapter(earthquakeAdapter);
        eList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Earthquake selected = earthquakeAdapter.getItem(position);
                Intent detail = new Intent(MainActivity.this, DetailActivity.class);
                detail.putExtra("EARTHQUAKE", selected);
                startActivity(detail);
            }
        });
    }

    public void downloadData(MenuItem menuItem){
        DownloadEarthquakeDataAsyncTask downloadEarthquakeDataAsyncTask = new DownloadEarthquakeDataAsyncTask(MainActivity.this);
        downloadEarthquakeDataAsyncTask.delegate = MainActivity.this;
        switch (menuItem.getItemId()) {
            case R.id.refresh:
                try {
                    URL url = new URL("https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_month.geojson");
                    Refresh(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.last_month:
                getEarthquakesFromDatabase(menuItem);
                break;
            case R.id.last_week:
                getEarthquakesFromDatabase(menuItem);
                break;
            case R.id.today:
                getEarthquakesFromDatabase(menuItem);
                break;
        }
    }

    public void Refresh(URL url) {
        DownloadEarthquakeDataAsyncTask e = new DownloadEarthquakeDataAsyncTask(this);
        e.delegate = this;
        e.execute(url);

    }


    @Override
    public void onEarthquakesDownloaded(ArrayList<Earthquake> eqList) {
        final EarthquakeAdapter earthquakeAdapter = new EarthquakeAdapter(this, R.layout.eq_listitem, eqList);
        eList.setAdapter(earthquakeAdapter);
        swipeRefreshLayout.setRefreshing(false);
        eList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Earthquake selected = earthquakeAdapter.getItem(position);
                Intent detail = new Intent(MainActivity.this, DetailActivity.class);
                detail.putExtra("EARTHQUAKE", selected);
                startActivity(detail);
            }
        });

    }

}
