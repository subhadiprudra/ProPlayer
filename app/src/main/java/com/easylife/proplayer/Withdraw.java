package com.easylife.proplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.UUID;

public class Withdraw extends AppCompatActivity {

    int balance;
    BasicFunctions basicFunctions;
    com.google.android.material.textfield.TextInputEditText amount,number;
    Button withdraw;
    Dialog dialog,dialogLoading;

    TextView msgDialogTitle,msgDialogmsg;
    Button ok;
    TextView bal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);

        bal=findViewById(R.id.balance);

        dialog=new Dialog(this);
        dialog.setContentView(R.layout.msg_dialog);
        msgDialogmsg=dialog.findViewById(R.id.msg);
        msgDialogTitle = dialog.findViewById(R.id.title);
        ok=dialog.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialogLoading=new Dialog(this);
        dialogLoading.setContentView(R.layout.loading_view);
        dialogLoading.setCancelable(false);
        ImageView imageView1 =dialogLoading.findViewById(R.id.loading_img);
        Glide.with(this).load(R.drawable.loading).into(imageView1);

        basicFunctions= new BasicFunctions(this);

        FirebaseDatabase.getInstance().getReference("accounts").child(basicFunctions.read("id")).child("balance").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String b= dataSnapshot.getValue(String.class);
                bal.setText(b);
                balance= Integer.parseInt(b);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        amount=findViewById(R.id.amount);
        number=findViewById(R.id.number);
        withdraw=findViewById(R.id.withdraw);


        withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int a= Integer.parseInt(amount.getText().toString());
                if(a>=20) {
                    if(balance>=a) {

                        dialogLoading.show();

                        String parent=generateString();


                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("number", number.getText().toString());
                        hashMap.put("amount", amount.getText().toString());
                        hashMap.put("id",basicFunctions.read("id"));
                        hashMap.put("time",basicFunctions.getDateTime());
                        hashMap.put("parent",parent);
                        hashMap.put("token",basicFunctions.read("token"));
                        FirebaseDatabase.getInstance().getReference("withdraw request").child(parent).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                int b = balance - Integer.parseInt(amount.getText().toString());
                                String x = String.valueOf(b);
                                FirebaseDatabase.getInstance().getReference("accounts").child(basicFunctions.read("id")).child("balance").setValue(x).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        msgDialogTitle.setText("Money withdrawn successfully");
                                        msgDialogmsg.setText("You will get your money\nin paytm wallet within\n6 hours");
                                        dialog.show();
                                        dialogLoading.dismiss();
                                        //Toast.makeText(Withdraw.this, "Money withdrawn successfully", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        });

                        HashMap<String,String> map= new HashMap<>();
                        map.put("action","You requested to transfer wallet balance to paytm");
                        map.put("amount","-"+amount.getText().toString());
                        map.put("time",basicFunctions.getDateTime());
                        FirebaseDatabase.getInstance().getReference("transactions").child(basicFunctions.read("id")).push().setValue(map);


                    }else {
                        Toast.makeText(Withdraw.this, "You have not enough balance", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(Withdraw.this, "Maximum withdraw limit is 20", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    private String generateString() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }
}




