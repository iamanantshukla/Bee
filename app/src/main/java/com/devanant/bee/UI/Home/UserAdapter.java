package com.devanant.bee.UI.Home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devanant.bee.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.mViewHolder>{

    private List<UserModel> viewPagerModel;
    private SelectedPager selectedpager;

    public UserAdapter(List<UserModel> viewPagerModel,SelectedPager selectedpager){
        this.viewPagerModel=viewPagerModel;
        this.selectedpager=selectedpager;
    }

    @NonNull
    @Override
    public UserAdapter.mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.other_profile_card, parent, false);
        //view.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return new UserAdapter.mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.mViewHolder holder, int position) {
     holder.setUserName(viewPagerModel.get(position).getUserName());
     holder.setProfilePic(viewPagerModel.get(position).getProfilePic());

    }

    @Override
    public int getItemCount() {
        return viewPagerModel.size();
    }
    public interface SelectedPager{
        void selectedpager(UserModel viewPagerModel);
    }

    public class mViewHolder extends RecyclerView.ViewHolder{
        View view;
        ImageView ProfilePic;
        TextView UserName;

        public mViewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedpager.selectedpager(viewPagerModel.get(getAdapterPosition()));
                }
            });
        }
        public void setUserName(String name){
            UserName=view.findViewById(R.id.pagerTitle);
            UserName.setText(name);
        }
        public void setProfilePic(String url){
            ProfilePic=view.findViewById(R.id.pagerCoverImage);
            Picasso.get().load(url).into(ProfilePic);
        }

    }
}
