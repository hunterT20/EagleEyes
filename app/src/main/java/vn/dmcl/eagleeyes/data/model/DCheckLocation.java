package vn.dmcl.eagleeyes.data.model;

import java.util.List;

public class DCheckLocation extends Location {
    private List<vn.dmcl.eagleeyes.data.model.Photo> Photo;

    public List<vn.dmcl.eagleeyes.data.model.Photo> getPhoto() {
        return Photo;
    }

    public void setPhoto(List<vn.dmcl.eagleeyes.data.model.Photo> photo) {
        Photo = photo;
    }
}
