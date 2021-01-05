package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Array;

public class PreferencesHelper {

    public final Preferences preferences;

    public PreferencesHelper() {
        preferences = Gdx.app.getPreferences("RobotRecharge");
    }

    public int getUnlockedLevel() {
        return preferences.getInteger("unlockedLevel");
    }

    public void unlockLevel(int level) {
        preferences.putInteger("unlockedLevel", Math.max(level, getUnlockedLevel()));
        preferences.flush();
    }

    public void saveLevel(LevelSave save) {
        preferences.putString("save/" + save.getLevel(), RobotAssets.json.toJson(save));
        preferences.flush();
    }

    public LevelSave getLevelSave(String level) {
        return RobotAssets.json.fromJson(LevelSave.class, preferences.getString("save/" + level));
    }

    public boolean hasLevelSave(String level) {
        return preferences.contains("save/" + level);
    }

    public void loadGameSave(GameSave save) {
        for (LevelSave levelSave : save.getLevelSaves())
            saveLevel(levelSave);
        unlockLevel(save.getUnlockedLevel());
    }

    public GameSave getGameSave() {
        Array<LevelSave> levelSaves = new Array<>();
        for (LevelInfo info : RobotRecharge.assets.levelInfo)
            if (hasLevelSave(info.getName()))
                levelSaves.add(getLevelSave(info.getName()));
        return new GameSave(levelSaves, getUnlockedLevel());
    }

    /**
     * A helper method to get a preference or write a default value if it doesn't yet exist
     *
     * @param key The preference key
     * @param val The default value
     * @return The preference or default value
     */
    public float getFloatOrWriteDefault(String key, float val) {
        if (!preferences.contains(key)) {
            preferences.putFloat(key, val);
            preferences.flush();
            return val;
        }
        return preferences.getFloat(key);
    }

    public float getMusicVolume() {
        return getFloatOrWriteDefault("musicVolume", 1f);
    }

    public void setMusicVolume(float volume) {
        preferences.putFloat("musicVolume", volume);
        preferences.flush();
    }

    public float getEffectsVolume() {
        return getFloatOrWriteDefault("effectsVolume", 1f);
    }

    public void setEffectsVolume(float volume) {
        preferences.putFloat("effectsVolume", volume);
        preferences.flush();
    }

    public WindowMode getWindowMode() {
        return WindowMode.valueOf(preferences.getString("windowMode", "Windowed"));
    }

    public void setWindowMode(WindowMode mode) {
        preferences.putString("windowMode", mode.name());
        preferences.flush();
    }

    public boolean hasGameJoltCredentials() {
        return !preferences.getString("gameJoltUsername").equals("")
                && !preferences.getString("gameJoltToken").equals("");
    }

    public void setGameJoltCredentials(String username, String token) {
        preferences.putString("gameJoltUsername", username);
        preferences.putString("gameJoltToken", token);
        preferences.flush();
    }

    public void clearGameJoltCredentials() {
        preferences.remove("gameJoltUsername");
        preferences.remove("gameJoltToken");
        preferences.flush();
    }

    public String getGameJoltUsername() {
        return preferences.getString("gameJoltUsername");
    }

    public String getGameJoltToken() {
        return preferences.getString("gameJoltToken");
    }

    public boolean hasUnlockedSolutions() {
        return preferences.getBoolean("purchases/solutions");
    }

    public void unlockSolutions() {
        preferences.putBoolean("purchases/solutions", true);
        preferences.flush();
    }

    public void restorePurchases(Array<String> purchases) {
        for (String purchase : purchases)
            preferences.putBoolean("purchases/" + purchase, true);
        preferences.flush();
    }

    public boolean hasRestoredSave() {
        return preferences.getBoolean("hasRestoredSave");
    }

    public void setRestoredSave() {
        preferences.putBoolean("hasRestoredSave", true);
        preferences.flush();
    }
}
