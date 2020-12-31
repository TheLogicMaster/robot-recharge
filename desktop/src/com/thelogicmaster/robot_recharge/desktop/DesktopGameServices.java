package com.thelogicmaster.robot_recharge.desktop;

import com.thelogicmaster.robot_recharge.BuildConfig;
import com.thelogicmaster.robot_recharge.GameServices;
import de.golfgl.gdxgamesvcs.GameJoltClient;

public class DesktopGameServices extends GameJoltClient implements GameServices {

    public DesktopGameServices() {
        initialize("570956", BuildConfig.GAME_JOLT_KEY);
    }

    @Override
    public void setCredentials(String user, String token) {
        setUserName(user);
        setUserToken(token);
    }
}
