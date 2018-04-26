package com.example.acorona.earthquakemonitor;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import static com.example.acorona.earthquakemonitor.EarthquakeContract.EarthquakeColumns.TABLE_NAME;

public class DownloadEarthquakeDataAsyncTask extends AsyncTask<URL, Void, ArrayList<Earthquake>>{
    public DownloadEarthquakeInterface delegate;
    private Context context;
    DownloadEarthquakeDataAsyncTask(Context context){
        this.context = context;
    }
    public interface DownloadEarthquakeInterface{
        void onEarthquakesDownloaded(ArrayList<Earthquake> eqList);
    }
    @Override
    protected ArrayList<Earthquake> doInBackground(URL... urls) {
        String data;
        ArrayList<Earthquake> list = null;
        try{
            data = downloadData(urls[0]);
            list = parseDataFromJson(data);
            saveEarthquakeDatabase(list);
        }catch(IOException e){
            e.printStackTrace();
        }
        return list;
    }

    private void saveEarthquakeDatabase(ArrayList<Earthquake> list) {
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME);
        for(Earthquake e : list){
            ContentValues cv = new ContentValues();
            cv.put(EarthquakeContract.EarthquakeColumns.MAGNITUDE,e.getMagnitude());
            cv.put(EarthquakeContract.EarthquakeColumns.PLACE,e.getLocation());
            cv.put(EarthquakeContract.EarthquakeColumns.LONGITUDE,e.getLongitude());
            cv.put(EarthquakeContract.EarthquakeColumns.LATITUDE,e.getLatitude());
            cv.put(EarthquakeContract.EarthquakeColumns.TIMESTAMP,e.getDate());
            db.insert(TABLE_NAME, null, cv);
        }

    }

    private ArrayList<Earthquake> parseDataFromJson(String data) {
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
                eqList.add(new Earthquake(place,longitude,latitude,magnitude, longdate));
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return eqList;
    }

    @Override
    protected void onPostExecute(ArrayList<Earthquake> eqList) {
        super.onPostExecute(eqList);
        delegate.onEarthquakesDownloaded(eqList);
    }

    private String downloadData(URL url) throws IOException{
        String jsonResponse = "";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try{
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();

            inputStream = urlConnection.getInputStream();
            jsonResponse = readFromStream(inputStream);
        }catch (IOException e){
           e.printStackTrace();
        } finally{
            if (urlConnection!=null){
                urlConnection.disconnect();
            }
            if(inputStream!=null){
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if(inputStream!=null){
            InputStreamReader inputStreamReader
                    = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while(line!=null){
                output.append(line);
                line = reader.readLine();
            }
        }

        return output.toString();
    }
}
