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

        // 初始化Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // 初始化UI组件
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        registerButton = findViewById(R.id.registerButton);
        loginLink = findViewById(R.id.loginLink);

        // 设置注册按钮点击事件
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 获取用户输入的邮箱和密码
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();

                // 检查输入是否为空
                if (TextUtils.isEmpty(email)) {
                    emailEditText.setError("请输入邮箱");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    passwordEditText.setError("请输入密码");
                    return;
                }

                if (TextUtils.isEmpty(confirmPassword)) {
                    confirmPasswordEditText.setError("请确认密码");
                    return;
                }

                // 检查密码是否一致
                if (!password.equals(confirmPassword)) {
                    confirmPasswordEditText.setError("密码不匹配");
                    return;
                }

                // 使用FirebaseAuth创建用户
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // 注册成功，发送验证邮件
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    user.sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(RegisterActivity.this,
                                                            "注册成功，请检查您的邮箱进行验证", Toast.LENGTH_SHORT).show();
                                                    // 跳转到登录界面或主界面
                                                } else {
                                                    Toast.makeText(RegisterActivity.this,
                                                            "发送验证邮件失败：" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                } else {
                                    // 如果注册失败，显示错误信息
                                    Toast.makeText(RegisterActivity.this, "注册失败：" + task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        // 设置登录链接点击事件
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
               startActivity(intent);
               finish();
                // 可以跳转到登录界面
            }
        });
    }
}
