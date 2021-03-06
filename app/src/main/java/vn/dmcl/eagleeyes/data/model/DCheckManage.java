package vn.dmcl.eagleeyes.data.model;

import java.util.List;

public class DCheckManage {
    private String Id;
    private long DCheckId;
    private long AgentId;
    private int Status;
    private String CreatedDate;
    private List<DCheckManageFlyer> Flyer;

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

    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }

    public List<DCheckManageFlyer> getFlyer() {
        return Flyer;
    }

    public void setFlyer(List<DCheckManageFlyer> flyer) {
        Flyer = flyer;
    }
}
