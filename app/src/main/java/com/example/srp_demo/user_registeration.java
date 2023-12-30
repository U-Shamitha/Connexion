package com.example.srp_demo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class user_registeration extends AppCompatActivity {

    EditText reg_name, reg_pwd, reg_pno;
    Button register_user,loginf;
    BackendUser bu;
    ImageView profilepic;
    public Uri imageUri;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    Integer isImageUploaded=0;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    FirebaseAuth mAuth;
    CountryCodePicker ccp;

    public static String nameS, phnoS, pwdS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registeration);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mAuth = FirebaseAuth.getInstance();

        reg_name = (EditText) findViewById(R.id.reg_name);
        reg_pwd = (EditText) findViewById(R.id.reg_pwd);
        reg_pno = (EditText) findViewById(R.id.reg_pno);
        ccp=(CountryCodePicker)findViewById(R.id.ccp) ;
        ccp.registerCarrierNumberEditText(reg_pno);
        profilepic = (ImageView) findViewById(R.id.profilepic);

        register_user = (Button) findViewById(R.id.user_reg) ;
        loginf = (Button) findViewById(R.id.loginf) ;
        bu = new BackendUser();

        //image upload firebase storage
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference() ;


        //auto login
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();


        String loginpno = pref.getString("Pnol", null);
        String loginpass = pref.getString("Pwdl", null);

//        editor.clear();
//        editor.commit();
//
//
//        Toast.makeText(user_registeration.this, loginpass+" "+loginpno, Toast.LENGTH_SHORT).show();


        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("User_details");
        Query query = databaseReference.orderByChild("user_pno").equalTo(loginpno);




        if(loginpno!=null && loginpass!=null ){
            Intent intent = new Intent(user_registeration.this, MainActivity.class);
            startActivity(intent);
        }

        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePic();
            }
        });

        loginf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(user_registeration.this,login.class);
                startActivity(intent);
            }
        });

        register_user.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if(reg_name.getText().toString().equals("")){
                    Toast.makeText(user_registeration.this,"enter name",Toast.LENGTH_LONG).show();
                }
                else if(reg_pno.getText().toString().equals("") || ccp.getFullNumberWithPlus().substring(3).length()!=10){
                    Toast.makeText(user_registeration.this,"enter valid phone",Toast.LENGTH_LONG).show();
                }
                else if(reg_pwd.getText().toString().equals("")){
                    Toast.makeText(user_registeration.this,"set password",Toast.LENGTH_LONG).show();
                }
                else if(isImageUploaded == 0){
                    Toast.makeText(user_registeration.this,"select a profile picture",Toast.LENGTH_LONG).show();
                }

                else{
                    nameS = reg_name.getText().toString();
                    pwdS = reg_pwd.getText().toString();
                    phnoS = ccp.getFullNumberWithPlus().substring(3).toString().trim();
                    //otpsend();
                    uploadPicture(reg_pno.getText().toString().trim());
                }
            }
        });
    }

    private void otpsend() {
        //Toast.makeText(user_registeration.this,"entered fun",Toast.LENGTH_LONG).show();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.d("UserRegistration", e.getMessage());
                Toast.makeText(user_registeration.this,e.getMessage(),Toast.LENGTH_LONG).show();
                Toast.makeText(user_registeration.this,"failed",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                Intent intent = new Intent(user_registeration.this,manageotp.class);
                intent.putExtra("mobile",ccp.getFullNumberWithPlus().trim());
                intent.putExtra("verificationId",verificationId);
                startActivity(intent);

            }
        };

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(ccp.getFullNumberWithPlus().trim())       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void choosePic(){
        Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Toast.makeText(user_registeration.this,"requestCode:" + requestCode + "")
        if(requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imageUri = data.getData();
            profilepic.setImageURI(imageUri);
            isImageUploaded = 1;
        }
    }

    private void uploadPicture(String pno){

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading details....");
        pd.show();

        StorageReference stf = storageReference.child("images/" +ccp.getFullNumberWithPlus().substring(3) );

        stf.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Snackbar.make(findViewById(android.R.id.content),"image uploaded ",Snackbar.LENGTH_LONG).show();
                        otpsend();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(user_registeration.this,"Failed to upload",Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progressPercent = (100.00 * snapshot.getBytesTransferred()/ snapshot.getTotalByteCount());
                        pd.setMessage("Progress: " + (int) progressPercent + "%");
                    }
                });
    }

}