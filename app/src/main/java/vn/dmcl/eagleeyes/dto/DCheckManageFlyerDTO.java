package vn.dmcl.eagleeyes.dto;

import java.util.List;

public class DCheckManageFlyerDTO {
    private long FlyerId;
    private String Name;
    private String PhoneNumber;
    private List<AreaDTO> Area;

    public long getFlyerId() {
        return FlyerId;
    }

    public void setFlyerId(long flyerId) {
        FlyerId = flyerId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public List<AreaDTO> getArea() {
        return Area;
    }

    public void setArea(List<AreaDTO> area) {
        Area = area;
    }
}
