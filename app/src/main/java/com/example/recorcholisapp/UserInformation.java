package com.example.recorcholisapp;

import java.util.List;
import java.util.Map;

public class UserInformation {
    public Double saldo;
    public int tickets;
    public Map<String, String> data;

    public UserInformation() {
    }

    public UserInformation(Double saldo, int tickets, Map<String, String> data) {
        this.saldo = saldo;
        this.tickets = tickets;
        this.data = data;
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
