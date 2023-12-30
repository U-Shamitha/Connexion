package com.example.srp_demo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RecyclerContactAdapter extends RecyclerView.Adapter<RecyclerContactAdapter.ViewHolder> {

    private static final int REQUEST_PHONE_CALL=1;


    //checking for unsubscription
    int count_category;
    int count_address;

    //shared preference for fetching user deatils
    Context context;
    Activity activity;
    ArrayList<ContactModel>  arrContacts;
    RecyclerContactAdapter(Context context, ArrayList<ContactModel> arrContacts, Activity activity){

        this.context=context;
        this.arrContacts = arrContacts;
        this.activity=activity;

    }

    String address;
    String category;

    Float prevrating= Float.valueOf(0);
    int noOfRatingsf = 0;
    float ratingf = 0;
    int exist=0;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.contact_row,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;

    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        ContactModel model = (ContactModel) arrContacts.get(position);

        holder.img.setImageBitmap(arrContacts.get(position).img1);
        holder.name.setText(arrContacts.get(position).name);
        holder.address.setText(arrContacts.get(position).address);
        holder.category.setText(arrContacts.get(position).category);
        holder.description.setText(arrContacts.get(position).description);
        holder.quantity.setText(arrContacts.get(position).quantity);
        holder.contact.setText(arrContacts.get(position).phone);
//        Toast.makeText(context, "noOfRat"+arrContacts.get(position).noOfRatings, Toast.LENGTH_SHORT).show();
        //make menu option visible in search page
        if(arrContacts.get(position).menu==1){
            holder.menu.setVisibility(View.VISIBLE);
        }

        //for card update
        holder.llRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(arrContacts.get(position).clickable==1) {
                    Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.add_update_lay);

                    EditText edtName = dialog.findViewById(R.id.edtName);
                    EditText edtDes = dialog.findViewById(R.id.edtDes);
                    EditText edtQuan = dialog.findViewById(R.id.edtQuan);
                    EditText edtPhone = dialog.findViewById(R.id.edtPhone);
                    Button btnAction = dialog.findViewById(R.id.btnAction);
                    TextView txtTitle = dialog.findViewById(R.id.txtTitle);

                    edtPhone.setClickable(false);
                    edtPhone.setEnabled(false);

                    txtTitle.setText("Update Details");

                    btnAction.setText("UPDATE");

//
                    //category autocomplete text
                    AutoCompleteTextView catAtv;
                    ArrayAdapter<String> arrayAdapterC = new ArrayAdapter<String>(activity, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,context.getResources().getStringArray(R.array.category));
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
                    ArrayAdapter<String> arrayAdapterL = new ArrayAdapter<String>(activity, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,context.getResources().getStringArray(R.array.location));
                    locAtv = (AutoCompleteTextView) dialog.findViewById(R.id.locAtv);
                    locAtv.setAdapter(arrayAdapterL);
                    locAtv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            locAtv.showDropDown();

                        }
                    });

                    address=(arrContacts.get(position)).address;
                    //Toast.makeText(context, address, Toast.LENGTH_SHORT).show();
                    category=(arrContacts.get(position)).category;
                    edtName.setText((arrContacts.get(position)).name);
                    catAtv.setText(category);
                    locAtv.setText(address);

                    catAtv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            category = (String) catAtv.getAdapter().getItem(i).toString();
                            //Toast.makeText(context, category, Toast.LENGTH_SHORT).show();
                        }
                    });

                    locAtv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            address = (String) locAtv.getAdapter().getItem(i).toString();
                            //Toast.makeText(context, address, Toast.LENGTH_SHORT).show();
                        }
                    });
//                    catAtv.setCompletionHint(arrContacts.get(position).category);
//                    locAtv.setCompletionHint(arrContacts.get(position).address);
//                    address_a.setSelection(adapter_a1.getPosition(arrContacts.get(position).address));
//                    category_a.setSelection(adapter_a2.getPosition(arrContacts.get(position).category));
                    edtDes.setText((arrContacts.get(position)).description);
                    edtQuan.setText((arrContacts.get(position)).quantity);
                    edtPhone.setText((arrContacts.get(position)).phone);

                    btnAction.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            String name = "", addrs = "", cat = "", des = "", quan = "", phone = "";


                            if (!edtName.getText().toString().equals("")) {
                                name = edtName.getText().toString();
                            } else {
                                Toast.makeText(context, "Please enter name", Toast.LENGTH_SHORT).show();
                            }
                            if (!address.toString().equals("") & address.equals(locAtv.getText().toString())) {
                                addrs = address.toString();
                            } else {
                                Toast.makeText(context, "Please select valid address", Toast.LENGTH_SHORT).show();
                            }
                            if (!category.toString().equals("") & category.equals(catAtv.getText().toString())) {
                                cat = category.toString();
                            } else {
                                Toast.makeText(context, "Please select valid category", Toast.LENGTH_SHORT).show();
                            }
                            if (!edtDes.getText().toString().equals("")) {
                                des = edtDes.getText().toString();
                            } else {
                                Toast.makeText(context, "Please enter description", Toast.LENGTH_SHORT).show();
                            }
                            if (!edtQuan.getText().toString().equals("")) {
                                quan = edtQuan.getText().toString();
                            } else {
                                Toast.makeText(context, "Please enter quantity", Toast.LENGTH_SHORT).show();
                            }
                            if (!edtPhone.getText().toString().equals("")) {
                                phone = edtPhone.getText().toString();
                            } else {
                                Toast.makeText(context, "Please enter contact number", Toast.LENGTH_SHORT).show();
                            }
                            if (!edtName.getText().toString().equals("") & !address.equals("") & address.equals(locAtv.getText().toString()) &
                                    !category.equals("") & category.equals(catAtv.getText().toString()) & !edtDes.getText().toString().equals("") &
                                    !edtQuan.getText().toString().equals("") & !edtPhone.getText().toString().equals("")
                            ) {

                                //update to firebase
                                Map<String, Object> map = new HashMap<>();
                                map.put("address", addrs.toString());
                                map.put("category", cat.toString());
                                map.put("description", des.toString());
                                map.put("name", name.toString());
                                map.put("phone", phone.toString());
                                //map.put("address",contact.img);
                                map.put("quantity", quan.toString());
                                map.put("key", arrContacts.get(position).key.toString());
                               long timeStamp=-1*System.currentTimeMillis();
                                map.put("timeStamp",timeStamp);
                                map.put("noOfRatings",arrContacts.get(position).noOfRatings);
                                map.put("rating",arrContacts.get(position).rating);
                                //map.put("address",contact.clickable);

                                FirebaseDatabase db = FirebaseDatabase.getInstance();

                                //for subscription and unsubscription
                                String ucat=arrContacts.get(position).category;
                                String uadrs=arrContacts.get(position).address;

                                SharedPreferences pref = context.getSharedPreferences("MyPref", 0); // 0 - for private mode
                                String loginpno = pref.getString("Pnol", null);

                                //checking for unsubscription
                                //unsub
                                DatabaseReference databaseReference2 = db.getReference("ContactModelW");
                                Query query = databaseReference2.orderByChild("phone").equalTo(loginpno);

                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            //Toast.makeText(context, "dataSnapshot exists", Toast.LENGTH_SHORT).show();
                                            for (DataSnapshot user : dataSnapshot.getChildren()) {
                                                ContactModel contact = user.getValue(ContactModel.class);
                                                if(ucat.equals(contact.category)){
                                                    count_category = count_category +1;
                                                }
                                                if(uadrs.equals(contact.address)){
                                                    count_address = count_address +1;
                                                }
                                            }
//                                            Toast.makeText(context, "cat:"+count_category, Toast.LENGTH_SHORT).show();
//                                            Toast.makeText(context, "adrs"+count_address, Toast.LENGTH_SHORT).show();
                                            if(count_category==0 && !arrContacts.get(position).category.equals(ucat)){
                                                FirebaseMessaging.getInstance().unsubscribeFromTopic(ucat);
//                                                Toast.makeText(context, "unsubscribed "+ucat, Toast.LENGTH_SHORT).show();
                                            }
                                            if(count_address==0 && !arrContacts.get(position).address.equals(uadrs)) {
                                                FirebaseMessaging.getInstance().unsubscribeFromTopic(uadrs);
//                                                Toast.makeText(context, "unsubscribed "+uadrs, Toast.LENGTH_SHORT).show();
                                            }
                                            count_category=0;
                                            count_address=0;
                                        } else {
//                                            Toast.makeText(context, "not found", Toast.LENGTH_LONG).show();
                                            if(!arrContacts.get(position).category.equals(ucat)) {
                                                FirebaseMessaging.getInstance().unsubscribeFromTopic(ucat);
//                                                Toast.makeText(context, "unsubscribed " + ucat, Toast.LENGTH_SHORT).show();
                                            }
                                            if(!arrContacts.get(position).address.equals(uadrs)) {
                                                FirebaseMessaging.getInstance().unsubscribeFromTopic(uadrs);
//                                                Toast.makeText(context, "unsubscribed " + uadrs, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                //update to firebase
                                DatabaseReference databaseReference = db.getReference(ContactModel.class.getSimpleName());
                                databaseReference
                                        .child(arrContacts.get(position).key)
                                        .updateChildren(map);

                                arrContacts.set(position, new ContactModel(arrContacts.get(position).img1, name, addrs, cat, des, quan, phone, (arrContacts.get(position)).key, 1,0,timeStamp,arrContacts.get(position).rating,arrContacts.get(position).noOfRatings));

                                notifyItemChanged(position);

                                //subscription
                                FirebaseMessaging.getInstance().subscribeToTopic(arrContacts.get(position).category);
                                FirebaseMessaging.getInstance().subscribeToTopic(arrContacts.get(position).address);

                                //Toast.makeText(context.getApplicationContext(), "Subscribed to "+arrContacts.get(position).category+" and "+arrContacts.get(position).address,Toast.LENGTH_LONG).show();
                                //Toast.makeText(getContext(), "Subscribed to all",Toast.LENGTH_LONG).show();



                                FcmNotificationsSender notificationsSender = new FcmNotificationsSender("/topics/"+arrContacts.get(position).category+"p","New "+arrContacts.get(position).address+" "+arrContacts.get(position).category+" work provider","Click here to know more",activity);
                                notificationsSender.SendNotifications();
                                FcmNotificationsSender notificationsSender2 = new FcmNotificationsSender("/topics/"+arrContacts.get(position).address+"p","New "+arrContacts.get(position).address+" "+arrContacts.get(position).category+" work provider","Click here to know more",  activity );
                                notificationsSender2.SendNotifications();


//                                FcmNotificationsSender notificationsSender1 = new FcmNotificationsSender("/topics/all","New any worker","Click here to know more", context.getApplicationContext(), dialog.getOwnerActivity());
//                                //notificationsSender1.SendNotifications();
                               // Toast.makeText(context.getApplicationContext(), "after sendNotifications2", Toast.LENGTH_SHORT).show();


                                dialog.dismiss();
                                //Toast.makeText(context.getApplicationContext(), "dialog dismissed", Toast.LENGTH_SHORT).show();

                            }

                        }
                    });
                    dialog.show();
                }
            }
        });


        //for card delete---------------------------------------------------------------------------
        holder.llRow.setOnLongClickListener(new View.OnLongClickListener(){

            @Override
            public boolean onLongClick(View view) {

                if(arrContacts.get(position).clickable==1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context)
                            .setTitle("Delete Announcement")
                            .setMessage("            Are you sure to delete?")
                            .setIcon(R.drawable.ic_baseline_delete_24)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    String adrs=arrContacts.get(position).address;
                                    String cat=arrContacts.get(position).category;

                                    SharedPreferences pref = context.getSharedPreferences("MyPref", 0); // 0 - for private mode
                                    String loginpno = pref.getString("Pnol", null);
                                    //Toast.makeText(context, loginpno, Toast.LENGTH_SHORT).show();

                                    FirebaseDatabase db = FirebaseDatabase.getInstance();

                                    DatabaseReference databaseReference2 = db.getReference("ContactModel");
                                    Query query = databaseReference2.orderByChild("phone").equalTo(loginpno);

                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                //Toast.makeText(context, "dataSnapshot exists", Toast.LENGTH_SHORT).show();
                                                for (DataSnapshot user : dataSnapshot.getChildren()) {
                                                    ContactModel contact = user.getValue(ContactModel.class);
                                                    if(cat.equals(contact.category)){
                                                        count_category = count_category +1;
                                                    }
                                                    if(adrs.equals(contact.address)){
                                                        count_address = count_address +1;
                                                    }
                                                }
//                                                Toast.makeText(context, "cat:"+count_category, Toast.LENGTH_SHORT).show();
//                                                Toast.makeText(context, "adrs"+count_address, Toast.LENGTH_SHORT).show();
                                                if(count_category==0){
                                                    FirebaseMessaging.getInstance().unsubscribeFromTopic(cat);
//                                                    Toast.makeText(context, "unsubscribed "+cat, Toast.LENGTH_SHORT).show();
                                                }
                                                if(count_address==0) {
                                                    FirebaseMessaging.getInstance().unsubscribeFromTopic(adrs);
//                                                    Toast.makeText(context, "unsubscribed "+adrs, Toast.LENGTH_SHORT).show();
                                                }
                                                count_category=0;
                                                count_address=0;
                                            } else {
                                                //Toast.makeText(context, "not found", Toast.LENGTH_LONG).show();
                                                FirebaseMessaging.getInstance().unsubscribeFromTopic(cat);
                                                //Toast.makeText(context, "unsubscribed "+cat, Toast.LENGTH_SHORT).show();
                                                FirebaseMessaging.getInstance().unsubscribeFromTopic(adrs);
                                                //Toast.makeText(context, "unsubscribed "+adrs, Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });


                                    DatabaseReference databaseReference = db.getReference(ContactModel.class.getSimpleName());
                                    databaseReference
                                            .child(arrContacts.get(position).key)
                                            .removeValue();

                                    arrContacts.remove(position);
                                    notifyItemRemoved(position);
                                    //Toast.makeText(context, "removed", Toast.LENGTH_SHORT).show();







                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                    builder.show();

                }
                //Toast.makeText(context, ""+ arrContacts.get(position).timeStamp, Toast.LENGTH_SHORT).show();

                if(arrContacts.get(position).timeStamp==2022){
                   // Toast.makeText(context, "in l click", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(context)
                            .setTitle("Remove from favourites")
                            .setMessage("            Are you sure to remove?")
                            .setIcon(R.drawable.ic_baseline_delete_24)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    String favohno=arrContacts.get(position).phone;;

                                    SharedPreferences pref = context.getSharedPreferences("MyPref", 0); // 0 - for private mode
                                    String loginpno = pref.getString("Pnol", null);
                                    //Toast.makeText(context, loginpno, Toast.LENGTH_SHORT).show();

                                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                    DatabaseReference databaseReference = firebaseDatabase.getReference("Favourites/"+loginpno);
                                    databaseReference.child(favohno).removeValue();

                                    arrContacts.remove(position);
                                    notifyItemRemoved(position);
                                    //Toast.makeText(context, "removed", Toast.LENGTH_SHORT).show();


                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                    builder.show();

                }

                return true;
            }
        });

        //for card options menu---------------------------------------------------------------------

        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(arrContacts.get(position).clickable==0) {

                    //create a popup menu
                    PopupMenu popupMenu = new PopupMenu(context, holder.menu);
                    //inflating menu from xml resource
                    popupMenu.inflate(R.menu.card_menu);
                    //adding click listener
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {

                                case R.id.Call:
                                    //call function
                                    onPhnClick(arrContacts.get(position).phone);
                                    return true;

                                case R.id.Message:
                                    Intent msgIntent = new Intent(Intent.ACTION_VIEW);
                                    msgIntent.setData(Uri.parse("sms:"+arrContacts.get(position).phone));
                                    context.startActivity(msgIntent);
                                    return true;

                                case R.id.addToCon:
                                    Intent contactIntent = new Intent(ContactsContract.Intents.Insert.ACTION);
                                    contactIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                                    contactIntent.putExtra(ContactsContract.Intents.Insert.NAME,arrContacts.get(position).name);
                                    contactIntent.putExtra(ContactsContract.Intents.Insert.PHONE,arrContacts.get(position).phone);
                                    context.startActivity(contactIntent);
                                    return true;

                                case R.id.addToFav:
                                    SharedPreferences pref = context.getSharedPreferences("MyPref", 0); // 0 - for private mode
                                    SharedPreferences.Editor editor = pref.edit();
                                    String loginpno = pref.getString("Pnol", null);
//                                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
//                                    DatabaseReference databaseReference = firebaseDatabase.getReference("Favoutires/"+loginpno);
//                                    Favourites df=new Favourites(loginpno);
//                                    df.add(df);
////                                    databaseReference.push().setValue(arrContacts.get(position).phone);
                                    backend bufav = new backend(loginpno);
                                    //Favourites favs = new Favourites((String)arrContacts.get(position).phone);
                                    bufav.addFav((String)arrContacts.get(position).phone);
                                    return true;



                                case R.id.StarRating:
                                    RatingBar ratingBar;
                                    Button submit;
                                    Dialog dialog = new Dialog(activity);
                                    dialog.setContentView(R.layout.rating);
                                    ratingBar=(RatingBar) dialog.findViewById(R.id.starRating);
                                    submit=(Button) dialog.findViewById(R.id.submit);

                                    pref = context.getSharedPreferences("MyPref", 0); // 0 - for private mode
                                    loginpno = pref.getString("Pnol", null);


                                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                    DatabaseReference databaseReference = firebaseDatabase.getReference("Ratings/"+loginpno);
                                    Query query = databaseReference;
                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            Toast.makeText(context, ""+snapshot.getValue(), Toast.LENGTH_SHORT).show();
                                            prevrating = snapshot.child(arrContacts.get(position).phone).getValue(Float.class);
                                            prevrating=prevrating==null?0:prevrating;
                                            Toast.makeText(context, "prevrating"+prevrating, Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                    databaseReference = firebaseDatabase.getReference(GotRatings.class.getSimpleName());
//                                    Map<String, Object> map = new HashMap<>();
//                                    map.put("phone", arrContacts.get(position).phone);
//                                    map.put("address", arrContacts.get(position).address);
//                                    map.put("category", arrContacts.get(position).category);
//                                    //map.put("address",contact.img);
//                                    map.put("noOfRatings",arrContacts.get(position).noOfRatings);
//                                    map.put("rating",arrContacts.get(position).rating);
//                                    databaseReference.child(arrContacts.get(position).phone).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void unused) {
//                                            Toast.makeText(context, "pushed", Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
                                    exist=0;
                                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot user: snapshot.getChildren()) {
                                               // GotRatings gRating =user.getValue(GotRatings.class);
                                                if(user.getKey().equals(arrContacts.get(position).phone)){
                                                    exist=1;
                                                    Toast.makeText(context, "yes", Toast.LENGTH_SHORT).show();
                                                    noOfRatingsf=user.child("noOfRatings").getValue(Integer.class);
                                                    ratingf=user.child("rating").getValue(Float.class);


                                                    //Toast.makeText(context, ""+snapshot.getValue(), Toast.LENGTH_SHORT).show();
                                                    Toast.makeText(context, "phone"+arrContacts.get(position).phone, Toast.LENGTH_SHORT).show();
//                                                    Toast.makeText(context, "noOfRat"+noOfRatingsf, Toast.LENGTH_SHORT).show();
                                                    Toast.makeText(context, "Rat"+ratingf, Toast.LENGTH_SHORT).show();
                                                }
                                                //Toast.makeText(context, ""+user, Toast.LENGTH_SHORT).show();

                                            }
                                            if(exist==0){
                                                //add with default values
                                                Map<String, Object> map = new HashMap<>();
                                                map.put("phone", arrContacts.get(position).phone);
                                                map.put("address", arrContacts.get(position).address);
                                                map.put("category", arrContacts.get(position).category);
                                                //map.put("address",contact.img);
                                                map.put("noOfRatings", 0);
                                                map.put("rating", 0);
                                                Toast.makeText(context, "map", Toast.LENGTH_SHORT).show();
                                                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                                DatabaseReference databaseReference = firebaseDatabase.getReference("GotRatings");
                                                databaseReference.child(arrContacts.get(position).phone).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(context, "added to firebase", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }

//                                            GotRatings gRating = (GotRatings) snapshot.getValue(GotRatings.class);
//                                            int noOfRatings = 0;
//                                            noOfRatings=gRating==null?0:gRating.noOfRatings;
//                                            float rating = 0;
//                                            rating=gRating==null?0:gRating.rating;
//
//                                            Toast.makeText(context, ""+snapshot.getValue(), Toast.LENGTH_SHORT).show();
//                                            Toast.makeText(context, "noOfRat"+gRating.noOfRatings, Toast.LENGTH_SHORT).show();
//                                            Toast.makeText(context, "Rat"+gRating.rating, Toast.LENGTH_SHORT).show();
//                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                    submit.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            float rating = ratingBar.getRating();
                                            Toast.makeText(context, "" + rating, Toast.LENGTH_SHORT).show();

                                            //store in user given ratings
                                            backend bRat = new backend(loginpno, "Ratings");
                                            bRat.addRat((String) arrContacts.get(position).phone, rating);
                                            float avgRating = 0;

                                            // no ratings by anyone
                                            if (noOfRatingsf == 0) {
                                                avgRating = rating;
                                                Toast.makeText(context, "1=" + avgRating, Toast.LENGTH_SHORT).show();
                                            }

                                            //no previous ratings from this user
                                            else if (prevrating == Float.valueOf(0) || prevrating.isNaN() || prevrating.equals(null)) {
                                                avgRating = (ratingf + rating) / (noOfRatingsf + 1);
                                                Toast.makeText(context, "2=" + avgRating, Toast.LENGTH_SHORT).show();
                                            }

                                            //has previous ratings from this user
                                            else if (noOfRatingsf  >= 1) {
                                                ratingf = ((ratingf * noOfRatingsf) - prevrating) / (noOfRatingsf - 1);
                                                noOfRatingsf-=1;
                                                avgRating = ((ratingf * noOfRatingsf) + rating) / (noOfRatingsf + 1);
                                                Toast.makeText(context, "3=" + avgRating, Toast.LENGTH_SHORT).show();
//
//                                            }
                                                Toast.makeText(context, "after conditions", Toast.LENGTH_SHORT).show();
                                                noOfRatingsf+=1;
                                                Toast.makeText(context, "map before", Toast.LENGTH_SHORT).show();
                                                //update to firebase
                                                Map<String, Object> map = new HashMap<>();
                                                map.put("phone", arrContacts.get(position).phone);
                                                map.put("address", arrContacts.get(position).address);
                                                map.put("category", arrContacts.get(position).category);
                                                //map.put("address",contact.img);
                                                map.put("noOfRatings", noOfRatingsf);
                                                map.put("rating", avgRating);
                                                Toast.makeText(context, "map", Toast.LENGTH_SHORT).show();
                                                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                                DatabaseReference databaseReference = firebaseDatabase.getReference("GotRatings");
                                                databaseReference.child(arrContacts.get(position).phone).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(context, "updated to firebase", Toast.LENGTH_SHORT).show();
                                                    }
                                                });



                                                dialog.dismiss();
                                            }
                                        }

                                    });


                                    dialog.show();
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    //display the popup
                    popupMenu.show();

                }
            }
        });









    }

    @Override
    public int getItemCount() {
        return arrContacts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView name,address,category,description,quantity,contact;
        ImageView img;
        Button menu;
        LinearLayout llRow;

        public ViewHolder(View itemView){
            super(itemView);

            name = itemView.findViewById(R.id.WpNamei);
            address = itemView.findViewById(R.id.WpAddi);
            category = itemView.findViewById(R.id.WCati);
            description = itemView.findViewById(R.id.WDesi);
            quantity = itemView.findViewById(R.id.WQuai);
            contact = itemView.findViewById(R.id.phonei);
            img = itemView.findViewById(R.id.pimg);
            menu = itemView.findViewById(R.id.menu);
            llRow = itemView.findViewById(R.id.llRow);

        }
    }
    //for search call
    public void onPhnClick(String phno){

        //Create builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Call");
        builder.setIcon(R.drawable.ic_baseline_local_phone_24);
        builder.setMessage(phno);
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", (DialogInterface.OnClickListener)(dialog, which) ->{
            if(ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions((Activity) context,new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
            }
            if(ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)== PackageManager.PERMISSION_GRANTED) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phno));
                context.startActivity(callIntent);
            }
        });
        builder.setNegativeButton("No",(DialogInterface.OnClickListener)(dailog,which)->{
            dailog.cancel();
        });
        //create alert dailog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}

