package com.example.fireledger;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormatSymbols;
import java.util.List;

/**
 * BillingAdapter is a custom RecyclerView.Adapter for displaying a list of billing items.
 * It extends RecyclerView.Adapter to provide custom behavior for binding billing data to views.
 */
public class BillingAdapter extends RecyclerView.Adapter<BillingAdapter.BillingViewHolder> {

    // A list of BillingItem objects to be displayed by the adapter.
    private final List<BillingItem> billingList;

    /**
     * Constructor for BillingAdapter.
     * Initializes the adapter with a list of billing items.
     *
     * @param billingList The list of billing items to be displayed.
     */
    public BillingAdapter(List<BillingItem> billingList) {
        this.billingList = billingList;
    }

    /**
     * Creates a new view holder for a billing item.
     * This method is called by the RecyclerView to create a new view holder for a billing item.
     *
     * @param parent The ViewGroup into which the new view will be added.
     * @param viewType The view type of the new view.
     * @return A new BillingViewHolder instance.
     */
    @NonNull
    @Override
    public BillingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for a billing item and create a new view holder.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_billing, parent, false);
        return new BillingViewHolder(view);
    }

    /**
     * Binds the data for a billing item to its corresponding view.
     * This method is called by the RecyclerView to update the contents of an item's view.
     *
     * @param holder The view holder for the billing item.
     * @param position The position of the item in the list.
     */
    @Override
    public void onBindViewHolder(@NonNull BillingViewHolder holder, int position) {
        // Retrieve the billing item at the specified position.
        BillingItem item = billingList.get(position);

        // Split the date string into components and update the view with the month name and day.
        String date = item.getDate();
        String[] dateParts = date.split("-");
        if (dateParts.length == 3) {
            int month = Integer.parseInt(dateParts[1]) - 1;
            String day = dateParts[2];
            String monthName = new DateFormatSymbols().getMonths()[month];

            holder.monthText.setText(monthName);
            holder.dateText.setText(day);
        } else {
            // If the date format is incorrect, display "Unknown" for the month and "?" for the day.
            holder.monthText.setText("Unknown");
            holder.dateText.setText("?");
        }

        // Update the view with the type, description, and amount of the billing item.
        holder.typeText.setText(item.getType());
        holder.descriptionText.setText(item.getDescription());
        holder.amountText.setText("$" + item.getAmount());
    }

    /**
     * Returns the total number of items in the list.
     * This method is called by the RecyclerView to determine the number of items in the list.
     *
     * @return The total number of items in the list.
     */
    @Override
    public int getItemCount() {
        return billingList.size();
    }

    /**
     * BillingViewHolder is a static inner class that extends RecyclerView.ViewHolder.
     * It provides a reference to each view within a billing item's layout.
     */
    static class BillingViewHolder extends RecyclerView.ViewHolder {
        // TextViews for displaying the month, date, type, description, and amount of a billing item.
        TextView monthText, dateText, typeText, descriptionText, amountText;

        /**
         * Constructor for BillingViewHolder.
         * Initializes the view holder with references to the views in the item layout.
         *
         * @param itemView The root view of the item layout.
         */
        public BillingViewHolder(@NonNull View itemView) {
            super(itemView);
            monthText = itemView.findViewById(R.id.monthText);
            dateText = itemView.findViewById(R.id.dateText);
            typeText = itemView.findViewById(R.id.typeText);
            descriptionText = itemView.findViewById(R.id.descriptionText);
            amountText = itemView.findViewById(R.id.amountText);
        }
    }
}
