package vn.dmcl.eagleeyes.data.dto;

import java.util.List;

public class LocationDTO {
    private double Lat;
    private double Lng;
    private String CreateDate;
    private double Time;

    LocationDTO() {}
    public LocationDTO(double lat, double lng, String createDate)
    {
        Lat = lat;
        Lng = lng;
        CreateDate = createDate;
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

    public String getCreateDate() {
        return CreateDate;
    }

    public void setCreateDate(String createDate) {
        CreateDate = createDate;
    }

    public double getTime() {
        return Time;
    }

    public void setTime(double time) {
        Time = time;
    }
    private List<PhotoDTO> Photo;

    public List<PhotoDTO> getPhoto() {
        return Photo;
    }

    public void setPhoto(List<PhotoDTO> photo) {
        Photo = photo;
    }
}
