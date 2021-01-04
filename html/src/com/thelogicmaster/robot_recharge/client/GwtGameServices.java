package com.thelogicmaster.robot_recharge.client;

import com.google.gwt.user.client.Window;
import com.thelogicmaster.robot_recharge.BuildConfig;
import com.thelogicmaster.robot_recharge.GameServices;
import de.golfgl.gdxgamesvcs.GameJoltClient;

public class GwtGameServices extends GameJoltClient implements GameServices {

    public GwtGameServices() {
        initialize("570956", BuildConfig.GAME_JOLT_KEY);
        if (Window.Location.getParameter(GameJoltClient.GJ_USERNAME_PARAM) != null) {
            setUserName(Window.Location.getParameter(GameJoltClient.GJ_USERNAME_PARAM));
            setUserToken(Window.Location.getParameter(GameJoltClient.GJ_USERTOKEN_PARAM));
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

    @Override
    public void showRewardAd(RewardAdListener listener) {
    }

    @Override
    public String getInAppPrice(String id) {
        return null;
    }

    @Override
    public void inAppPurchase(String id, InAppPurchaseListener listener) {
    }

    @Override
    public void restorePurchases(RestorePurchasesListener listener) {
    }
}
