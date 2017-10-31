package vn.dmcl.eagleeyes.dto;

import java.util.List;

public class FlyerLogDTO {
    private String Id;
    private int FlyerId;
    private int AgentId;
    private String PhoneNumber;
    private String OTP;
    private String Key;
    private int Status;
    private String ExpiredTime;
    private String CreatedDate;
    private AreaDTO Area;
    private List<LocationDTO> Location;

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

    public int getAgentId() {
        return AgentId;
    }

    public void setAgentId(int agentId) {
        AgentId = agentId;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getOTP() {
        return OTP;
    }

    public void setOTP(String OTP) {
        this.OTP = OTP;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public String getExpiredTime() {
        return ExpiredTime;
    }

    public void setExpiredTime(String expiredTime) {
        ExpiredTime = expiredTime;
    }

    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }

    public AreaDTO getArea() {
        return Area;
    }

    public void setArea(AreaDTO area) {
        Area = area;
    }

    public List<LocationDTO> getLocation() {
        return Location;
    }

    public void setLocation(List<LocationDTO> location) {
        Location = location;
    }
}
