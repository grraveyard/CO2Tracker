package com.example.co2tracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import android.view.MenuItem;

public class ForgotPasswordActivity extends AppCompatActivity {
    private TextInputEditText emailInput;
    private MaterialButton resetButton;
    private TextView backToLoginLink;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Enable the back button in the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        userManager = new UserManager(this);

        // Get the email from the intent if it was passed
        String email = getIntent().getStringExtra("email");

        emailInput = findViewById(R.id.emailInput);
        resetButton = findViewById(R.id.resetButton);
        backToLoginLink = findViewById(R.id.backToLoginLink);

        // Set the email if it was passed
        if (email != null && !email.isEmpty()) {
            emailInput.setText(email);
        }

        resetButton.setOnClickListener(v -> handlePasswordReset());

        // Handle back to login button click
        backToLoginLink.setOnClickListener(v -> navigateToLogin());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            navigateToLogin();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        navigateToLogin();
    }

    private void handlePasswordReset() {
        String email = emailInput.getText().toString().trim();

        if (!isValidEmail(email)) {
            emailInput.setError("Please enter a valid email address");
            return;
        }

        // Here you would typically implement your password reset logic
        // For now, we'll just show a confirmation message
        Toast.makeText(this,
                "Password reset instructions sent to " + email,
                Toast.LENGTH_LONG).show();

        // Navigate back to login after successful reset
        navigateToLogin();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
