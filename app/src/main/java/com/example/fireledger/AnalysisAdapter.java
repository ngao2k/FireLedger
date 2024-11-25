package com.example.fireledger;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AnalysisAdapter extends RecyclerView.Adapter<AnalysisAdapter.AnalysisViewHolder> {
    private final List<AnalysisItem> analysisData;

    public AnalysisAdapter(List<AnalysisItem> analysisData) {
        this.analysisData = analysisData;
    }

    @NonNull
    @Override
    public AnalysisViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_analysis, parent, false);
        return new AnalysisViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnalysisViewHolder holder, int position) {
        AnalysisItem item = analysisData.get(position);
        holder.title.setText(item.getTitle());
        holder.description.setText(item.getDescription());
    }

    @Override
    public int getItemCount() {
        return analysisData.size();
    }

    static class AnalysisViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;

        public AnalysisViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.analysisTitleText);
            description = itemView.findViewById(R.id.analysisDescriptionText);
        }
    }
}
