/**package com.example.homescreen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ViewHolder>{

    private final RecyclerViewInterface recyclerViewInterface;
    ArrayList<ChildItem> childItemArrayList;

    public ChildAdapter(RecyclerViewInterface recyclerViewInterface, ArrayList<ChildItem> childItemArrayList) {
        this.recyclerViewInterface = recyclerViewInterface;
        this.childItemArrayList = childItemArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ChildItem childItem = childItemArrayList.get(position);

        holder.tvItemName.setText(childItem.itemName);
        holder.tvqtyChild.setText(childItem.itemQty);
        holder.tvPrice.setText(childItem.itemPrice);
        holder.ivChild.setImageResource(childItem.imageID);
    }

    @Override
    public int getItemCount() {
        return childItemArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvItemName, tvqtyChild, tvPrice;
        CircleImageView ivChild;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvItemName = itemView.findViewById(R.id.tvFischArt);
            tvqtyChild = itemView.findViewById(R.id.tvqtychild);
            tvPrice = itemView.findViewById(R.id.tvpricechild);
            ivChild = itemView.findViewById(R.id.ivChild);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(recyclerViewInterface != null){
                        int position = getAdapterPosition();

                        if(position != RecyclerView.NO_POSITION){
                            recyclerViewInterface.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}**/
