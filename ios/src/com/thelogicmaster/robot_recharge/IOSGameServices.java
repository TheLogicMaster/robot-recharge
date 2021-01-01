package com.thelogicmaster.robot_recharge;

import de.golfgl.gdxgamesvcs.GameCenterClient;
import org.robovm.apple.uikit.UIViewController;

public class IOSGameServices extends GameCenterClient implements GameServices {

    public IOSGameServices(UIViewController viewController) {
        super(viewController);
    }

    @Override
    public void setCredentials(String user, String token) {
    }

    @Override
    public boolean needsCredentials() {
        return false;
    }
}
