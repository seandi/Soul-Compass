package com.example.soulcompass.ui.quiz;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.soulcompass.R;
import com.example.soulcompass.SoulCompassDatabase;
import com.google.android.material.button.MaterialButton;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

public class QuizResultFragment extends Fragment {

    private static final int STRESS_LEVELS = 10;
    private static final double STRESS_HIGH_THRESHOLD = 0.66;
    private static final int SUGGEST_WALKING_MAX_STEPS = 100;

    private int result = 0;
    private int scale = 1;
    private double result_normalized = result;

    private TextView result_text, result_description_text;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_quiz_result, container, false);
        result_text = root.findViewById(R.id.test_result);
        result_description_text = root.findViewById(R.id.result_description);

        assert getArguments() != null;
        result = getArguments().getInt("RESULT");
        scale = getArguments().getInt("SCALE");
        result_normalized = ((double) result) / ((double) scale);
        Log.d("STRESS TEST", "Test result is " + String.valueOf(result_normalized) + "/1.0");

        result_text.setText("Result: " + String.valueOf(result) + "/" + String.valueOf(scale));

        int stress_level = (int) Math.ceil(result_normalized * STRESS_LEVELS);
        int descriptionId = getId("result_description_"+stress_level, R.string.class);

        result_description_text.setText(getString(descriptionId));

        if(result_normalized>STRESS_HIGH_THRESHOLD) {
            Date cDate = new Date();
            String fDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);
            int steps_today = SoulCompassDatabase.loadSingleRecord(getContext(), fDate);
            if(steps_today < SUGGEST_WALKING_MAX_STEPS){
                Toast.makeText(getContext(), "You have not walked much today, why not having a walk to relax?",Toast.LENGTH_LONG).show();
            }
        }


        return root;
    }

    public static int getId(String resourceName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resourceName);
            return idField.getInt(idField);
        } catch (Exception e) {
            throw new RuntimeException("No resource ID found for: "
                    + resourceName + " / " + c, e);
        }
    }
}