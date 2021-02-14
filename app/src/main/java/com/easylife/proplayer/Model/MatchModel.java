package com.easylife.proplayer.Model;

public class MatchModel {

    String date, entryFee, entryNumber, map, matchId, maxEntries, perKill, prizeDetails, status, time, type, version;

    public MatchModel(String date, String entryFee, String entryNumber, String map, String matchId, String maxEntries, String perKill, String prizeDetails, String prizePool, String status, String time, String type, String version) {
        this.date = date;
        this.entryFee = entryFee;
        this.entryNumber = entryNumber;
        this.map = map;
        this.matchId = matchId;
        this.maxEntries = maxEntries;
        this.perKill = perKill;
        this.prizeDetails = prizeDetails;
        //this.prizePool = prizePool;
        this.status = status;
        this.time = time;
        this.type = type;
        this.version = version;
    }

    public MatchModel(){}

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEntryFee() {
        return entryFee;
    }

    public void setEntryFee(String entryFee) {
        this.entryFee = entryFee;
    }

    public String getEntryNumber() {
        return entryNumber;
    }

    public void setEntryNumber(String entryNumber) {
        this.entryNumber = entryNumber;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public String getMaxEntries() {
        return maxEntries;
    }

    public void setMaxEntries(String maxEntries) {
        this.maxEntries = maxEntries;
    }

    public String getPerKill() {
        return perKill;
    }

    public void setPerKill(String perKill) {
        this.perKill = perKill;
    }

    public String getPrizeDetails() {
        return prizeDetails;
    }

    public void setPrizeDetails(String prizeDetails) {
        this.prizeDetails = prizeDetails;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
