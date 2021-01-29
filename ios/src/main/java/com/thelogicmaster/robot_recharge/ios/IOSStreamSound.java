package com.thelogicmaster.robot_recharge.ios;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.StreamUtils;
import org.robovm.apple.avfoundation.*;
import org.robovm.rt.bro.ptr.BytePtr;

import javax.sound.sampled.AudioInputStream;
import java.nio.ByteBuffer;

public class IOSStreamSound implements Sound {

    private AVAudioEngine engine;
    private AVAudioPlayerNode player;
    private AVAudioPCMBuffer buffer;

    public IOSStreamSound(AudioInputStream stream) {
        try {
            engine = new AVAudioEngine();
            player = new AVAudioPlayerNode(); // 48000?
            AVAudioFormat format = new AVAudioFormat(AVAudioCommonFormat.PCMFormatInt32, 44100, 1, false);
            buffer = new AVAudioPCMBuffer(format, (int) stream.getFrameLength());
            ByteBuffer bytes = buffer.getInt16ChannelData().as(BytePtr.BytePtrPtr.class).get().asByteBuffer((int) stream.getFrameLength() * stream.getFormat().getFrameSize());
            StreamUtils.copyStream(stream, bytes);
            engine.attachNode(player);
            engine.connect(player, engine.getMainMixerNode(), buffer.getFormat());
            engine.start();
        } catch (Exception e) {
            Gdx.app.error("StreamSound", "Failed to init sound", e);
        }
    }

    @Override
    public long play() {
        player.play();
        player.scheduleBuffer(buffer, null);
        return 0;
    }

    @Override
    public long play(float volume) {
        return 0;
    }

    @Override
    public long play(float volume, float pitch, float pan) {
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
}
