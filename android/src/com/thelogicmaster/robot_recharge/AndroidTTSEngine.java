package com.thelogicmaster.robot_recharge;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.StreamUtils;
import com.marytts.android.link.MaryLink;
import lib.sound.sampled.AudioInputStream;
import marytts.LocalMaryInterface;
import marytts.exceptions.MaryConfigurationException;

public class AndroidTTSEngine implements TTSEngine {

    private LocalMaryInterface mary;
    private Context context;
    private AudioFormat format;

    public AndroidTTSEngine(Context context) {
        this.context = context;
        format = new AudioFormat.Builder()
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setSampleRate(44100)
                .build();
    }

    @Override
    public void init() {
        if (mary != null)
            return;
        MaryLink.load(context);
        try {
            mary = new LocalMaryInterface();
        } catch (MaryConfigurationException e) {
            Log.e("MaryTTS", "Failed to initialize", e);
        }
    }

    @Override
    public Sound textToSpeech(String text) {
        if (mary == null)
            return null;
        try {
            AudioInputStream stream = mary.generateAudio(text);
            AudioTrack track = new AudioTrack(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build(),
                    format, (int) stream.getFrameLength() * stream.getFormat().getFrameSize(),
                    AudioTrack.MODE_STREAM, AudioManager.AUDIO_SESSION_ID_GENERATE);
            track.write(StreamUtils.copyStreamToByteArray(stream), 0, (int) stream.getFrameLength() * stream.getFormat().getFrameSize());
            return new AudioTrackSound(track);
        } catch (Exception e) {
            Gdx.app.error("MaryTTS", "Failed to generate sound", e);
            return null;
        }
    }
}
