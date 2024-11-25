package com.example.co2tracker;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.example.co2tracker.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private ProgressBar progressBar;
    private TextView progressText;
    private BarChart emissionsChart;
    private TextView totalFoodEmissions;
    private TextView totalCarEmissions;
    private TextView highestDay;
    private TextView lowestDay;
    private TextView tipText;
    private Button newTipButton;
    private HomeViewModel homeViewModel;
    private String[] tips;
    private Random random = new Random();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Initialize views using binding
        progressBar = binding.progressBar;
        progressText = binding.progressText;
        emissionsChart = binding.emissionsChart;
        totalFoodEmissions = binding.totalFoodEmissions;
        totalCarEmissions = binding.totalCarEmissions;
        highestDay = binding.highestDay;
        lowestDay = binding.lowestDay;
        tipText = binding.tipText;
        newTipButton = binding.newTipButton;

        // Load localized tips
        tips = getResources().getStringArray(R.array.eco_tips_array);

        // Initialize ViewModel
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        // Setup tip functionality
        showRandomTip();
        newTipButton.setOnClickListener(v -> showRandomTip());

        // Observe weekly progress
        homeViewModel.getWeeklyProgress().observe(getViewLifecycleOwner(), progress -> {
            if (progress != null) {
                int percentage = (int) ((progress.getCurrentProgress() / progress.getWeeklyGoal()) * 100);
                progressBar.setProgress(Math.min(percentage, 100));
                binding.progressText.setText(getString(R.string.kg_co2_saved, 
                    progress.getCurrentProgress(), progress.getWeeklyGoal()));
            }
        });

        setupChart();

        // Observe daily emissions
        homeViewModel.getDailyEmissions().observe(getViewLifecycleOwner(), emissions -> {
            if (emissions != null && !emissions.isEmpty()) {
                updateChartData(emissions);
            }
        });

        return view;
    }

    private void updateChartData(List<DailyEmissions> emissions) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        float totalFood = 0f;
        float totalCar = 0f;
        float maxEmissions = 0f;
        float minEmissions = Float.MAX_VALUE;
        int maxDay = 0;
        int minDay = 0;
        
        // Create a map of day of week to emissions
        float[][] dailyEmissions = new float[7][2]; // [day][0] = food, [day][1] = car
        
        // Initialize with zeros
        for (int i = 0; i < 7; i++) {
            dailyEmissions[i][0] = 0f;
            dailyEmissions[i][1] = 0f;
        }
        
        // Fill in actual data
        for (DailyEmissions emission : emissions) {
            int dayIndex = emission.getDayOfWeek() - 1;
            float foodEmissions = (float) emission.getFoodEmissions();
            float carEmissions = (float) emission.getCarEmissions();
            
            // Skip negative values (these are savings, not emissions)
            if (foodEmissions < 0 || carEmissions < 0) continue;
            
            dailyEmissions[dayIndex][0] = foodEmissions;
            dailyEmissions[dayIndex][1] = carEmissions;
            
            float dayTotal = foodEmissions + carEmissions;
            if (dayTotal > maxEmissions) {
                maxEmissions = dayTotal;
                maxDay = dayIndex;
            }
            if (dayTotal < minEmissions && dayTotal > 0) {
                minEmissions = dayTotal;
                minDay = dayIndex;
            }
            
            totalFood += foodEmissions;
            totalCar += carEmissions;
        }
        
        // Create entries for all days
        for (int i = 0; i < 7; i++) {
            entries.add(new BarEntry(i, new float[]{
                dailyEmissions[i][0],  // Food emissions
                dailyEmissions[i][1]   // Car emissions
            }));
        }

        // Update the chart with modern styling
        BarDataSet set = new BarDataSet(entries, "");
        set.setColors(
            getResources().getColor(R.color.colorPrimary),  // Food emissions
            getResources().getColor(R.color.colorAccent)    // Car emissions
        );
        set.setStackLabels(new String[]{getString(R.string.food), getString(R.string.car)});
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(getResources().getColor(android.R.color.tab_indicator_text));
        set.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value > 0) {
                    return String.format("%.1f", value);
                }
                return "";
            }
        });

        BarData data = new BarData(set);
        data.setBarWidth(0.7f);
        emissionsChart.setData(data);
        emissionsChart.invalidate();

        // Update summary statistics
        String[] days = {
            getString(R.string.sunday), getString(R.string.monday), 
            getString(R.string.tuesday), getString(R.string.wednesday), 
            getString(R.string.thursday), getString(R.string.friday), 
            getString(R.string.saturday)
        };

        if (maxEmissions > 0) {
            highestDay.setText(getString(R.string.highest_day, days[maxDay], maxEmissions));
        }
        if (minEmissions < Float.MAX_VALUE) {
            lowestDay.setText(getString(R.string.lowest_day, days[minDay], minEmissions));
        }
        
        // Format the emissions with proper units
        totalFoodEmissions.setText(String.format("%s: %.1f kg", getString(R.string.total_food_emissions), totalFood));
        totalCarEmissions.setText(String.format("%s: %.1f kg", getString(R.string.total_car_emissions), totalCar));
    }

    private void setupChart() {
        // Modern chart styling
        emissionsChart.getDescription().setEnabled(false);
        emissionsChart.setDrawGridBackground(false);
        emissionsChart.setDrawBarShadow(false);
        emissionsChart.setHighlightFullBarEnabled(true);
        emissionsChart.setPinchZoom(false);
        emissionsChart.setDoubleTapToZoomEnabled(false);
        emissionsChart.setDrawValueAboveBar(true);
        emissionsChart.setFitBars(true);
        emissionsChart.animateY(1000);
        emissionsChart.setExtraBottomOffset(10f);
        emissionsChart.setExtraTopOffset(10f);

        // Modern legend styling
        Legend legend = emissionsChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(true);
        legend.setTextSize(12f);
        legend.setTextColor(getResources().getColor(android.R.color.tab_indicator_text));
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setFormSize(8f);
        legend.setXEntrySpace(10f);
        legend.setYEntrySpace(5f);

        // X-Axis styling
        XAxis xAxis = emissionsChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(12f);
        xAxis.setTextColor(getResources().getColor(android.R.color.tab_indicator_text));
        xAxis.setAxisLineColor(getResources().getColor(android.R.color.tab_indicator_text));
        xAxis.setAxisLineWidth(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{
            getString(R.string.sunday), getString(R.string.monday), 
            getString(R.string.tuesday), getString(R.string.wednesday), 
            getString(R.string.thursday), getString(R.string.friday), 
            getString(R.string.saturday)
        }));

        // Y-Axis styling
        YAxis leftAxis = emissionsChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(getResources().getColor(android.R.color.darker_gray));
        leftAxis.setGridLineWidth(0.5f);
        leftAxis.setTextColor(getResources().getColor(android.R.color.tab_indicator_text));
        leftAxis.setAxisLineColor(getResources().getColor(android.R.color.tab_indicator_text));
        leftAxis.setAxisLineWidth(1f);
        leftAxis.setTextSize(12f);
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("%.1f", value);
            }
        });

        // Disable right Y-Axis
        emissionsChart.getAxisRight().setEnabled(false);
    }

    private void showRandomTip() {
        if (tipText != null && tips.length > 0) {
            tipText.setText(tips[random.nextInt(tips.length)]);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}