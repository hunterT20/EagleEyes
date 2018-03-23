package vn.dmcl.eagleeyes.data.model;

import java.util.List;

public class DCheckManageFlyer {
    private long FlyerId;
    private String Name;
    private String PhoneNumber;
    private List<vn.dmcl.eagleeyes.data.model.Area> Area;

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

    public List<vn.dmcl.eagleeyes.data.model.Area> getArea() {
        return Area;
    }

    public void setArea(List<vn.dmcl.eagleeyes.data.model.Area> area) {
        Area = area;
    }
}
