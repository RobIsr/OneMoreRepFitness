package com.example.israelsson.onemorerepfitness;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
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
    private String mResultReference;
    private int mResultPosition;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_results);
        ActionBar actionBar = this.getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setElevation(10);
        }

        //Get the workout_position from the Intent used to determine which workout is currently selected.
        position = getIntent().getStringExtra("workout_position");
        mResultReference = getIntent().getStringExtra("result_difficulty");
        Log.d("recieved", position + mResultReference);

        //Initialize the FirebaseReference
        database = FirebaseDatabase.getInstance();
        myRefResults = database.getReference().child(mResultReference).child(position);

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
    public void onClick(View v) {
        final View deleteView = v;
        final ImageButton deleteButton = v.findViewById(R.id.deleteButton);

        if (deleteButton.getVisibility() == View.VISIBLE) {
            deleteView.setBackgroundColor(Color.TRANSPARENT);
            deleteButton.setVisibility(View.INVISIBLE);
        } else {
            deleteView.setBackgroundColor(Color.RED);
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (view == deleteButton) {
                        Toast.makeText(getApplicationContext(), "Will be deleted", Toast.LENGTH_SHORT).show();
                        deleteButton.setVisibility(View.INVISIBLE);
                        deleteView.setBackgroundColor(Color.TRANSPARENT);
                    }
                }
            });
        }
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
