package com.example.israelsson.onemorerepfitness;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.israelsson.onemorerepfitness.adapters.ResultsAdapter;
import com.example.israelsson.onemorerepfitness.models.Results;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ShowResults extends AppCompatActivity implements ResultsAdapter.OnItemClickHandler {
    FirebaseDatabase database;
    DatabaseReference myRefResults;
    int posInt;
    String position;
    ArrayList<String> resultList = new ArrayList();
    private RecyclerView resultRecyclerView;
    private ResultsAdapter adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_results);
        ActionBar actionBar = this.getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setElevation(0);
        }

        //Get the workout_position from the Intent used to determine which workout is currently selected.
        position = getIntent().getStringExtra("workout_position");
        Log.d("recieved", position);

        //Initialize the FirebaseReference
        database = FirebaseDatabase.getInstance();
        myRefResults = database.getReference().child("results").child(position);

        resultRecyclerView = findViewById(R.id.rv_results);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        resultRecyclerView.setLayoutManager(layoutManager);
        resultRecyclerView.setHasFixedSize(true);
        adapter = new ResultsAdapter(resultList, this);
        resultRecyclerView.setAdapter(adapter);


        /*
        Get the results for this workout position form Firebase, add them to resultList and notify
        the adapter.
         */
        myRefResults.addChildEventListener(new ChildEventListener() {

            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Results results = dataSnapshot.getValue(Results.class);
                resultList.add(0, results.getDate() + "\n\n" + results.getTime() + "\n");
                adapter.notifyDataSetChanged();
            }

            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onClick(String result) {
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }
}
