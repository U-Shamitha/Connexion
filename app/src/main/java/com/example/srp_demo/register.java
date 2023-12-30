package com.example.srp_demo;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Context;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class register extends Fragment {

    RecyclerContactAdapterW adapterw;
    FloatingActionButton btnOpenDialog;
    RecyclerView recyclerVieww;
    ImageView noRegImg;
    ArrayList<ContactModelW> arrContacts= new ArrayList<>();
    backendw bdw = new backendw();
    DatabaseReference databaseReference;

    String category="";
    String address="";

    View view2;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view2 = inflater.inflate(R.layout.fragment_register, container, false);

        recyclerVieww = view2.findViewById(R.id.recyclerContactW);
        noRegImg = view2.findViewById(R.id.noReg_img);
        btnOpenDialog = view2.findViewById(R.id.btnOpenDialogW);

        SharedPreferences pref = getContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        String loginpno = pref.getString("Pnol", null);

        btnOpenDialog.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.add_annw);

                EditText edtName = dialog.findViewById(R.id.edtNameW);
                EditText edtDes = dialog.findViewById(R.id.edtDesW);
                EditText edtQuan = dialog.findViewById(R.id.edtQuanW);
                EditText edtPhone = dialog.findViewById(R.id.edtPhoneW);
                Button btnAction = dialog.findViewById(R.id.btnActionW);

                edtPhone.setText(loginpno);
                edtPhone.setClickable(false);
                edtPhone.setEnabled(false);
                edtName.setText(MainActivity.Uname);

                //category autocomplete text
                AutoCompleteTextView catAtvW;
                ArrayAdapter<String> arrayAdapterC = new ArrayAdapter<String>(getActivity(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,getResources().getStringArray(R.array.category));
                catAtvW = (AutoCompleteTextView) dialog.findViewById(R.id.catAtvW);
                catAtvW.setAdapter(arrayAdapterC);
                catAtvW.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        catAtvW.showDropDown();

                    }
                });

                //category autocomplete text
                AutoCompleteTextView locAtvW;
                ArrayAdapter<String> arrayAdapterL = new ArrayAdapter<String>(getActivity(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,getResources().getStringArray(R.array.location));
                locAtvW = (AutoCompleteTextView) dialog.findViewById(R.id.locAtvW);
                locAtvW.setAdapter(arrayAdapterL);
                locAtvW.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        locAtvW.showDropDown();

                    }
                });

                catAtvW.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        category = (String) catAtvW.getAdapter().getItem(i).toString();
                        //Toast.makeText(getContext(), category, Toast.LENGTH_SHORT).show();
                    }
                });

                locAtvW.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        address = (String) locAtvW.getAdapter().getItem(i).toString();
                        //Toast.makeText(getContext(), address, Toast.LENGTH_SHORT).show();
                    }
                });


                btnAction.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        String name = "", addrs = "", cat = "", des = "", quan = "", phone = "";

                        if (!edtName.getText().toString().equals("")) {
                            name = edtName.getText().toString();
                        } else {
                            Toast.makeText(getContext(), "Please enter name", Toast.LENGTH_SHORT).show();
                        }
                        if (!address.equals("") && address.equals(locAtvW.getText().toString())) {
                            addrs = address;
                        } else {
                            Toast.makeText(getContext(), "Please select valid address", Toast.LENGTH_SHORT).show();
                        }
                        if (!category.equals("") && category.equals(catAtvW.getText().toString())) {
                            cat = category;
                        } else {
                            Toast.makeText(getContext(), "Please select valid category", Toast.LENGTH_SHORT).show();
                        }
                        if (!edtDes.getText().toString().equals("")) {
                            des = edtDes.getText().toString();
                        } else {
                            Toast.makeText(getContext(), "Please enter description", Toast.LENGTH_SHORT).show();
                        }
                        if (!edtQuan.getText().toString().equals("")) {
                            quan = edtQuan.getText().toString();
                        } else {
                            Toast.makeText(getContext(), "Please enter experience", Toast.LENGTH_SHORT).show();
                        }
                        if (!edtPhone.getText().toString().equals("")) {
                            phone = edtPhone.getText().toString();
                        } else {
                            Toast.makeText(getContext(), "Please enter contact number", Toast.LENGTH_SHORT).show();
                        }
                        if(!edtName.getText().toString().equals("") & !address.equals("") & address.equals(locAtvW.getText().toString()) &
                                !category.equals("") & category.equals(catAtvW.getText().toString()) & !edtDes.getText().toString().equals("") &
                                !edtQuan.getText().toString().equals("") & !edtPhone.getText().toString().equals("")
                        ){

                            noRegImg.setVisibility(View.GONE);

                            //generate key
                            FirebaseDatabase db = FirebaseDatabase.getInstance();
                            databaseReference = db.getReference(ContactModelW.class.getSimpleName());
                            String key= databaseReference.push().getKey();
                            //Toast.makeText(getContext(),key,Toast.LENGTH_LONG).show();

                            //create contact object
                            ContactModelW contact = new ContactModelW(R.drawable.pimg,name, addrs, cat, des, quan, phone,key,1,0,-1*System.currentTimeMillis(),0,0);
                            dialog.dismiss();


                            //add to firebase
                            bdw.add(contact).addOnSuccessListener(suc ->{
                                //Toast.makeText(getContext(), "successfull", Toast.LENGTH_LONG).show();
                            }).addOnFailureListener(er ->{
                                //Toast.makeText(getContext(), ""+er.getMessage(), Toast.LENGTH_LONG).show();
                            });

                            //add object to array contacts
                            arrContacts.add(new ContactModelW(MainActivity.bitmap_main,contact.name, contact.address, contact.category, contact.description, contact.quantity, contact.phone , contact.key,1,0, contact.timeStamp,contact.rating,contact.noOfRatings));
                            adapterw = new RecyclerContactAdapterW(getContext(),arrContacts,getActivity());
                            recyclerVieww.setAdapter(adapterw);
                            if(arrContacts.size()>1){
                                adapterw.notifyItemInserted(arrContacts.size() - 1);
                                recyclerVieww.scrollToPosition(arrContacts.size() - 1);
                            }

                            //subscribe to topic
                            FirebaseMessaging.getInstance().subscribeToTopic(contact.category+"p");
                            FirebaseMessaging.getInstance().subscribeToTopic(contact.address+"p");
                            //Toast.makeText(getContext(), "Subscribed to "+contact.category+"p and "+contact.address+"p",Toast.LENGTH_LONG).show();
                            //Toast.makeText(getContext(), "Subscribed to all",Toast.LENGTH_LONG).show();


                            FcmNotificationsSender notificationsSender = new FcmNotificationsSender("/topics/"+contact.category,"New "+contact.address+" "+contact.category+" worker","Click here to know more",getActivity());
                            notificationsSender.SendNotifications();
                            //Toast.makeText(getContext(), "delay", Toast.LENGTH_SHORT).show();
                            FcmNotificationsSender notificationsSender2 = new FcmNotificationsSender("/topics/"+contact.address,"New "+contact.address+" "+contact.category+" worker","Click here to know more",getActivity());
                            notificationsSender2.SendNotifications();

                        }
                    }
                });
                dialog.show();
            }
        });

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("ContactModelW");
        Query query = databaseReference.orderByChild("timeStamp");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int count=0;
                    for (DataSnapshot user : dataSnapshot.getChildren()) {
                        ContactModelW contact = user.getValue(ContactModelW.class);
                        if(contact.phone.equals(loginpno)) {
                            count=1;
                            //Toast.makeText(announcement.this,"found", Toast.LENGT}H_LONG).show();
                            arrContacts.add(new ContactModelW(MainActivity.bitmap_main, contact.name, contact.address, contact.category, contact.description, contact.quantity, contact.phone, contact.key, 1,0, contact.timeStamp,contact.rating,contact.noOfRatings));
                            adapterw = new RecyclerContactAdapterW(getContext(), arrContacts, getActivity());
                            recyclerVieww.setAdapter(adapterw);
                        }
                        //Toast.makeText(getContext(), "displayed", Toast.LENGTH_LONG).show();
                    }
                    if(count==0){
                        noRegImg.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), "No Registrations Found", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        recyclerVieww.setLayoutManager(new LinearLayoutManager(getContext()));

        return view2;
    }
}