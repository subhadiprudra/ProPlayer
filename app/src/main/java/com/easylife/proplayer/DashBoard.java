package com.easylife.proplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jsoup.Jsoup;

import java.io.IOException;

public class DashBoard extends AppCompatActivity {

    Toolbar toolbar;

    TabLayout tabLayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("  Pro Player" );
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.ic_whatshot_black_24dp);
        tabLayout=findViewById(R.id.tab);
        viewPager=findViewById(R.id.view_pager);

        updateChacker();



        tabLayout.addTab(tabLayout.newTab().setText("Matches"));
        tabLayout.addTab(tabLayout.newTab().setText("Joined"));
        tabLayout.addTab(tabLayout.newTab().setText("Result"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        final MyAdapter adapter = new MyAdapter(this,getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });




    }


    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tool_menu, menu);
        return true;

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch(id){

            case (R.id.wallet) :
                Intent intent = new Intent(getApplicationContext(),Wallet.class);
                intent.putExtra("action","");
                startActivity(intent);
                break;

            case(R.id.account):
                Intent intent1 = new Intent(getApplicationContext(),AccountDetails.class);
                startActivity(intent1);
                break;



        }

        return super.onOptionsItemSelected(item);
    }


    int version ;
    String link;
    String msg;
    int cutoff;
    String website;
    BasicFunctions basicFunctions;



    public void updateChacker(){


        FirebaseDatabase.getInstance().getReference("RawInfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                basicFunctions = new BasicFunctions(getApplicationContext());

                version= dataSnapshot.child("version").getValue(Integer.class);
                link = dataSnapshot.child("applink").getValue(String.class);
                msg =dataSnapshot.child("updatemsg").getValue(String.class);
                cutoff= dataSnapshot.child("cutoffv").getValue(Integer.class);
                website=dataSnapshot.child("website").getValue(String.class);
                basicFunctions.write("website",website);

                if(version>BuildConfig.VERSION_CODE){

                    AlertDialog.Builder builder = new AlertDialog.Builder(DashBoard.this);
                    builder.setTitle("Update available");

                    builder.setMessage(msg)
                            .setCancelable(false)
                            .setPositiveButton("Update now", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));

                                }
                            });

                    if(cutoff<BuildConfig.VERSION_CODE){
                        builder.setNegativeButton("Not now", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                    }

                    AlertDialog alert = builder.create();
                    alert.show();

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(DashBoard.this);
        builder.setTitle("Exit");

        builder.setMessage("Do you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        moveTaskToBack(true);
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();}

}
