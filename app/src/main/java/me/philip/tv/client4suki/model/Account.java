package me.philip.tv.client4suki.model;

import com.google.gson.annotations.SerializedName;

public class Account {
    @SerializedName("name")
    private String name;
    @SerializedName("password")
    private String password;
    @SerializedName("remmember")
    private Boolean remmember;

    public String getUsr() {
        return name;
    }

    public void setUsr(String usr) {
        this.name = usr;
    }

    public String getPsw() {
        return password;
    }

    public void setPsw(String psw) {
        this.password = psw;
    }

    public Boolean getRemember() {
        return remmember;
    }

    public void setRemember(Boolean remember) {
        this.remmember = remember;
    }
}
