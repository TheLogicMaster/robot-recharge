package com.thelogicmaster.robot_recharge.desktop;

import com.thelogicmaster.robot_recharge.BuildConfig;
import com.thelogicmaster.robot_recharge.GameServices;
import de.golfgl.gdxgamesvcs.GameJoltClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class DesktopGameServices extends GameJoltClient implements GameServices {

    public DesktopGameServices() {
        initialize("570956", BuildConfig.GAME_JOLT_KEY);
        File credentialsFile = new File(".gj-credentials");
        try (BufferedReader reader = new BufferedReader(new FileReader(credentialsFile))) {
            reader.readLine();
            setCredentials(reader.readLine(), reader.readLine());
        } catch (IOException e) {
            System.out.println("Failed to get GameJolt credentials from file");
        }
    }

    @Override
    public void setCredentials(String user, String token) {
        setUserName(user);
        setUserToken(token);
    }

    @Override
    public boolean needsCredentials() {
        return userToken == null || userName == null;
    }
}
