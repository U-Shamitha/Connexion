package com.example.srp_demo;

import android.widget.Toast;

import com.example.srp_demo.ContactModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class backend {
    private DatabaseReference databaseReference;

    public backend(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference(ContactModel.class.getSimpleName());
    }
    public Task<Void> add(ContactModel contact){


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



            return databaseReference.child(contact.key.toString()).setValue(map);

    }

    //favlist of user
    private DatabaseReference databaseReferencefav;
    String uphno;
    public backend(String uphno){
        this.uphno=uphno;
        FirebaseDatabase dbu = FirebaseDatabase.getInstance();
        databaseReferencefav = dbu.getReference(Favourites.class.getSimpleName());
    }
    public Task<Void> addFav(String favphno){
        Map<String,Object> map= new HashMap<>();
        map.put(favphno.toString(),favphno.toString());

        return databaseReferencefav.child(uphno).setValue(map);

    }

    //ratings of user
    private DatabaseReference databaseReferenceRat;
    public backend(String uphno,String Ratings){
        this.uphno=uphno;
        FirebaseDatabase dbu= FirebaseDatabase.getInstance();
        databaseReferenceRat = dbu.getReference(Ratings.class.getSimpleName());
    }
    public Task<Void> addRat(String otherUsrphno,float rating){
        Map<String,Object> map= new HashMap<>();
        map.put(otherUsrphno.toString(),rating);
        return databaseReferenceRat.child(uphno).setValue(map);

    }

}
