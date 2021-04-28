package com.devanant.bee.Firebase;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.devanant.bee.Database.TinyDB;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class UserFirebase {

    private Context application;
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private TinyDB tinyDB;
    private ArrayList<String> Friends, ReqSent,ReqReceived;
    private String UserID, profileUserID;
    private static String TAG="UserFirebase";
    public UserFirebase(Context application, String UserID, String profileUserID) {
        this.application = application;
        tinyDB=new TinyDB(application);
        this.UserID=UserID;
        this.profileUserID=profileUserID;
        firestore=FirebaseFirestore.getInstance();
    }

    public int checkStatus(){
        Friends=new ArrayList<>();
        ReqSent=new ArrayList<>();
        ReqReceived=new ArrayList<>();

        Friends=tinyDB.getListString("Friends");
        ReqSent=tinyDB.getListString("ReqSent");
        ReqReceived=tinyDB.getListString("ReqReceived");

        if(Friends.contains(profileUserID)){
            return 0;
        }else if(ReqSent.contains(profileUserID)){
            return 1;
        }else if(ReqReceived.contains(profileUserID)){
            return 3;
        }else{
            return 2;
        }
    }

    public void AcceptReq() {
        firestore.collection("Users").document(profileUserID)
                .update("ReqSent", FieldValue.arrayRemove(UserID)
                        ,"Friends",FieldValue.arrayUnion(UserID))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        updateFriendsAfterAccept();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("UserFirebase", "onFailure: "+e.getMessage());
            }
        });
    }

    private void updateFriendsAfterAccept() {
        firestore.collection("Users").document(UserID)
                .update("ReqReceived", FieldValue.arrayRemove(profileUserID)
                        ,"Friends",FieldValue.arrayUnion(profileUserID))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        ReqReceived.remove(profileUserID);
                        Friends.add(profileUserID);
                        Toast.makeText(application, "You are Friends now", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("UserFirebase", "onFailure: "+e.getMessage());
            }
        });
    }

    public void sendFriendReq() {
        firestore.collection("Users").document(profileUserID)
                .update("ReqReceived", FieldValue.arrayUnion(UserID))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        updateDatabase();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "onFailure: "+e.getMessage());
            }
        });
    }

    private void updateDatabase() {
        firestore.collection("Users").document(UserID).update("ReqSent", FieldValue.arrayUnion(profileUserID)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                ReqSent.add(profileUserID);
                tinyDB.putListString("ReqSent", ReqSent);
                Toast.makeText(application, "Request sent", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "onFailure: "+e.getMessage());;
            }
        });
    }

    private void RemoveFromThisSide() {
        firestore.collection("Users").document(UserID)
                .update("Friends", FieldValue.arrayRemove(profileUserID))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Friends.remove(profileUserID);
                        tinyDB.putListString("Friends",Friends);
                        Toast.makeText(application,"Removed Successfully", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "onFailure: "+e.getMessage());
            }
        });
    }

    public void RemoveFriend() {
        firestore.collection("Users").document(profileUserID)
                .update("Friends", FieldValue.arrayRemove(UserID))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        RemoveFromThisSide();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "onFailure: "+e.getMessage());
            }
        });
    }

}
