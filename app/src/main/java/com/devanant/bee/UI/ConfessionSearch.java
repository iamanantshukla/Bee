package com.devanant.bee.UI;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import com.devanant.bee.R;
import com.devanant.bee.UI.Home.ConfessionAdapter;
import com.devanant.bee.UI.Home.ConfessionModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ConfessionSearch extends AppCompatActivity implements ConfessionAdapter.SelectedPager {

    private FirebaseFirestore firestore;
    private String college;
    private RecyclerView recyclerView;
    private TextView searchText;
    private List<ConfessionModel> confessionModels;
    private ConfessionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confession_search);

        searchText=findViewById(R.id.userSearch2);
        recyclerView=findViewById(R.id.RecyclerSearch);

        confessionModels=new ArrayList<>();
        college=getIntent().getStringExtra("College");
        adapter=new ConfessionAdapter(confessionModels,this,getApplicationContext(), college);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>=3){
                    updateRecyclerView(s.toString());
                }else{
                    confessionModels.clear();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        firestore=FirebaseFirestore.getInstance();

    }

    private void updateRecyclerView(String s) {
        firestore.collection("Data").document(college)
                .collection("Confessions")
                .whereEqualTo("ID", s)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(!value.isEmpty()){
                            for(DocumentSnapshot snapshot: value){
                                confessionModels.add(snapshot.toObject(ConfessionModel.class));
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    @Override
    public void selectedpager(ConfessionModel viewPagerModel) {
        Intent i=new Intent(ConfessionSearch.this, ConfessionComment.class);
        i.putExtra("College", college);
        i.putExtra("Model", viewPagerModel);
        startActivity(i);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}