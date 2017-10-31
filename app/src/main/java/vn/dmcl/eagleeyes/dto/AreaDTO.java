package vn.dmcl.eagleeyes.dto;

public class AreaDTO {
    private String Id;
    private String Name;
    private double Lat;
    private double Lng;
    private double Radius;
    private int Count;
    private int Status;

    public AreaDTO() {}

    public AreaDTO(String id, String name, double lat, double lng, double radius, int count, int status) {
        Id = id;
        Name = name;
        Lat = lat;
        Lng = lng;
        Radius = radius;
        Count = count;
        Status = status;

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
