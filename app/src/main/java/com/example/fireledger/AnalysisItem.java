package com.example.fireledger;

public class AnalysisItem {
    private final String title;       // 分析结果的标题
    private final String description; // 分析结果的描述

    // 构造函数
    public AnalysisItem(String title, String description) {
        this.title = title;
        this.description = description;
    }

    // Getter 方法
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
