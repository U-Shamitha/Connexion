package com.example.srp_demo;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.srp_demo.ContactModelW;


import java.util.HashMap;
import java.util.Map;

public class backendw {
    private DatabaseReference databaseReferencew;

    public backendw(){
        FirebaseDatabase dbw = FirebaseDatabase.getInstance();
        databaseReferencew = dbw.getReference(ContactModelW.class.getSimpleName());
    }
    public Task<Void> add(ContactModelW contact){
        Map<String,Object> map= new HashMap<>();
        map.put("address",contact.address.toString());
        map.put("category",contact.category.toString());
        map.put("description",contact.description.toString());
        map.put("name",contact.name.toString());
        map.put("phone",contact.phone.toString());
        //map.put("address",contact.img);
        map.put("quantity",contact.quantity.toString());
        map.put("key",contact.key.toString());
        map.put("timeStamp",contact.timeStamp);
        map.put("rating",contact.rating);
        map.put("noOfRatings",contact.noOfRatings);
        //map.put("address",contact.clickable);



        return databaseReferencew.child(contact.key.toString()).setValue(map);
    }

    //favlist of user
    private DatabaseReference databaseReferencefav;
    String uphno;
    public backendw(String uphno){
        this.uphno=uphno;
        FirebaseDatabase dbu = FirebaseDatabase.getInstance();
        databaseReferencefav = dbu.getReference("FavouritesW");
    }
    public Task<Void> addFav(String favphno){
        Map<String,Object> map= new HashMap<>();
        map.put(favphno.toString(),favphno.toString());

        return databaseReferencefav.child(uphno).setValue(map);

    }
}
