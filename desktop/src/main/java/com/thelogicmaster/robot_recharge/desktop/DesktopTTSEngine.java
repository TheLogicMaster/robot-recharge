package com.thelogicmaster.robot_recharge.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.thelogicmaster.robot_recharge.TTSEngine;
import marytts.LocalMaryInterface;
import marytts.exceptions.MaryConfigurationException;
import marytts.exceptions.SynthesisException;

public class DesktopTTSEngine implements TTSEngine {

    LocalMaryInterface mary;

    public DesktopTTSEngine() {
        try {
            System.setProperty("log4j.logger.marytts", "OFF");
            mary = new LocalMaryInterface();
            mary.setAudioEffects("Robot amount:100.0;");
        } catch (MaryConfigurationException e) {
            Gdx.app.error("MaryTTS", "Failed to initialize", e);
        }
    }

    @Override
    public Sound textToSpeech(String text) {
        if (mary == null)
            return null;
        try {
            return new DesktopStreamSound(mary.generateAudio(text));
        } catch (SynthesisException e) {
            Gdx.app.error("MaryTTS", "Failed to generate sound", e);
            return null;
        }
    }

    @Override
    public void init() {
    }
}
