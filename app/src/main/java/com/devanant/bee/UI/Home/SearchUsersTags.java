package com.devanant.bee.UI.Home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;

import com.devanant.bee.R;
import com.devanant.bee.UI.organisationAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SearchUsersTags extends AppCompatActivity implements AdapterUserTagSearch.SelectedItem {
    private EditText searchbar;
    private ImageButton searchbutton;
    private RecyclerView searchresults;
    private FirebaseFirestore fstore;
    private List<UserSearchModel> userModel;
    private AdapterUserTagSearch userAdapter;
    private FirebaseAuth mAuth;
    private String UserID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_users_tags);
        searchbar = (EditText) findViewById(R.id.searchBar);
        searchbutton = (ImageButton)findViewById(R.id.searchButton);
        searchresults = (RecyclerView)findViewById(R.id.searchResults);
        fstore=FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();

        userModel=new ArrayList<>();
        userAdapter=new AdapterUserTagSearch(userModel, this);
        searchresults.setAdapter(userAdapter);

        searchresults.hasFixedSize();
        searchresults.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        searchbar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d("SearchUser", "afterTextChanged: "+editable.toString());
            }
        });
    }

    @Override
    public void selectedItem(UserSearchModel userModel) {
        Log.i("sentIntent", "selectedItem: " + userModel.UserID);
    }
}