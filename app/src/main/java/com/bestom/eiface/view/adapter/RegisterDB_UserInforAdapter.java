package com.bestom.eiface.view.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bestom.ei_library.EIFace;
import com.bestom.ei_library.commons.beans.UserInfoDBBean;
import com.bestom.eiface.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class RegisterDB_UserInforAdapter extends RecyclerView.Adapter<RegisterDB_UserInforAdapter.MyViewHolder> implements Filterable{
    private Context context;
    private List<UserInfoDBBean> mUserInfoDBBeanList;
    private List<UserInfoDBBean> mFiltereUserInfoDBBeanList;
    private boolean isSwipeAvailable = false;
    private boolean isDebug = false;

    public List<UserInfoDBBean> getfilterenrolledDBBeanList() {
        return mFiltereUserInfoDBBeanList;
    }

    public RegisterDB_UserInforAdapter(Context context, List<UserInfoDBBean> listObjects,
                                       boolean isSwipeAvailable, boolean isDebug) {
        this.mUserInfoDBBeanList = listObjects;
        this.context = context;
        this.isSwipeAvailable = isSwipeAvailable;
        this.mFiltereUserInfoDBBeanList = mUserInfoDBBeanList;
        this.isDebug = isDebug;
    }

    @Override
    public int getItemCount() {
        return mFiltereUserInfoDBBeanList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.register_db_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Glide.with(context).load(mFiltereUserInfoDBBeanList.get(position).getImage_path())
                .into(holder.curr_image);
        holder.name.setText(mFiltereUserInfoDBBeanList.get(position).getName());
        holder.ID.setText(mFiltereUserInfoDBBeanList.get(position).getID());
        holder.image_index.setText((position+1)+"");

        if (!isSwipeAvailable){

            Glide.with(context).load(R.drawable.close)
                    .into(holder.delete_icon);
            if (isDebug){
                holder.delete_icon.setVisibility(View.VISIBLE);
            }
            else {
                holder.delete_icon.setVisibility(View.INVISIBLE);
            }
            holder.delete_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeleteDialog(position,
                            mFiltereUserInfoDBBeanList.get(position).getImage_id());
                }
            });
        }
    }


    //region showDeleteDialog
    private void showDeleteDialog(final int position,final int record_id){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        // Setting Dialog Title
        alertDialog.setTitle("Delete Record!!");

        // Setting Dialog Message
        alertDialog.setMessage("Are you sure you want Delete this Record?");

        // Setting Icon to Dialog

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                // Write your code here to invoke YES event
                dialog.cancel();


            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event
//                wffrdualcamapp.deletePerson(record_id);
                EIFace.deletePerson(record_id);
                Toast.makeText(context, "Record Deleted", Toast.LENGTH_SHORT).show();
                removeItem(position);

            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
    //endregion

    public void removeItem(int position) {
        int image_id = mFiltereUserInfoDBBeanList.get(position).getImage_id();
        for (int i = 0; i< mUserInfoDBBeanList.size(); i++){
            if (mUserInfoDBBeanList.get(i).getImage_id() == image_id){
                mUserInfoDBBeanList.remove(i);
            }
        }
        for (int i = 0; i< mFiltereUserInfoDBBeanList.size(); i++){
            if (mFiltereUserInfoDBBeanList.get(i).getImage_id() == image_id){
                mFiltereUserInfoDBBeanList.remove(i);
            }
        }
//        filteredenrolledDatabaseObjectList.remove(position);

        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    public void restoreItem(UserInfoDBBean item, int position) {
        mFiltereUserInfoDBBeanList.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mFiltereUserInfoDBBeanList = mUserInfoDBBeanList;
                } else {
                    List<UserInfoDBBean> filteredList = new ArrayList<>();
                    for (UserInfoDBBean dbBean : mUserInfoDBBeanList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or ID match
                        if (dbBean.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(dbBean);
                        }
                    }

                    mFiltereUserInfoDBBeanList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFiltereUserInfoDBBeanList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFiltereUserInfoDBBeanList = (ArrayList<UserInfoDBBean>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView curr_image,delete_icon;
        public TextView name,image_index,ID;
        public MyViewHolder(View view) {
            super(view);
            curr_image = (ImageView) view.findViewById(R.id.enrolled_image);
            if (!isSwipeAvailable){
                delete_icon = (ImageView) view.findViewById(R.id.delete_image);
            }
            name = (TextView) view.findViewById(R.id.enrolled_name);
            ID = view.findViewById(R.id.enrolled_ID);
            image_index = (TextView) view.findViewById(R.id.image_index);

        }
    }

}