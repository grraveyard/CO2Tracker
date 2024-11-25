package com.example.co2tracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TasksFragment extends Fragment implements TaskAdapter.TaskClickListener {
    private TaskAdapter adapter;
    private TaskManager taskManager;
    private Task selectedTask;
    private HomeViewModel homeViewModel;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<Intent> takePictureLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taskManager = new TaskManager();
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        
        requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    launchCamera();
                } else {
                    Toast.makeText(getContext(), 
                        "Camera permission is required to complete tasks", 
                        Toast.LENGTH_LONG).show();
                }
            }
        );

        takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK) {
                    handleTaskCompletion();
                }
            }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);
        setupRecyclerView(view);
        return view;
    }

    private void setupRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.taskList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TaskAdapter(getContext(), taskManager.getTasks(), this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onTaskClicked(Task task) {
        if (task.isCompleted()) {
            Toast.makeText(getContext(), "Task already completed!", Toast.LENGTH_SHORT).show();
            return;
        }

        selectedTask = task;
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            launchCamera();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            takePictureLauncher.launch(intent);
        } else {
            Toast.makeText(getContext(), "No camera app found", Toast.LENGTH_LONG).show();
        }
    }

    private void handleTaskCompletion() {
        if (selectedTask != null) {
            taskManager.completeTask(selectedTask);
            adapter.updateTasks(taskManager.getTasks());
            
            // Update points in MainActivity if available
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).updatePoints(selectedTask.getPoints());
            }
            
            // Update CO2 savings
            homeViewModel.updateCO2Savings(selectedTask.getCo2Savings());
            
            Toast.makeText(getContext(), "Task completed successfully!", Toast.LENGTH_SHORT).show();
        }
    }
}