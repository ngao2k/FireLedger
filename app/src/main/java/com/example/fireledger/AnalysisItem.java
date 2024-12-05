package com.example.fireledger;

/**
 * Represents an analysis item, containing the title and description of the analysis result.
 */
public class AnalysisItem {
    private final String title;       // 分析结果的标题
    private final String description; // 分析结果的描述

    /**
     * Constructs an AnalysisItem object.
     *
     * @param title       The title of the analysis result.
     * @param description The description of the analysis result.
     */
    public AnalysisItem(String title, String description) {
        this.title = title;
        this.description = description;
    }

    /**
     * Gets the title of the analysis result.
     *
     * @return The title of the analysis result.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the description of the analysis result.
     *
     * @return The description of the analysis result.
     */
    public String getDescription() {
        return description;
    }
}

