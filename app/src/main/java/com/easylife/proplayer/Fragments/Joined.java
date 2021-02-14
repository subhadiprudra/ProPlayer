package com.easylife.proplayer.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.easylife.proplayer.Adapters.MatchesAdapter;
import com.easylife.proplayer.BasicFunctions;
import com.easylife.proplayer.Model.JoinedModel;
import com.easylife.proplayer.Model.MatchModel;
import com.easylife.proplayer.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Joined extends Fragment {

    RecyclerView recyclerView;
    DatabaseReference fdb;
    List<MatchModel> matchModelList;
    List<JoinedModel> joinedModels;
    MatchesAdapter adapter;
    Context context;
    BasicFunctions basicFunctions;
    ImageView imageView;
    LinearLayout linearLayout;
    TextView textView;
    Dialog dialog;



    public Joined() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        matchModelList=new ArrayList<>();
        joinedModels = new ArrayList<>();
        basicFunctions = new BasicFunctions(getContext());

        View view = inflater.inflate(R.layout.matches_view, container, false);
        dialog=new Dialog(getContext());
        dialog.setContentView(R.layout.loading_view);
        dialog.setCancelable(false);
        ImageView imageView1 =dialog.findViewById(R.id.loading_img);
        Glide.with(view).load(R.drawable.loading).into(imageView1);


        imageView=view.findViewById(R.id.image_view);
        Glide.with(view).load(R.drawable.empty2).into(imageView);
        linearLayout =view.findViewById(R.id.emptyView);
        textView=view.findViewById(R.id.textView);
        textView.setText("You have not joined in any match yet");
        linearLayout.setVisibility(View.GONE);


        adapter= new MatchesAdapter(matchModelList,getContext(),"joined");
        recyclerView= view.findViewById(R.id.matches_re);
        recyclerView.hasFixedSize();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        fdb= FirebaseDatabase.getInstance().getReference("pubg mobile");

        fdb.child("joined").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                joinedModels.clear();

                for (DataSnapshot data : dataSnapshot.getChildren()){
                    JoinedModel joinedModel = data.getValue(JoinedModel.class);
                    assert joinedModel != null;

                    joinedModels.add(joinedModel);
                }

                matchModelListGenarate(joinedModels);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();

            }
        });


        return view;
    }

    public void matchModelListGenarate(final List<JoinedModel> joinList){



        fdb.child("matches").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                matchModelList.clear();

                for (DataSnapshot data : dataSnapshot.getChildren()){
                    MatchModel matchModel = data.getValue(MatchModel.class);
                    assert matchModel != null;
                    if( matchModel.getStatus().equals("dead")||matchModel.getStatus().equals("released")){
                        continue;
                    }
                    for(int i=0;i<joinList.size();i++){
                        try {
                            if (joinList.get(i).getAccountId().equals(basicFunctions.read("id")) && joinList.get(i).getMatchId().equals(matchModel.getMatchId())) {
                                matchModelList.add(matchModel);
                                break;
                            }
                        }catch (Exception e){

                        }
                    }
                }

                for(int i=0;i<joinList.size();i++){
                    for(int j=0;j<matchModelList.size();j++){
                        if(joinList.get(i).getMatchId().equals(matchModelList.get(j).getMatchId())){
                            int joined= Integer.parseInt(matchModelList.get(j).getEntryNumber())+1;
                            String joieds= String.valueOf(joined);
                            matchModelList.get(j).setEntryNumber(joieds);
                        }
                    }
                }



                if(!matchModelList.isEmpty()){
                    linearLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }else {
                    linearLayout.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }

                adapter.setItemlist(matchModelList);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();

            }
        });

    }



}
