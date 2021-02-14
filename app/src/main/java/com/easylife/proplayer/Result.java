package com.easylife.proplayer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.easylife.proplayer.Adapters.MatchesAdapter;
import com.easylife.proplayer.Adapters.ResultAdapter;
import com.easylife.proplayer.Model.Item;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class Result extends AppCompatActivity {

    RecyclerView killView,runnersUp;
    ResultAdapter killAdapter,runnersAdapter;
    DatabaseReference ref;

    List<Item> runnersUpList,killList;

    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Results" );
        toolbar.setSubtitle(getIntent().getStringExtra("title"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        killView=findViewById(R.id.killsv);
        runnersUp=findViewById(R.id.runnersup);

        runnersUpList = new ArrayList<>();
        killList = new ArrayList<>();


        runnersAdapter= new ResultAdapter(runnersUpList,this);
        runnersUp.hasFixedSize();
        runnersUp.setAdapter(runnersAdapter);
        runnersUp.setLayoutManager(new LinearLayoutManager(this));

        killAdapter= new ResultAdapter(killList,this);
        killView.hasFixedSize();
        killView.setAdapter(killAdapter);
        killView.setLayoutManager(new LinearLayoutManager(this));

        ref= FirebaseDatabase.getInstance().getReference("pubg mobile").child("results").child(getIntent().getStringExtra("matchId"));

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    Item item = data.getValue(Item.class);

                    if(item.kills.contains("(")){

                        String kills= item.kills;
                        String x= item.kills.substring(0,item.kills.indexOf("("));
                        String y=item.kills.substring(item.kills.indexOf("(")+1,item.kills.indexOf(")"));

                        Item item1 = new Item();
                        item1.setKills(x);
                        item1.setName(item.name);
                        killList.add(item1);

                        Item item2 = new Item();
                        item2.setKills(y);
                        item2.setName(item.name);
                        runnersUpList.add(item2);

                    }else {
                        killList.add(item);
                    }





                }

                killAdapter.setItemlist(killList);
                killAdapter.notifyDataSetChanged();

                runnersAdapter.setItemlist(runnersUpList);
                runnersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }


}
