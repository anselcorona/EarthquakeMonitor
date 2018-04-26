package com.example.acorona.earthquakemonitor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeAdapter extends ArrayAdapter<Earthquake> {

    private ArrayList<Earthquake> EarthquakeList;
    private Context context;
    private int layoutId;
    public EarthquakeAdapter(@NonNull Context context, int resource, @NonNull List<Earthquake> objects) {
        super(context, resource, objects);
        this.context = context;
        this.layoutId = resource;
        EarthquakeList = new ArrayList<>(objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
       ViewHolder holder;

       if(convertView==null){
           LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

           convertView = inflater.inflate(R.layout.eq_listitem,null);
           holder = new ViewHolder();
           holder.location = convertView.findViewById(R.id.location);
           holder.magnitude = convertView.findViewById(R.id.magnitude);
           convertView.setTag(holder);
       } else{
           holder = (ViewHolder) convertView.getTag();
       }


        Earthquake earthquake = EarthquakeList.get(position);
        Double magnitude = earthquake.getMagnitude();
        Double five = 5.000;
        if(magnitude-five>=0){
            holder.magnitude.setTextColor(context.getResources().getColor(R.color.colorAccent));
        }else{
            holder.magnitude.setTextColor(context.getResources().getColor(R.color.black));
        }
        holder.magnitude.setText(String.valueOf(magnitude));
        holder.location.setText(earthquake.getLocation());



        return convertView;
    }

    public class ViewHolder{
        public TextView magnitude;
        public TextView location;

    }

}
