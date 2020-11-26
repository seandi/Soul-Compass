package com.example.soulcompass.ui.quiz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.soulcompass.R;
import com.google.android.material.button.MaterialButton;

public class StartQuizFragment extends Fragment {

    MaterialButton start_button;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_start_quiz, container, false);
        start_button = root.findViewById(R.id.start_button);



        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment quiz_fragment = new QuizFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction().setReorderingAllowed(true);
                ft.replace(R.id.nav_host_fragment, quiz_fragment);
                ft.commit();

            }

        });


        return root;
    }
}