//package com.example.srp_demo;
//
//import android.widget.Toast;
//
//import com.example.srp_demo.ContactModel;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class backend2 {
//    private DatabaseReference databaseReference;
//
//    public backend2(){
//        FirebaseDatabase db = FirebaseDatabase.getInstance();
//        databaseReference = db.getReference(ContactModelW.class.getSimpleName());
//    }
//    public Task<Void> add(ContactModelW contact){
//        return databaseReference.push().setValue(contact);
//    }
//
//
//}
