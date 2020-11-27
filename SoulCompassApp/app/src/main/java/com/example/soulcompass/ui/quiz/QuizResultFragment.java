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

    private double result = 0.0;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_quiz_result, container, false);
        result = getArguments().getDouble("Result");
        Log.d("STRESS TEST", "Test result is " + String.valueOf(result) + "/1.0");


        return root;
    }
}