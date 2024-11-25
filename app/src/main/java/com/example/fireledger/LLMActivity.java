package com.example.fireledger;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.noties.markwon.Markwon;

public class LLMActivity extends AppCompatActivity {
    private TextView titleText;
    private TextView resultText;
    private ScrollView resultScrollView;
    private Button backButton;
    private BillingDatabaseHelper databaseHelper;
    private String selectedMonth;

    private Spark spark;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_llm);

        // 初始化视图组件
        titleText = findViewById(R.id.titleText);
        resultText = findViewById(R.id.llmresultText);
        resultScrollView = findViewById(R.id.resultScrollView);
        backButton = findViewById(R.id.backButton);

        databaseHelper = new BillingDatabaseHelper(this);

        // 初始化 Spark 模型和线程池
        spark = new Spark();
        executorService = Executors.newSingleThreadExecutor();

        // 返回按钮功能
        backButton.setOnClickListener(v -> finish());

        // 显示月份选择
        showMonthSelectionDialog();
    }

    private void showMonthSelectionDialog() {
        List<String> availableMonths = databaseHelper.getAvailableMonths();
        Log.d("LLMActivity", "Available Months: " + availableMonths);
        if (availableMonths.isEmpty()) {
            Toast.makeText(this, "无可用账单数据", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String[] monthArray = availableMonths.toArray(new String[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择分析月份")
                .setItems(monthArray, (dialog, which) -> {
                    selectedMonth = monthArray[which];
                    Toast.makeText(this, "选择月份: " + selectedMonth, Toast.LENGTH_SHORT).show();
                    performAnalysis(selectedMonth);
                })
                .setCancelable(false);
        builder.create().show();
    }

    private void performAnalysis(String month) {
        List<BillingItem> bills = databaseHelper.getBillingsByMonth(month);
        if (bills.isEmpty()) {
            resultText.setText("所选月份无账单数据");
            return;
        }

        // 拼接月度账单数据
        StringBuilder aggregatedData = new StringBuilder("以下是").append(month).append("的账单数据:");
        for (BillingItem bill : bills) {
            aggregatedData.append("- 日期：").append(bill.getDate()).append(",")
                    .append("，类型：").append(bill.getType()).append(",")
                    .append("，金额：$").append(bill.getAmount()).append(",")
                    .append("，备注：").append(bill.getDescription()).append(".");
        }
        aggregatedData.append("请分析这份账单并提供建议。");

        // 显示加载提示
        resultText.setText("正在分析" + month + "的数据，请稍候...");

        // 后台调用 Spark 模型
        executorService.execute(() -> {
            try {
                Log.d("LLMActivity", "Aggregated Data: " + aggregatedData.toString());
                String analysisResult = spark.request(aggregatedData.toString());

                // 更新分析结果
                runOnUiThread(() -> updateAnalysisResults(analysisResult));

            } catch (IOException e) {
                runOnUiThread(() -> Toast.makeText(LLMActivity.this, "网络错误，请稍后重试。", Toast.LENGTH_SHORT).show());
            } catch (JSONException e) {
                runOnUiThread(() -> Toast.makeText(LLMActivity.this, "数据解析错误，请联系支持团队。", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void updateAnalysisResults(String result) {
        // 使用 Markwon 渲染 Markdown
        Markwon markwon = Markwon.create(this);
        markwon.setMarkdown(resultText, result);

        // 自动滚动到顶部
        resultScrollView.post(() -> resultScrollView.fullScroll(ScrollView.FOCUS_UP));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
