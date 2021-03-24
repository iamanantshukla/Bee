package com.devanant.bee.UI.Chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.devanant.bee.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class ChatFirst extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_first);
        EditText nameText=findViewById(R.id.nametext);
        Button joingroup=findViewById(R.id.joingroup);

        mAuth=FirebaseAuth.getInstance();

        firestore= FirebaseFirestore.getInstance();

        firestore.collection("Users").document(mAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                  username=documentSnapshot.getString("Username");
            }
        });

        Socket mSocket = null;
        try {
            mSocket=IO.socket("https://bee-server-chat.herokuapp.com");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        mSocket.connect();
        JSONObject jsongroupjoin=new JSONObject();
        try {
            jsongroupjoin.put("group",nameText.getText().toString());
            jsongroupjoin.put("username",username);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        mSocket.emit("join",jsongroupjoin);


        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},10);



        joingroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ChatFirst.this,ChatSecond.class);
                intent.putExtra("name",nameText.getText().toString());
                startActivity(intent);

            }
        });
    }
}