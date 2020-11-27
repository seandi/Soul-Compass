package com.example.soulcompass.ui.quiz;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.soulcompass.R;
import com.google.android.material.button.MaterialButton;

public class QuizResultFragment extends Fragment {

    private int result = 0;
    private int scale = 1;
    private double result_normalized = result;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_quiz_result, container, false);

        assert getArguments() != null;
        result = getArguments().getInt("RESULT");
        scale = getArguments().getInt("SCALE");
        result_normalized = ((double) result) / ((double) scale);
        Log.d("STRESS TEST", "Test result is " + String.valueOf(result_normalized) + "/1.0");


        return root;
    }
}