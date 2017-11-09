package com.example.israelsson.onemorerepfitness;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.israelsson.onemorerepfitness.model.Results;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ShowResults extends AppCompatActivity {
    ArrayAdapter<String> adapter;
    FirebaseDatabase database;
    DatabaseReference myRefResults;
    int posInt;
    String position;
    ArrayList<String> resultList = new ArrayList();
    private ListView resultListView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_results);

        position = String.valueOf(getIntent().getIntExtra("workout_position", posInt));
        Log.d("Recieved", position);

        database = FirebaseDatabase.getInstance();
        myRefResults = database.getReference().child("results").child(position);
        resultListView = (ListView) findViewById(R.id.RESULT_LIST_VIEW);
        adapter = new ArrayAdapter(this, R.layout.result_listview_item , resultList);
        resultListView.setAdapter(adapter);


        myRefResults.addChildEventListener(new ChildEventListener() {

            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Results results = (Results) dataSnapshot.getValue(Results.class);
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
}
