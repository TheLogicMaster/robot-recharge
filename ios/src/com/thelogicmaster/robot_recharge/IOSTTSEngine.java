package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import marytts.LocalMaryInterface;
import marytts.exceptions.MaryConfigurationException;
import marytts.exceptions.SynthesisException;

public class IOSTTSEngine implements TTSEngine {

    LocalMaryInterface mary;

    public IOSTTSEngine() {
        try {
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
            return new IOSStreamSound(mary.generateAudio(text));
        } catch (SynthesisException e) {
            Gdx.app.error("MaryTTS", "Failed to generate sound", e);
            return null;
        }
    }

    @Override
    public void init() {
    }
}
