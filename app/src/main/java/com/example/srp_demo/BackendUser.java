package com.example.srp_demo;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class BackendUser {
    private DatabaseReference databaseReferencew;

    public BackendUser(){
        FirebaseDatabase dbu = FirebaseDatabase.getInstance();
        databaseReferencew = dbu.getReference(User_details.class.getSimpleName());
    }
    public Task<Void> add(User_details details){
        return databaseReferencew.push().setValue(details);
    }




}
