package com.example.soulcompass.ui.quiz;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.soulcompass.R;
import com.google.android.material.button.MaterialButton;

import java.lang.reflect.Field;

public class QuizResultFragment extends Fragment {

    private static final int STRESS_LEVELS = 10;

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