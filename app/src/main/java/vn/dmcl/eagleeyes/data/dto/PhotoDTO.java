package vn.dmcl.eagleeyes.data.dto;

public class PhotoDTO {
    private String Id;
    private String PhotoLink;
    private String CreateDate;
    private String Description;
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
