package com.devanant.bee.UI.Home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devanant.bee.R;
import com.devanant.bee.UI.organisationAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterUserTagSearch extends RecyclerView.Adapter<AdapterUserTagSearch.mViewHolder> {



    private List<UserModel> userList;
    private SelectedItem selectedItem;

    public AdapterUserTagSearch(List<UserModel> userList, SelectedItem selectedItem) {
        this.userList = userList;
        this.selectedItem = selectedItem;
    }

    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_user_list,parent,false);
        return new AdapterUserTagSearch.mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, int position) {
            holder.setDetails(userList.get(position).getUsername(),userList.get(position).getProfilePic());
    }
    public interface SelectedItem{
        void selectedItem(UserModel userModel);
    }
    @Override
    public int getItemCount() {
        return userList.size();
    }


    public class mViewHolder extends RecyclerView.ViewHolder{
        private TextView usernameText;
        private ImageView imageUserSearch;
        private View view;
        public mViewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedItem.selectedItem(userList.get(getAdapterPosition()));

                }
            });
        }


        public void setDetails(String userName, String profileImage) {
            usernameText = view.findViewById(R.id.UserName);
            imageUserSearch = view.findViewById(R.id.userImage);
            usernameText.setText(userName);
            Picasso.get().load(profileImage).into(imageUserSearch);
        }
    }
}
