package com.devanant.bee.UI.Home;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.devanant.bee.Database.TinyDB;
import com.devanant.bee.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfessionAdapter extends RecyclerView.Adapter<ConfessionAdapter.mViewHolder>{

    private List<ConfessionModel> viewPagerModel;
    private SelectedPager selectedpager;
    private TinyDB tinyDB;
    private ArrayList<String> liked, disliked;
    private Context context;
    private String College;

    public ConfessionAdapter(List<ConfessionModel> viewPagerModel,SelectedPager selectedpager, Context context, String College){
        this.viewPagerModel=viewPagerModel;
        this.selectedpager=selectedpager;
        this.context=context;
        tinyDB=new TinyDB(context);
        liked=new ArrayList<>();
        disliked=new ArrayList<>();
        liked=tinyDB.getListString("Liked");
        disliked=tinyDB.getListString("Disliked");
        this.College=College;
    }

    @NonNull
    @Override
    public ConfessionAdapter.mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.confession_layout, parent, false);
        //view.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return new ConfessionAdapter.mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConfessionAdapter.mViewHolder holder, int position) {

        holder.setDate(viewPagerModel.get(position).getDate());
        holder.setDesc(viewPagerModel.get(position).getDesc());
        holder.setDislikes(viewPagerModel.get(position).getDislike());
        holder.setLikes(viewPagerModel.get(position).getLike());
        holder.setID(viewPagerModel.get(position).getID());
        holder.DocumentID=viewPagerModel.get(position).getDocumentID();

    }

    @Override
    public int getItemCount() {
        return viewPagerModel.size();
    }


    public interface SelectedPager{
        void selectedpager(ConfessionModel viewPagerModel);
    }

    public class mViewHolder extends RecyclerView.ViewHolder{
        View view;
        TextView LikeText, DislikeText, DescText, DateText, IDText;
        LinearLayout Like, Dislike, Comment;
        String PostID, DocumentID;
        Integer likes,dislikes,comments;

        public mViewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView;

            LikeText=view.findViewById(R.id.likeCount);
            DislikeText=view.findViewById(R.id.dislikeCount);
            DescText=view.findViewById(R.id.ConfessionDesc);
            IDText=view.findViewById(R.id.ConfessionID);
            DateText=view.findViewById(R.id.ConfessionDate);
            Like=view.findViewById(R.id.LIkeConfession);
            Dislike=view.findViewById(R.id.DislikeConfession);
            Comment=view.findViewById(R.id.CommentConfession);

            Like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    likes=likes+1;
                    LikeText.setText(likes.toString());
                    if(liked.contains(PostID)){
                        Toast.makeText(context, "Already Liked", Toast.LENGTH_SHORT).show();
                        likes=likes-1;
                        LikeText.setText(likes.toString());
                    }else if(disliked.contains(PostID)) {
                        updateLike(1,-1, DocumentID);
                        dislikes=dislikes-1;
                        DislikeText.setText(dislikes.toString());
                        disliked.remove(PostID);
                        liked.add(PostID);
                        tinyDB.putListString("Liked",liked);
                        tinyDB.putListString("Disliked",disliked);
                    }
                    else{
                        liked.add(PostID);
                        updateLike(1,0, DocumentID);
                        tinyDB.putListString("Liked",liked);
                    }
                }
            });

            Dislike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dislikes=dislikes+1;
                    DislikeText.setText(dislikes.toString());
                    if(disliked.contains(PostID)){
                        Toast.makeText(context, "Already Disliked", Toast.LENGTH_SHORT).show();
                        dislikes=dislikes-1;
                        DislikeText.setText(dislikes.toString());
                    }else if(liked.contains(PostID)){
                        updateLike(-1,1, DocumentID);
                        likes=likes-1;
                        LikeText.setText(likes.toString());
                        liked.remove(PostID);
                        disliked.add(PostID);
                        tinyDB.putListString("Liked",liked);
                        tinyDB.putListString("Disliked",disliked);
                    }
                    else{
                        disliked.add(PostID);
                        updateLike(0,1, DocumentID);
                        tinyDB.putListString("Disliked",disliked);
                    }
                }
            });

            Comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedpager.selectedpager(viewPagerModel.get(getAdapterPosition()));
                }
            });

            DescText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedpager.selectedpager(viewPagerModel.get(getAdapterPosition()));
                }
            });

        }
        public void setID(String id){
            PostID=id;
            IDText.setText(id);
        }

        public void setDesc(String desc){
            DescText.setText(desc);
        }

        public void setLikes(Integer likes){
            this.likes=likes;
            LikeText.setText(likes.toString());
        }

        public void setDislikes(Integer dislikes){
            this.dislikes=dislikes;
            DislikeText.setText(dislikes.toString());
        }

        public void setDate(String date){
            DateText.setText(date);
        }

    }

    private void updateLike(int like, int dislike, String PostID) {
        FirebaseFirestore firestore=FirebaseFirestore.getInstance();
        firestore.collection("Data").document(College).collection("Confessions")
                .document(PostID).update("Like", FieldValue.increment(like), "Dislike", FieldValue.increment(dislike)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("ReactionConfession", "onSuccess: ");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("ReactionConfession", "onFailure: "+e.getMessage());
            }
        });
    }
}
