package vn.dmcl.eagleeyes.data.dto;

import java.util.List;

public class DCheckManageDTO {
    private String Id;
    private long DCheckId;
    private long AgentId;
    private int Status;
    private String CreatedDate;
    private List<DCheckManageFlyerDTO> Flyer;

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

    public List<DCheckManageFlyerDTO> getFlyer() {
        return Flyer;
    }

    public void setFlyer(List<DCheckManageFlyerDTO> flyer) {
        Flyer = flyer;
    }
}
