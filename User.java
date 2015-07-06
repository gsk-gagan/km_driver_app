package com.knowmiles.www.driverapp;

public class User {
    String name, mobile, cabInfo, servProv, passwrd;

    public User(String name, String mobile, String cabInfo, String servProv, String passwrd) {
        this.name = name;
        this.mobile = mobile;
        this.cabInfo = cabInfo;
        this.servProv = servProv;
        this.passwrd = passwrd;
    }

    public User(String mobile, String passwrd) {
        this.mobile = mobile;
        this.passwrd = passwrd;
        this.name = "";
        this.cabInfo = "";
        this.servProv = "";
    }

}
