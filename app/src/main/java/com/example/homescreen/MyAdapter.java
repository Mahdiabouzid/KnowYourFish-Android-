package com.example.homescreen;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    public interface OnKategorieClick {
        void OnKategorieClick(int position);

    }

    private MemberAdp.OnClickOnSubKategorie onClickOnSubKategorie;

    private OnKategorieClick monKategorieClick;
    private Activity activity;
    ArrayList<ParentItem> parentItemArrayList;
    ArrayList<ArrayList> kategorieItemList;

    public MyAdapter(Activity activity, ArrayList<ParentItem> parentItemArrayList, ArrayList<ArrayList> childItemArrayList, OnKategorieClick onKategorieClick, MemberAdp.OnClickOnSubKategorie onClickOnSubKategorie) {
        this.activity = activity;
        this.parentItemArrayList = parentItemArrayList;
        this.kategorieItemList = childItemArrayList;
        this.monKategorieClick = onKategorieClick;
        this.onClickOnSubKategorie = onClickOnSubKategorie;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);

        return new ViewHolder(view, monKategorieClick);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ParentItem parentItem = parentItemArrayList.get(position);

        holder.tvKategorie.setText(parentItem.kategorie);
        try {
            ChildItem childItem = (ChildItem) kategorieItemList.get(position).get(0);

            try {
                byte[] byteArray = childItem.currentFisch.bild_URL;
                Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

                holder.ivParent.setImageBitmap(bmp);
            } catch (Exception e) {
                e.printStackTrace();
                Bitmap bmp = null;
                holder.ivParent.setImageBitmap(bmp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            try{
                byte[] byteArray = parentItem.icon_bild;
                Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

                holder.ivParent.setImageBitmap(bmp);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }


        MemberAdp adapterMember = new MemberAdp(kategorieItemList.get(position), onClickOnSubKategorie, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        holder.nested_rv.setLayoutManager(linearLayoutManager);
        holder.nested_rv.setAdapter(adapterMember);

    }

    @Override
    public int getItemCount() {

        return parentItemArrayList.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        TextView tvKategorie;
        CircleImageView ivParent;
        RecyclerView nested_rv;
        OnKategorieClick onKategorieClick;

        public ViewHolder(@NonNull View itemView, OnKategorieClick onKategorieClick) {
            super(itemView);

            tvKategorie = itemView.findViewById(R.id.tvKategorie);
            ivParent = itemView.findViewById(R.id.ivparent);
            nested_rv = itemView.findViewById(R.id.nested_rv);
            this.onKategorieClick = onKategorieClick;

            itemView.findViewById(R.id.rlTabelle).setOnClickListener(this);
            itemView.findViewById(R.id.btHide_Show).setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {

            onKategorieClick.OnKategorieClick(getAdapterPosition());
        }
    }
}
