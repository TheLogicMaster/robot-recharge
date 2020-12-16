package com.thelogicmaster.robot_recharge.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.backends.lwjgl.audio.OpenALLwjglAudio;
import com.badlogic.gdx.backends.lwjgl.audio.OpenALSound;
import com.badlogic.gdx.backends.lwjgl.audio.Wav;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.StreamUtils;
import com.thelogicmaster.robot_recharge.RobotUtils;

import javax.sound.sampled.AudioInputStream;
import java.lang.reflect.Method;

public class DesktopStreamSound extends OpenALSound {

    public DesktopStreamSound(AudioInputStream input) {
        super((OpenALLwjglAudio) Gdx.audio);
        try {
            Method method = OpenALSound.class.getDeclaredMethod("setup", byte[].class, int.class, int.class);
            method.setAccessible(true);
            method.invoke(this, StreamUtils.copyStreamToByteArray(input, input.available()), input.getFormat().getChannels(), (int)input.getFormat().getSampleRate());
        } catch (Exception e) {
            Gdx.app.error("StreamSound", "Failed to initialize sound from stream", e);
        }
    }
}
