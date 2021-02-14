package com.easylife.proplayer.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easylife.proplayer.BasicFunctions;
import com.easylife.proplayer.Model.TransactionModel;
import com.easylife.proplayer.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;



public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder>   {

    Context mContext;
    List<TransactionModel> list;

    public TransactionAdapter(Context mContext, List<TransactionModel> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public TransactionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.transaction_view,parent,false);
        return new  TransactionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionAdapter.ViewHolder holder, int position) {

        holder.action.setText(list.get(position).getAction());
        holder.time.setText(list.get(position).getTime());
        holder.amount.setText(list.get(position).getAmount());

        if(!list.get(position).getAmount().contains("-")){
            holder.amount.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
            holder.amount.setText("+"+list.get(position).getAmount());
        }else {
            holder.amount.setTextColor(mContext.getResources().getColor(R.color.deepAccent));
        }

    }

    @Override
    public int getItemCount() {
        if(list==null){
            return 0;
        }else {
            return list.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView action,amount,time;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            action= itemView.findViewById(R.id.action);
            amount=itemView.findViewById(R.id.amount);
            time=itemView.findViewById(R.id.time);
        }
    }

    public void setList(List<TransactionModel> list) {
        this.list = list;
    }
}
