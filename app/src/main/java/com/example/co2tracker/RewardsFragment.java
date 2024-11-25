package com.example.co2tracker;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static android.content.Context.MODE_PRIVATE;

public class RewardsFragment extends Fragment {

    private int totalPoints;
    private Button futureFinderButton;
    private Button ecoRoseButton;
    private UserManager userManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rewards, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UserManager and get points
        userManager = new UserManager(requireContext());
        totalPoints = userManager.getPoints();

        // Initialize views
        futureFinderButton = view.findViewById(R.id.futureFinder_redeem_btn);
        ecoRoseButton = view.findViewById(R.id.ecoRose_redeem_btn);

        // Set up reward points texts
        TextView futureFinderPoints = view.findViewById(R.id.futureFinder_points);
        TextView ecoRosePoints = view.findViewById(R.id.ecoRose_points);
        futureFinderPoints.setText(getString(R.string.points_required, 1500));
        ecoRosePoints.setText(getString(R.string.points_required, 2000));

        // Set up FutureFinder redemption
        setupRewardButton(futureFinderButton, 1500, "FutureFinder",
                "https://www.instagram.com/futurefinder_uf/");

        // Set up EcoRose redemption
        setupRewardButton(ecoRoseButton, 2000, "EcoRose",
                "https://www.instagram.com/ecoroseuf/");
    }

    private void setupRewardButton(Button button, int requiredPoints, String companyName, String instagramUrl) {
        // Update button state based on points
        button.setEnabled(totalPoints >= requiredPoints);
        button.setText(getString(totalPoints >= requiredPoints ? R.string.redeem_reward : R.string.not_enough_points));

        button.setOnClickListener(v -> showRedemptionProcess(companyName, instagramUrl, requiredPoints));
    }

    private void showRedemptionProcess(String companyName, String instagramUrl, int pointsCost) {
        // Create verification dialog
        Dialog verificationDialog = new Dialog(requireContext());
        verificationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        verificationDialog.setContentView(R.layout.dialog_verification);
        verificationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        verificationDialog.setCancelable(false);

        TextView titleText = verificationDialog.findViewById(R.id.verification_title);
        TextView messageText = verificationDialog.findViewById(R.id.verification_message);

        titleText.setText(getString(R.string.verification_title));
        messageText.setText(getString(R.string.verification_message));

        verificationDialog.show();

        // Simulate verification process
        new Handler().postDelayed(() -> {
            // Deduct points
            totalPoints -= pointsCost;
            userManager.setPoints(totalPoints);

            // Update main activity points display
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).updatePointsDisplay(totalPoints);
            }

            // Update buttons
            setupRewardButton(futureFinderButton, 1500, "FutureFinder",
                    "https://www.instagram.com/futurefinder_uf/");
            setupRewardButton(ecoRoseButton, 2000, "EcoRose",
                    "https://www.instagram.com/ecoroseuf/");

            verificationDialog.dismiss();
            showSuccessDialog(companyName, instagramUrl, pointsCost);
        }, 2000);
    }

    private void showSuccessDialog(String companyName, String instagramUrl, int pointsCost) {
        Dialog successDialog = new Dialog(requireContext());
        successDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        successDialog.setContentView(R.layout.dialog_reward_success);
        successDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView titleText = successDialog.findViewById(R.id.success_title);
        TextView messageText = successDialog.findViewById(R.id.success_message);
        Button nextButton = successDialog.findViewById(R.id.next_button);

        titleText.setText(getString(R.string.success_title));

        if (companyName.equals("FutureFinder")) {
            messageText.setText(getString(R.string.futurefinder_success_message, pointsCost, pointsCost/50));
        } else {
            messageText.setText(getString(R.string.ecorose_success_message, pointsCost, pointsCost/100));
        }

        nextButton.setText(getString(R.string.next));

        nextButton.setOnClickListener(v -> {
            successDialog.dismiss();
            showRedeemInstructions(companyName, instagramUrl);
        });

        successDialog.show();
    }

    private void showRedeemInstructions(String companyName, String instagramUrl) {
        Dialog instructionsDialog = new Dialog(requireContext());
        instructionsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        instructionsDialog.setContentView(R.layout.dialog_redeem_instructions);
        instructionsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView instructionsText = instructionsDialog.findViewById(R.id.instructions_text);
        Button instagramButton = instructionsDialog.findViewById(R.id.instagram_button);
        Button laterButton = instructionsDialog.findViewById(R.id.later_button);

        int uniqueCode = (int) (System.currentTimeMillis() % 10000);

        if (companyName.equals("FutureFinder")) {
            instructionsText.setText(getString(R.string.claim_instructions_futurefinder,
                    companyName, uniqueCode));
        } else {
            instructionsText.setText(getString(R.string.claim_instructions_ecorose,
                    companyName, uniqueCode));
        }

        instagramButton.setText(getString(R.string.open_instagram));
        laterButton.setText(getString(R.string.later));

        instagramButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(instagramUrl));
            startActivity(intent);
        });

        laterButton.setOnClickListener(v -> instructionsDialog.dismiss());

        instructionsDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Update points when fragment resumes
        totalPoints = userManager.getPoints();
        setupRewardButton(futureFinderButton, 1500, "FutureFinder",
                "https://www.instagram.com/futurefinder_uf/");
        setupRewardButton(ecoRoseButton, 2000, "EcoRose",
                "https://www.instagram.com/ecoroseuf/");
    }
}