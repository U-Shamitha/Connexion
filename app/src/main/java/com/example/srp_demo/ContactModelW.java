package com.example.srp_demo;

import android.graphics.Bitmap;

public class ContactModelW {
    public int img,clickable,menu;
    Bitmap img1;
    public String name,address,category,description,quantity,key,phone;
    long timeStamp;
    int noOfRatings;
    float rating;



    public ContactModelW(){}

    public ContactModelW(int img, String name, String address, String category, String description, String quantity, String phone,String key,int clickable,int menu,long timeStamp,float rating,int noOfRatings){
        this.img=img;
        this.name = name;
        this.address=address ;
        this.category=category;
        this.description=description;
        this.quantity=quantity;
        this.key=key;
        this.phone=phone;
        this.clickable=clickable;
        this.menu=menu;
        this.timeStamp=timeStamp;
        this.rating=rating;
        this.noOfRatings=noOfRatings;
    }


    public ContactModelW(Bitmap img1, String name, String address, String category, String description, String quantity, String phone, String key, int clickable,int menu, long timeStamp,float rating,int noOfRatings){
        this.img1=img1;
        this.name = name;
        this.address=address ;
        this.category=category;
        this.description=description;
        this.quantity=quantity;
        this.key=key;
        this.phone=phone;
        this.clickable=clickable;
        this.menu=menu;
        this.timeStamp=timeStamp;
        this.rating=rating;
        this.noOfRatings=noOfRatings;
    }


}
