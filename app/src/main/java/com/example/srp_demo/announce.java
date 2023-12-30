package com.example.srp_demo;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class announce extends Fragment implements  AdapterView.OnItemSelectedListener{

    View view3;
    View annView;
    RecyclerContactAdapter adapter;
    ImageView noAnnouncementImg;
    FloatingActionButton btnOpenDialog;
    RecyclerView recyclerView;
    Bitmap bitmap;
    ArrayList<ContactModel> arrContacts = new ArrayList<>();
    backend bd = new backend();
    private DatabaseReference databaseReference;

    StorageReference storageReference;
    ProgressDialog progressDialog;

    String category="";
    String address="";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view3 =  inflater.inflate(R.layout.fragment_announce, container, false);

        recyclerView = view3.findViewById(R.id.recyclerContact);
        noAnnouncementImg = view3.findViewById(R.id.noAnn_img);
        btnOpenDialog = view3.findViewById(R.id.btnOpenDialog);

        SharedPreferences pref = getContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();

        String loginpno = pref.getString("Pnol", null);

        btnOpenDialog.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.add_ann);

                EditText edtName = dialog.findViewById(R.id.edtName);
                EditText edtDes = dialog.findViewById(R.id.edtDes);
                EditText edtQuan = dialog.findViewById(R.id.edtQuan);
                EditText edtPhone = dialog.findViewById(R.id.edtPhone);
                Button btnAction = dialog.findViewById(R.id.btnAction);

                edtPhone.setText(loginpno);
                edtPhone.setClickable(false);
                edtPhone.setEnabled(false);
                edtName.setText(MainActivity.Uname);


                //category autocomplete text
                AutoCompleteTextView catAtv;
                ArrayAdapter<String> arrayAdapterC = new ArrayAdapter<String>(getActivity(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,getResources().getStringArray(R.array.category));
                catAtv = (AutoCompleteTextView) dialog.findViewById(R.id.catAtv);
                catAtv.setAdapter(arrayAdapterC);
                catAtv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        catAtv.showDropDown();
                    }
                });

                //category autocomplete text
                AutoCompleteTextView locAtv;
                ArrayAdapter<String> arrayAdapterL = new ArrayAdapter<String>(getActivity(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,getResources().getStringArray(R.array.location));
                locAtv = (AutoCompleteTextView) dialog.findViewById(R.id.locAtv);
                locAtv.setAdapter(arrayAdapterL);
                locAtv.setOnClickListener(new View.OnClickListener() {
                    //@RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View view) {

                        locAtv.showDropDown();

                    }
                });
                //Toast.makeText(getContext(), "here2", Toast.LENGTH_SHORT).show();

                catAtv.setText("");
                locAtv.setText("");


                catAtv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        category = (String) catAtv.getAdapter().getItem(i).toString();
                    }
                });
                //Toast.makeText(getContext(), "here3", Toast.LENGTH_SHORT).show();

                locAtv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        address = (String) locAtv.getAdapter().getItem(i).toString();
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
                        if (!address.equals("") & address.equals(locAtv.getText().toString())) {
                            addrs = address.toString();
                        } else {
                            Toast.makeText(getContext(), "Please select valid address", Toast.LENGTH_SHORT).show();
                        }
                        if (!category.equals("") & category.equals(catAtv.getText().toString())) {
                            cat = category.toString();
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
                            Toast.makeText(getContext(), "Please enter quantity", Toast.LENGTH_SHORT).show();
                        }
                        if (!edtPhone.getText().toString().equals("")) {
                            phone = edtPhone.getText().toString();
                        } else {
                            Toast.makeText(getContext(), "Please enter contact number", Toast.LENGTH_SHORT).show();
                        }
                        if(!edtName.getText().toString().equals("") &  !address.equals("")  & address.equals(locAtv.getText().toString()) &
                                !category.equals("") & category.equals(catAtv.getText().toString()) &
                                   !edtDes.getText().toString().equals("") &
                                !edtQuan.getText().toString().equals("") & !edtPhone.getText().toString().equals("")
                        ){
                            //hide the no announcement img when new announce
                            noAnnouncementImg.setVisibility(View.GONE);

                            //generate key
                            FirebaseDatabase db = FirebaseDatabase.getInstance();
                            databaseReference = db.getReference(ContactModel.class.getSimpleName());
                            String key= databaseReference.push().getKey();
                            //Toast.makeText(getContext(),key,Toast.LENGTH_LONG).show();

                            //create contact object
                            ContactModel contact = new ContactModel(MainActivity.bitmap_main,name, addrs,cat, des, quan, phone,key,1,0,-1*System.currentTimeMillis(),0,0);
                            dialog.dismiss();
                            //Toast.makeText(getContext(), "dailog dismissed", Toast.LENGTH_SHORT).show();



                            //add to firebase
                            bd.add(contact).addOnSuccessListener(suc ->{
                                //Toast.makeText(getContext(), "successfull", Toast.LENGTH_LONG).show();
                            }).addOnFailureListener(er ->{
                                //Toast.makeText(getContext(), ""+er.getMessage(), Toast.LENGTH_LONG).show();
                            });

                            //add object to array contacts
                            arrContacts.add(new ContactModel(MainActivity.bitmap_main,contact.name, contact.address, contact.category, contact.description, contact.quantity, contact.phone , contact.key,1,0,contact.timeStamp,contact.rating,contact.noOfRatings));
                            adapter = new RecyclerContactAdapter(getContext(),arrContacts,getActivity());
                            recyclerView.setAdapter(adapter);
                            if(arrContacts.size()>1){
                                adapter.notifyItemInserted(arrContacts.size() - 1);
                                recyclerView.scrollToPosition(arrContacts.size() - 1);
                            }

//                          //subscribe to topic
                            FirebaseMessaging.getInstance().subscribeToTopic(contact.category);
                            FirebaseMessaging.getInstance().subscribeToTopic(contact.address);

                            FcmNotificationsSender notificationsSender = new FcmNotificationsSender("/topics/"+contact.category+"p","New "+contact.address+" "+contact.category+" work provider","Click here to know more",getActivity());
                            notificationsSender.SendNotifications();
                            //Toast.makeText(getContext(), "delay", Toast.LENGTH_SHORT).show();
                            FcmNotificationsSender notificationsSender2 = new FcmNotificationsSender("/topics/"+contact.address+"p","New "+contact.address+" "+contact.category+" work provider","Click here to know more",getActivity());
                            notificationsSender2.SendNotifications();



                        }
                    }
                });
                dialog.show();
            }
        });


        //fetching data when initialized
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("ContactModel");
        Query query = databaseReference.orderByChild("timeStamp");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int count=0;
                    for (DataSnapshot user : dataSnapshot.getChildren()) {
                        ContactModel contact = user.getValue(ContactModel.class);
                        if(contact.phone.equals(loginpno)) {
                            count=1;
                            arrContacts.add(new ContactModel(MainActivity.bitmap_main, contact.name, contact.address, contact.category, contact.description, contact.quantity, contact.phone, contact.key, 1,0, contact.timeStamp,contact.rating,contact.noOfRatings));
                            adapter = new RecyclerContactAdapter(getContext(), arrContacts, getActivity());
                            recyclerView.setAdapter(adapter);
                        }
                    }
                    if(count==0){
                        noAnnouncementImg.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), "No announcements found", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));



        return view3;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}