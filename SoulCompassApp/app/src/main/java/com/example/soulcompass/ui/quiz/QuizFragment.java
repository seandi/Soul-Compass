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
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.soulcompass.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.radiobutton.MaterialRadioButton;


import net.kibotu.heartrateometer.HeartRateOmeter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizFragment extends Fragment {

    private static final String[] CHOICE_LABELS = {
            "Strongly disagree",
            "Disagree",
            "Agree",
            "Strongly agree"
    };

    private static final String[] QUESTIONS_PHYSICAL_STRESS = {
            "I often feel tired",
            "I often wake up during the night",
            "If i wake up during the night, i struggle to sleep again",
            "I exercise less then twice a week",
            "Others think that i have too much worries"
    };

    private static final String[] QUESTIONS_MENTAL_STRESS = {
            "I rarely introduce a novel activity in my job",
            "I rarely read a book",
            "I do not know any mentally relaxing activity",
            "I rarely read anything besides newspapers",
            "I do not have nay hobby"
    };

    private static final int QUESTION_MAX_VALUE = 3;

    private Map<Integer, Integer> test_answers = new HashMap<>();
    private MaterialButton finishButton;
    private Bundle test_result_bundle = new Bundle();
    private int test_result = 0;
    private int test_scale = 0;
    private int num_questions = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_quiz, container, false);
        finishButton = root.findViewById(R.id.finish_button);

        LinearLayout parent_layout = root.findViewById(R.id.quiz_questions_layout);
        generateTestSection(parent_layout, "Physical stress", QUESTIONS_PHYSICAL_STRESS, 100);
        generateTestSection(parent_layout, "Mental stress", QUESTIONS_MENTAL_STRESS, 200);



        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 1. Check if all questions have been answered
                if (test_answers.size() != num_questions){
                    Toast.makeText(getContext(), "Some answers are missing!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Compute test result
                computeTestResult();

                // Crete result fragment and add result bundle
                Fragment result_fragment = new QuizResultFragment();
                double test_result_normalized = ((double) test_result) / ((double) test_scale);
                test_result_bundle.putDouble("Result", test_result_normalized);
                result_fragment.setArguments(test_result_bundle);

                // Execute fragment transaction
                FragmentTransaction ft = getFragmentManager().beginTransaction().setReorderingAllowed(true);
                ft.replace(R.id.nav_host_fragment, result_fragment);
                ft.commit();
            }
        });

        return root;
    }

    private void computeTestResult(){
        this.test_result = test_answers.values().stream().reduce(0, Integer::sum);
    }

    private void generateTestSection(LinearLayout parent_layout, String section_title, String[] questions, int base_id){

        // 1. Generate section header
        TextView section_header = new TextView(getContext());
        int height = (int) getResources().getDimension(R.dimen.question_text_height);
        int padding = (int) getResources().getDimension(R.dimen.question_text_padding);

        section_header.setText(section_title);

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                height);
        param.setMargins(padding,padding,padding,0);
        section_header.setLayoutParams(param);


        section_header.setTextColor(Color.WHITE);
        section_header.setTextSize(20);
        section_header.setGravity(Gravity.CENTER);
        section_header.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        section_header.setPadding(2*padding, 0, 0 ,0);
        section_header.setTextAppearance(android.graphics.Typeface.BOLD);


        GradientDrawable gradientDrawable=new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setCornerRadius(getResources().getDimension(R.dimen.header_corner_radius));
        //gradientDrawable.setStroke(4,getResources().getColor(R.color.teal_dark));
        gradientDrawable.setColor(getResources().getColorStateList(R.color.teal_dark));
        section_header.setBackground(gradientDrawable);


        parent_layout.addView(section_header);

        // 2. Generate Questions
        int id = base_id;
        for ( String question: questions) {
            id++;
            LinearLayout question_layout = createNewQuestion(parent_layout, 5);
            createQuestionText(question_layout, question);
            createMultipleChoiceButtons(question_layout, CHOICE_LABELS.length, id, CHOICE_LABELS);
        }

        // 3. Update test scale and number of questions
        this.test_scale += questions.length * QUESTION_MAX_VALUE;
        this.num_questions += questions.length;
    }


    private LinearLayout createNewQuestion(LinearLayout parent_layout, int divider_height){
        LinearLayout question_layout = new LinearLayout(getContext());
        question_layout.setOrientation(LinearLayout.VERTICAL);

        GradientDrawable gradientDrawable=new GradientDrawable();
        gradientDrawable.setStroke(0,getResources().getColor(R.color.teal_dark));
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
            buttons[i].setId(id+i);
            buttons[i].setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            buttons[i].setLayoutParams(new RadioGroup.LayoutParams(240,
                    ViewGroup.LayoutParams.MATCH_PARENT, (float) 0.18
                    ));

            buttons[i].setButtonTintList(getContext().getColorStateList(R.color.teal));
            rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    Log.d("Stree Test",
                            "Selected answer " + String.valueOf(checkedId - id) + " for question num " + String.valueOf(id));
                    test_answers.put(id, checkedId - id);
                }
            });
            rg.addView(buttons[i]);
        }
        layout.addView(rg);
    }
}