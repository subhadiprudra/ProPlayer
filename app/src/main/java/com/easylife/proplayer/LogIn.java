package com.easylife.proplayer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;

import java.io.IOException;


public class LogIn extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    CardView cardView;
    private GoogleApiClient googleApiClient;
    private static final int RC_SIGN_IN = 1;
    BasicFunctions basicFunctions;
    DatabaseReference ref;
    String androidId;
    ProgressDialog dialog;
    int i=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        cardView=findViewById(R.id.logIn);

        cardView.setVisibility(View.GONE);

       // new UpdateChecker().execute();



        androidId= Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);


        FirebaseDatabase.getInstance().getReference("blocked list").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    String id= data.child("androidId").getValue(String.class);


                    if(id.equals(androidId)){

                        i=1;

                        AlertDialog.Builder builder = new AlertDialog.Builder(LogIn.this);
                        builder.setTitle("Exit");
                        builder.setCancelable(false);

                        builder.setMessage("Your account is blocked")
                                .setCancelable(false)
                                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //moveTaskToBack(true);
                                        android.os.Process.killProcess(android.os.Process.myPid());
                                        System.exit(1);
                                    }
                                });

                        AlertDialog alert = builder.create();
                        alert.show();

                    }
                }

                if (i == 0) {
                    inIt();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });








    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

       /* if (requestCode == RC_SIGN_IN) {

            Toast.makeText(this, "re", Toast.LENGTH_SHORT).show();
            if( resultCode == RESULT_OK) {
                GoogleSignIn.getSignedInAccountFromIntent(data).addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
                    @Override
                    public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                        handleSignInResult(result);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LogIn.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

            }else {
                Toast.makeText(this, "Result is not ok", Toast.LENGTH_SHORT).show();
            }
        }*/

        if(requestCode==RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }



    @Override
    protected void onStart() {
        super.onStart();

    }

    public void inIt(){


        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent,RC_SIGN_IN);
            }
        });
        cardView.setVisibility(View.GONE);


        GoogleSignInOptions gso =  new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleApiClient=new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        OptionalPendingResult<GoogleSignInResult> opr= Auth.GoogleSignInApi.silentSignIn(googleApiClient);

        if(opr.isDone()){
            GoogleSignInResult result=opr.get();
            handleSignInResult(result);

        }else{

            cardView.setVisibility(View.VISIBLE);

        }
    }



    private void handleSignInResult(GoogleSignInResult result){
        basicFunctions=new BasicFunctions(getApplicationContext());
        GoogleSignInAccount account = result.getSignInAccount();


        ref = FirebaseDatabase.getInstance().getReference("accounts").child(account.getId());

        basicFunctions.write("email", account.getEmail());
        basicFunctions.write("name", account.getDisplayName());
        basicFunctions.write("id", account.getId());


        if (result.isSuccess()) {


            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    String num = dataSnapshot.child("number").getValue(String.class);
                    if (num==null) {
                        Intent intent = new Intent(getApplicationContext(), EditProfile.class);
                        intent.putExtra("action","add");
                        startActivity(intent);
                        finish();
                    } else {
                        basicFunctions.write("pubgId",dataSnapshot.child("pubgId").getValue(String.class));
                        basicFunctions.write("pubgName",dataSnapshot.child("pubgName").getValue(String.class));
                        Intent intent = new Intent(getApplicationContext(), DashBoard.class);
                        startActivity(intent);
                        finish();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        } else {
             Toast.makeText(getApplicationContext(), result.getStatus().getStatusMessage(), Toast.LENGTH_LONG).show();
        }



    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
