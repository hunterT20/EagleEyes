package vn.dmcl.eagleeyes.data.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Photo {
    @SerializedName("Id")
    @Expose
    private String Id;
    @SerializedName("PhotoLink")
    @Expose
    private String PhotoLink;
    @SerializedName("CreateDate")
    @Expose
    private String CreateDate;
    @SerializedName("Description")
    @Expose
    private String Description;
    @SerializedName("Status")
    @Expose
    private int Status;

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getPhotoLink() {
        return PhotoLink;
    }

    public void setPhotoLink(String photoLink) {
        PhotoLink = photoLink;
    }

    public String getCreateDate() {
        return CreateDate;
    }

    public void setCreateDate(String createDate) {
        CreateDate = createDate;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }
}
