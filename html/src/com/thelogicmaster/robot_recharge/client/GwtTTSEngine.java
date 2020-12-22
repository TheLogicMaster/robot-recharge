package com.thelogicmaster.robot_recharge.client;

import com.badlogic.gdx.audio.Sound;
import com.thelogicmaster.robot_recharge.TTSEngine;

public class GwtTTSEngine implements TTSEngine {

    private native void speak(String text, float volume, float pitch, float rate)/*-{
        var msg = new SpeechSynthesisUtterance();
        msg.text = text;
        msg.volume = volume;
        msg.rate = rate;
        msg.pitch = pitch;
        $wnd.speechSynthesis.speak(msg);
    }-*/;

    @Override
    public Sound textToSpeech(final String text) {
        return new Sound() {
            @Override
            public long play() {
                return play(1, 1, 1);
            }

            @Override
            public long play(float volume) {
                return play(volume, 1, 1);
            }

            @Override
            public long play(float volume, float pitch, float pan) {
                speak(text, volume, pitch, pan);
                return 0;
            }

            @Override
            public long loop() {
                return 0;
            }

            @Override
            public long loop(float volume) {
                return 0;
            }

            @Override
            public long loop(float volume, float pitch, float pan) {
                return 0;
            }

            @Override
            public void stop() {

            }

            @Override
            public void pause() {

            }

            @Override
            public void resume() {

            }

            @Override
            public void dispose() {

            }

            @Override
            public void stop(long soundId) {

            }

            @Override
            public void pause(long soundId) {

            }

            @Override
            public void resume(long soundId) {

            }

            @Override
            public void setLooping(long soundId, boolean looping) {

            }

            @Override
            public void setPitch(long soundId, float pitch) {

            }

            @Override
            public void setVolume(long soundId, float volume) {

            }

            @Override
            public void setPan(long soundId, float pan, float volume) {

            }
        };
    }

    @Override
    public void init() {

    }
}
