package com.amap.track.sql;

//历史数据对象
public class ListData {
    public String title;//
    public String nameEnd;//
    public String time;//
    public Double latitude;//
    public Double longitude;//
    public Double latitudeStart;//
    public Double longitudeEnd;//
    public int id;//

    public ListData(int id, String title, String nameEnd, String time, Double latitude,Double longitude, Double latitudeStart,Double longitudeEnd) {
        this.title = title;
        this.nameEnd = nameEnd;
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
        this.latitudeStart = latitudeStart;
        this.longitudeEnd = longitudeEnd;
        this.id = id;
    }

    public String getNameEnd() {
        return nameEnd;
    }

    public void setNameEnd(String nameEnd) {
        this.nameEnd = nameEnd;
    }

    public Double getLatitudeStart() {
        return latitudeStart;
    }

    public void setLatitudeStart(Double latitudeStart) {
        this.latitudeStart = latitudeStart;
    }

    public Double getLongitudeEnd() {
        return longitudeEnd;
    }

    public void setLongitudeEnd(Double longitudeEnd) {
        this.longitudeEnd = longitudeEnd;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
