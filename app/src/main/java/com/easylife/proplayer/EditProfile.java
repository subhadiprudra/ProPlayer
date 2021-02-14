package com.easylife.proplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.easylife.proplayer.Model.AccountModel;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EditProfile extends AppCompatActivity {

    Toolbar toolbar;
    DatabaseReference ref,reference;
    com.google.android.material.textfield.TextInputEditText number,id,username;
    CardView save;
    BasicFunctions basicFunctions;
    String action;

    Dialog msgDialog;
    TextView msgDialogTitle,msgDialogmsg;
    Button msgOk;
    String balance;

    String token="ddd";

    List<AccountModel> accounts;
    GoogleApiClient googleApiClient;
    GoogleSignInOptions gso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        basicFunctions= new BasicFunctions(this);

        msgDialog = new Dialog(this);
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


        action= getIntent().getStringExtra("action");
        if(action.equals(" ")){
            msgDialogTitle.setText("Alert !");
            msgDialogmsg.setText("Do not change any info\nafter joining any match");
            msgDialog.show();
        }

        number=findViewById(R.id.number);
        id=findViewById(R.id.pubgid);
        username=findViewById(R.id.username);
        save=findViewById(R.id.save);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Edit profile" );
        //toolbar.setSubtitle("AddScdul friends");
        setSupportActionBar(toolbar);

        final ProgressDialog dialog,dialog1;

        dialog = new ProgressDialog(this); // this = YourActivity
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Initialising...");
        dialog.setMessage("Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        dialog1 = new ProgressDialog(this); // this = YourActivity
        dialog1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog1.setTitle("Initialising...");
        dialog1.setMessage("Please wait...");
        dialog1.setIndeterminate(true);
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();




        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        accounts= new ArrayList<>();

        FirebaseDatabase.getInstance().getReference("accounts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                accounts.clear();

                for(DataSnapshot data : dataSnapshot.getChildren()){
                    AccountModel accountModel = data.getValue(AccountModel.class);
                    accounts.add(accountModel);
                }

                dialog1.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        reference= FirebaseDatabase.getInstance().getReference("accounts").child(basicFunctions.read("id")).child("balance");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                balance = dataSnapshot.getValue(String.class);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        ref = FirebaseDatabase.getInstance().getReference("accounts").child(basicFunctions.read("id"));

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!number.getText().toString().equals("") || id.getText().toString().equals("") || !username.getText().toString().equals("")){


                    FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if(task.isSuccessful()){
                                token= task.getResult().getToken();
                                FirebaseMessaging.getInstance().subscribeToTopic("all");
                                if(isIdUserNamePresent(id.getText().toString(),username.getText().toString())){
                                    saveData();
                                }else {
                                    Toast.makeText(EditProfile.this, "User id or Username is already register with another account", Toast.LENGTH_SHORT).show();
                                }


                            }
                        }
                    });



                }else {
                    Toast.makeText(EditProfile.this, "Fill all boxes", Toast.LENGTH_SHORT).show();
                }
            }
        });


        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String num = dataSnapshot.child("number").getValue(String.class);
                String pubgId = dataSnapshot.child("pubgId").getValue(String.class);
                String pubgName = dataSnapshot.child("pubgName").getValue(String.class);

                number.setText(num);
                id.setText(pubgId);
                username.setText(pubgName);
                dialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }


    public  void saveData(){

        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("email",basicFunctions.read("email"));
        hashMap.put("name",basicFunctions.read("name"));
        hashMap.put("number",number.getText().toString());
        hashMap.put("pubgId",id.getText().toString());
        hashMap.put("pubgName",username.getText().toString());
        hashMap.put("androidId", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
        hashMap.put("token",token);

        if(action.equals(" ")){
            hashMap.put("balance",balance);
        }else {
            hashMap.put("balance","0");
        }

        ref.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(EditProfile.this, "Saved successfully", Toast.LENGTH_SHORT).show();
                    basicFunctions.write("pubgId",id.getText().toString());
                    basicFunctions.write("pubgName",username.getText().toString());
                    basicFunctions.write("token",token);
                    if(action.equals("add")){
                        Intent intent = new Intent(getApplicationContext(),DashBoard.class);
                        startActivity(intent);
                        finish();
                    }

                }

                else {
                    Toast.makeText(EditProfile.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
           /* if(action.equals("add")){

                gso =  new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();


                googleApiClient=new GoogleApiClient.Builder(this)
                        .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                        .build();

                googleApiClient.connect();


                Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                if (status.isSuccess()){
                                    finish();

                                }else{
                                    Toast.makeText(getApplicationContext(),"Session not close", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }*/
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

           /* if(action.equals("add")){

                gso =  new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();


                googleApiClient=new GoogleApiClient.Builder(this)
                        .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                        .build();

                googleApiClient.connect();


                Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                if (status.isSuccess()){
                                    finish();

                                }else{
                                    Toast.makeText(getApplicationContext(),"Session not close", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }*/
            finish();


    }

    public boolean isIdUserNamePresent(String id , String username){

        int i=0;

        for(AccountModel model : accounts){
            if(model.getPubgId().equals(id) || model.getPubgName().equals(username)){
                i=1;
                break;
            }

        }

        if(i==0){
            return true;
        }else {
            return false;
        }

    }

}
