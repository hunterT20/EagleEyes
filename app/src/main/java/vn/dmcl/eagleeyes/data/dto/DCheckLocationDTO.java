package vn.dmcl.eagleeyes.data.dto;

import java.util.List;

public class DCheckLocationDTO extends LocationDTO {
    private List<PhotoDTO> Photo;

    public List<PhotoDTO> getPhoto() {
        return Photo;
    }

    public void setPhoto(List<PhotoDTO> photo) {
        Photo = photo;
    }
}
