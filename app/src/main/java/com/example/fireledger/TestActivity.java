package com.example.fireledger;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestActivity extends AppCompatActivity {

    private EditText etUserMessage;
    private TextView tvResponse;
    private Spark spark;
    private ExecutorService executorService;
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        etUserMessage = findViewById(R.id.et_user_message);
        tvResponse = findViewById(R.id.tv_response);
        Button btnSend = findViewById(R.id.btn_send);

        spark = new Spark();

        // 创建 ExecutorService 和 Handler 实例
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userMessage = etUserMessage.getText().toString().trim();
                if (!userMessage.isEmpty()) {
                    tvResponse.setText("正在获取响应...");
                    executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // 网络请求在后台线程中执行
                                String result = spark.request(userMessage);

                                // 更新 UI 要在主线程中进行
                                mainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        tvResponse.setText(result);
                                    }
                                });
                            } catch (IOException e) {
                                mainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        tvResponse.setText("网络错误，请稍后重试。");
                                    }
                                });
                            } catch (JSONException e) {
                                mainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        tvResponse.setText("数据解析错误，请联系支持团队。");
                                    }
                                });
                            }
                        }
                    });
                } else {
                    tvResponse.setText("请输入消息");
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 关闭 ExecutorService，以避免内存泄漏
        executorService.shutdown();
    }
}
