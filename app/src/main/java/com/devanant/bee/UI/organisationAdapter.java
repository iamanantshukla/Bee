package com.devanant.bee.UI;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devanant.bee.R;

import java.util.ArrayList;

public class organisationAdapter extends RecyclerView.Adapter<organisationAdapter.mViewHolder> {

    ArrayList<String> Organisations;
    SelectedItem selectedItem;

    public organisationAdapter(ArrayList<String> organisations, SelectedItem selectedItem) {
        Organisations = organisations;
        this.selectedItem = selectedItem;
    }

    public void updateList(ArrayList<String> org){
        Organisations=org;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public organisationAdapter.mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.choose_organisation, parent, false);
        //view.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return new organisationAdapter.mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull organisationAdapter.mViewHolder holder, int position) {
        holder.organisationName(Organisations.get(position));
    }

    public interface SelectedItem{
        void selectedItem(String organisation);
    }

    @Override
    public int getItemCount() {
        return Organisations.size();
    }

    public class mViewHolder extends RecyclerView.ViewHolder{

        private View view;
        private TextView Name;

        public mViewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedItem.selectedItem(Organisations.get(getAdapterPosition()));
                }
            });
        }

        public void organisationName(String name){
            Name=view.findViewById(R.id.UserListText);
            Name.setText(name);
        }

    }

}
