package com.example.srp_demo;

import android.graphics.Bitmap;
import android.widget.TextView;

public class ContactModel {
    public int img,clickable;
    public String name,address,category,description,quantity,phone,key;
    Bitmap img1;
    int menu;
    long timeStamp;
    int noOfRatings;
    float rating;


    public ContactModel(){

    }


    public ContactModel(int img,String name,String address,String category,String description,String quantity,String phone,String key, int clickable,int menu,long timeStamp,float rating,int noOfRatings){
        this.img=img;
        this.name = name;
        this.address=address ;
        this.category=category;
        this.description=description;
        this.quantity=quantity;
        this.phone=phone;
        this.key=key;
        this.clickable=clickable;
        this.menu=menu;
        this.timeStamp=timeStamp;
        this.rating=rating;
        this.noOfRatings=noOfRatings;
    }


    public ContactModel(Bitmap img1,String name,String address,String category,String description,String quantity,String phone,String key, int clickable,int menu,long timeStamp,float rating,int noOfRatings){
        this.img1=img1;
        this.name = name;
        this.address=address ;
        this.category=category;
        this.description=description;
        this.quantity=quantity;
        this.phone=phone;
        this.key=key;
        this.clickable=clickable;
        this.menu=menu;
        this.timeStamp=timeStamp;
        this.rating=rating;
        this.noOfRatings=noOfRatings;
    }



}
