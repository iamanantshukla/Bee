package com.devanant.bee.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.devanant.bee.Database.TinyDB;
import com.devanant.bee.R;
import com.devanant.bee.UI.Home.ConfessionModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfessionComment extends AppCompatActivity {

    private EditText textField;
    private String username;
    private ArrayList<String> comments;
    private String DocumentID, College;
    private RecyclerView recyclerView;
    private CommentAdapter adapter;
    private TextView DescText,IDText, DateText;
    private ConfessionModel Model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confession_comment);

        textField=findViewById(R.id.EditComment);
        recyclerView=findViewById(R.id.commentRecycler);
        DescText=findViewById(R.id.ConfessionDesc2);
        IDText=findViewById(R.id.ConfessionID2);
        DateText=findViewById(R.id.ConfessionDate2);
        Model= (ConfessionModel) getIntent().getSerializableExtra("Model");

        comments= Model.getComments();
        College=getIntent().getStringExtra("College");
        DocumentID=Model.getDocumentID();

        DescText.setText(Model.getDesc());
        DateText.setText(Model.getDate());
        IDText.setText(Model.getID());

        TinyDB tinyDB=new TinyDB(getApplicationContext());
        Map<String, Object> map=new HashMap<>();
        map=tinyDB.getObject("UserProfile", map.getClass());
        username=map.get("Username").toString();

        comments.remove("empty");

        adapter=new CommentAdapter(comments);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();

    }

    public void sendMessage(View view){
        String message = textField.getText().toString().trim();
        if(TextUtils.isEmpty(message)){
            return;
        }
        textField.setText("");

        String comment=username+"#"+message;

        addToFirebase(comment);

    }

    private void addToFirebase(String comment) {
        FirebaseFirestore firestore=FirebaseFirestore.getInstance();
        firestore.collection("Data").document(College).collection("Confessions")
                .document(DocumentID).update("comments", FieldValue.arrayUnion(comment))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        comments.add(comment);
                        adapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("PostComment", "onFailure: "+e.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}