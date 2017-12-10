package com.example.israelsson.onemorerepfitness;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    boolean isTimeActivated = false;
    Chronometer chronometer;
    int position = 0;
    private static final int MAIN_DATA_LOADER_ID = 0;
    private static final String WORKOUT_EXTRA_ID = "workouts";
    private static final String TIME_EXTRA_ID = "time";
    private static final String IS_TIME_ACTIVATED_ID = "time_active_or_not";
    private static final String TIME_EXTRA_SAVED_BASE_ID = "base";
    private static final String TIME_WHEN_STOPPED_EXTRA = "timestopped";
    private static final String IS_SAVE_VISIBLE_ID = "visibleornot";
    DatabaseReference myRef;
    DatabaseReference myRefResults;
    List<Workouts> items = new ArrayList<>();
    List<Results> resultItems = new ArrayList<>();
    Workouts value;
    Results resultsValue;
    private TextView workoutTextView;
    private TextView resetButton;
    private TextView saveButton;
    private TextView yesToSave;
    private TextView resultNumberButton;
    private long baseTime;
    private long elapsedRealTime;
    private long timeWhenStopped = 0;
    private boolean isSaveVisible = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration config = getResources().getConfiguration();
        if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_main);
        } else {
            setContentView(R.layout.activity_main_landscape);
        }
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setElevation(0);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("workouts");
        myRefResults = database.getReference().child("results");
        getFireBaseWorkouts();
        getNumberOfResults();

        workoutTextView = (TextView) findViewById(R.id.workoutTextView);
        chronometer = (Chronometer) findViewById(R.id.chronometer3);
        resetButton = (TextView) findViewById(R.id.resetButton);
        saveButton = (TextView) findViewById(R.id.saveButton);


        //If the device has been rotated then restore the values for the chronometer and and workout position
        if (savedInstanceState != null) {
            //Restore the the position so that correct workout is selected
            position = savedInstanceState.getInt(WORKOUT_EXTRA_ID);
            //Check if timing was activated before rotation
            isTimeActivated = savedInstanceState.getBoolean(IS_TIME_ACTIVATED_ID);
            //Check if the "save" TextView was visible before rotation
            isSaveVisible = savedInstanceState.getBoolean(IS_SAVE_VISIBLE_ID);
            timeWhenStopped = savedInstanceState.getLong(TIME_WHEN_STOPPED_EXTRA);
            baseTime = savedInstanceState.getLong(TIME_EXTRA_SAVED_BASE_ID);
            //If time was activated before rotation, continue counting up

            if (isTimeActivated) {
                chronometer.setBase(baseTime);
                chronometer.start();
                /*If not then restore the time at which timing was stopped and if the "save" TextView was visible
                before rotation, then set to visible now again*/
            } else {
                chronometer.stop();
                baseTime = SystemClock.elapsedRealtime() + timeWhenStopped;
                chronometer.setBase(baseTime);
                if (isSaveVisible) {
                    resetButton.setVisibility(View.VISIBLE);
                    saveButton.setVisibility(View.VISIBLE);
                }
            }
        }




        ImageView timingButton = (ImageView) findViewById(R.id.timingButton);
        timingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isTimeActivated) {
                    baseTime = SystemClock.elapsedRealtime() + timeWhenStopped;
                    chronometer.setBase(baseTime);
                    chronometer.start();
                    isTimeActivated = true;
                } else if (isTimeActivated) {
                    chronometer.stop();
                    chronometer.setBase(baseTime);
                    elapsedRealTime = SystemClock.elapsedRealtime();
                    timeWhenStopped = baseTime - elapsedRealTime;
                    isTimeActivated = false;
                    resetButton.setVisibility(View.VISIBLE);
                    saveButton.setVisibility(View.VISIBLE);
                    isSaveVisible = true;
                }
            }
        });


        ImageView previousButton = (ImageView) findViewById(R.id.previousWorkoutButton);
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position > 0) {
                    position--;
                    getFireBaseWorkouts();
                    getNumberOfResults();
                }
            }
        });


        ImageView nextButton = (ImageView) findViewById(R.id.nextWorkoutButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position < items.size() - 1) {
                    position++;
                    getFireBaseWorkouts();
                    getNumberOfResults();
                }
            }
        });


        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chronometer.setBase(SystemClock.elapsedRealtime());
                timeWhenStopped = 0;
                resetButton.setVisibility(View.INVISIBLE);
                saveButton.setVisibility(View.INVISIBLE);
                isSaveVisible = false;
                isTimeActivated = false;
            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog saveDialog = new Dialog(MainActivity.this);
                saveDialog.setContentView(R.layout.save_dialog);
                saveDialog.show();

                yesToSave = (TextView) saveDialog.findViewById(R.id.negativeButton);
                yesToSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setResults();
                        getNumberOfResults();
                        saveDialog.dismiss();
                        resetButton.setVisibility(View.INVISIBLE);
                        saveButton.setVisibility(View.INVISIBLE);
                        isSaveVisible = false;
                    }
                });
            }
        });


        resultNumberButton = (TextView) findViewById(R.id.resultNumberButton);

        resultNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startResults();
            }
        });

    }

    /*If device is being rotated, then save the data for the workout position and chronometer to be able
      to restore it after rotation is complete.*/
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(WORKOUT_EXTRA_ID, position);
        outState.putLong(TIME_EXTRA_SAVED_BASE_ID, timeWhenStopped + baseTime);
        outState.putLong(TIME_WHEN_STOPPED_EXTRA, timeWhenStopped);
        outState.putBoolean(IS_TIME_ACTIVATED_ID, isTimeActivated);
        outState.putBoolean(IS_SAVE_VISIBLE_ID, isSaveVisible);
        super.onSaveInstanceState(outState);
    }

    public void getFireBaseWorkouts() {
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
        Date date = new Date();
        Results result = new Results(chronometer.getText().toString(), date.toString(), position);
        myRefResults.child(String.valueOf(position)).push().setValue(result);
    }


    public void getNumberOfResults() {
        myRefResults.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                resultItems.clear();
                Iterable<DataSnapshot> children = dataSnapshot.child(String.valueOf(position)).getChildren();
                for (DataSnapshot child : children) {
                    resultsValue = child.getValue(Results.class);
                    resultItems.add(resultsValue);

                }

                resultNumberButton.setText(String.valueOf(resultItems.size()));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Error: " + databaseError.getCode());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    private void startResults() {
        Intent intent = new Intent(this, ShowResults.class);
        intent.putExtra("workout_position", String.valueOf(position));
        Log.d("sent", String.valueOf(position));
        startActivity(intent);
    }

}




