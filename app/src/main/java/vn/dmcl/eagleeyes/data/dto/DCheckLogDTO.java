package vn.dmcl.eagleeyes.data.dto;

import java.util.List;

public class DCheckLogDTO {
    private String Id;
    private long DCheckId;
    private long FlyerId;
    private long AgentId;
    private int Status;
    private String ExpiredTime;
    private String CreatedDate;
    private Area Area;
    private String Key;

    public List<vn.dmcl.eagleeyes.data.dto.Location> getLocation() {
        return Location;
    }

    public void setLocation(List<vn.dmcl.eagleeyes.data.dto.Location> location) {
        Location = location;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public long getDCheckId() {
        return DCheckId;
    }

    public void setDCheckId(long DCheckId) {
        this.DCheckId = DCheckId;
    }

    public long getFlyerId() {
        return FlyerId;
    }

    public void setFlyerId(long flyerId) {
        FlyerId = flyerId;
    }

    public long getAgentId() {
        return AgentId;
    }

    public void setAgentId(long agentId) {
        AgentId = agentId;
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

    public vn.dmcl.eagleeyes.data.dto.Area getArea() {
        return Area;
    }

    public void setArea(vn.dmcl.eagleeyes.data.dto.Area area) {
        Area = area;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    private List<vn.dmcl.eagleeyes.data.dto.Location> Location;
}
