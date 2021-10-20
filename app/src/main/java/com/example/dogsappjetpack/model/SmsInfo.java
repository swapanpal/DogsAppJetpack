package com.example.dogsappjetpack.model;

public class SmsInfo {
    // variable for sms data
    public String to;
    public String text;
    public String imageUrl;

    // Constructor
    public SmsInfo(String to, String text, String imageUrl) {
        this.to = to;
        this.text = text;
        this.imageUrl = imageUrl;
    }
}
