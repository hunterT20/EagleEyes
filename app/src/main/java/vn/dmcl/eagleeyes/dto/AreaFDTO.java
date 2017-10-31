package vn.dmcl.eagleeyes.dto;

import java.util.List;

public class AreaFDTO {
    private String Id;
    private int FlyerId;
    private String CreatedDate;
    private List<AreaDTO> Area;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public int getFlyerId() {
        return FlyerId;
    }

    public void setFlyerId(int flyerId) {
        FlyerId = flyerId;
    }

    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }

    public List<AreaDTO> getArea() {
        return Area;
    }

    public void setArea(List<AreaDTO> area) {
        Area = area;
    }
}
