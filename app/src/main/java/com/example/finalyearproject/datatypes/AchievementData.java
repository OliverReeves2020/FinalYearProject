package com.example.finalyearproject.datatypes;

public class AchievementData {
    private String description;
    private String text;
    private String iconUrl;

    public AchievementData(String text, String iconUrl, String description) {
        this.text = text;
        this.iconUrl = iconUrl;
        this.description = description;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

