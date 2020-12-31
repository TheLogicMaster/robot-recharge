package com.thelogicmaster.robot_recharge;

import de.golfgl.gdxgamesvcs.IGameServiceClient;

public interface GameServices extends IGameServiceClient {

    /**
     * Used to set the GameJolt credentials
     * @param user The username
     * @param token The user token
     */
    void setCredentials(String user, String token);
}
