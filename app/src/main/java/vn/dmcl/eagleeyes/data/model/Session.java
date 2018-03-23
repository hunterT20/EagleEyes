package vn.dmcl.eagleeyes.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Session {
    @SerializedName("Key")
    @Expose
    private String Key;
    @SerializedName("UserType")
    @Expose
    private int UserType;

    public String getKey() {
        return Key;
    }

    public void setKey(String Key) {
        this.Key = Key;
    }

    public int getUserType() {
        return UserType;
    }

    public void setUserType(int userType) {
        UserType = userType;
    }
}
