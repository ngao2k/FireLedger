package com.example.fireledger;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormatSymbols;
import java.util.List;

public class BillingAdapter extends RecyclerView.Adapter<BillingAdapter.BillingViewHolder> {

    private final List<BillingItem> billingList;

    public BillingAdapter(List<BillingItem> billingList) {
        this.billingList = billingList;
    }

    @NonNull
    @Override
    public BillingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_billing, parent, false);
        return new BillingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillingViewHolder holder, int position) {
        BillingItem item = billingList.get(position);

        // 获取完整日期
        String date = item.getDate(); // 假设日期格式为 "YYYY-MM-DD"
        String[] dateParts = date.split("-");
        if (dateParts.length == 3) {
            // 提取年月日
            int year = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]) - 1; // 月份索引从 0 开始
            String day = dateParts[2]; // 日期部分

            // 转换为英文月份
            String monthName = new DateFormatSymbols().getMonths()[month];

            // 设置到视图
            holder.monthText.setText(monthName);
            holder.dateText.setText(day);
        } else {
            // 如果日期格式不正确，显示默认值
            holder.monthText.setText("Unknown");
            holder.dateText.setText("?");
        }

        // 设置其他账单信息
        holder.typeText.setText(item.getType());
        holder.descriptionText.setText(item.getDescription());
        holder.amountText.setText("$" + item.getAmount());
    }

    @Override
    public int getItemCount() {
        return billingList.size();
    }

    static class BillingViewHolder extends RecyclerView.ViewHolder {
        TextView monthText, dateText, typeText, descriptionText, amountText;

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
