package com.easylife.proplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.easylife.proplayer.Payment.PaymentActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Wallet extends AppCompatActivity {
    Toolbar toolbar;

    Button add,withdraw;

    DatabaseReference reference;
    BasicFunctions basicFunctions;
    TextView balancetv;
    Dialog dialog;
    Button tran;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        balancetv=findViewById(R.id.balance);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("My wallet" );
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        basicFunctions = new BasicFunctions(this);

        withdraw=findViewById(R.id.withdrawMoney);




        dialog= new Dialog(this);

        reference= FirebaseDatabase.getInstance().getReference("accounts").child(basicFunctions.read("id")).child("balance");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String balance = dataSnapshot.getValue(String.class);
                balancetv.setText(balance);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        add=findViewById(R.id.addMoney);
        Button btnOk;
        final EditText editText;
        dialog.setContentView(R.layout.add_money);

        btnOk=(Button)dialog.findViewById(R.id.addbtn);
        editText=dialog.findViewById(R.id.amount);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!editText.getText().toString().equals("")) {
                    Intent intent = new Intent(Wallet.this, PaymentActivity.class);
                    intent.putExtra("amount", editText.getText().toString());
                    startActivity(intent);
                }
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.show();

            }
        });

        if(getIntent().getStringExtra("action").equals("add")){
            dialog.show();
        }

        withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Withdraw.class);
                startActivity(intent);
            }
        });

        tran=findViewById(R.id.viewTransactions);
        tran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ViewTran.class);
                startActivity(intent);
            }
        });




    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){ finish();}
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }
}
