package com.example.israelsson.onemorerepfitness;

import android.app.Dialog;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView workoutTextView;
    private TextView resetButton;
    private TextView saveButton;
    private long timeWhenStopped = 0;
    boolean isTimeActivated = false;
    Chronometer chronometer;
    int position = 0;
    DatabaseReference myRef;
    List<Workouts> items = new ArrayList<>();
    Workouts value;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("workouts");
        getFireBaseValues();

        workoutTextView = (TextView) findViewById(R.id.workoutTextView);

        chronometer = (Chronometer) findViewById(R.id.chronometer3);

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
        Date date = new Date();
        Results result = new Results(date.toString(), chronometer.getText().toString());
        myRef.push().setValue(result);
    }

}


