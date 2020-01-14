package edu.neu.cs5520.liferecorder;


import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "venue")
public class VenueItem {

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    int id;

    @ColumnInfo(name = "name")
    String name;

    @ColumnInfo(name = "address")
    String address;

    @ColumnInfo(name = "lat")
    @NonNull
    double lat;

    @ColumnInfo(name = "lng")
    @NonNull
    double lng;

    @ColumnInfo(name = "description")
    String description;

    @ColumnInfo(name = "created_at")
    Date created;

    @ColumnInfo(name = "photo_uri")
    Uri photoUri;

    @ColumnInfo(name = "item_type")
    String itemType;

    public VenueItem(String name, String address, double lat, double lng, String description, Uri photoUri, String itemType) {
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.description = description;
        this.created = new Date(System.currentTimeMillis());
        this.photoUri = photoUri;
        this.itemType = itemType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public String toString(){
        return String.format("name: %s, address: %s, description: %s", name, address, description);
    }

}
