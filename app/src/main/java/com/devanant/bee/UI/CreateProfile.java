package com.devanant.bee.UI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.FaceDetector;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.devanant.bee.Database.TinyDB;
import com.devanant.bee.R;
import com.devanant.bee.UI.Home.HomeActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateProfile extends AppCompatActivity {

    private TinyDB tinyDB;
    private String PhoneNo,Username,Organisation,Facebook, Instagram;
    private TextView EditName,EditFB,EditInsta,EditInterest;
    private TextView EditOrg;
    private Button btnCreate;
    private FirebaseFirestore firestore;
    private ArrayList<String> Organisations,userInterest;
    private static String TAG="CreateProfile";
    private String uInterest;
    private FirebaseAuth mAuth;
    private ArrayAdapter<String> adapter;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        EditName=findViewById(R.id.EditName);
        EditOrg=findViewById(R.id.EditOrg);
        EditFB=findViewById(R.id.EditFacebook);
        EditInsta=findViewById(R.id.EditInsta);
        btnCreate=findViewById(R.id.btnCreate);
        EditInterest=findViewById(R.id.EditInterest);

        mAuth=FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        Organisations=new ArrayList<>();
        userInterest=new ArrayList<>();
        tinyDB=new TinyDB(getApplicationContext());
        userInterest=tinyDB.getListString("UserInterest");
        setupText();
        PhoneNo=tinyDB.getString("PhoneNumber");

        firestore=FirebaseFirestore.getInstance();

        getOrganisationList(firestore);
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, Organisations);

        EditOrg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(CreateProfile.this, ChooseOrganisation.class);
                i.putExtra("Organisations",Organisations);
                startActivity(i);
            }
        });

        EditInterest.setOnClickListener(v->{
            Intent i=new Intent(CreateProfile.this, ChooseInterest.class);
            i.putExtra("Organisations",Organisations);
            startActivity(i);
        });



        btnCreate.setOnClickListener(v -> {
            Username=EditName.getText().toString();
            Organisation=EditOrg.getText().toString();
            Facebook=EditFB.getText().toString();
            Instagram=EditInsta.getText().toString();

            if(check()){
                createProfile();
            }
        });


    }

    private void setupText() {

        uInterest="";
        for(int i=0;i<userInterest.size();i++){
            uInterest=uInterest+userInterest.get(i)+" | ";
        }
        EditInterest.setText(uInterest);
    }

    private void createProfile() {
        ArrayList<Map<String, Object>> Friends=new ArrayList<>();
        Map<String, Object> fMap=new HashMap<>();
        fMap.put("UID", mAuth.getCurrentUser().getUid());
        fMap.put("username", Username);
        fMap.put("profilePic", "");

        Friends.add(fMap);

        ArrayList<String> ReqSent=new ArrayList<>();
        ReqSent.add("DefaultUser");

        ArrayList<String> ReqReceived=new ArrayList<>();
        ReqSent.add("ReqReceived");

        Map<String, Object> map=new HashMap<>();
        map.put("Username", Username);
        map.put("Organisation", Organisation);
        map.put("Facebook", Facebook);
        map.put("Instagram", Instagram);
        map.put("Bio","");
        map.put("ProfilePic","");
        map.put("PhoneNumber", PhoneNo);
        map.put("Interest", userInterest);
        map.put("UserID", mAuth.getCurrentUser().getUid());
        map.put("Friends", Friends);
        map.put("ReqSent", ReqSent);
        map.put("ReqReceived", ReqReceived);
        map.put("SecretCrush", true);
        tinyDB.putObject("UserProfile", map);
        firestore.collection("Users").document(mAuth.getCurrentUser().getUid()).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(CreateProfile.this, "Profile Created", Toast.LENGTH_SHORT).show();
                Intent i=new Intent(CreateProfile.this, HomeActivity.class);
                startActivity(i);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "onFailure: "+e.getMessage());
            }
        });
    }

    private void getOrganisationList(FirebaseFirestore firestore) {
        firestore.collection("Data").document("Organisations").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                Log.i(TAG, "onSuccess: Organisation List");
                Organisations= (ArrayList<String>) snapshot.get("College");
                adapter.addAll(Organisations);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "onFailure: Organisation List");
            }
        });
    }

    private boolean check() {
        if(Username.isEmpty()){
            return false;
        }else if(Organisation.isEmpty()){
            return false;
        }else{
            if(Organisations.contains(Organisation)){
                if(!Instagram.isEmpty()){
                    if(!URLUtil.isValidUrl(Instagram)){
                        EditInsta.setError("Enter valid URL or else leave empty");
                        return false;
                    }else{
                        if(!Instagram.contains("instagram") || !Instagram.toLowerCase().contains("instagram")){
                            EditInsta.setError("Enter valid URL or else leave empty");
                            return false;
                        }
                    }
                }
                if(!Facebook.isEmpty()){
                    if(!URLUtil.isValidUrl(Facebook)){
                        EditFB.setError("Enter valid URL or else leave empty");
                        return false;
                    }else{
                        if(!Facebook.contains("facebook") || !Facebook.toLowerCase().contains("facebook")){
                            EditFB.setError("Enter valid URL or else leave empty");
                            return false;
                        }
                    }
                }
                return true;
            }else{
                return false;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        EditOrg.setText(tinyDB.getString("Organisation"));
        userInterest=tinyDB.getListString("UserInterest");
        setupText();
    }
}