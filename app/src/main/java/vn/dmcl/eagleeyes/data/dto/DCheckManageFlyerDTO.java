package vn.dmcl.eagleeyes.data.dto;

import java.util.List;

public class DCheckManageFlyerDTO {
    private long FlyerId;
    private String Name;
    private String PhoneNumber;
    private List<vn.dmcl.eagleeyes.data.dto.Area> Area;

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

    public List<vn.dmcl.eagleeyes.data.dto.Area> getArea() {
        return Area;
    }

    public void setArea(List<vn.dmcl.eagleeyes.data.dto.Area> area) {
        Area = area;
    }
}
