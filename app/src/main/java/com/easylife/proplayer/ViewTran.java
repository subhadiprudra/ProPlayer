package com.easylife.proplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.easylife.proplayer.Adapters.TransactionAdapter;
import com.easylife.proplayer.Model.TransactionModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ViewTran extends AppCompatActivity {

    RecyclerView recyclerView;
    TransactionAdapter adapter;
    List<TransactionModel> list,list1;
    BasicFunctions basicFunctions;
    Toolbar toolbar;
    LinearLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tran);

        linearLayout=findViewById(R.id.emptyView);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Transactions" );
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        recyclerView=findViewById(R.id.re_tran);
        list= new ArrayList<>();
        list1= new ArrayList<>();
        basicFunctions= new BasicFunctions(this);

        adapter= new TransactionAdapter(this,list);
        recyclerView.hasFixedSize();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseDatabase.getInstance().getReference("transactions").child(basicFunctions.read("id")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                list1.clear();
                list.clear();


                for(DataSnapshot data : dataSnapshot.getChildren()){
                    TransactionModel transactionModel = data.getValue(TransactionModel.class);
                    list.add(transactionModel);
                }

                for(int i =0;i<list.size();i++){
                    list1.add(list.get(list.size()-i-1));
                }

                adapter.setList(list1);
                if(list1.isEmpty()){
                    recyclerView.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.VISIBLE);

                }else {
                    recyclerView.setVisibility(View.VISIBLE);
                    linearLayout.setVisibility(View.GONE);

                }
                adapter.notifyDataSetChanged();

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


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }
}
