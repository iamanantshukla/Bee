package com.devanant.bee.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import com.devanant.bee.Database.TinyDB;
import com.devanant.bee.R;
import com.devanant.bee.UI.Home.UserSearchModel;

import java.util.ArrayList;

public class ChooseOrganisation extends AppCompatActivity implements organisationAdapter.SelectedItem{

    private organisationAdapter adapter;
    private ArrayList<String> organisations;
    private RecyclerView recyclerView;
    private TextView editOrg;
    private TinyDB tinyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_organisation);

        recyclerView=findViewById(R.id.orgRecycler);
        editOrg=findViewById(R.id.editOrg);

        organisations=new ArrayList<>();
        organisations=getIntent().getStringArrayListExtra("Organisations");
        adapter=new organisationAdapter(organisations,this);

        tinyDB=new TinyDB(getApplicationContext());

        recyclerView.setAdapter(adapter);

        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        editOrg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    void filter(String text){
        ArrayList<String> temp = new ArrayList();
        for(String d: organisations){
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if(d.contains(text)){
                temp.add(d);
            }else if(d.toLowerCase().contains(text)){
                temp.add(d);
            }
        }
        //update recyclerview
        adapter.updateList(temp);
    }



        @Override
        public void selectedItem(String organisation) {
            tinyDB.putString("Organisation", organisation);
            finish();
        }

}