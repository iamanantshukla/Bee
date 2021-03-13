package com.devanant.bee.UI.Home;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.devanant.bee.Database.TinyDB;
import com.devanant.bee.R;
import com.devanant.bee.UI.GridSpacingItemDecoration;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class homeFragment extends Fragment implements UserAdapter.SelectedPager{

    private FirebaseFirestore fstore;
    private List<UserModel> userModels;
    private UserAdapter userAdapter;
    private RecyclerView suggestionPager;
    private ArrayList<String> interest;
    private FirebaseAuth mAuth;
    private RecyclerView categoryRecyclerView;
    private List<String> titles;
    private List<Integer> mImages;
    private TinyDB tinyDB;
    private CategoryAdapter categoryAdapter;
    private CircleImageView circleImageView;
    private StorageReference storageReference;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        fstore=FirebaseFirestore.getInstance();

        tinyDB=new TinyDB(getContext());
        userModels=new ArrayList<>();
        interest=new ArrayList<>();
        mAuth=FirebaseAuth.getInstance();
        storageReference= FirebaseStorage.getInstance().getReference();

    }

    public static String encodeTobase64(Bitmap image) {
        Bitmap immage = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);


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
                    for(QueryDocumentSnapshot doc:value)
                    {
                        //if(!doc.getId().equals(mAuth.getCurrentUser().getUid())){
                        UserModel model=doc.toObject(UserModel.class);
                        userModels.add(model);
                        userAdapter.notifyDataSetChanged();
                        Log.i("HomeFragmentSuggestion",model.getUsername());
                        //}
                    }
                    for(QueryDocumentSnapshot doc:value)
                    {
                        //if(!doc.getId().equals(mAuth.getCurrentUser().getUid())){
                        UserModel model=doc.toObject(UserModel.class);
                        userModels.add(model);
                        userAdapter.notifyDataSetChanged();
                        Log.i("HomeFragmentSuggestion",model.getUsername());
                        //}
                    }
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
        searchbtn = root.findViewById(R.id.searchOnHome);
        suggestionPager=root.findViewById(R.id.SuggestionViewPager);
        categoryRecyclerView=root.findViewById(R.id.CategoryRecyclerView);
        circleImageView=root.findViewById(R.id.circleImageView);
        loadProfileImage();



        titles=new ArrayList<>();
        mImages=new ArrayList<>();
        categoryAdapter=new CategoryAdapter(getContext(),titles,mImages);
        mImages.add(R.drawable.dance);
        mImages.add(R.drawable.music);
        mImages.add(R.drawable.photography);
        mImages.add(R.drawable.design);
        mImages.add(R.drawable.video);
        mImages.add(R.drawable.dramatics);
        mImages.add(R.drawable.reading);
        mImages.add(R.drawable.dance);
        mImages.add(R.drawable.music);
        mImages.add(R.drawable.photography);
        mImages.add(R.drawable.video);
        mImages.add(R.drawable.dramatics);
        mImages.add(R.drawable.reading);
        mImages.add(R.drawable.dance);



        titles.add("Dance");
        titles.add("Music");
        titles.add("Photography");
        titles.add("Designing");
        titles.add("Videography");
        titles.add("Dramatics");
        titles.add("Reading");
        titles.add("Dance");
        titles.add("Music");
        titles.add("Photography");
        titles.add("Designing");
        titles.add("Videography");
        titles.add("Dramatics");
        titles.add("Reading");

        GridLayoutManager gridLayoutManager=new GridLayoutManager(getContext(),2,GridLayoutManager.VERTICAL,false);
        categoryRecyclerView.setLayoutManager(gridLayoutManager);

        int spanCount = 3; // 3 columns
        int spacing = 50; // 50px
        boolean includeEdge = true;
        categoryRecyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));

        categoryRecyclerView.setHasFixedSize(true);
        categoryRecyclerView.setAdapter(categoryAdapter);
        //categoryRecyclerView.setNestedScrollingEnabled(false);
        suggestionPager.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false));
        suggestionPager.setHasFixedSize(true);
        setUpViewPager();

        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(getActivity(),SearchUsersTags.class);
                startActivity(i);
            }
        });
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