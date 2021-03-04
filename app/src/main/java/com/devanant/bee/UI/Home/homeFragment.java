package com.devanant.bee.UI.Home;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.devanant.bee.Database.TinyDB;
import com.devanant.bee.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class homeFragment extends Fragment implements UserAdapter.SelectedPager{

    private FirebaseFirestore fstore;
    private List<UserModel> userModels;
    private UserAdapter userAdapter;
    private RecyclerView suggestionPager;
    private ArrayList<String> interest;
    private FirebaseAuth mAuth;

    private TinyDB tinyDB;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fstore=FirebaseFirestore.getInstance();
        tinyDB=new TinyDB(getContext());
        userModels=new ArrayList<>();
        interest=new ArrayList<>();
        mAuth=FirebaseAuth.getInstance();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        interest=tinyDB.getListString("UserInterest");
        fstore.collection("Users").whereArrayContainsAny("Interest",interest).limit(5).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(!value.isEmpty())
                {
                    for(QueryDocumentSnapshot doc:value)
                    {
                        //if(!doc.getId().equals(mAuth.getCurrentUser().getUid())){
                        UserModel model=doc.toObject(UserModel.class);
                        userModels.add(model);
                        userAdapter.notifyDataSetChanged();
                        Log.i("HomeFragmentSuggestion",model.getUsername());
                        //}
                    }
                }
                else{
                    Log.i("HomeFragmentSuggestion","Empty"+interest.toString());
                }

            }
        });

        View root=inflater.inflate(R.layout.fragment_home, container, false);
        suggestionPager=root.findViewById(R.id.SuggestionViewPager);
        suggestionPager.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false));
        suggestionPager.setHasFixedSize(true);
        setUpViewPager();
        return root;
    }

    private void setUpViewPager() {
        userAdapter=new UserAdapter(userModels,this);
        suggestionPager.setAdapter(userAdapter);
    }


    @Override
    public void selectedpager(UserModel viewPagerModel) {

    }
}