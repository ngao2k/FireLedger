package com.example.fireledger;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * AnalysisAdapter is a RecyclerView.Adapter for displaying analysis items.
 * It binds the analysis data to the RecyclerView, allowing users to scroll through the list of analysis items.
 */
public class AnalysisAdapter extends RecyclerView.Adapter<AnalysisAdapter.AnalysisViewHolder> {
    // List to store analysis data
    private final List<AnalysisItem> analysisData;

    /**
     * Constructor for AnalysisAdapter.
     * Initializes the adapter with a list of analysis data.
     *
     * @param analysisData List of analysis data items
     */
    public AnalysisAdapter(List<AnalysisItem> analysisData) {
        this.analysisData = analysisData;
    }

    /**
     * Creates a new AnalysisViewHolder.
     * This method is responsible for inflating the item view and returning an instance of AnalysisViewHolder.
     *
     * @param parent ViewGroup for attaching the item view
     * @param viewType Type of the item view
     * @return AnalysisViewHolder instance
     */
    @NonNull
    @Override
    public AnalysisViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_analysis, parent, false);
        return new AnalysisViewHolder(view);
    }

    /**
     * Binds data to the AnalysisViewHolder.
     * This method sets the title and description text for the item view based on its position in the list.
     *
     * @param holder AnalysisViewHolder instance
     * @param position Index position of the item in the list
     */
    @Override
    public void onBindViewHolder(@NonNull AnalysisViewHolder holder, int position) {
        AnalysisItem item = analysisData.get(position);
        holder.title.setText(item.getTitle());
        holder.description.setText(item.getDescription());
    }

    /**
     * Returns the total number of items in the analysis data list.
     * This method is used by the RecyclerView to determine the number of items in the data set.
     *
     * @return Total number of items in the analysis data list
     */
    @Override
    public int getItemCount() {
        return analysisData.size();
    }

    /**
     * AnalysisViewHolder is a static inner class for holding the views needed to display an analysis item.
     * It initializes the title and description TextViews in the constructor.
     */
    static class AnalysisViewHolder extends RecyclerView.ViewHolder {
        // TextViews for displaying the title and description of the analysis item
        TextView title, description;

        /**
         * Constructor for AnalysisViewHolder.
         * Initializes the title and description TextViews based on the provided itemView.
         *
         * @param itemView The root view of the analysis item
         */
        public AnalysisViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.analysisTitleText);
            description = itemView.findViewById(R.id.analysisDescriptionText);
        }
    }
}
