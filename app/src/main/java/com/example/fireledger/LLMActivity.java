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

/**
 * An activity for performing analysis on billing data using a Spark model and displaying the results.
 */
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

        // Initialize view components
        titleText = findViewById(R.id.titleText);
        resultText = findViewById(R.id.llmresultText);
        resultScrollView = findViewById(R.id.resultScrollView);
        backButton = findViewById(R.id.backButton);

        databaseHelper = new BillingDatabaseHelper(this);

        // Initialize the Spark model and thread pool
        spark = new Spark();
        executorService = Executors.newSingleThreadExecutor();

        // Set up back button functionality
        backButton.setOnClickListener(v -> finish());

        // Display month selection dialog
        showMonthSelectionDialog();
    }

    /**
     * Shows a dialog for the user to select a month from available billing data.
     */
    private void showMonthSelectionDialog() {
        List<String> availableMonths = databaseHelper.getAvailableMonths();
        Log.d("LLMActivity", "Available Months: " + availableMonths);
        if (availableMonths.isEmpty()) {
            Toast.makeText(this, "No available billing data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String[] monthArray = availableMonths.toArray(new String[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Analysis Month")
                .setItems(monthArray, (dialog, which) -> {
                    selectedMonth = monthArray[which];
                    Toast.makeText(this, "Selected Month: " + selectedMonth, Toast.LENGTH_SHORT).show();
                    performAnalysis(selectedMonth);
                })
                .setCancelable(false);
        builder.create().show();
    }

    /**
     * Performs analysis on the selected month's billing data using the Spark model.
     *
     * @param month The month of billing data to analyze.
     */
    private void performAnalysis(String month) {
        List<BillingItem> bills = databaseHelper.getBillingsByMonth(month);
        if (bills.isEmpty()) {
            resultText.setText("No billing data for the selected month");
            return;
        }

        // Concatenate monthly billing data into a string
        StringBuilder aggregatedData = new StringBuilder("Below is the billing data for ").append(month).append(":");
        for (BillingItem bill : bills) {
            aggregatedData.append("- Date: ").append(bill.getDate()).append(",")
                    .append(" Type: ").append(bill.getType()).append(",")
                    .append(" Amount: $").append(bill.getAmount()).append(",")
                    .append(" Description: ").append(bill.getDescription()).append(".");
        }
        aggregatedData.append(" Please analyze this bill and provide recommendations. Answer in English.");

        // Show loading message
        resultText.setText("Analyzing " + month + " data, please wait...");

        // Call the Spark model in the background
        executorService.execute(() -> {
            try {
                Log.d("LLMActivity", "Aggregated Data: " + aggregatedData.toString());
                String analysisResult = spark.request(aggregatedData.toString());

                // Update UI with analysis results
                runOnUiThread(() -> updateAnalysisResults(analysisResult));

            } catch (IOException e) {
                runOnUiThread(() -> Toast.makeText(LLMActivity.this, "Network error, please try again later.", Toast.LENGTH_SHORT).show());
            } catch (JSONException e) {
                runOnUiThread(() -> Toast.makeText(LLMActivity.this, "Data parsing error, please contact support team.", Toast.LENGTH_SHORT).show());
            }
        });
    }

    /**
     * Updates the UI with the analysis results rendered as Markdown.
     *
     * @param result The analysis result text to display.
     */
    private void updateAnalysisResults(String result) {
        // Render Markdown using Markwon
        Markwon markwon = Markwon.create(this);
        markwon.setMarkdown(resultText, result);

        // Automatically scroll to top
        resultScrollView.post(() -> resultScrollView.fullScroll(ScrollView.FOCUS_UP));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Shutdown the executor service when the activity is destroyed
        executorService.shutdown();
    }
}