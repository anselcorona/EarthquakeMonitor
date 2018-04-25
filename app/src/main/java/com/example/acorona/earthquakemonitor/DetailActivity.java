package com.example.acorona.earthquakemonitor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Bundle extras = getIntent().getExtras();
        Earthquake e = extras.getParcelable("EARTHQUAKE");

        TextView Magnitude = findViewById(R.id.magnitude_number);
        TextView Lon = findViewById(R.id.longitude_number);
        TextView Lat = findViewById(R.id.latitude_number);
        TextView Place = findViewById(R.id.place);
        TextView Date = findViewById(R.id.date_string);

        Double magnitud = e.getMagnitude();
        Magnitude.setText(magnitud.toString());
        Double longitud = e.getLongitude();
        Lon.setText(longitud.toString());
        Double latitud = e.getLatitude();
        Lat.setText(latitud.toString());
        String lugar = e.getLocation();
        Place.setText(lugar);
        String fecha = e.getDate();
        Date.setText(fecha);

    }
}
