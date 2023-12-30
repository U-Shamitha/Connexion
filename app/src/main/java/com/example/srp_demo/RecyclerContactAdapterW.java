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
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

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

public class RecyclerContactAdapterW extends RecyclerView.Adapter<RecyclerContactAdapterW.ViewHolder> {

    private static final int REQUEST_PHONE_CALL=1;

    //checking for unsubscription
    int count_category;
    int count_address;

    String address="";
    String category="";


    Context context;
    ArrayList<ContactModelW>  arrContacts;
    Activity activity;
    RecyclerContactAdapterW(Context context, ArrayList arrContacts ,Activity activity ){

        this.context=context;
        this.arrContacts = arrContacts;
        this.activity = activity;

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.contact_roww,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        ContactModelW model = (ContactModelW) arrContacts.get(position);

        holder.img.setImageBitmap(arrContacts.get(position).img1);
        holder.name.setText(arrContacts.get(position).name);
        holder.address.setText(arrContacts.get(position).address);
        holder.category.setText(arrContacts.get(position).category);
        holder.description.setText(arrContacts.get(position).description);
        holder.quantity.setText(arrContacts.get(position).quantity);
        holder.contact.setText(arrContacts.get(position).phone);
        //make menu option visible in search page
        if(arrContacts.get(position).menu==1){
            holder.menu.setVisibility(View.VISIBLE);
        }

        holder.llRow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                if(arrContacts.get(position).clickable==1) {

                    Dialog dialogw = new Dialog(context);
                    dialogw.setContentView(R.layout.add_updatew_lay);

                    EditText edtName = dialogw.findViewById(R.id.edtNameW);
                    EditText edtDes = dialogw.findViewById(R.id.edtDesW);
                    EditText edtQuan = dialogw.findViewById(R.id.edtQuanW);
                    EditText edtPhone = dialogw.findViewById(R.id.edtPhoneW);
                    Button btnAction = dialogw.findViewById(R.id.btnActionW);
                    TextView txtTitle = dialogw.findViewById(R.id.txtTitleW);

                    edtPhone.setClickable(false);
                    edtPhone.setEnabled(false);

                    txtTitle.setText("Update Details");

                    btnAction.setText("UPDATE");

//                    String [] values =  {"Select Location","nellore","ananthapur","kadapa","kurnool","chithoor"};
//                    Spinner address_a = (Spinner)dialogw.findViewById(R.id.spinner_locw);
//                    ArrayAdapter<String> adapter_a1 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, values);
//                    adapter_a1.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
//                    address_a.setAdapter(adapter_a1);
//
//                    String [] values2 =  {"Select Category","construction worker","textile worker","tailor","plumber","electrician"};
//                    Spinner category_a = (Spinner)dialogw.findViewById(R.id.spinner_catw);
//                    ArrayAdapter<String> adapter_a2 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, values2);
//                    adapter_a2.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
//                    category_a.setAdapter(adapter_a2);

                    //category autocomplete text
                    AutoCompleteTextView catAtvW;
                    ArrayAdapter<String> arrayAdapterC = new ArrayAdapter<String>(activity, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,context.getResources().getStringArray(R.array.category));
                    catAtvW = (AutoCompleteTextView) dialogw.findViewById(R.id.catAtvW);
                    catAtvW.setAdapter(arrayAdapterC);
                    catAtvW.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            catAtvW.showDropDown();

                        }
                    });

                    //category autocomplete text
                    AutoCompleteTextView locAtvW;
                    ArrayAdapter<String> arrayAdapterL = new ArrayAdapter<String>(activity, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,context.getResources().getStringArray(R.array.location));
                    locAtvW = (AutoCompleteTextView) dialogw.findViewById(R.id.locAtvW);
                    locAtvW.setAdapter(arrayAdapterL);
                    locAtvW.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            locAtvW.showDropDown();

                        }
                    });

                    address=(arrContacts.get(position)).address;
                    category=(arrContacts.get(position)).category;
                    edtName.setText((arrContacts.get(position)).name);
                    catAtvW.setText(category);
                    locAtvW.setText(address);

                    catAtvW.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            category = (String) catAtvW.getAdapter().getItem(i).toString();
                        }
                    });

                    locAtvW.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            address = (String) locAtvW.getAdapter().getItem(i).toString();
                        }
                    });
//                    locAtvW.setSelection(locAtvW.getPosition(arrContacts.get(position).address));
//                    catAtvW.setSelection(catAtvW.getPosition(arrContacts.get(position).category));
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
                            if (!address.toString().equals("") && address.equals(locAtvW.getText().toString())) {
                                addrs = address;
                            } else {
                                Toast.makeText(context, "Please select valid address", Toast.LENGTH_SHORT).show();
                            }
                            if (!category.toString().equals("") && category.equals(catAtvW.getText().toString())) {
                                cat = category;
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
                                Toast.makeText(context, "Please enter experience", Toast.LENGTH_SHORT).show();
                            }
                            if (!edtPhone.getText().toString().equals("")) {
                                phone = edtPhone.getText().toString();
                            } else {
                                Toast.makeText(context, "Please enter contact number", Toast.LENGTH_SHORT).show();
                            }
                            if (!edtName.getText().toString().equals("") & !address.equals("") & address.equals(locAtvW.getText().toString()) &
                                    !category.equals("") & category.equals(catAtvW.getText().toString()) & !edtDes.getText().toString().equals("") &
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
                                //Toast.makeText(context.getApplicationContext(), arrContacts.get(position).key.toString(),Toast.LENGTH_LONG).show();

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
//                                            Toast.makeText(context, "dataSnapshot exists", Toast.LENGTH_SHORT).show();
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
                                                FirebaseMessaging.getInstance().unsubscribeFromTopic(ucat+"p");
//                                                Toast.makeText(context, "unsubscribed "+ucat+"p", Toast.LENGTH_SHORT).show();
                                            }
                                            if(count_address==0 && !arrContacts.get(position).address.equals(uadrs)) {
                                                FirebaseMessaging.getInstance().unsubscribeFromTopic(uadrs+"p");
//                                                Toast.makeText(context, "unsubscribed "+uadrs+"p", Toast.LENGTH_SHORT).show();
                                            }
                                            count_category=0;
                                            count_address=0;
                                        } else {
//                                            Toast.makeText(context, "not found", Toast.LENGTH_LONG).show();
                                            if(!arrContacts.get(position).category.equals(ucat)) {
                                                FirebaseMessaging.getInstance().unsubscribeFromTopic(ucat + "p");
//                                                Toast.makeText(context, "unsubscribed "+ucat+"p", Toast.LENGTH_SHORT).show();
                                            }
                                            if(!arrContacts.get(position).address.equals(uadrs)) {
                                                FirebaseMessaging.getInstance().unsubscribeFromTopic(uadrs + "p");
//                                                Toast.makeText(context, "unsubscribed " + uadrs + "p", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });


                                //update in firebase
                                DatabaseReference databaseReference = db.getReference(ContactModelW.class.getSimpleName());
                                databaseReference
                                        .child(arrContacts.get(position).key)
                                        .updateChildren(map);

                                arrContacts.set(position, new ContactModelW(arrContacts.get(position).img1, name, addrs, cat, des, quan, phone, arrContacts.get(position).key, 1,0,timeStamp,arrContacts.get(position).rating,arrContacts.get(position).noOfRatings));
                                notifyItemChanged(position);

                                //subscription
                                FirebaseMessaging.getInstance().subscribeToTopic(arrContacts.get(position).category+"p");
                                FirebaseMessaging.getInstance().subscribeToTopic(arrContacts.get(position).address+"p");

                                //Toast.makeText(context.getApplicationContext(), "Subscribed to "+arrContacts.get(position).category+"p and "+arrContacts.get(position).address+"p",Toast.LENGTH_LONG).show();
                                //Toast.makeText(getContext(), "Subscribed to all",Toast.LENGTH_LONG).show();


                                FcmNotificationsSender notificationsSender = new FcmNotificationsSender("/topics/"+arrContacts.get(position).category,"New "+arrContacts.get(position).address+" "+arrContacts.get(position).category+" worker","Click here to know more",activity);
                                notificationsSender.SendNotifications();
                                FcmNotificationsSender notificationsSender2 = new FcmNotificationsSender("/topics/"+arrContacts.get(position).address,"New "+arrContacts.get(position).address+" "+arrContacts.get(position).category+" worker","Click here to know more", activity );
                                notificationsSender2.SendNotifications();
//                                FcmNotificationsSender notificationsSender1 = new FcmNotificationsSender("/topics/all","New any worker","Click here to know more", context.getApplicationContext(), dialogw.getOwnerActivity());
//                                notificationsSender1.SendNotifications();
//                                Toast.makeText(context.getApplicationContext(), "after sendNotifications", Toast.LENGTH_SHORT).show();
                                //ma.fcmNotification(arrContacts.get(position).category,arrContacts.get(position).address);

                                dialogw.dismiss();
                            }

                        }
                    });
                    dialogw.show();
                }


            }


        });

        holder.llRow.setOnLongClickListener(new View.OnLongClickListener(){

            @Override
            public boolean onLongClick(View view) {

                if(arrContacts.get(position).clickable==1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context)
                            .setTitle("Delete Registration")
                            .setMessage("            Are you sure to delete?")
                            .setIcon(R.drawable.ic_baseline_delete_24)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    String cat=arrContacts.get(position).category;
                                    String adrs=arrContacts.get(position).address;

                                    SharedPreferences pref = context.getSharedPreferences("MyPref", 0); // 0 - for private mode
                                    String loginpno = pref.getString("Pnol", null);

                                    //checking for unsubscription
                                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                                    DatabaseReference databaseReference2 = db.getReference("ContactModelW");
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
                                                    FirebaseMessaging.getInstance().unsubscribeFromTopic(cat+"p");
//                                                    Toast.makeText(context, "unsubscribed "+cat+"p", Toast.LENGTH_SHORT).show();
                                                }
                                                if(count_address==0) {
                                                    FirebaseMessaging.getInstance().unsubscribeFromTopic(adrs+"p");
//                                                    Toast.makeText(context, "unsubscribed "+adrs+"p", Toast.LENGTH_SHORT).show();
                                                }
                                                count_category=0;
                                                count_address=0;
                                            } else {
//                                                Toast.makeText(context, "not found", Toast.LENGTH_LONG).show();
                                                FirebaseMessaging.getInstance().unsubscribeFromTopic(cat+"p");
//                                                Toast.makeText(context, "unsubscribed "+cat+"p", Toast.LENGTH_SHORT).show();
                                                FirebaseMessaging.getInstance().unsubscribeFromTopic(adrs+"p");
//                                                Toast.makeText(context, "unsubscribed "+adrs+"p", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                    //delete from database
                                    DatabaseReference databaseReference = db.getReference(ContactModelW.class.getSimpleName());
                                    databaseReference
                                            .child(arrContacts.get(position).key)
                                            .removeValue();

                                    arrContacts.remove(position);
                                    notifyItemRemoved(position);

                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                    builder.show();

                }

                if(arrContacts.get(position).timeStamp==2022){
                   //Toast.makeText(context, "in l click", Toast.LENGTH_SHORT).show();
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
                                    DatabaseReference databaseReference = firebaseDatabase.getReference("FavouritesW/"+loginpno);
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
                                    backendw bufav = new backendw(loginpno);
                                    //Favourites favs = new Favourites((String)arrContacts.get(position).phone);
                                    bufav.addFav((String)arrContacts.get(position).phone);
                                    return true;

                                case R.id.Rating:
                                    Toast.makeText(context, "rating", Toast.LENGTH_SHORT).show();
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

            name = itemView.findViewById(R.id.WpNameiW);
            address = itemView.findViewById(R.id.WpAddiW);
            category = itemView.findViewById(R.id.WCatiW);
            description = itemView.findViewById(R.id.WDesiW);
            quantity = itemView.findViewById(R.id.WQuaiW);
            contact = itemView.findViewById(R.id.phoneiW);
            img = itemView.findViewById(R.id.pimgW);
            menu = itemView.findViewById(R.id.menu);
            llRow = itemView.findViewById(R.id.llRowW);

        }
    }

//    public void setpimg(Bitmap bitmap, View view){
//        Toast.makeText(context,"enterd setpic",Toast.LENGTH_LONG).show();
//        ViewHolder vh = new ViewHolder(view);
//        vh.img.setImageBitmap(bitmap);
//
//    }
//call to number
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

