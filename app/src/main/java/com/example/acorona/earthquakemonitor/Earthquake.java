package com.example.acorona.earthquakemonitor;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Earthquake implements Parcelable {

    private String Location;
    private Double Longitude;
    private Double Latitude;
    private Double Magnitude;
    private String date;

    public Earthquake(String location, Double longitude, Double latitude, Double magnitude, String date) {
        Location = location;
        Longitude = longitude;
        Latitude = latitude;
        Magnitude = magnitude;
        this.date = date;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public Double getLongitude() {
        return Longitude;
    }

    public void setLongitude(Double longitude) {
        Longitude = longitude;
    }

    public Double getLatitude() {
        return Latitude;
    }

    public void setLatitude(Double latitude) {
        Latitude = latitude;
    }

    public Double getMagnitude() {
        return Magnitude;
    }

    public void setMagnitude(Double magnitude) {
        Magnitude = magnitude;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    protected Earthquake(Parcel in) {
        Location = in.readString();
        Longitude = in.readByte() == 0x00 ? null : in.readDouble();
        Latitude = in.readByte() == 0x00 ? null : in.readDouble();
        Magnitude = in.readByte() == 0x00 ? null : in.readDouble();
        date = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Location);
        if (Longitude == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(Longitude);
        }
        if (Latitude == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(Latitude);
        }
        if (Magnitude == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(Magnitude);
        }
        dest.writeString(date);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Earthquake> CREATOR = new Parcelable.Creator<Earthquake>() {
        @Override
        public Earthquake createFromParcel(Parcel in) {
            return new Earthquake(in);
        }

        @Override
        public Earthquake[] newArray(int size) {
            return new Earthquake[size];
        }
    };
}