package com.devanant.bee.UI.Home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.devanant.bee.Database.TinyDB;
import com.devanant.bee.R;
import com.devanant.bee.UI.ConfessionComment;
import com.devanant.bee.UI.ConfessionSearch;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class messageFragment extends Fragment implements ConfessionAdapter.SelectedPager {

    private View view;
    private RecyclerView recyclerView;
    private List<ConfessionModel> confessionModels;
    private ConfessionAdapter adapter;
    private static String TAG="ConfessionFragment";
    private DocumentSnapshot lastSnapshot;
    private static Integer IS_LOADING=0,LAST_VISIBLE=1;
    private ProgressBar loading;
    private FirebaseFirestore firestore;
    private String college;
    private Map<String, Object> map;
    private TinyDB tinyDB;
    private ImageView blurBg;
    private NestedScrollView scrollHome;
    private ImageView SearchBtn;

    public messageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_message, container, false);
        recyclerView=view.findViewById(R.id.confRecycler);
        loading=view.findViewById(R.id.browseLoading2);
        blurBg=view.findViewById(R.id.blurBackground2);
        scrollHome=view.findViewById(R.id.scrollHome);
        SearchBtn=view.findViewById(R.id.ConfessionSearchBtn);

        tinyDB=new TinyDB(getContext());
        map=new HashMap<>();
        map=tinyDB.getObject("UserProfile", map.getClass());
        college=map.get("Organisation").toString();

        confessionModels=new ArrayList<>();
        adapter=new ConfessionAdapter(confessionModels, this, getContext(),college);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        SearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(), ConfessionSearch.class);
                i.putExtra("College", college);
                startActivity(i);
            }
        });

        firestore=FirebaseFirestore.getInstance();

        setFirstBatch();

        return view;
    }

    private void setFirstBatch() {
        firestore.collection("Data").document(college).collection("Confessions").limit(5).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.i(TAG, "onSuccess: "+queryDocumentSnapshots.size());
                for(DocumentSnapshot snapshot:queryDocumentSnapshots){
                    confessionModels.add(snapshot.toObject(ConfessionModel.class));
                    //confessionModels.add(snapshot.toObject(ConfessionModel.class));
                    //confessionModels.add(snapshot.toObject(ConfessionModel.class));
                    //confessionModels.add(snapshot.toObject(ConfessionModel.class));
                    //confessionModels.add(snapshot.toObject(ConfessionModel.class));
                    Log.i(TAG, "onSuccess: "+confessionModels.size());
                    adapter.notifyDataSetChanged();
                    lastSnapshot=snapshot;
                }
                if(queryDocumentSnapshots.size()<5){
                    LAST_VISIBLE=0;
                }
                blurBg.setVisibility(View.INVISIBLE);;
                loading.setVisibility(View.INVISIBLE);
                IS_LOADING=0;
                scrollHome.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged()
                    {
                        View view = (View)scrollHome.getChildAt(scrollHome.getChildCount() - 1);

                        int diff = (view.getBottom() - (scrollHome.getHeight() + scrollHome
                                .getScrollY()));

                        if (diff == 0 && LAST_VISIBLE==1 && IS_LOADING==0) {
                            loading.setVisibility(View.VISIBLE);
                            loadLatestPost();
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "onFailure: "+e.getMessage());
            }
        });
    }

    private void loadLatestPost() {
        IS_LOADING=1;
        Query nextquery=firestore.collection("Data").document(college).collection("Confession").startAfter(lastSnapshot).limit(5);
        nextquery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty()){
                    Log.i("LatestPost", "onSuccess: Empty");
                }else if(confessionModels.size()%2==0) {
                    List<DocumentSnapshot> snapshots = queryDocumentSnapshots.getDocuments();
                    for(DocumentSnapshot doc: snapshots){
                        confessionModels.add(doc.toObject(ConfessionModel.class));
                    }
                    adapter.notifyDataSetChanged();
                    lastSnapshot=snapshots.get(snapshots.size()-1);
                    IS_LOADING=0;
                    loading.setVisibility(View.INVISIBLE);
                    blurBg.setVisibility(View.INVISIBLE);
                    if(snapshots.size()<5){
                        Log.i("LatestPost", "onScrollChanged: limit reached");
                        LAST_VISIBLE=0;
                    }
                    scrollHome.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                        @Override
                        public void onScrollChanged()
                        {
                            View view = (View)scrollHome.getChildAt(scrollHome.getChildCount() - 1);

                            int diff = (view.getBottom() - (scrollHome.getHeight() + scrollHome
                                    .getScrollY()));

                            if (diff == 0 && LAST_VISIBLE==1 && IS_LOADING==0) {
                                Log.i("LatestPost", "onScrollChanged: More");
                                loading.setVisibility(View.VISIBLE);
                                loadLatestPost();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void selectedpager(ConfessionModel viewPagerModel) {
        Intent i=new Intent(getActivity(), ConfessionComment.class);
        i.putExtra("College", college);
        i.putExtra("Model", viewPagerModel);
        startActivity(i);
    }
}