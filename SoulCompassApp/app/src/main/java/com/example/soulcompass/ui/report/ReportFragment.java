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
import com.example.soulcompass.ui.quiz.QuizFragment;

import com.anychart.APIlib;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.TooltipPositionMode;
import com.example.soulcompass.R;
import com.example.soulcompass.SoulCompassDatabase;
import com.example.soulcompass.ui.quiz.QuizFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ReportFragment extends Fragment {

    AnyChartView anyChartViewStress;
    AnyChartView anyChartViewUnlocks;
    public Map<String, Integer> stressData = null;
    public Map<String, Integer> unlocksData = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_report, container, false);

        //##### Create column chart
        anyChartViewStress = root.findViewById(R.id.StressLevelChart);
        APIlib.getInstance().setActiveAnyChartView(anyChartViewStress);
        //anyChartViewUnlocks = root.findViewById(R.id.UnlocksChart);
        //#####

        // Retrieve number of unlocks per day
        Map<String,Integer> unlocksByDate = SoulCompassDatabase.loadUnlocksByDay(getContext());
        Log.d("REPORT", String.valueOf(unlocksByDate));

        SoulCompassDatabase database = new SoulCompassDatabase(getContext());
        stressData = database.loadTestResults(QuizFragment.getCurrentScale());
        database.close();
        //#####

        //#####
        unlocksData = SoulCompassDatabase.loadUnlocksByDay(getContext());
        //#####

        //##### Added Stress chart
        Cartesian cartesian_stress = createBarChartStress();
        anyChartViewStress.setBackgroundColor("#00000000");
        anyChartViewStress.setChart(cartesian_stress);


        anyChartViewUnlocks = root.findViewById(R.id.UnlocksChart);
        APIlib.getInstance().setActiveAnyChartView(anyChartViewUnlocks);
        //#####

        //##### Added Unlocks chart
        Cartesian cartesian_unlocks = createBarChartUnlocks();
        anyChartViewUnlocks.setBackgroundColor("#00000000");
        anyChartViewUnlocks.setChart(cartesian_unlocks);
        //#####

        return root;
    }

    //##### Create chart function for stress chart
    public Cartesian createBarChartStress(){
        //***** Read data from SQLiteDatabase *********/

        Map<String, Integer> graph_map = new TreeMap<>();
        for (Map.Entry day : graph_map.entrySet()) {
            graph_map.put(day.getKey().toString(), 0);
        }


        graph_map.putAll(stressData);


        //***** Create column chart using AnyChart library *********/

        Cartesian cartesian = AnyChart.column();

        List<DataEntry> data = new ArrayList<>();

        for (Map.Entry<String,Integer> entry : graph_map.entrySet())
            data.add(new ValueDataEntry(entry.getKey(), entry.getValue()));

        Column column = cartesian.column(data);

        //***** Modify the UI of the chart *********/

        column.fill("#00867d");
        column.stroke("#00867d");

        column.tooltip()
                .titleFormat("At day: {%X}")
                .format("{%Value}{groupsSeparator: } Stress Level")
                .anchor(Anchor.RIGHT_TOP)
                .offsetX(0d)
                .offsetY(5);

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);
        cartesian.yScale().minimum(0);

        cartesian.yAxis(0).title("Stress Level");
        cartesian.xAxis(0).title("Day");
        cartesian.background().fill("#00000000");
        cartesian.animation(true);


        return cartesian;
    }
    //#####

    //##### Create chart function for unlocks chart
    public Cartesian createBarChartUnlocks(){

        Map<String, Integer> graph_map2 = new TreeMap<>();
        for (Map.Entry day : graph_map2.entrySet()) {
            graph_map2.put(day.getKey().toString(), 0);
        }


        graph_map2.putAll(unlocksData);


        //***** Create column chart using AnyChart library *********/

        Cartesian cartesian2 = AnyChart.column();

        List<DataEntry> data = new ArrayList<>();

        for (Map.Entry<String,Integer> entry : graph_map2.entrySet())
            data.add(new ValueDataEntry(entry.getKey(), entry.getValue()));

        Column column = cartesian2.column(data);

        //***** Modify the UI of the chart *********/

        column.fill("#00867d");
        column.stroke("#00867d");

        column.tooltip()
                .titleFormat("At day: {%X}")
                .format("{%Value}{groupsSeparator: } Unlocks")
                .anchor(Anchor.RIGHT_TOP)
                .offsetX(0d)
                .offsetY(5);

        cartesian2.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian2.interactivity().hoverMode(HoverMode.BY_X);
        cartesian2.yScale().minimum(0);

        cartesian2.yAxis(0).title("Unlocks");
        cartesian2.xAxis(0).title("Day");
        cartesian2.background().fill("#00000000");
        cartesian2.animation(true);


        return cartesian2;
    }
    //#####
}