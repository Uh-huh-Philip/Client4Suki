package me.philip.tv.client4suki.model;

public class Account {
    private String host;
    private String usr;
    private String psw;
    public void Account(){
        this.host = "";
        this.usr = "";
        this.psw = "";
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUsr() {
        return usr;
    }

    public void setUsr(String usr) {
        this.usr = usr;
    }

    public String getPsw() {
        return psw;
    }

    public void setPsw(String psw) {
        this.psw = psw;
    }
}
