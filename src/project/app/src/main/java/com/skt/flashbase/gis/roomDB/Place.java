package com.skt.flashbase.gis.roomDB;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "placeInfo")
public class Place {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    private int pidx;
    @NonNull
    @ColumnInfo(name = "category")
    private int pCategory;
    @NonNull
    @ColumnInfo(name = "name")
    private String pName;
    @NonNull
    @ColumnInfo(name = "latitude")
    private double pLatitude;
    @NonNull
    @ColumnInfo(name = "longitude")
    private double pLongitude;

    public Place(@NonNull int pidx, @NonNull int pCategory, @NonNull String pName, @NonNull double pLatitude, @NonNull double pLongitude) {
        this.pidx = pidx;
        this.pCategory = pCategory;
        this.pName = pName;
        this.pLatitude = pLatitude;
        this.pLongitude = pLongitude;
    }

    public int getPidx() {
        return pidx;
    }

    public int getPCategory() {
        return pCategory;
    }

    public String getPName() {
        return pName;
    }

    public double getPLatitude() {
        return pLatitude;
    }

    public double getPLongitude() {
        return pLongitude;
    }
}

