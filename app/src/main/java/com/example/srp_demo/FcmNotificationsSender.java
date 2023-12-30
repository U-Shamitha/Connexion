package com.example.srp_demo;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.core.Context;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FcmNotificationsSender{
    String userFcmToken,title,body;
    android.content.Context mContext;
    Activity mActivity;

    private RequestQueue requestQueue;
    private final String postUrl = "https://fcm.googleapis.com/fcm/send";
    private final String fcmServerKey="AAAANDRJs-o:APA91bHp-lPcXsOL3C14DHg6L2YcbGAFP7Yt6dDtyFD4IH7THSIf2o2yF8UzZNQBxYKTCUSthjoccjeNXL-k0RBCZex5ahJOSzg8FbX4AwNb5ZVv4djiJK_eZwcIZa3Wvl2DL6WLfFD7";
    //private final String fcmServerKey=MainActivity.token;


    public FcmNotificationsSender(String userFcmToken, String title, String body, Activity mActivity){
        this.userFcmToken=userFcmToken;
        this.title=title;
        this.body=body;
        //this.mContext=mContext;
        this.mActivity=mActivity;
    }
    public FcmNotificationsSender(String userFcmToken, String title, String body, android.content.Context mContext, Activity mActivity){
        this.userFcmToken=userFcmToken;
        this.title=title;
        this.body=body;
        this.mContext=mContext;
        this.mActivity=mActivity;
    }

    public void SendNotifications(){

        //Toast.makeText(mContext, "in sendNotifications", Toast.LENGTH_SHORT).show();
        requestQueue = Volley.newRequestQueue(mActivity);
        JSONObject mainObj = new JSONObject();
        try {
            mainObj.put("to",userFcmToken);
            JSONObject notiObject = new JSONObject();
            notiObject.put("title",title);
            notiObject.put("icon","icon");

            mainObj.put("notification", notiObject);
            mainObj.put("data", notiObject);


            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postUrl, mainObj,
                    //response -> Toast.makeText(mContext, "in response", Toast.LENGTH_SHORT).show(),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            //Toast.makeText(mContext, "in response", Toast.LENGTH_SHORT).show();
                        }
                    },
                    //error -> Toast.makeText(mContext, "in error", Toast.LENGTH_SHORT).show()){
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError{

                    Map<String, String> header = new HashMap<>();
                    header.put("content-type","application/json");
                    header.put("authorization","key="+fcmServerKey);
                    //Toast.makeText(mContext, "in header", Toast.LENGTH_SHORT).show();
                    return header;
                }
            };
            //Toast.makeText(mContext, "after header", Toast.LENGTH_SHORT).show();
            requestQueue.add(request);
            //Toast.makeText(mContext, "after requestQueue", Toast.LENGTH_SHORT).show();



        } catch (JSONException e) {
            e.printStackTrace();
            // Toast.makeText(mContext, "in catch", Toast.LENGTH_SHORT).show();
            //Log.d("notification","onCreate: "+e.getMessage());
        }
    }
}
