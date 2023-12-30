package com.example.srp_demo;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.judemanutd.autostarter.AutoStartPermissionHelper;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {


    ImageButton btn_home, btn_register, btn_search, btn_announce, logout_btn;
    CardView logout_cv;
    AppCompatButton worker_search, announce_search;
    LinearLayout btnlyt,welcome,curve;
    TextView ul_announce,ul_worker;
    StorageReference storageReference;
    ProgressDialog progressDialog;
    ImageView ppic;
    TextView name_dis;
    public static Bitmap bitmap_main;
    public static InputMethodManager imm;
    public  static String Uname;


    //Firebase notification
    public static final String CHANNEL_ID="connexion";
    public static final String CHANNEL_ID1="connexion1";
    private static final String CHANNEL_NAME="connexion";
    private static final String CHANNEL_DES="connexion notifications";
    //public static String uid;
    public static String token;
    public static String usertoken;

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//        builder.setTitle("Allow app to autostart");
//        builder.setIcon(R.drawable.ic_baseline_local_phone_24);
//        builder.setMessage("If not allowed you won't get notifications when app is background");
//        builder.setCancelable(true);
//        builder.setPositiveButton("Allow", (DialogInterface.OnClickListener)(dialog, which) ->{
//            AutoStartPermissionHelper.Companion.getInstance().getAutoStartPermission(MainActivity.this,true,true);
//        });
//        builder.setNegativeButton("Deny",(DialogInterface.OnClickListener)(dailog,which)->{
//            dailog.cancel();
//        });
//        //create alert dailog
//        AlertDialog alertDialog = builder.create();
//        alertDialog.show();

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);



        //Firebase notification

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DES);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        //get token
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if(!task.isSuccessful()){
                          //  Log.w(TAG,"Fetching FCM registration token failed", task.getException());
                            //Toast.makeText(MainActivity.this, "Token is missing", Toast.LENGTH_SHORT).show();
                            return;
                        }

//                        //get new fcm registration token
//                        token = task.getResult();
//                        //Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
//                        FirebaseDatabase.getInstance().getReference().child(uid).child("token").setValue(token);
//                        //log and toast
////                        String msg = getString(R.string.msg_token_fmt, token);
////                        Log.w(TAG, msg);
////                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }

                });


        btn_home = (ImageButton) findViewById(R.id.btn_home);
        btn_register = (ImageButton) findViewById(R.id.btn_register);
        btn_search = (ImageButton) findViewById(R.id.btn_search);
        btn_announce = (ImageButton) findViewById(R.id.btn_announce);
        worker_search = (AppCompatButton) findViewById(R.id.worker_s) ;
        announce_search=(AppCompatButton) findViewById(R.id.announcements_s) ;
        btnlyt = (LinearLayout) findViewById(R.id.btnlyt);
        ul_announce=(TextView) findViewById(R.id.ul_announce);
        ul_worker=(TextView) findViewById(R.id.ul_worker);
        logout_cv = findViewById(R.id.logout_cv);
        logout_btn = findViewById(R.id.logout_btn);
        welcome = (LinearLayout) findViewById(R.id.welcome);
        curve = (LinearLayout) findViewById(R.id.curve);
        ppic=(ImageView) findViewById(R.id.ppic) ;
        name_dis = (TextView) findViewById(R.id.name_dis);

        announce_search.setVisibility(View.INVISIBLE);
        worker_search.setVisibility(View.INVISIBLE);
        btnlyt.setVisibility(View.INVISIBLE);
//        logout_btn.setVisibility(View.INVISIBLE);

        SharedPreferences pref = this.getSharedPreferences("MyPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();

        String loginpno = pref.getString("Pnol", null);

        storageReference = FirebaseStorage.getInstance().getReference("images/"+loginpno);

        try {
            File localfile = File.createTempFile("tempFile",".jpg");
            storageReference.getFile(localfile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                                bitmap_main = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                                ppic.setImageBitmap(bitmap_main);


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    //Toast.makeText(MainActivity.this, "failed to retreive", Toast.LENGTH_SHORT).show();

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("User_details");
        Query query = databaseReference.orderByChild("user_pno").equalTo(loginpno);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot user : dataSnapshot.getChildren()) {
                        User_details contact = user.getValue(User_details.class);
                        name_dis.setText(contact.user_name);
                        Uname = contact.user_name;
                    }
                } else {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        replaceFragment(new home());

        announce_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new search());
                //announce_search.setBackgroundColor(getResources().getColor(R.color.white));
                announce_search.setTextColor(getResources().getColor(R.color.final_green));
                ul_announce.setBackgroundColor(getResources().getColor(R.color.final_green));
                //worker_search.setBackgroundColor(getResources().getColor(com.google.android.material.R.color.design_dark_default_color_secondary));
                worker_search.setTextColor(getResources().getColor(R.color.white));
                ul_worker.setBackgroundColor(getResources().getColor(R.color.final_color));
            }
        });

        worker_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new search_worker());
                //worker_search.setBackgroundColor(getResources().getColor(R.color.white));
                worker_search.setTextColor(getResources().getColor(R.color.final_green));
                ul_worker.setBackgroundColor(getResources().getColor(R.color.final_green));
                //announce_search.setBackgroundColor(getResources().getColor(com.google.android.material.R.color.design_dark_default_color_secondary));
                announce_search.setTextColor(getResources().getColor(R.color.white));
                ul_announce.setBackgroundColor(getResources().getColor(R.color.final_color));

            }
        });

        btn_home.setBackgroundResource(R.drawable.circle_green);

        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                announce_search.setVisibility(View.INVISIBLE);
                worker_search.setVisibility(View.INVISIBLE);
                btnlyt.setVisibility(View.INVISIBLE);
                replaceFragment(new home());
                logout_cv.setVisibility(View.VISIBLE);
                welcome.setVisibility(View.VISIBLE);
                curve.setVisibility(View.VISIBLE);

                btn_home.setBackgroundResource(R.drawable.circle_green);
                btn_register.setBackgroundResource(R.drawable.circle_black);
                btn_announce.setBackgroundResource(R.drawable.circle_black);
                btn_search.setBackgroundResource(R.drawable.circle_black);


            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                announce_search.setVisibility(View.INVISIBLE);
                worker_search.setVisibility(View.INVISIBLE);
                btnlyt.setVisibility(View.INVISIBLE);
                logout_cv.setVisibility(View.INVISIBLE);
                replaceFragment(new register());
                welcome.setVisibility(View.INVISIBLE);
                curve.setVisibility(View.INVISIBLE);

                btn_register.setBackgroundResource(R.drawable.circle_green);
                btn_home.setBackgroundResource(R.drawable.circle_black);
                btn_announce.setBackgroundResource(R.drawable.circle_black);
                btn_search.setBackgroundResource(R.drawable.circle_black);

            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                announce_search.setVisibility(View.VISIBLE);
                worker_search.setVisibility(View.VISIBLE);
                btnlyt.setVisibility(View.VISIBLE);
                logout_cv.setVisibility(View.INVISIBLE);
                replaceFragment(new search());
                announce_search.setTextColor(getResources().getColor(R.color.final_green));
                ul_announce.setBackgroundColor(getResources().getColor(R.color.final_green));
                //worker_search.setBackgroundColor(getResources().getColor(com.google.android.material.R.color.design_dark_default_color_secondary));
                worker_search.setTextColor(getResources().getColor(R.color.white));
                ul_worker.setBackgroundColor(getResources().getColor(R.color.final_color));
                welcome.setVisibility(View.INVISIBLE);
                curve.setVisibility(View.INVISIBLE);

                btn_search.setBackgroundResource(R.drawable.circle_green);
                btn_home.setBackgroundResource(R.drawable.circle_black);
                btn_announce.setBackgroundResource(R.drawable.circle_black);
                btn_register.setBackgroundResource(R.drawable.circle_black);



            }
        });

        btn_announce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                announce_search.setVisibility(View.INVISIBLE);
                worker_search.setVisibility(View.INVISIBLE);
                btnlyt.setVisibility(View.INVISIBLE);
                logout_cv.setVisibility(View.INVISIBLE);
                replaceFragment(new announce());
                welcome.setVisibility(View.INVISIBLE);
                curve.setVisibility(View.INVISIBLE);

                btn_announce.setBackgroundResource(R.drawable.circle_green);
                btn_home.setBackgroundResource(R.drawable.circle_black);
                btn_register.setBackgroundResource(R.drawable.circle_black);
                btn_search.setBackgroundResource(R.drawable.circle_black);

            }
        });



        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.clear();
                editor.commit();

                Intent intent = new Intent(MainActivity.this,user_registeration.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

    }

    private void replaceFragment(Fragment fragment) {

        FragmentManager fragmentManager  = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment);
        fragmentTransaction.commit();
    }

}