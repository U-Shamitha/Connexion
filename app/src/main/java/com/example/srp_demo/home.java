package com.example.srp_demo;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.inputmethod.InputConnectionCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class home extends Fragment {

    private static final int REQUEST_PHONE_CALL = 1;
    View view;
    Button button;
    EditText topic;
    AutoCompleteTextView atv;
    ArrayList<ContactModelW> arrWContacts = new ArrayList<>();
    ArrayList<ContactModel> arrContacts = new ArrayList<>();
    TextView WpNamei, WpAddi, WCati, WDesi, WQuai, phonei;
    ImageView rpic;
    TextView WpNamei1, WpAddi1, WCati1, WDesi1, WQuai1, phonei1;
    ImageView rpic1;

    TextView WpNamei3, WpAddi3, WCati3, WDesi3, WQuai3, phonei3;
    ImageView rpic3;
    TextView WpNamei4, WpAddi4, WCati4, WDesi4, WQuai4, phonei4;
    ImageView rpic4;

    LinearLayout card1, card2, card3, card4, noWorkSugg_ll, noWorkerSugg_ll;

    StorageReference storageReference;
    Bitmap bitmap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);

        int width = getScreenWidth(getActivity());
        LinearLayout ll = view.findViewById(R.id.ll);
        int childCount = ll.getChildCount();

        for (int i = 0; i < childCount; i++) {
            LinearLayout c = (LinearLayout) ll.getChildAt(i);
            c.setMinimumWidth((int) width);
            c.getChildAt(0).setMinimumWidth((int) (width * 0.8));

        }

        LinearLayout ll1 = view.findViewById(R.id.ll1);
        int childCount1 = ll1.getChildCount();
        for (int i = 0; i < childCount1; i++) {
            LinearLayout c = (LinearLayout) ll1.getChildAt(i);
            c.setMinimumWidth(width);
            c.getChildAt(0).setMinimumWidth((int) (width * 0.8));
        }


        topic = view.findViewById(R.id.topic);
        button = view.findViewById(R.id.unsubTopic);

        WpNamei = (TextView) view.findViewById(R.id.WpNamei);
        WCati = (TextView) view.findViewById(R.id.WCati);
        WpAddi = (TextView) view.findViewById(R.id.WpAddi);
        WDesi = (TextView) view.findViewById(R.id.WDesi);
        WQuai = (TextView) view.findViewById(R.id.WQuai);
        phonei = (TextView) view.findViewById(R.id.phonei);
        rpic = (ImageView) view.findViewById(R.id.rpic);

        WpNamei1 = (TextView) view.findViewById(R.id.WpNamei1);
        WCati1 = (TextView) view.findViewById(R.id.WCati1);
        WpAddi1 = (TextView) view.findViewById(R.id.WpAddi1);
        WDesi1 = (TextView) view.findViewById(R.id.WDesi1);
        WQuai1 = (TextView) view.findViewById(R.id.WQuai1);
        phonei1 = (TextView) view.findViewById(R.id.phonei1);
        rpic1 = (ImageView) view.findViewById(R.id.rpic1);

        WpNamei3 = (TextView) view.findViewById(R.id.WpNamei3);
        WCati3 = (TextView) view.findViewById(R.id.WCati3);
        WpAddi3 = (TextView) view.findViewById(R.id.WpAddi3);
        WDesi3 = (TextView) view.findViewById(R.id.WDesi3);
        WQuai3 = (TextView) view.findViewById(R.id.WQuai3);
        phonei3 = (TextView) view.findViewById(R.id.phonei3);
        rpic3 = (ImageView) view.findViewById(R.id.rpic3);

        WpNamei4 = (TextView) view.findViewById(R.id.WpNamei4);
        WCati4 = (TextView) view.findViewById(R.id.WCati4);
        WpAddi4 = (TextView) view.findViewById(R.id.WpAddi4);
        WDesi4 = (TextView) view.findViewById(R.id.WDesi4);
        WQuai4 = (TextView) view.findViewById(R.id.WQuai4);
        phonei4 = (TextView) view.findViewById(R.id.phonei4);
        rpic4 = (ImageView) view.findViewById(R.id.rpic4);

        topic.setVisibility(View.INVISIBLE);
        button.setVisibility(View.INVISIBLE);

        card1 = view.findViewById(R.id.card1);
        card2 = view.findViewById(R.id.card2);
        card3 = view.findViewById(R.id.card3);
        card4 = view.findViewById(R.id.card4);
        noWorkSugg_ll = view.findViewById(R.id.noWorkSugg_ll);
        noWorkerSugg_ll = view.findViewById(R.id.noWorkerSugg_ll);

        card1.setVisibility(View.GONE);
        card2.setVisibility(View.GONE);
        card3.setVisibility(View.GONE);
        card4.setVisibility(View.GONE);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String[] all = {"nellore", "ananthapur", "kadapa", "kurnool", "chithoor", "construction worker", "textile worker", "tailor", "plumber", "electrician"};
                for (String i : all) {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(i.toString());
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(i.toString() + "p");
                    //Toast.makeText(getContext(), "Unsbscribed all", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //auto login
        SharedPreferences pref = getContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();

        String loginpno = pref.getString("Pnol", null);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("ContactModelW");
        Query query = databaseReference.orderByChild("phone").equalTo(loginpno);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot user : dataSnapshot.getChildren()) {
                        ContactModelW contact1 = user.getValue(ContactModelW.class);

                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                        DatabaseReference databaseReference = firebaseDatabase.getReference("ContactModel");
                        Query query = databaseReference.orderByChild("category").equalTo(contact1.category);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot user : dataSnapshot.getChildren()) {
                                        ContactModelW contact = user.getValue(ContactModelW.class);
                                        if (!contact.phone.equals(loginpno)) {
                                            arrContacts.add(new ContactModel(MainActivity.bitmap_main, contact.name, contact.address, contact.category, contact.description, contact.quantity, contact.phone, contact.key, 0, 1, contact.timeStamp, contact.rating, contact.noOfRatings));

                                            //Toast.makeText(getContext(),contact.name,Toast.LENGTH_LONG).show();
                                            //Toast.makeText(getContext(),arrContacts.size()+" ",Toast.LENGTH_LONG).show();

                                            if (arrContacts.size() == 1) {
                                                noWorkSugg_ll.setVisibility(View.GONE);
                                                card1.setVisibility(View.VISIBLE);
                                                WpNamei.setText(contact.name);
                                                WpAddi.setText(contact.address);
                                                WCati.setText(contact.category);
                                                WDesi.setText(contact.description);
                                                WQuai.setText(contact.quantity);
                                                phonei.setText(contact.phone);
                                            }
                                            if (arrContacts.size() == 2) {
                                                card2.setVisibility(View.VISIBLE);
                                                WpNamei1.setText(contact.name);
                                                WpAddi1.setText(contact.address);
                                                WCati1.setText(contact.category);
                                                WDesi1.setText(contact.description);
                                                WQuai1.setText(contact.quantity);
                                                phonei1.setText(contact.phone);
                                            }

                                            setPpic(phonei, rpic);
                                            setPpic(phonei1, rpic1);
                                        }

                                    }
                                } else {
                                    //Toast.makeText(getContext(), "not found", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                } else {
                    //Toast.makeText(getContext(), "not found", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        FirebaseDatabase firebaseDatabase2 = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference2 = firebaseDatabase2.getReference("ContactModel");
        Query query2 = databaseReference2.orderByChild("phone").equalTo(loginpno);
        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot user : dataSnapshot.getChildren()) {
                        ContactModelW contact1 = user.getValue(ContactModelW.class);

                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                        DatabaseReference databaseReference = firebaseDatabase.getReference("ContactModelW");
                        Query query = databaseReference.orderByChild("category").equalTo(contact1.category);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot user : dataSnapshot.getChildren()) {
                                        ContactModelW contact = user.getValue(ContactModelW.class);
                                        if (!contact.phone.equals(loginpno)) {
                                            arrWContacts.add(new ContactModelW(MainActivity.bitmap_main, contact.name, contact.address, contact.category, contact.description, contact.quantity, contact.phone, contact.key, 0, 0, contact.timeStamp, contact.rating, contact.noOfRatings));


                                            if (arrWContacts.size() == 1) {
                                                noWorkerSugg_ll.setVisibility(View.GONE);
                                                card3.setVisibility(View.VISIBLE);
                                                WpNamei3.setText(contact.name);
                                                WpAddi3.setText(contact.address);
                                                WCati3.setText(contact.category);
                                                WDesi3.setText(contact.description);
                                                WQuai3.setText(contact.quantity);
                                                phonei3.setText(contact.phone);
                                            }
                                            if (arrWContacts.size() == 2) {
                                                card4.setVisibility(View.VISIBLE);
                                                WpNamei4.setText(contact.name);
                                                WpAddi4.setText(contact.address);
                                                WCati4.setText(contact.category);
                                                WDesi4.setText(contact.description);
                                                WQuai4.setText(contact.quantity);
                                                phonei4.setText(contact.phone);
                                            }

                                            setPpic(phonei3, rpic3);
                                            setPpic(phonei4, rpic4);
                                        }

                                    }
                                } else {
                                    //Toast.makeText(getContext(), "not found", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                } else {
                    //Toast.makeText(getContext(), "not found", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //on click listeners for description
        WDesi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDesClick(WDesi);
            }
        });
        WDesi1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDesClick(WDesi1);
            }
        });
        WDesi3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDesClick(WDesi3);
            }
        });
        WDesi4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDesClick(WDesi4);
            }
        });

        //on click listeners for phone numbers
        phonei.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPhnClick(phonei);
            }
        });
        phonei1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPhnClick(phonei1);
            }
        });
        phonei3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPhnClick(phonei3);
            }
        });
        phonei4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPhnClick(phonei4);
            }
        });


        return view;


    }


    //display description in dialog
    public void onDesClick(TextView tv) {
        //Create builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Description");
        builder.setMessage(tv.getText().toString());
        builder.setCancelable(true);
        //create alert dailog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //call to number
    public void onPhnClick(TextView tv) {

        //Create builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Call");
        builder.setIcon(R.drawable.ic_baseline_local_phone_24);
        builder.setMessage(tv.getText().toString());
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
            }
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                String phno = tv.getText().toString();
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phno));
                startActivity(callIntent);
            }
        });
        builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dailog, which) -> {
            dailog.cancel();
        });
        //create alert dailog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private int getScreenWidth(FragmentActivity activity) {
        WindowManager windowManager = (WindowManager) activity.getSystemService(activity.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;

    }

    private void setPpic(TextView phone, ImageView iv) {
        //Toast.makeText(getContext(),phone.getText(),Toast.LENGTH_LONG).show();
        storageReference = FirebaseStorage.getInstance().getReference("images/" + phone.getText());

        try {
            File localfile = File.createTempFile("tempFile", ".jpg");
            storageReference.getFile(localfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                    bitmap = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                    //Toast.makeText(getContext(),bitmap+" ",Toast.LENGTH_LONG).show();
                    iv.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
