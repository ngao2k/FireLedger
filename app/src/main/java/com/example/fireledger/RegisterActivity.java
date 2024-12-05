package com.example.fireledger;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.*;

public class RegisterActivity extends AppCompatActivity {
    private EditText emailEditText, passwordEditText, confirmPasswordEditText;
    private Button registerButton;
    private TextView loginLink;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI components
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        registerButton = findViewById(R.id.registerButton);
        loginLink = findViewById(R.id.loginLink);

        /**
         * Sets up click listener for the register button.
         * Validates user input and registers a new user using Firebase Authentication.
         */
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user input from EditText fields
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();

                // Check if any of the fields are empty
                if (TextUtils.isEmpty(email)) {
                    emailEditText.setError("Please enter your email");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    passwordEditText.setError("Please enter your password");
                    return;
                }

                if (TextUtils.isEmpty(confirmPassword)) {
                    confirmPasswordEditText.setError("Please confirm your password");
                    return;
                }

                // Check if passwords match
                if (!password.equals(confirmPassword)) {
                    confirmPasswordEditText.setError("Passwords do not match");
                    return;
                }

                /**
                 * Creates a new user with the provided email and password using Firebase Authentication.
                 * Sends a verification email upon successful registration.
                 */
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Registration successful, send verification email
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    user.sendEmailVerification()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(RegisterActivity.this,
                                                                "Registration successful, please verify your email", Toast.LENGTH_SHORT).show();
                                                        // Redirect to login screen or main screen
                                                    } else {
                                                        Toast.makeText(RegisterActivity.this,
                                                                "Failed to send verification email: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    // If registration fails, display error message
                                    Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        /**
         * Sets up click listener for the login link.
         * Navigates to the login screen when clicked.
         */
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                // Navigate to login screen
            }
        });
    }
}