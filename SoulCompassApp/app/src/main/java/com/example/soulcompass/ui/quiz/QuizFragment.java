package com.example.soulcompass.ui.quiz;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.soulcompass.R;
import com.google.android.material.radiobutton.MaterialRadioButton;


import net.kibotu.heartrateometer.HeartRateOmeter;

public class QuizFragment extends Fragment {

    private static final String[] CHOICE_LABELS = {
            "Strongly disagree",
            "Disagree",
            "Agree",
            "Strongly agree"
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_quiz, container, false);


        LinearLayout parent_layout = root.findViewById(R.id.quiz_layout);


        return root;
    }


    private LinearLayout createNewQuestion(LinearLayout parent_layout, int divider_height){
        LinearLayout question_layout = new LinearLayout(getContext());
        question_layout.setOrientation(LinearLayout.VERTICAL);

        GradientDrawable gradientDrawable=new GradientDrawable();
        gradientDrawable.setStroke(1,getResources().getColor(R.color.teal_dark));
        question_layout.setBackground(gradientDrawable);

        int height_question = (int) getResources().getDimension(R.dimen.question_text_height);
        int height_choice = (int) getResources().getDimension(R.dimen.question_choices_height);
        question_layout.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        parent_layout.addView(question_layout);

        LinearLayout empty_layout = new LinearLayout(getContext());
        empty_layout.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                divider_height
        ));
        parent_layout.addView(empty_layout);

        return question_layout;
    }

    private void createQuestionText(LinearLayout layout, String questionText){
        int height = (int) getResources().getDimension(R.dimen.question_text_height);
        int padding = (int) getResources().getDimension(R.dimen.question_text_padding);


        TextView question = new TextView(getContext());
        question.setText(questionText);
        question.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                height
        ));
        question.setTextSize(16);
        question.setGravity(Gravity.CENTER);
        question.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        question.setPadding(padding, 0, 0 ,0);

        question.setTextColor(getResources().getColor(R.color.black));


        layout.addView(question);
    }

    private void createMultipleChoiceButtons(LinearLayout layout, int num_choices, int id,  String[] choice_labels) {
        final MaterialRadioButton[] buttons = new MaterialRadioButton[4];

        int height = (int) getResources().getDimension(R.dimen.question_choices_height);
        int padding = (int) getResources().getDimension(R.dimen.question_choices_padding);
        RadioGroup rg = new RadioGroup(getContext()); //create the RadioGroup
        rg.setLayoutParams(new RadioGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                height
        ));
        rg.setPadding(padding, 0, 0, padding);

        rg.setOrientation(RadioGroup.HORIZONTAL);//or RadioGroup.VERTICAL
        for(int i=0; i<num_choices; i++){
            buttons[i]  = new MaterialRadioButton(getContext());
            buttons[i].setText(choice_labels[i]);
            buttons[i].setId(i + id);
            buttons[i].setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            buttons[i].setLayoutParams(new RadioGroup.LayoutParams(240,
                    ViewGroup.LayoutParams.MATCH_PARENT, (float) 0.18
                    ));

            buttons[i].setButtonTintList(getContext().getColorStateList(R.color.teal));
            rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    for(int i=0; i<num_choices; i++){
                        //buttons[i].setEnabled(false);
                    }
                    Log.d("TAG",String.valueOf(checkedId));
                }
            });
            rg.addView(buttons[i]);
        }
        layout.addView(rg);//you add the whole RadioGroup to the layout

    }
}