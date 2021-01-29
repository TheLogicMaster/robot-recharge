package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.audio.Sound;

public interface TTSEngine {

    Sound textToSpeech(String text);

    void init();
}
