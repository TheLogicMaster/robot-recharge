package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.utils.Array;
import de.golfgl.gdxgamesvcs.IGameServiceClient;

public interface GameServices extends IGameServiceClient {

    /**
     * Used to set the GameJolt credentials
     *
     * @param user  The username
     * @param token The user token
     */
    void setCredentials(String user, String token);

    /**
     * Whether manual GameJolt credentials are needed or not
     *
     * @return Needs credentials
     */
    boolean needsCredentials();

    void showRewardAd(RewardAdListener listener);

    String getInAppPrice(String id);

    void inAppPurchase(String id, InAppPurchaseListener listener);

    void restorePurchases(RestorePurchasesListener listener);

    interface InAppPurchaseListener {
        void onPurchase();
    }

    interface RestorePurchasesListener {
        void onRestorePurchases(Array<String> purchases);
    }

    interface RewardAdListener {
        void onAdReward();
    }
}
