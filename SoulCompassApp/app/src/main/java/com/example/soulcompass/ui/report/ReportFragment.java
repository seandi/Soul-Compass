package com.example.soulcompass.ui.report;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.soulcompass.R;
import com.example.soulcompass.SoulCompassDatabase;

import java.util.Map;

public class ReportFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_report, container, false);
        final TextView textView = root.findViewById(R.id.text_notifications);

        // Retrieve number of unlocks per day
        Map<String,Integer> unlocksByDate = SoulCompassDatabase.loadUnlocksByDay(getContext());
        Log.d("REPORT", String.valueOf(unlocksByDate));

        return root;
    }
}