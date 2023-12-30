package com.example.srp_demo;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;

public class Favourites {
    public String favphno;
    public Favourites(String favphno){
        this.favphno=favphno;
    }
}
