package com.thelogicmaster.robot_recharge;

public class TutorialPage {

    private String image;
    private String text;

    public TutorialPage(String image, String text) {
        this.image = image;
        this.text = text;
    }

    private TutorialPage() {

    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
