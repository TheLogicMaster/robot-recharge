package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class PreferencesHelper {

    public final Preferences preferences;

    public PreferencesHelper() {
        preferences = Gdx.app.getPreferences("RobotRecharge");;
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

    public LevelSave getSave(String level) {
        return RobotAssets.json.fromJson(LevelSave.class, preferences.getString("save/" + level));
    }

    public boolean hasSave(String level) {
        return preferences.contains("save/" + level);
    }

    /**
     * A helper method to get a preference or write a default value if it doesn't yet exist
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
}
