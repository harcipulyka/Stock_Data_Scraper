package com.company;

public class Entry {
    String follower;
    String trend;
    String message;

    public Entry(String follower, String trend, String messsage) {
        this.follower = follower;
        this.trend = trend;
        this.message = messsage;
    }

    public String toString() {
        return follower + " : " + trend +  " : " + message;
    }
}
