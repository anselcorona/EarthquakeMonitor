package com.example.acorona.earthquakemonitor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

public class MainActivity extends AppCompatActivity implements DownloadEarthquakeDataAsyncTask.DownloadEarthquakeInterface{
    private ListView eList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        eList = findViewById(R.id.e_list);

        FabSpeedDial fabSpeedDial = (FabSpeedDial) findViewById(R.id.speed_dial);
        fabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.last_month:
                        try {
                            URL url = new URL("https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_month.geojson");
                            Refresh(url);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        break;
                    case R.id.last_week:
                        try {
                            URL url = new URL("https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_week.geojson");
                            Refresh(url);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }

                        break;
                    case R.id.today:
                        try {
                            URL url = new URL("https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_day.geojson");
                            Refresh(url);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        break;

                }
                return false;
            }
        });

    }

    public void Refresh(URL url){
        DownloadEarthquakeDataAsyncTask e = new DownloadEarthquakeDataAsyncTask();
        e.delegate = this;
        e.execute(url);
    }

    @Override
    public void onEarthquakesDownloaded(String data) {
        ArrayList<Earthquake> eqList = new ArrayList<>();
        try{
            JSONObject jsonObject = new JSONObject(data);
            JSONArray jsonArray = jsonObject.getJSONArray("features");

            for(int i=0; i<jsonArray.length(); i++){
                JSONObject featuresjsonObject = jsonArray.getJSONObject(i);
                JSONObject propertiesJsonObject = featuresjsonObject.getJSONObject("properties");
                double magnitude = propertiesJsonObject.getDouble("mag");
                String place = propertiesJsonObject.getString("place");
                JSONObject geometryJsonObject = featuresjsonObject.getJSONObject("geometry");
                JSONArray coordinatesJsonArray = geometryJsonObject.getJSONArray("coordinates");
                double longitude = coordinatesJsonArray.getDouble(0);
                double latitude = coordinatesJsonArray.getDouble(1);
                Long longdate = propertiesJsonObject.getLong("time");
                Date date = new Date(longdate);
                DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
                String formatted = format.format(date);
                eqList.add(new Earthquake(place,longitude,latitude,magnitude, formatted));
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        final EarthquakeAdapter earthquakeAdapter = new EarthquakeAdapter(this, R.layout.eq_listitem, eqList);
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

}
