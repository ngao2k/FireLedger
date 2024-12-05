package com.example.fireledger;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // RecyclerView to display the list of billings.
    private RecyclerView recyclerView;
    // Database helper class for interacting with the billing database.
    private BillingDatabaseHelper databaseHelper;
    // List of billings, storing the billing information retrieved from the database.
    private List<BillingItem> billingList;
    // Adapter for the RecyclerView to update the displayed list of billings.
    private BillingAdapter adapter;

    /**
     * Initialization method called when the activity is created.
     * @param savedInstanceState The saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the RecyclerView and set its layout manager.
        recyclerView = findViewById(R.id.billingList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the database helper and fetch the list of billings.
        databaseHelper = new BillingDatabaseHelper(this);
        billingList = databaseHelper.getAllBillings();
        // Create and set the adapter for the RecyclerView.
        adapter = new BillingAdapter(billingList);
        recyclerView.setAdapter(adapter);

        // Add swipe-to-delete functionality.
        addSwipeToDelete();

        // Set click listener for the add billing button.
        findViewById(R.id.addButton).setOnClickListener(view -> showAddBillingDialog());
        findViewById(R.id.needReportText).setOnClickListener(view -> {
            // Navigate to the report page.
            startActivity(new Intent(MainActivity.this, LLMActivity.class));
        });
    }

    /**
     * Method to add swipe-to-delete functionality to the RecyclerView.
     */
    private void addSwipeToDelete() {
        // Create an ItemTouchHelper instance to handle swipe events.
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            // Disable move functionality by returning false.
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            /**
             * Handle swiping a billing item to delete it.
             * @param viewHolder The ViewHolder that was swiped.
             * @param direction The direction of the swipe.
             */
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // Get the position and the billing item of the swiped view.
                int position = viewHolder.getAdapterPosition();
                BillingItem item = billingList.get(position);

                // Delete the billing item from the database.
                databaseHelper.deleteBilling(item);
                // Remove the item from the list and notify the adapter.
                billingList.remove(position);
                adapter.notifyItemRemoved(position);

                // Show a toast message indicating the item has been deleted.
                Toast.makeText(MainActivity.this, "Deleted: " + item.getType() + " - " + item.getAmount(), Toast.LENGTH_SHORT).show();
            }

            /**
             * Draw the background and icon during swipe action.
             * @param c Canvas to draw on.
             * @param recyclerView The RecyclerView.
             * @param viewHolder The ViewHolder.
             * @param dX Amount of horizontal scroll.
             * @param dY Amount of vertical scroll.
             * @param actionState State of the swipe action.
             * @param isCurrentlyActive Whether the action is currently active.
             */
            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    // Set the paint color to red.
                    Paint paint = new Paint();
                    paint.setColor(Color.RED);

                    // Get the position of the view.
                    float top = viewHolder.itemView.getTop();
                    float bottom = viewHolder.itemView.getBottom();
                    float right = viewHolder.itemView.getRight();
                    float left = right + dX;

                    // Draw a red rectangle as background.
                    c.drawRect(left, top, right, bottom, paint);

                    // Load and set the position of the delete icon.
                    Drawable icon = ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_delete);
                    if (icon != null) {
                        int iconMargin = (viewHolder.itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                        int iconTop = (int) top + iconMargin;
                        int iconBottom = (int) bottom - iconMargin;
                        int iconLeft = (int) right - iconMargin - icon.getIntrinsicWidth();
                        int iconRight = (int) right - iconMargin;

                        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                        icon.draw(c);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        });

        // Attach the ItemTouchHelper to the RecyclerView.
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    /**
     * Method to show the dialog for adding a new billing item.
     */
    private void showAddBillingDialog() {
        // Create an AlertDialog.Builder and layout inflater.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_billing, null);
        builder.setView(dialogView);

        // Initialize views within the dialog.
        Button datePickerButton = dialogView.findViewById(R.id.datePickerButton);
        Spinner typeSpinner = dialogView.findViewById(R.id.typeSpinner);
        EditText amountInput = dialogView.findViewById(R.id.amountInput);
        EditText descriptionInput = dialogView.findViewById(R.id.descriptionText);
        Button saveButton = dialogView.findViewById(R.id.saveButton);

        // Store the selected date.
        final String[] selectedDate = {""};
        // Set a click listener for the date picker button.
        datePickerButton.setOnClickListener(view -> {
            // Create a DatePickerDialog and handle the date selection event.
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (datePicker, year, month, dayOfMonth) -> {
                        selectedDate[0] = year + "-" + (month + 1) + "-" + dayOfMonth;
                        datePickerButton.setText(selectedDate[0]);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        // Initialize the type selection spinner.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.transaction_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);

        // Create and display the dialog.
        AlertDialog dialog = builder.create();
        // Set a click listener for the save button.
        saveButton.setOnClickListener(view -> {
            // Retrieve the entered billing details.
            String date = selectedDate[0];
            String type = typeSpinner.getSelectedItem().toString();
            String amount = amountInput.getText().toString();
            String description = descriptionInput.getText().toString();

            // Validate the input fields.
            if (date.isEmpty() || type.isEmpty() || amount.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Add the billing details to the database.
                databaseHelper.addBilling(date, type, amount, description);

                // Create a new billing item and update the list and adapter.
                BillingItem newItem = new BillingItem("", date, type, amount, description);
                billingList.add(0, newItem);
                this.adapter.notifyItemInserted(0);
                recyclerView.scrollToPosition(0);

                // Close the dialog and show a success toast message.
                dialog.dismiss();
                Toast.makeText(this, "Billing added", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }
}