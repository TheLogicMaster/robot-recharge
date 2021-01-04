package com.thelogicmaster.robot_recharge;

import android.app.Activity;
import androidx.annotation.NonNull;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.pay.*;
import com.badlogic.gdx.pay.android.googlebilling.PurchaseManagerGoogleBilling;
import com.badlogic.gdx.utils.Array;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import de.golfgl.gdxgamesvcs.GpgsClient;

public class AndroidGameServices extends GpgsClient implements GameServices {

    private RewardedAd rewardedAd;
    private Activity context;
    private PurchaseManager purchaseManager;
    private RestorePurchasesListener restorePurchasesListener;
    private InAppPurchaseListener purchaseListener;

    @Override
    public GpgsClient initialize(Activity context, boolean enableDriveAPI) {
        this.context = context;
        MobileAds.initialize(context, (OnInitializationCompleteListener) null);
        loadRewardedAd();
        purchaseManager = new PurchaseManagerGoogleBilling(context);
        PurchaseManagerConfig purchaseConfig = new PurchaseManagerConfig();
        purchaseConfig.addOffer(new Offer().setType(OfferType.CONSUMABLE).setIdentifier("solutions"));
        purchaseManager.install(new PurchaseObserver() {
            @Override
            public void handleInstall() {

            }

            @Override
            public void handleInstallError(Throwable e) {

            }

            @Override
            public void handleRestore(Transaction[] transactions) {
                Array<String> purchases = new Array<>();
                for (Transaction transaction: transactions)
                    purchases.add(transaction.getIdentifier());
                if (restorePurchasesListener != null)
                    restorePurchasesListener.onRestorePurchases(purchases);
                restorePurchasesListener = null;
            }

            @Override
            public void handleRestoreError(Throwable e) {
                restorePurchasesListener = null;
            }

            @Override
            public void handlePurchase(Transaction transaction) {
                if (purchaseListener != null)
                    purchaseListener.onPurchase();
                purchaseListener = null;
            }

            @Override
            public void handlePurchaseError(Throwable e) {

            }

            @Override
            public void handlePurchaseCanceled() {

            }
        }, purchaseConfig, true);
        return super.initialize(context, enableDriveAPI);
    }

    @Override
    public void setCredentials(String user, String token) {
    }

    @Override
    public boolean needsCredentials() {
        return false;
    }

    @Override
    public String getInAppPrice(String id) {
        if (purchaseManager.getInformation(id) == null)
            return "Err";
        return purchaseManager.getInformation(id).getLocalPricing();
    }

    @Override
    public void inAppPurchase(String id, InAppPurchaseListener listener) {
        purchaseManager.purchase(id);
        purchaseListener = listener;
    }

    @Override
    public void restorePurchases(RestorePurchasesListener listener) {
        restorePurchasesListener = listener;
        purchaseManager.purchaseRestore();
    }

    private void loadRewardedAd() {
        rewardedAd = new RewardedAd(context, "ca-app-pub-3940256099942544/5224354917");
        rewardedAd.loadAd(new AdRequest.Builder().build(), new RewardedAdLoadCallback());
    }

    @Override
    public void showRewardAd(final RewardAdListener listener) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!rewardedAd.isLoaded())
                    return;
                rewardedAd.show(context, new RewardedAdCallback() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                listener.onAdReward();
                            }
                        });
                    }
                });
                loadRewardedAd();
            }
        });
    }
}
