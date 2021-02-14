package com.easylife.proplayer.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.easylife.proplayer.BasicFunctions;
import com.easylife.proplayer.ExpandView;
import com.easylife.proplayer.Model.MatchModel;
import com.easylife.proplayer.R;
import com.easylife.proplayer.Result;
import com.easylife.proplayer.Wallet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;



public class MatchesAdapter extends RecyclerView.Adapter<MatchesAdapter.ViewHolder>  {

    List<MatchModel> itemlist;
    Context mContext;
    String from;
    final String[] statusa={"ongoing","locked","dead"};
    DatabaseReference ref;

    BasicFunctions basicFunctions ;
    String titlel;
    Dialog dialog;
    Dialog dialogLoading;
    int balance;
    Dialog noConnection,blow,msgDialog;
    Button cBtn;

    TextView lbalance;
    TextView joiningFee;
    Button addMoney;

    TextView msgDialogTitle,msgDialogmsg,msgOk;

    int h= 0;
    int w=0;

    int joiedNumber;

    public MatchesAdapter(List<MatchModel> itemlist, Context mContext,String from) {
        this.itemlist = itemlist;
        this.mContext = mContext;
        this.from=from;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.matches_item,parent,false);
        basicFunctions=new BasicFunctions(mContext);

        dialogLoading=new Dialog(mContext);
        dialogLoading.setContentView(R.layout.loading_view);
        dialogLoading.setCancelable(false);
        ImageView imageView1 =dialogLoading.findViewById(R.id.loading_img);
        Glide.with(mContext).load(R.drawable.loading).into(imageView1);

        noConnection= new Dialog(mContext);
        noConnection.setContentView(R.layout.no_internet);

        blow = new Dialog(mContext);
        blow.setContentView(R.layout.low_balance);
        lbalance = blow.findViewById(R.id.acbal);
        joiningFee = blow.findViewById(R.id.jfee);
        addMoney = blow.findViewById(R.id.addm);

        addMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, Wallet.class);
                intent.putExtra("action","add");
                mContext.startActivity(intent);
            }
        });

        msgDialog = new Dialog(mContext);
        msgDialog.setContentView(R.layout.msg_dialog);
        msgDialogTitle = msgDialog.findViewById(R.id.title);
        msgDialogmsg = msgDialog.findViewById(R.id.msg);
        msgOk = msgDialog.findViewById(R.id.ok);

        msgOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msgDialog.dismiss();
            }
        });



        FirebaseDatabase.getInstance().getReference("accounts").child(basicFunctions.read("id")).child("balance").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String b= dataSnapshot.getValue(String.class);
                balance= Integer.parseInt(b);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        return new  ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MatchesAdapter.ViewHolder holder, final int position) {


        dialogLoading.dismiss();


        if(from.equals("matches")){
            holder.join.setVisibility(View.VISIBLE);
            holder.cancel.setVisibility(View.GONE);
            holder.idpass.setVisibility(View.GONE);
            holder.result.setVisibility(View.GONE);
        }else if(from.equals("joined")){
            holder.result.setVisibility(View.GONE);
            holder.join.setVisibility(View.GONE);
            holder.cancel.setVisibility(View.VISIBLE);
            holder.idpass.setVisibility(View.GONE);
            if(itemlist.get(position).getStatus().equals("locked")){
                holder.join.setVisibility(View.GONE);
                holder.cancel.setVisibility(View.GONE);
                holder.idpass.setVisibility(View.VISIBLE);
            }
        }else {
            holder.join.setVisibility(View.GONE);
            holder.cancel.setVisibility(View.GONE);
            holder.idpass.setVisibility(View.GONE);
            holder.result.setVisibility(View.VISIBLE);
            holder.process.setVisibility(View.GONE);
            holder.progressBar.setVisibility(View.GONE);
            if(itemlist.get(position).getStatus().equals("dead")){
                holder.result.setText("Result will be released soon");
            }
        }

        basicFunctions= new BasicFunctions(mContext);

        titlel=itemlist.get(position).getType()+" - Match #"+itemlist.get(position).getMatchId();

        holder.title.setText(titlel);
        holder.subTitel.setText("Starts on "+itemlist.get(position).getDate()+" at "+itemlist.get(position).getTime());
       // holder.prizePool.setText(itemlist.get(position).getPrizePool());
        holder.perKill.setText("₹"+itemlist.get(position).getPerKill());
        holder.winp.setText("₹"+itemlist.get(position).getPrizeDetails());
        holder.entryFee.setText("₹"+itemlist.get(position).getEntryFee());
        holder.version.setText(itemlist.get(position).getVersion());
        holder.map.setText(itemlist.get(position).getMap());
        holder.process.setText("Joined : "+itemlist.get(position).getEntryNumber()+" / "+itemlist.get(position).getMaxEntries());
        holder.progressBar.setMax(Integer.parseInt(itemlist.get(position).getMaxEntries()));
        holder.progressBar.setProgress(Integer.parseInt(itemlist.get(position).getEntryNumber()));
        holder.type.setText(itemlist.get(position).getType());
        if(from.equals("result")){
            holder.subTitel.setText("Started on "+itemlist.get(position).getDate()+" at "+itemlist.get(position).getTime());
        }


        if(Integer.parseInt(itemlist.get(position).getEntryNumber())==Integer.parseInt(itemlist.get(position).getMaxEntries())) {
            holder.join.setText("Match full");

        }

        /*switch (itemlist.get(position).getType()){
            case "Squard":
                holder.imageView.setBackgroundResource(R.drawable.squard);
                break;
            case "Duo":
                holder.imageView.setBackgroundResource(R.drawable.duo);
                break;

            case "TDM":
                holder.imageView.setBackgroundResource(R.drawable.tdm);
                break;

            case "Solo":
                holder.imageView.setBackgroundResource(R.drawable.solo);
                break;

        }*/



        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, ExpandView.class);
                intent.putExtra("action", "edit");
                intent.putExtra("id", itemlist.get(position).getMatchId());
                intent.putExtra("time", itemlist.get(position).getTime());
                intent.putExtra("date", itemlist.get(position).getDate());
                // intent.putExtra("prize pool", itemlist.get(position).getPrizePool());
                intent.putExtra("prize details", itemlist.get(position).getPrizeDetails());
                intent.putExtra("per kill", itemlist.get(position).getPerKill());
                intent.putExtra("entry fee", itemlist.get(position).getEntryFee());
                intent.putExtra("type", itemlist.get(position).getType());
                intent.putExtra("version", itemlist.get(position).getVersion());
                intent.putExtra("map", itemlist.get(position).getMap());
                intent.putExtra("max entries", itemlist.get(position).getMaxEntries());
                intent.putExtra("joined", itemlist.get(position).getEntryNumber());

                mContext.startActivity(intent);
            }

        });

        ref=FirebaseDatabase.getInstance().getReference("pubg mobile");

        final String match = itemlist.get(position).getMatchId();
        final int joinFee = Integer.parseInt(itemlist.get(position).getEntryFee());

        class Join extends AsyncTask{
            Boolean isOn;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialogLoading.show();
            }

            @Override
            protected Object doInBackground(Object[] objects) {
                isOn=basicFunctions.isInternetOn();
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);

                dialogLoading.dismiss();
                if(isOn==true){


                    HashMap<String,String> map= new HashMap<>();
                    map.put("action","You joined "+itemlist.get(position).getType()+" - Match #"+itemlist.get(position).getMatchId());
                    map.put("amount","-"+itemlist.get(position).getEntryFee());
                    map.put("time",basicFunctions.getDateTime());
                    FirebaseDatabase.getInstance().getReference("transactions").child(basicFunctions.read("id")).push().setValue(map);

                    FirebaseMessaging.getInstance().subscribeToTopic("match"+itemlist.get(position).getMatchId());
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("matchId", itemlist.get(position).getMatchId());
                    hashMap.put("accountId", basicFunctions.read("id"));
                    hashMap.put("pubgId", basicFunctions.read("pubgId"));
                    hashMap.put("name", basicFunctions.read("pubgName"));
                    hashMap.put("email", basicFunctions.read("email"));
                    ref.child("joined").child(itemlist.get(position).getMatchId() + basicFunctions.read("id")).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {



                                FirebaseDatabase.getInstance().getReference("accounts").child(basicFunctions.read("id")).child("balance").setValue(String.valueOf(balance-joinFee)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                       // Toast.makeText(mContext, "Joined successfully", Toast.LENGTH_SHORT).show();
                                        msgDialogTitle.setText("   Joined successfully   ");
                                        msgDialogmsg.setText("Room id and password will\n be given 15 minutes before\n the match");
                                        msgDialog.show();

                                    }
                                });


                            } else if (task.isCanceled()) {
                                Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });


                }else {
                    Toast.makeText(mContext, "You are not connected to internet", Toast.LENGTH_SHORT).show();
                    noConnection.show();
                }


            }
        }



        holder.join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (basicFunctions.isNetworkAvailable()) {
                    if (joinFee <= balance) {
                        if(Integer.parseInt(itemlist.get(position).getEntryNumber())<Integer.parseInt(itemlist.get(position).getMaxEntries())) {

                            Join join = new Join();
                            join.execute();
                        }else {
                            Toast.makeText(mContext, "Match is full", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        //Toast.makeText(mContext, "You have not enough balance to join", Toast.LENGTH_SHORT).show();
                        lbalance.setText("Account balance : "+balance);
                        joiningFee.setText("Match joining fee : "+joinFee);
                        blow.show();
                    }

                }else {
                    //Toast.makeText(mContext, "You are not connected to internet", Toast.LENGTH_SHORT).show();
                    noConnection.show();
                }
            }
        });


        class Cancel extends AsyncTask{
            Boolean isOn;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialogLoading.show();
            }

            @Override
            protected Object doInBackground(Object[] objects) {
                isOn=basicFunctions.isInternetOn();
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);

                dialogLoading.dismiss();
                if(isOn==true){

                    HashMap<String,String> map= new HashMap<>();
                    map.put("action","You canceled "+itemlist.get(position).getType()+" - Match #"+itemlist.get(position).getMatchId());
                    map.put("amount",itemlist.get(position).getEntryFee());
                    map.put("time",basicFunctions.getDateTime());
                    FirebaseDatabase.getInstance().getReference("transactions").child(basicFunctions.read("id")).push().setValue(map);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("match"+itemlist.get(position).getMatchId());


                    ref.child("joined").child(itemlist.get(position).getMatchId() + basicFunctions.read("id")).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                FirebaseDatabase.getInstance().getReference("accounts").child(basicFunctions.read("id")).child("balance").setValue(String.valueOf(balance+joinFee)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                       // Toast.makeText(mContext, "Canceled successfully", Toast.LENGTH_SHORT).show();
                                        msgDialogTitle.setText("Canceled successfully");
                                        msgDialogmsg.setText("Your money is refunded");
                                        msgDialog.show();


                                    }
                                });




                            } else if (task.isCanceled()) {
                                Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });



                }else {
                   // Toast.makeText(mContext, "You are not connected to internet", Toast.LENGTH_SHORT).show();
                    noConnection.show();
                }


            }
        }


        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (basicFunctions.isNetworkAvailable()) {

                    new Cancel().execute();

                }else {

                    Toast.makeText(mContext, "You are not connected to internet", Toast.LENGTH_SHORT).show();
                    noConnection.show();
                }

            }
        });


        holder.result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.result.getText().equals("View result")) {
                    Intent intent = new Intent(mContext, Result.class);
                    intent.putExtra("matchId", itemlist.get(position).getMatchId());
                    intent.putExtra("title", itemlist.get(position).getType()+" - Match #"+itemlist.get(position).getMatchId());
                    mContext.startActivity(intent);
                }
            }
        });





        holder.idpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref= FirebaseDatabase.getInstance().getReference("pubg mobile").child("IdPass");

                dialog= new Dialog(mContext);
                Button btnOk;
                final TextView title;
                dialog.setContentView(R.layout.id_pass_view);
                btnOk=(Button)dialog.findViewById(R.id.okbtn);
                title=dialog.findViewById(R.id.title_d);
                title.setText(itemlist.get(position).getType()+" - Match #"+itemlist.get(position).getMatchId());


                ref.child(itemlist.get(position).getMatchId()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String id = dataSnapshot.child("id").getValue(String.class);
                        String pass = dataSnapshot.child("pass").getValue(String.class);


                        if(id==null){

                            msgDialogTitle.setText("Not released yet");
                            msgDialogmsg.setText("Room id and password\nwill be released soon");


                        }else {

                            msgDialogTitle.setText("Released");
                            msgDialogmsg.setText("Room id : "+id+"\nPassword : "+pass);

                        }



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                msgDialog.show();

            }
        });



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

        TextView title,subTitel,prizePool,perKill,entryFee,version,map,process,winp,type;
        ProgressBar progressBar;
        CardView cardView;
        Button join,cancel,idpass,result;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title=itemView.findViewById(R.id.titel);
            subTitel=itemView.findViewById(R.id.subTitle);
            //prizePool=itemView.findViewById(R.id.prize_pool_r);
            perKill=itemView.findViewById(R.id.per_kill_r);
            entryFee=itemView.findViewById(R.id.entry_fee_r);
            version=itemView.findViewById(R.id.version_r);
            map=itemView.findViewById(R.id.map_r);
            process=itemView.findViewById(R.id.joined);
            cardView=itemView.findViewById(R.id.card);
            progressBar =itemView.findViewById(R.id.processbar);
            //imageView=itemView.findViewById(R.id.title_image);
            join=itemView.findViewById(R.id.join);
            cancel=itemView.findViewById(R.id.cancel);
            idpass=itemView.findViewById(R.id.viewid);
            result=itemView.findViewById(R.id.result);
            winp=itemView.findViewById(R.id.fstprize_r);
            type=itemView.findViewById(R.id.type);


      }
    }

    public void setItemlist(List<MatchModel> itemlist) {
        this.itemlist = itemlist;
    }





}
