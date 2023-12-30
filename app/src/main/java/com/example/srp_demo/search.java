package com.example.srp_demo;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
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

public class search extends Fragment implements  AdapterView.OnItemSelectedListener{
    ImageButton imbtn;
    ArrayList<ContactModel> arrContacts_search2 = new ArrayList<>();
    RecyclerContactAdapter adapter_s2;
    RecyclerView recyclerView_s2;


    StorageReference storageReference;
    ProgressDialog progressDialog;
    Bitmap bitmap, bitmap_default;

    String category_spn="";
    String address_spn="";
    String flag;

    TextView menu;

    int count=0;

    View view4;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view4 = inflater.inflate(R.layout.fragment_search, container, false);
        imbtn = (ImageButton) view4.findViewById(R.id.imbtn);
        recyclerView_s2 = view4.findViewById(R.id.recyclerContact_s2);
        //make option menu visible

        bitmap_default = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.ppic_default);

        //category autocomplete text
        AutoCompleteTextView catAtv;
        ArrayAdapter<String> arrayAdapterC = new ArrayAdapter<String>(getActivity(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,getResources().getStringArray(R.array.category));
        catAtv = (AutoCompleteTextView) view4.findViewById(R.id.catAtv);
        catAtv.setAdapter(arrayAdapterC);
        catAtv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                catAtv.showDropDown();
            }
        });

        //location autocomplete text
        AutoCompleteTextView locAtv;
        ArrayAdapter<String> arrayAdapterL = new ArrayAdapter<String>(getActivity(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,getResources().getStringArray(R.array.location));
        locAtv = (AutoCompleteTextView) view4.findViewById(R.id.locAtv);
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


        //search selected
        imbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(locAtv.getText().toString().equals("") || catAtv.getText().toString().equals("") || locAtv.getText().equals(null) || catAtv.getText().equals(null))) {


                    progressDialog = new ProgressDialog(getContext());
                    progressDialog.setMessage("fetching details");
                    progressDialog.setCancelable(true);
                    progressDialog.show();
                    flag = "no";

                    MainActivity.imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

                    Toast.makeText(getContext(), "Searching", Toast.LENGTH_SHORT).show();
                    arrContacts_search2.clear();
                    adapter_s2 = new RecyclerContactAdapter(view4.getContext(), arrContacts_search2, getActivity());
                    recyclerView_s2.setAdapter(adapter_s2);

                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = firebaseDatabase.getReference("ContactModel");
                    Query query2 = databaseReference.orderByChild("timeStamp");
                    query2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (address_spn.equals(locAtv.getText().toString())) {
                                if (category_spn.equals(catAtv.getText().toString())) {
                                    if (dataSnapshot.exists()) {
                                        for (DataSnapshot user : dataSnapshot.getChildren()) {

                                            ContactModel contact = user.getValue(ContactModel.class);

                                            if (contact.category.equals(category_spn) && contact.address.equals(address_spn)) {
                                                //Toast.makeText(getContext(),contact.name,Toast.LENGTH_LONG).show();

                                                arrContacts_search2.add(new ContactModel(bitmap_default, contact.name, contact.address, contact.category, contact.description, contact.quantity, contact.phone, contact.key, 0, 1, contact.timeStamp,contact.rating,contact.noOfRatings));
//                                                Toast.makeText(getContext(),"noOfRatings"+contact.noOfRatings, Toast.LENGTH_SHORT).show();
                                                adapter_s2 = new RecyclerContactAdapter(view4.getContext(), arrContacts_search2, getActivity());
                                                recyclerView_s2.setAdapter(adapter_s2);
                                                if (progressDialog.isShowing()) {
                                                    progressDialog.dismiss();
                                                }
                                                setpic(arrContacts_search2.size() - 1);

                                                flag = "yes";

                                            }
                                        }

                                        if (flag.equals("no")) {
                                            if (progressDialog.isShowing()) {
                                                progressDialog.dismiss();
                                            }
                                            Toast.makeText(view4.getContext(), "no workers in this location and category", Toast.LENGTH_LONG).show();

                                        }

                                    } else {
                                        if (progressDialog.isShowing()) {
                                            progressDialog.dismiss();
                                        }
                                        Toast.makeText(view4.getContext(), "no workers in this location and category", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(view4.getContext(), "Select valid category", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(view4.getContext(), "Select valid address", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else{
                    Toast.makeText(getContext(),"Please select address and category", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //search selected

        //saerch favourites
        imbtn.setOnLongClickListener(new View.OnLongClickListener() {
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

                arrContacts_search2.clear();
                adapter_s2 = new RecyclerContactAdapter(view4.getContext(), arrContacts_search2, getActivity());
                recyclerView_s2.setAdapter(adapter_s2);
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = firebaseDatabase.getReference("Favourites/"+loginpno);
                Query query = databaseReference.orderByChild("favphno");
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        count=0;
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot user : dataSnapshot.getChildren()) {
                                String favphno = user.getValue().toString();
                               // Toast.makeText(getContext(), ""+user.getValue(), Toast.LENGTH_SHORT).show();
                                FirebaseDatabase firebaseDatabase2 = FirebaseDatabase.getInstance();
                                DatabaseReference databaseReferenceFavInfo= firebaseDatabase2.getReference("ContactModel");
                                Query queryFavInfo = databaseReferenceFavInfo.orderByChild("timeStamp");
                                //Toast.makeText(getContext(), ""+queryFavInfo, Toast.LENGTH_SHORT).show();
                                queryFavInfo.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        //Toast.makeText(getContext(), "before exists", Toast.LENGTH_SHORT).show();

                                        if (dataSnapshot.exists()) {
                                            //Toast.makeText(getContext(), "exists", Toast.LENGTH_SHORT).show()
                                            for (DataSnapshot user : dataSnapshot.getChildren()) {
                                                ContactModel contact = user.getValue(ContactModel.class);
                                                if(contact.phone.equals(favphno)) {
                                                    //Toast.makeText(getContext(), "" + contact, Toast.LENGTH_SHORT).show();
                                                    arrContacts_search2.add(new ContactModel(bitmap_default, contact.name, contact.address, contact.category, contact.description, contact.quantity, contact.phone, contact.key, 0, 1,2002,contact.rating,contact.noOfRatings));
//                                                    Toast.makeText(getContext(), "noOfRatings1"+contact.noOfRatings, Toast.LENGTH_SHORT).show();
                                                    adapter_s2 = new RecyclerContactAdapter(view4.getContext(), arrContacts_search2, getActivity());
                                                    recyclerView_s2.setAdapter(adapter_s2);
                                                    if (progressDialog.isShowing()) {
                                                        progressDialog.dismiss();
                                                    }
                                                    setpic(arrContacts_search2.size() - 1);

                                                    count = 1;
                                                }

                                            }//for loop end
                                            if(count==0){
                                                if (progressDialog.isShowing()) {
                                                    progressDialog.dismiss();
                                                                 }
                                                Toast.makeText(getContext(), "No worker is available from your favourites", Toast.LENGTH_SHORT).show();
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

                //

                return true;
            }
        });

        recyclerView_s2.setLayoutManager(new LinearLayoutManager(view4.getContext()));
        return view4;

    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    //set profile image------------------

    public void setpic (int position){


        storageReference = FirebaseStorage.getInstance().getReference("images/"+arrContacts_search2.get(position).phone);
        //Toast.makeText(getContext(),arrContacts_search2.get(position).phone + " 1",Toast.LENGTH_LONG).show();

        try {
            File localfile = File.createTempFile("tempFile",".jpg");
            storageReference.getFile(localfile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {

                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            //Toast.makeText(getContext(),position+" 2",Toast.LENGTH_LONG).show();
                            bitmap = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                            //Toast.makeText(getContext(),position+" 3",Toast.LENGTH_LONG).show();
                            arrContacts_search2.set(position, new ContactModel(bitmap, arrContacts_search2.get(position).name, arrContacts_search2.get(position).address,arrContacts_search2.get(position).category, arrContacts_search2.get(position).description, arrContacts_search2.get(position).quantity,arrContacts_search2.get(position).phone, arrContacts_search2.get(position).key, 0,1,2022,arrContacts_search2.get(position).rating,arrContacts_search2.get(position).noOfRatings));
//                            Toast.makeText(getContext(),"noOfRatings3"+arrContacts_search2.get(position).noOfRatings, Toast.LENGTH_SHORT).show();
                            //Toast.makeText(getContext(),position+" 4",Toast.LENGTH_LONG).show();
                            adapter_s2.notifyItemChanged(position);
                            //Toast.makeText(getContext(),position+" 5",Toast.LENGTH_LONG).show();


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getContext(), "failed to retreive", Toast.LENGTH_SHORT).show();

                }
            });
        } catch (
                IOException e) {
            e.printStackTrace();
        }
        //
    }

}
