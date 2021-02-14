package com.easylife.proplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.easylife.proplayer.Model.JoinedModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ExpandView extends AppCompatActivity {

    TextView title,subTitel,prizePool,perKill,entryFee,version,map,process,winp,type;
    ProgressBar progressBar;
    ImageView imageView;
    Button button;
    TextView rules,names;

    String ruleduo=  "1. PUBG Mobile Accounts with level less then 30 will be kicked out from the room.\n\n" +
                     "2. For this match there will be two winners and the 1st prize will be distributed among two winners equally.\n\n" +
                     "3. Each team will have two participants. Each participants have to join this match with their individual app.\n\n" +
                     "4. If in anyway fail to join the match in given time then we are not responsible for it. Refund will not given in such cases.\n\n" +
                     "5. Room id and password will be given 15 minutes before the match.\n\n" +
                     "6. Do not change you profile details after joining any match.\n\n" +
                     "7. Once you join the match do not keep changing your positions if you do so you will be kicked out from the room.\n\n" +
                     "8. Do not use any Hacks or emulator. If you do so your account will be banned without any warnings.\n\n" +
                     "9. Do not share your room id and password with anyone otherwise your account will be banned without any warnings.\n\n" +
                     "10. If you need any kind of help contact us on help@ProPlayer@gmail.com";

    String rulesuqard=  "1. PUBG Mobile Accounts with level less then 30 will be kicked out from the room.\n\n" +
            "2. For this match there will be four winners and the 1st prize will be distributed among four winners equally.\n\n" +
            "3. Each team will have four participants. Each participants have to join this match with their individual app.\n\n" +
            "4. If in anyway fail to join the match in given time then we are not responsible for it. Refund will not given in such cases.\n\n" +
            "5. Room id and password will be given 15 minutes before the match.\n\n" +
            "6. Do not change you profile details after joining any match.\n\n" +
            "7. Once you join the match do not keep changing your positions if you do so you will be kicked out from the room.\n\n" +
            "8. Do not use any Hacks or emulator. If you do so your account will be banned without any warnings.\n\n" +
            "9. Do not share your room id and password with anyone otherwise your account will be banned without any warnings.\n\n" +
            "10. If you need any kind of help contact us on help@ProPlayer@gmail.com";

    String rulesolo=  "1. PUBG Mobile Accounts with level less then 30 will be kicked out from the room.\n\n" +
            "2. If in anyway fail to join the match in given time then we are not responsible for it. Refund will not given in such cases.\n\n" +
            "3. Room id and password will be given 15 minutes before the match.\n\n" +
            "4. Do not change you profile details after joining any match.\n\n" +
            "5. Once you join the match do not keep changing your positions if you do so you will be kicked out from the room.\n\n" +
            "6. Do not use any Hacks or emulator. If you do so your account will be banned without any warnings.\n\n" +
            "7. Do not share your room id and password with anyone otherwise your account will be banned without any warnings.\n\n" +
            "8. If you need any kind of help contact us on help@ProPlayer@gmail.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expand_view);

        title=findViewById(R.id.titel);
        subTitel=findViewById(R.id.subTitle);
        //prizePool=itemView.findViewById(R.id.prize_pool_r);
        perKill=findViewById(R.id.per_kill_r);
        entryFee=findViewById(R.id.entry_fee_r);
        version=findViewById(R.id.version_r);
        map=findViewById(R.id.map_r);
        process=findViewById(R.id.joined);
        progressBar =findViewById(R.id.processbar);
       // imageView=findViewById(R.id.title_image);
        winp=findViewById(R.id.fstprize_r);
        type=findViewById(R.id.type);
        button=findViewById(R.id.load);

        names=findViewById(R.id.participants);
        String titlel=getIntent().getStringExtra("type")+" - Match #"+getIntent().getStringExtra("id");


        title.setText(titlel);
        subTitel.setText("Starts on "+getIntent().getStringExtra("date")+" at "+getIntent().getStringExtra("time"));
        // holder.prizePool.setText(itemlist.get(position).getPrizePool());
        perKill.setText("₹"+getIntent().getStringExtra("per kill"));
        winp.setText("₹"+getIntent().getStringExtra("prize details"));
        entryFee.setText("₹"+getIntent().getStringExtra("entry fee"));
        version.setText(getIntent().getStringExtra("version"));
        map.setText(getIntent().getStringExtra("map"));
        process.setText("Joined : "+getIntent().getStringExtra("joined")+" / "+getIntent().getStringExtra("max entries"));
        progressBar.setMax(Integer.parseInt(getIntent().getStringExtra("max entries")));
        progressBar.setProgress(Integer.parseInt(getIntent().getStringExtra("joined")));
        type.setText(getIntent().getStringExtra("type"));
        rules=findViewById(R.id.rules);



        switch (getIntent().getStringExtra("type")){
            case "Squard":
                //imageView.setBackgroundResource(R.drawable.squard);
                rules.setText(rulesuqard);
                break;
            case "Duo":
               // imageView.setBackgroundResource(R.drawable.duo);
                rules.setText(ruleduo);
                break;

            case "TDM":
                //imageView.setBackgroundResource(R.drawable.tdm);
                break;

            case "Solo":
               // imageView.setBackgroundResource(R.drawable.solo);
                rules.setText(rulesolo);
                break;

        }


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setVisibility(View.GONE);
                FirebaseDatabase.getInstance().getReference("pubg mobile").child("joined").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int i=1;
                        for(DataSnapshot data : dataSnapshot.getChildren()){
                            JoinedModel joinedModel = data.getValue(JoinedModel.class);
                            if(joinedModel.getMatchId().equals(getIntent().getStringExtra("id"))){
                                names.append(i+". "+joinedModel.getName()+"\n");
                                i++;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }
}
