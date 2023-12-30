package com.example.srp_demo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class search_worker extends Fragment implements  AdapterView.OnItemSelectedListener{

    int i;
    View view5;
    ImageButton imbtn_w;
    ArrayList<ContactModelW> arrContacts_searchw = new ArrayList<>();
    RecyclerContactAdapterW adapter_sw;
    RecyclerView recyclerView_sw;
    String flag;


    StorageReference storageReference;
    ProgressDialog progressDialog;
    Bitmap bitmap;
    Bitmap bitmap_default;

    String category_spn="";
    String address_spn="";

    int count=0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view5 = inflater.inflate(R.layout.fragment_search_worker, container, false);
        imbtn_w = (ImageButton) view5.findViewById(R.id.imbtn);
        recyclerView_sw = view5.findViewById(R.id.recyclerContact_sw);

        bitmap_default = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.ppic_default);


        //category autocomplete text
        AutoCompleteTextView catAtv;
        ArrayAdapter<String> arrayAdapterC = new ArrayAdapter<String>(getActivity(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,getResources().getStringArray(R.array.category));
        catAtv = (AutoCompleteTextView) view5.findViewById(R.id.catAtv);
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
        locAtv = (AutoCompleteTextView) view5.findViewById(R.id.locAtv);
        locAtv.setAdapter(arrayAdapterL);
        locAtv.setOnClickListener(new View.OnClickListener() {
            //@RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {

                locAtv.showDropDown();

            }
        });

        catAtv.setText("");
        locAtv.setText("");

        //store selected values
        catAtv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                category_spn = (String) catAtv.getAdapter().getItem(i).toString();
            }
        });

        locAtv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                address_spn = (String) locAtv.getAdapter().getItem(i).toString();
            }
        });


            imbtn_w.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage("fetching details");
                progressDialog.setCancelable(true);
                progressDialog.show();
                flag = "no";

                MainActivity.imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),0);

                Toast.makeText(getContext(), "Searching", Toast.LENGTH_SHORT).show();
                arrContacts_searchw.clear();
                adapter_sw = new RecyclerContactAdapterW(view5.getContext(), arrContacts_searchw, getActivity());
                recyclerView_sw.setAdapter(adapter_sw);
                //Toast.makeText(search_activity.this,category_spn + " "+ location_spn ,Toast.LENGTH_LONG).show();
                i=0;
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = firebaseDatabase.getReference("ContactModelW");
                Query query2 = databaseReference.orderByChild("timeStamp");
                query2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(address_spn.equals(locAtv.getText().toString())) {
                            if (category_spn.equals(catAtv.getText().toString())) {

                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot user : dataSnapshot.getChildren()) {

                                        ContactModel contact = user.getValue(ContactModel.class);

                                        if (contact.address.equals(address_spn) && contact.address.equals(address_spn)) {
                                            arrContacts_searchw.add(new ContactModelW(bitmap_default, contact.name, contact.address, contact.category, contact.description, contact.quantity, contact.phone, contact.key, 0,1,contact.timeStamp,contact.rating,contact.noOfRatings));
                                            //Collections.reverse(arrContacts_searchw);
                                            adapter_sw = new RecyclerContactAdapterW(view5.getContext(), arrContacts_searchw, getActivity());
                                            recyclerView_sw.setAdapter(adapter_sw);
                                            if(progressDialog.isShowing()){
                                                progressDialog.dismiss();
                                            }
                                            setpic(arrContacts_searchw.size()-1);

                                            flag = "yes";
                                        }
                                    }
                                    if(flag.equals("no")) {
                                        if(progressDialog.isShowing()){
                                            progressDialog.dismiss();
                                        }
                                        Toast.makeText(view5.getContext(), "no workers in this location and category", Toast.LENGTH_LONG).show();

                                    }
                                } else {
                                    if(progressDialog.isShowing()){
                                        progressDialog.dismiss();
                                    }
                                    Toast.makeText(getContext(), "no workers in this category and address", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(view5.getContext(), "Select valid category", Toast.LENGTH_LONG).show();
                            }
                        }else{
                            Toast.makeText(view5.getContext(), "Select valid address", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
            recyclerView_sw.setLayoutManager(new LinearLayoutManager(view5.getContext()));

        //search favourites
        imbtn_w.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                SharedPreferences pref = getContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                SharedPreferences.Editor editor = pref.edit();
                String loginpno = pref.getString("Pnol", null);

                Toast.makeText(getContext(), "Searching", Toast.LENGTH_SHORT).show();

                progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage("fetching details");
                progressDialog.setCancelable(true);
                progressDialog.show();

                arrContacts_searchw.clear();
                adapter_sw = new RecyclerContactAdapterW(view5.getContext(), arrContacts_searchw, getActivity());
                recyclerView_sw.setAdapter(adapter_sw);
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = firebaseDatabase.getReference("FavouritesW/"+loginpno);
                Query query = databaseReference.orderByChild("favphno");
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        count=0;
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot user : dataSnapshot.getChildren()) {
                                String favphno = user.getValue().toString();
                                //Toast.makeText(getContext(), ""+user.getValue(), Toast.LENGTH_SHORT).show();
                                FirebaseDatabase firebaseDatabase2 = FirebaseDatabase.getInstance();
                                DatabaseReference databaseReferenceFavInfo= firebaseDatabase2.getReference("ContactModelW");
                                Query queryFavInfo = databaseReferenceFavInfo.orderByChild("phone").equalTo(favphno);
                               // Toast.makeText(getContext(), ""+queryFavInfo, Toast.LENGTH_SHORT).show();
                                queryFavInfo.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        //Toast.makeText(getContext(), "before exists", Toast.LENGTH_SHORT).show();

                                        if (dataSnapshot.exists()) {
                                            //Toast.makeText(getContext(), "exists", Toast.LENGTH_SHORT).show();
                                            for (DataSnapshot user : dataSnapshot.getChildren()) {
                                                ContactModel contact = user.getValue(ContactModel.class);
                                                //Toast.makeText(getContext(), ""+contact, Toast.LENGTH_SHORT).show();
                                                arrContacts_searchw.add(new ContactModelW(bitmap_default,contact.name, contact.address, contact.category, contact.description, contact.quantity, contact.phone, contact.key,0,1,2022,contact.rating,contact.noOfRatings));
                                                adapter_sw = new RecyclerContactAdapterW(view5.getContext(), arrContacts_searchw, getActivity());
                                                recyclerView_sw.setAdapter(adapter_sw);
                                                if (progressDialog.isShowing()) {
                                                    progressDialog.dismiss();
                                                }
                                                setpic(arrContacts_searchw.size() - 1);

                                                count=1;

                                            }//for loop end
                                            if(count==0){
                                                if (progressDialog.isShowing()) {
                                                    progressDialog.dismiss();
                                                }
                                                Toast.makeText(getContext(), "No worker is avialable from your favourites", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                        else{
                                            Toast.makeText(getContext(), "You have no favorite workers", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        } else {

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                return true;
            }
        });

        return view5;
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    public void setpic (int position){


        storageReference = FirebaseStorage.getInstance().getReference("images/"+arrContacts_searchw.get(position).phone);
        //Toast.makeText(getContext(),arrContacts_searchw.get(position).phone + " 1",Toast.LENGTH_LONG).show();

        try {
            File localfile = File.createTempFile("tempFile",".jpg");
            storageReference.getFile(localfile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {

                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            bitmap = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                            arrContacts_searchw.set(position, new ContactModelW(bitmap, arrContacts_searchw.get(position).name, arrContacts_searchw.get(position).address,arrContacts_searchw.get(position).category, arrContacts_searchw.get(position).description, arrContacts_searchw.get(position).quantity,arrContacts_searchw.get(position).phone, arrContacts_searchw.get(position).key, 0,1,2022,arrContacts_searchw.get(position).rating,arrContacts_searchw.get(position).noOfRatings));
                            adapter_sw.notifyItemChanged(position);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getContext(), "failed to retrieve", Toast.LENGTH_SHORT).show();


                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}