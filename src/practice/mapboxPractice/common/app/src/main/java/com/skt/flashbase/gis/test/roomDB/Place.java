package com.skt.flashbase.gis.test.roomDB;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

//Entity public 이여야 하고 getter 필수
@Entity(tableName = "placeInfo")
public class Place {
// class 안의 변수들이 데이터베이스의 column 이 됨

    /*
     *  Entity 파일의 특징은 class의 변수들이 colum이 되어 데이터베이스의 table로 된다는 것입니다.
     * ColumnInfo로 필드의 이름을 설정하지 않는다면 필드이름과 같은 colum이름으로 생성됩니다.
     * 이때, 실제 table로 만들고 싶지 않는 필드가 있다면 @Ignore을 위에 선언합니다.
     */

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

