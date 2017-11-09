package com.example.israelsson.onemorerepfitness;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.israelsson.onemorerepfitness.model.Results;
import com.example.israelsson.onemorerepfitness.model.Workouts;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private TextView workoutTextView;
    private TextView resetButton;
    private TextView saveButton;
    private TextView numberOfResults;
    private long timeWhenStopped = 0;
    boolean isTimeActivated = false;
    Chronometer chronometer;
    int position = 0;
    DatabaseReference myRef;
    DatabaseReference myRefResults;
    ArrayList<Workouts> items = new ArrayList<>();
    ArrayList<String> resultList = new ArrayList();
    Workouts value;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("workouts");
        myRefResults = database.getReference("results");
        getFireBaseValues();

        workoutTextView = (TextView) findViewById(R.id.workoutTextView);

        chronometer = (Chronometer) findViewById(R.id.chronometer3);


        numberOfResults = (TextView) findViewById(R.id.number_of_results);
        getResults();

        numberOfResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ShowResults.class);
                intent.putExtra("workout_position", position);
                startActivity(intent);
            }
        });


        ImageView timingButton = (ImageView) findViewById(R.id.timingButton);
        timingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isTimeActivated) {
                    chronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
                    chronometer.start();
                    isTimeActivated = true;
                } else if (isTimeActivated) {
                    chronometer.getBase();
                    chronometer.stop();
                    timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime();
                    isTimeActivated = false;
                    resetButton.setVisibility(View.VISIBLE);
                    saveButton.setVisibility(View.VISIBLE);
                }
            }
        });


        ImageView previousButton = (ImageView) findViewById(R.id.previousWorkoutButton);
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position > 0) {
                    position--;
                    getFireBaseValues();
                    getResults();
                }
            }
        });


        ImageView nextButton = (ImageView) findViewById(R.id.nextWorkoutButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position < items.size() - 1) {
                    position++;
                    getFireBaseValues();
                    getResults();
                }
            }
        });


        resetButton = (TextView) findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chronometer.setBase(SystemClock.elapsedRealtime());
                timeWhenStopped = 0;
                resetButton.setVisibility(View.INVISIBLE);
                saveButton.setVisibility(View.INVISIBLE);
            }
        });


        saveButton = (TextView) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog saveDialog = new Dialog(MainActivity.this);
                saveDialog.setContentView(R.layout.save_dialog);
                saveDialog.show();

                TextView positiveButton = (TextView) saveDialog.findViewById(R.id.positiveButton);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        resultList.clear();
                        setResults();
                        saveDialog.dismiss();
                    }
                });
            }
        });

    }


    public void getFireBaseValues() {
        items.clear();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {
                    value = child.getValue(Workouts.class);
                    items.add(value);
                }

                workoutTextView.setText(items.get(position).getDescription().toUpperCase() + "\n" + items.get(position).getExersize_1() + "\n" + items.get(position).getExersize_2()
                        + "\n" + items.get(position).getExersize_3() + "\n" + items.get(position).getExersize_4());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Error: " + databaseError.getCode());
            }
        });
    }

    public void setResults() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Date date = new Date();
        String d = df.format(date);
        Results result = new Results(chronometer.getText().toString(), d);
        myRefResults.child(String.valueOf(position)).push().setValue(result);
    }


    public void getResults() {
        resultList.clear();
        myRefResults.child(String.valueOf(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {
                    Results results = (Results) dataSnapshot.getValue(Results.class);
                    resultList.add(0, results.getDate() + "\n\n" + results.getTime());
                }
                numberOfResults.setText(String.valueOf(resultList.size()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Error: " + databaseError.getCode());
            }
        });
    }

}


