package com.easylife.proplayer.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easylife.proplayer.BasicFunctions;
import com.easylife.proplayer.Model.Item;
import com.easylife.proplayer.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ResultAdapter extends  RecyclerView.Adapter<ResultAdapter.ViewHolder>{


    List<Item> itemlist;
    Context mContext;
    BasicFunctions basicFunctions;

    public ResultAdapter(List<Item> itemlist, Context mContext) {
        this.itemlist = itemlist;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ResultAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.result_item,parent,false);
        basicFunctions = new BasicFunctions(mContext);
        return new  ResultAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ResultAdapter.ViewHolder holder, int position) {
        holder.slNo.setText(position+1+"");
        holder.name.setText(itemlist.get(position).name);
        holder.kills.setText(itemlist.get(position).kills);
        if(itemlist.get(position).name.equals(basicFunctions.read("pubgName"))){
            holder.card.setBackgroundColor(mContext.getResources().getColor(R.color.liteAccent));
        }

    }

    @Override
    public int getItemCount() {
        if(itemlist==null){
            return 0;
        }else {
            return itemlist.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView slNo,name,kills;
        LinearLayout card;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            slNo=itemView.findViewById(R.id.slNo);
            name=itemView.findViewById(R.id.name);
            kills = itemView.findViewById(R.id.kills);
            card=itemView.findViewById(R.id.r_card);


        }
    }

    public void setItemlist(List<Item> itemlist) {
        this.itemlist = itemlist;
    }

}

