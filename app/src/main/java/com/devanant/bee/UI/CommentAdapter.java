package com.devanant.bee.UI;

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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.mViewHolder>{

    private List<String> comments;

    public CommentAdapter(List<String> comments) {
        this.comments = comments;
    }

    @NonNull
    @Override
    public CommentAdapter.mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.their_message, parent, false);
        //view.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return new CommentAdapter.mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.mViewHolder holder, int position) {
        holder.setComment(comments.get(position));

    }

    @Override
    public int getItemCount() {
        return comments.size();
    }


    public class mViewHolder extends RecyclerView.ViewHolder{
        View view;
        TextView usernameText;
        TextView commentText;

        public mViewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView;
            usernameText=view.findViewById(R.id.name);
            commentText=view.findViewById(R.id.message_body);

        }

        public void setComment(String comment){

            String username="", message="";
            boolean user=true;
            for(int i=0;i<comment.length();i++){
                if(user && comment.charAt(i)!='#'){
                    username=username+comment.charAt(i);
                }else if(user && comment.charAt(i)=='#'){
                    user=false;
                }else{
                    message=message+comment.charAt(i);
                }
            }

            usernameText.setText(username);
            commentText.setText(message);

        }

    }
}
