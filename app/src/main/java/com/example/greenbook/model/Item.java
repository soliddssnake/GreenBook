package com.example.greenbook.model;

public class Item {



    String title;
    String comment;
    String downloadUrl;
    String date;

    public Item(String title, String comment, String downloadUrl, String date) {
        this.title = title;
        this.comment = comment;
        this.downloadUrl = downloadUrl;
        this.date = date;
    }
}
