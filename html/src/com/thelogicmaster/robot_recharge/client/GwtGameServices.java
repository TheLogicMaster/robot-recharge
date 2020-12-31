package com.thelogicmaster.robot_recharge.client;

import com.thelogicmaster.robot_recharge.BuildConfig;
import com.thelogicmaster.robot_recharge.GameServices;
import de.golfgl.gdxgamesvcs.GameJoltClient;

public class GwtGameServices extends GameJoltClient implements GameServices {

    public GwtGameServices() {
        initialize("570956", BuildConfig.GAME_JOLT_KEY);
    }

    @Override
    public void setCredentials(String user, String token) {
        setUserName(user);
        setUserToken(token);
    }
}
