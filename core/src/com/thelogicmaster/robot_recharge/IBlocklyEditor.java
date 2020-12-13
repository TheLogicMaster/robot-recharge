package com.thelogicmaster.robot_recharge;

public interface IBlocklyEditor {

    void show();

    void resize(int screenWidth, int screenHeight);

    void setWidth(int width);

    void hide();

    boolean isShown();

    void load(String data);

    void clear();

    void save(Consumer<String> callback);

    void generateCode(Language language, Consumer<String> callback);

    boolean isLoaded();
}
