package vn.dmcl.eagleeyes.data.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AreaFlyer {
    @SerializedName("Id")
    @Expose
    private String Id;
    @SerializedName("Name")
    @Expose
    private String Name;
    @SerializedName("PhoneNumber")
    @Expose
    private String PhoneNumber;
    @SerializedName("FlyerId")
    @Expose
    private Integer FlyerId;
    @SerializedName("AgentId")
    @Expose
    private Integer AgentId;
    @SerializedName("CreatedDate")
    @Expose
    private String CreatedDate;
    @SerializedName("Status")
    @Expose
    private Integer Status;
    @SerializedName("Area")
    @Expose
    private List<Area> Area;

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

    public Integer getAgentId() {
        return AgentId;
    }

    public void setAgentId(Integer agentId) {
        AgentId = agentId;
    }

    public Integer getStatus() {
        return Status;
    }

    public void setStatus(Integer status) {
        Status = status;
    }

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

    public List<vn.dmcl.eagleeyes.data.dto.Area> getArea() {
        return Area;
    }

    public void setArea(List<vn.dmcl.eagleeyes.data.dto.Area> area) {
        Area = area;
    }
}
