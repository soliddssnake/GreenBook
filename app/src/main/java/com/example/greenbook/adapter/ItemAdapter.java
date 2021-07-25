package com.example.greenbook.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenbook.databinding.RecyclerviewRowBinding;
import com.example.greenbook.model.Item;
import com.example.greenbook.view.addAct;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemHolder> {

    private ArrayList<Item> itemArrayList;

    public ItemAdapter(ArrayList<Item> itemArrayList) {
        this.itemArrayList = itemArrayList;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerviewRowBinding recyclerviewRowBinding = RecyclerviewRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ItemHolder(recyclerviewRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.ItemHolder holder, int position) {

        holder.recyclerviewRowBinding.recyclerViewRowDateView.setText(itemArrayList.get(position).date);
        holder.recyclerviewRowBinding.recyclerViewRowTitleView.setText(itemArrayList.get(position).title);
        Picasso.get().load(itemArrayList.get(position).downloadUrl).into(holder.recyclerviewRowBinding.recyclerViewRowImageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.itemView.getContext(), addAct.class);
                intent.putExtra("info","detail");
                intent.putExtra("itemId",itemArrayList.get(position).downloadUrl);
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemArrayList.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder{

        RecyclerviewRowBinding recyclerviewRowBinding;

        public ItemHolder(RecyclerviewRowBinding recyclerviewRowBinding) {
            super(recyclerviewRowBinding.getRoot());
            this.recyclerviewRowBinding = recyclerviewRowBinding;
        }
    }
}
