package vn.dmcl.eagleeyes.data.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Area {
    @SerializedName("Status")
    @Expose
    private Integer Status;
    @SerializedName("Id")
    @Expose
    private String Id;
    @SerializedName("LocalId")
    @Expose
    private Integer localId;
    @SerializedName("Name")
    @Expose
    private String Name;
    @SerializedName("Lat")
    @Expose
    private Double Lat;
    @SerializedName("Lng")
    @Expose
    private Double Lng;
    @SerializedName("Radius")
    @Expose
    private Double Radius;
    @SerializedName("Count")
    @Expose
    private Integer Count;

    public Area() {}

    public Integer getLocalId() {
        return localId;
    }

    public void setLocalId(Integer localId) {
        this.localId = localId;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public double getLat() {
        return Lat;
    }

    public void setLat(double lat) {
        Lat = lat;
    }

    public double getLng() {
        return Lng;
    }

    public void setLng(double lng) {
        Lng = lng;
    }

    public double getRadius() {
        return Radius;
    }

    public void setRadius(double radius) {
        Radius = radius;
    }

    public int getCount() {
        return Count;
    }

    public void setCount(int count) {
        Count = count;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }
}
