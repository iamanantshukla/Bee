package com.devanant.bee.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.devanant.bee.Database.TinyDB;
import com.devanant.bee.Firebase.UserFirebase;
import com.devanant.bee.R;
import com.devanant.bee.UI.Home.UserModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.internal.operators.observable.ObservableOnErrorNext;

public class OtherProfile extends AppCompatActivity {

    private ImageView profilePic;
    private TextView TextName, TextBio, TextSkill1, TextSkill2, TextSkill3;
    private UserModel userModel;
    private Button FRBtn;
    private FloatingActionButton SCBtn;
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private TinyDB tinyDB;
    private ArrayList<String> Friends, ReqSent,ReqReceived;
    private String profileUserID, UserID;
    private int STATUS=3;  //Loading->3, SendFR=2, SentFR=1, AlreadyF=0
    private static String TAG="UserSTATUS";
    private UserFirebase userFirebase;
    private Map<String, Object> map;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);
        TextName=findViewById(R.id.oUsername);
        TextBio=findViewById(R.id.oBioText);
        profilePic=findViewById(R.id.oProImage);
        TextSkill1=findViewById(R.id.oprofileSkill1);
        TextSkill2=findViewById(R.id.oprofileSkill2);
        TextSkill3=findViewById(R.id.oprofileSkill3);
        FRBtn=findViewById(R.id.oFRbtn);
        SCBtn=findViewById(R.id.oSCbtn);

        firestore=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
        tinyDB=new TinyDB(getApplicationContext());
        map=new HashMap<>();
        map=tinyDB.getObject("UserProfile", map.getClass());

        userModel= (UserModel) getIntent().getSerializableExtra("ProfileModel");
        profileUserID=userModel.getUserID();
        UserID=mAuth.getCurrentUser().getUid();


        userFirebase=new UserFirebase(getApplicationContext(), UserID, profileUserID);

        TextName.setText(userModel.getUsername());
        TextBio.setText(userModel.getBio());
        ArrayList<String> Interests = userModel.getInterest();
        for(int i=0;i<Interests.size();i++){
            if(i==0){
                TextSkill1.setText(Interests.get(i));
                TextSkill1.setVisibility(View.VISIBLE);
            }else if(i==1){
                TextSkill2.setText(Interests.get(i));
                TextSkill2.setVisibility(View.VISIBLE);
            }else if(i==2){
                TextSkill3.setText(Interests.get(i));
                TextSkill3.setVisibility(View.VISIBLE);
                break;
            }
        }
        String url=userModel.getProfilePic();
        Picasso.get().load(url).into(profilePic);

        STATUS=userFirebase.checkStatus();
        if(STATUS==0){
            FRBtn.setText("Remove Friend");
        }else if(STATUS==1){
            FRBtn.setText("Pending Request");
        }else if(STATUS==3){
            FRBtn.setText("ACCEPT REQUEST");
        }else{
            FRBtn.setText("Send Friend Request");
        }

        FRBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(STATUS==0){
                        //Remove Friend
                        userFirebase.RemoveFriend();
                        FRBtn.setText("Send Friend Request");
                        STATUS=2;
                    }else if(STATUS==1){
                        //Pending Request
                        Toast.makeText(getApplicationContext(), "You have already requested", Toast.LENGTH_SHORT).show();
                    }else if(STATUS==2){
                        //Send Friend request
                        userFirebase.sendFriendReq();
                        FRBtn.setText("Pending Request");
                        STATUS=1;
                    }else if(STATUS==3){
                        userFirebase.AcceptReq();
                        FRBtn.setText("Remove Friend");
                        STATUS=0;
                    }

                    else{
                        Toast.makeText(getApplicationContext(), "None", Toast.LENGTH_SHORT).show();
                    }
            }
        });

        SCBtn.setOnClickListener(v->{

            boolean SC= (boolean) map.get("SecretCrush");

            if(SC){
                FirebaseSecretCrushUpdate(map.get("UserID").toString(), userModel.getUserID());

            }else{
                //already used
            }
        });

    }

    private void FirebaseSecretCrushUpdate(String myID, String userID) {
        String College=userModel.getOrganisation();
        firestore.collection("Data").document(College)
                .collection("SecretCrush").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                if(snapshot.exists()) {
                    String crushList = (String) snapshot.get("CrushList");
                    if (crushList.contains(myID)) {
                        map.put("SecretCrush", false);
                        tinyDB.putObject("UserProfile", map);
                        CrushMatch();
                    } else {
                        addToMyCrush(myID, userID);
                    }
                }else{
                    addToMyCrush(myID, userID);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "onFailure: "+e.getMessage());
            }
        });
    }

    private void CrushMatch() {
        AlertDialog.Builder alert=new AlertDialog.Builder(OtherProfile.this);
        View view=getLayoutInflater().inflate(R.layout.dialog_match,null);
        alert.setView(view);
        AlertDialog show=alert.show();
        alert.setCancelable(true);
        show.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        updateFirebase();
    }

    private void updateFirebase() {
        firestore.collection("Users").document(mAuth.getCurrentUser().getUid())
                .update("SecretCrush", false).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //
            }
        });
    }

    private void addToMyCrush(String myID, String userID) {
        Map<String, String> Smap;
        Smap=new HashMap<>();
        Smap.put("CrushList", userID);
        String College=userModel.getOrganisation();
        firestore.collection("Data").document(College)
                .collection("SecretCrush").document(myID).set(Smap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Added to your crush list", Toast.LENGTH_SHORT).show();
                        map.put("SecretCrush", false);
                        tinyDB.putObject("UserProfile", map);
                        updateFirebase();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "onFailure: "+e.getMessage());
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}