package com.example.homescreen;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MemberAdp extends RecyclerView.Adapter<MemberAdp.ViewHolder> {

    public ChildItem childItem;

    public interface OnClickOnSubKategorie {
        void OnClickOnSubFisch(ChildItem childItem);
        void OnClickOnEdit(ChildItem childItem);

    }


    ArrayList<ChildItem> childarrayList;
    private OnClickOnSubKategorie m_onClickOnSubKategorie;
    public MyAdapter myMemberAdp;

    public MemberAdp(ArrayList<ChildItem> childarrayList, OnClickOnSubKategorie onClickOnSubKategorie, MyAdapter memberAdp) {

        this.childarrayList = childarrayList;
        this.m_onClickOnSubKategorie = onClickOnSubKategorie;
        myMemberAdp = memberAdp;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_item,parent,false);

        return new ViewHolder(view, m_onClickOnSubKategorie);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        childItem = childarrayList.get(position);

        holder.tvFischArt.setText(childItem.itemName);
        holder.tvKategorieGewicht.setText(childItem.itemQty);
        holder.tvReadedGewicht.setText(childItem.itemPrice + "g");

        try {
            byte[] byteArray = childItem.currentFisch.bild_URL;
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            holder.ivgefangenerFisch.setImageBitmap(bmp);
        } catch (Exception e) {
            e.printStackTrace();
            Bitmap bmp = null;
            holder.ivgefangenerFisch.setImageBitmap(bmp);
        }

    }

    @Override
    public int getItemCount() {

        return childarrayList.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvFischArt, tvKategorieGewicht, tvReadedGewicht;
        CircleImageView ivgefangenerFisch;
        OnClickOnSubKategorie onClickOnSubKategorie;

        public ViewHolder(@NonNull View itemView, OnClickOnSubKategorie onClickOnSubKategorie) {
            super(itemView);

            tvFischArt = itemView.findViewById(R.id.tvFischArt);
            tvKategorieGewicht = itemView.findViewById(R.id.tvKategorieGewicht);
            tvReadedGewicht = itemView.findViewById(R.id.tvReadedGewicht);
            ivgefangenerFisch = itemView.findViewById(R.id.ivGefangenerFisch);
            this.onClickOnSubKategorie = onClickOnSubKategorie;

            itemView.findViewById(R.id.rl_child).setOnClickListener(this::onClickRL_Child);
            itemView.findViewById(R.id.btEditChild).setOnClickListener(this::onClickBT_EditChild);

        }

        public void onClickRL_Child(View view){
            onClickOnSubKategorie.OnClickOnSubFisch(childarrayList.get(getAdapterPosition()));
        }

        public  void onClickBT_EditChild(View view){
            //int parentPosition = findParentPosition(getAdapterPosition());
            onClickOnSubKategorie.OnClickOnEdit(childarrayList.get(getAdapterPosition()));
        }
    }

}
