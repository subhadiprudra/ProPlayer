package com.easylife.proplayer.Model;

public class AccountModel {
    String balance,
    email,
    name,
    number,
    pubgId,
    pubgName,
    androidId,
    token;

    public AccountModel(){}

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPubgId() {
        return pubgId;
    }

    public void setPubgId(String pubgId) {
        this.pubgId = pubgId;
    }

    public String getPubgName() {
        return pubgName;
    }

    public void setPubgName(String pubgName) {
        this.pubgName = pubgName;
    }

    public String getAndroidId() {
        return androidId;
    }

    public void setAndroidId(String androidId) {
        this.androidId = androidId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
