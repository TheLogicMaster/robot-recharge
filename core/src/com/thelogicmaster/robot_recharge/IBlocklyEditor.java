package com.thelogicmaster.robot_recharge;

public interface IBlocklyEditor {

    void show();

    void resize(int x, int width, int height);

    void hide();

    boolean isShown();

    void load(String data);

    void clear();

    void save(Consumer<String> callback);

    void generateCode(Consumer<String> callback);

    boolean isLoaded();
}
