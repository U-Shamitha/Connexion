package com.example.srp_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class login extends AppCompatActivity {

    EditText user_pno , user_pwd;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        user_pno = (EditText) findViewById(R.id.user_pno);
        user_pwd = (EditText) findViewById(R.id.user_pwd);

        String phone = user_pno.getText().toString();

        login = (Button) findViewById(R.id.login);

        //
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();

        String loginpno = pref.getString("Pnol", null);
        String loginpass = pref.getString("Pwdl", null);



        //




        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = firebaseDatabase.getReference("User_details");
                Query query = databaseReference.orderByChild("user_pno").equalTo(user_pno.getText().toString());

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            Boolean passwordMatched = false;

                            Log.d("Login", dataSnapshot.getChildren().toString());

                            for (DataSnapshot user : dataSnapshot.getChildren()) {
                                  User_details details = user.getValue(User_details.class);
                                if(details.user_pwd.equals(user_pwd.getText().toString())){
                                    passwordMatched=true;
                                    //Toast.makeText(login.this, "login successfully", Toast.LENGTH_LONG).show();
                                    editor.putString("Pnol", user_pno.getText().toString()); // Storing string
                                    editor.putString("Pwdl", user_pwd.getText().toString()); // Storing string
                                    editor.commit();
                                    Intent intent = new Intent(login.this,MainActivity.class);
                                    startActivity(intent);
                                }

                            }
                            if(!passwordMatched){
                                Toast.makeText(login.this, "password didn't match", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(login.this, "No user found with phone number "+user_pno.getText(), Toast.LENGTH_LONG).show();
//                            Toast.makeText(login.this, " "+user_pno.getText().toString(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }
}