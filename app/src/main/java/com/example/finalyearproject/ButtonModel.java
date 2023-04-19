package com.example.finalyearproject;

public class ButtonModel {

    private String text;
    private String imageUrl;

    public ButtonModel(String text, String imageUrl) {
        this.text = text;
        this.imageUrl = imageUrl;
    }

    public String getText() {
        return text;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}

