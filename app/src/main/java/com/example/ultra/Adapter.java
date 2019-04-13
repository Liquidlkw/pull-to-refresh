package com.example.ultra;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>{

    private final List<Bean> mlist;

    public Adapter(List<Bean> list)
    {
        mlist = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view  = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Bean bean = mlist.get(i);
        Picasso
                .with(viewHolder.itemView.getContext())
                .load(bean.getPicSmall())
                .into(viewHolder.image);

        viewHolder.descripton.setText(bean.getDescription());
        viewHolder.name.setText(bean.getName());
        viewHolder.num.setText(bean.getLearner()+"");


    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        private final ImageView image;
        private final TextView name;
        private final TextView descripton;
        private final TextView num;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.pic);
            name = itemView.findViewById(R.id.name);
            descripton = itemView.findViewById(R.id.description);
            num = itemView.findViewById(R.id.number);


        }
    }
}
