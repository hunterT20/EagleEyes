package vn.dmcl.eagleeyes.data.dto;

import java.util.List;

public class DCheckLocation extends Location {
    private List<vn.dmcl.eagleeyes.data.dto.Photo> Photo;

    public List<vn.dmcl.eagleeyes.data.dto.Photo> getPhoto() {
        return Photo;
    }

    public void setPhoto(List<vn.dmcl.eagleeyes.data.dto.Photo> photo) {
        Photo = photo;
    }
}
