package com.example.co2tracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.CheckBox;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText loginInput;
    private TextInputEditText passwordInput;
    private MaterialButton loginButton;
    private CheckBox rememberMeCheckbox;
    private TextView signUpLink;
    private TextView forgotPasswordLink;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userManager = new UserManager(this);

        if (userManager.isUserLoggedIn()) {
            startMainActivity();
            return;
        }

        loginInput = findViewById(R.id.emailInput);  // Keep the ID same for now
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        rememberMeCheckbox = findViewById(R.id.rememberMeCheckbox);
        signUpLink = findViewById(R.id.registerLink);
        forgotPasswordLink = findViewById(R.id.forgotPasswordLink);

        loginButton.setOnClickListener(v -> attemptLogin());

        signUpLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        forgotPasswordLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            String loginId = loginInput.getText().toString().trim();
            if (!loginId.isEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(loginId).matches()) {
                intent.putExtra("email", loginId);
            }
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }

    private boolean isValidPassword(String password) {
        // Check for minimum 8 characters
        if (password.length() < 8) return false;

        // Check for at least one uppercase
        if (!password.matches(".*[A-Z].*")) return false;

        // Check for at least one number
        if (!password.matches(".*\\d.*")) return false;

        // Check for at least one special character
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) return false;

        return true;
    }

    private void attemptLogin() {
        String loginId = loginInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (loginId.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidPassword(password)) {
            passwordInput.setError("Password must contain at least 8 characters, 1 uppercase, 1 number, and 1 special character");
            return;
        }

        if (userManager.loginUser(loginId, password, rememberMeCheckbox.isChecked())) {
            startMainActivity();
        } else {
            Toast.makeText(this, "Invalid username/email or password", Toast.LENGTH_SHORT).show();
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}