package com.example.recorcholisapp;

import java.util.List;
import java.util.Map;

public class UserInformation {
    public String uuid;
    public String userID;
    public Double saldo;
    public int tickets;
    public Map<String,String> data;

    public UserInformation() {
    }

    public UserInformation(String uuid, String userID, Double saldo, int tickets, Map<String, String> data) {
        this.uuid = uuid;
        this.userID = userID;
        this.saldo = saldo;
        this.tickets = tickets;
        this.data = data;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }

    public int getTickets() {
        return tickets;
    }

    public void setTickets(int tickets) {
        this.tickets = tickets;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}
