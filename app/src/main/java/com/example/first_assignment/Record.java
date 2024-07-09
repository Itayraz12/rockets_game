package com.example.first_assignment;

import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Record {
    private String userName;
    private String date;
    private String time;
    private int points;
    private double latitude;
    private double longitude;

    public Record(String user_name ,int points, double latitude, double longitude) {
        setUserName(user_name);
        setPoints(points);
        setLatitude(latitude);
        setLongitude(longitude);
        setDate();
        setTime();
    }

    private void setDate() {
        LocalDate dateNow = LocalDate.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        this.date = dateNow.format(dateFormatter);
    }

    private void setTime() {
        LocalTime timeNow = LocalTime.now();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        this.time = timeNow.format(timeFormatter);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    private void setPoints(int points) {
        this.points = points;
    }

    private void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    private void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public int getPoints() {
        return points;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @NonNull
    @Override
    public String toString() {
        return "Record{" +
                "date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", points=" + points +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}