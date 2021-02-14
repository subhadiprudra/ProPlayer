package com.easylife.proplayer.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class Result extends Fragment {

    RecyclerView recyclerView;
    DatabaseReference fdb;
    List<MatchModel> matchModelList;
    List<JoinedModel> joinedModels;
    MatchesAdapter adapter;
    Context context;
    BasicFunctions basicFunctions;

    Dialog dialog;

    LinearLayout linearLayout;
    TextView textView;



    public Result() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        matchModelList=new ArrayList<>();
        joinedModels = new ArrayList<>();
        basicFunctions = new BasicFunctions(getContext());

        View view = inflater.inflate(R.layout.matches_view, container, false);

        linearLayout =view.findViewById(R.id.emptyView);
        textView=view.findViewById(R.id.textView);
        textView.setText("No result to show");
        linearLayout.setVisibility(View.GONE);



        dialog=new Dialog(getContext());
        dialog.setContentView(R.layout.loading_view);
        dialog.setCancelable(false);

        adapter= new MatchesAdapter(matchModelList,getContext(),"result");
        recyclerView= view.findViewById(R.id.matches_re);
        recyclerView.hasFixedSize();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        matchModelListGenarate();


        return view;
    }

    public void matchModelListGenarate(){


        fdb= FirebaseDatabase.getInstance().getReference("pubg mobile");

        fdb.child("matches").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //dialog.show();

                try {

                    matchModelList.clear();

                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        MatchModel matchModel = data.getValue(MatchModel.class);
                        assert matchModel != null;

                        if (matchModel.getStatus().equals("dead") || matchModel.getStatus().equals("released")) {
                            matchModelList.add(matchModel);
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
                }catch (NullPointerException e){}
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();

            }
        });

    }



}
