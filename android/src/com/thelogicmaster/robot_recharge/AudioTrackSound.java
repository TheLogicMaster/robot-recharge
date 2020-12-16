package com.thelogicmaster.robot_recharge;

import android.media.AudioTrack;
import com.badlogic.gdx.audio.Sound;

public class AudioTrackSound implements Sound {

    private final AudioTrack track;

    public AudioTrackSound(AudioTrack track) {
        this.track = track;
    }

    @Override
    public long play() {
        return play(1);
    }

    @Override
    public long play(float volume) {
        track.setVolume(volume);
        track.play();
        return 0;
    }

    @Override
    public long play(float volume, float pitch, float pan) {
        return play(volume);
    }

    @Override
    public long loop() {
        return play();
    }

    @Override
    public long loop(float volume) {
        return play(volume);
    }

    @Override
    public long loop(float volume, float pitch, float pan) {
        return play(volume);
    }

    @Override
    public void stop() {
        track.stop();
    }

    @Override
    public void pause() {
        track.pause();
    }

    @Override
    public void resume() {
        track.play();
    }

    @Override
    public void dispose() {
        track.release();
    }

    @Override
    public void stop(long soundId) {
        stop();
    }

    @Override
    public void pause(long soundId) {
        pause();
    }

    @Override
    public void resume(long soundId) {
        resume();
    }

    @Override
    public void setLooping(long soundId, boolean looping) {
    }

    @Override
    public void setPitch(long soundId, float pitch) {
    }

    @Override
    public void setVolume(long soundId, float volume) {
        track.setVolume(volume);
    }

    @Override
    public void setPan(long soundId, float pan, float volume) {
    }
}
