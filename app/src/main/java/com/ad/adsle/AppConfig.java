package com.ad.adsle;

public class AppConfig {

    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION_WITH_DATA = "pushNotificationData";
    public static final String PUSH_NOTIFICATION_WITHOUT_DATA = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;

    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String SHARED_PREF = "ah_firebase";

    public static final String SERVER_KEY = "AAAAWyyCQeQ:APA91bHAfzgISDkh_Y-2v5ptQdzoNJakDc-EIkSPjeXTb2FtuwbNksnOhVVYo4QBCuCNSl4O_K9vq0dKlf9q9HUB5QMx9x9vQ7PTgJyY4z1MRiGaigUB8La-oJ5j_V6ksShEuGGJ8rDa";

    public static final String FCM_URL = "https://fcm.googleapis.com/fcm/send";

    public static final String STRIPE_API_CHARGE_URL = "https://api.stripe.com/v1/charges";

    public static final String PAYSTACK_TRANSFER_URL = "https://api.paystack.co/transferrecipient";

    public static final String PAYSTACK_INITIATE_TRANSFER_URL = "https://api.paystack.co/transfer";

    public static final String PAYSTACK_FINALISE_TRANSFER_URL = "https://api.paystack.co/transfer/finalize_transfer";

    public static final String GET_LOCATION_FROM_SERVER = "https://maps.googleapis.com/maps/api/geocode/json?latlng=";

    public static final String GOOGLE_API_KEY = "AIzaSyCc5mJtCQBVVM4MqnTVhEV0Y9EMdafErKY";

    public static final String SMS_USERNAME = "adsleapp@gmail.com";

    public static final String SMS_PASSWORD = "Olaoluwa@5323";

    public static final String DATA_API_URL = "https://clients.primeairtime.com/api/";
}



//    String link_option = campaignInformation.getCampaign_link_option();
//        if (link_option.contentEquals("App Install")) {
//                Log.e("UpdateService", "DisplayAndSaveData ================ 2");
//                if (app_install_number < campaign_reach) {
//        if (campaignData.getAppInstallId(campaignInformation.getId())) {
//        updateCampaignData(false, campaignInformation.getId(), "views_number", 1);
//        } else {
//        boolean isAppInstalled = CheckForAppInstallsStatus();
//        if (isAppInstalled) {
//        campaignData.setAppInstallId(true, campaignInformation.getId());///check here
//        updateCampaignData(true, campaignInformation.getId(), "app_installs_number", 1);
//        updateCampaignData(false, campaignInformation.getId(), "views_number", 1);
//        } else {
//        updateCampaignData(false, campaignInformation.getId(), "views_number", 1);
//        }
//        }
//        updateWidgetRemoteView();
//        int newNext = next + 1;
//        DisplayNextCampaign(newNext);
//        } else {
//        updateWidgetRemoteView();
//        updateCampaignData(false, campaignInformation.getId(), "views_number", 1);
//        int newNext = next + 1;
//        DisplayNextCampaign(newNext);
//        }
//        } else if (link_option.contentEquals("Click")) {
//        Log.e("UpdateService", "DisplayAndSaveData ================ 3");
//        if (click_number < campaign_reach) {
//        if (campaignData.getClicked(campaignInformation.getId())) {
//        Log.e("UpdateService", "DisplayAndSaveData ================ 3.1");
//        updateCampaignData(false, campaignInformation.getId(), "views_number", 1);
//        } else {
//        Log.e("UpdateService", "DisplayAndSaveData ================ 3.2");
//        updateCampaignData(false, campaignInformation.getId(), "views_number", 1);
//        }
//        Log.e("UpdateService", "DisplayAndSaveData ================ 3.3");
//        updateWidgetRemoteView();
//        int newNext = next + 1;
//        DisplayNextCampaign(newNext);
//        } else {
//        updateWidgetRemoteView();
//        updateCampaignData(false, campaignInformation.getId(), "views_number", 1);
//        Log.e("UpdateService", "DisplayAndSaveData ================ 3.4");
//        int newNext = next + 1;
//        DisplayNextCampaign(newNext);
//        }
//        } else if (link_option.contentEquals("Reach")) {
//        Log.e("UpdateService", "DisplayAndSaveData ================ 4");
//        if (reach_number < campaign_reach) {
//        if (campaignData.getReached(campaignInformation.getId())) {
//        Log.e("UpdateService", "DisplayAndSaveData ================ 4.1");
//        updateCampaignData(false, campaignInformation.getId(), "views_number", 1);
//        } else {
//        Log.e("UpdateService", "DisplayAndSaveData ================ 4.2");
//        campaignData.setReached(true, campaignInformation.getId());
//        updateCampaignData(true, campaignInformation.getId(), "reach_number", 1);
//        updateCampaignData(false, campaignInformation.getId(), "views_number", 1);
//        }
//        updateWidgetRemoteView();
//        int newNext = next + 1;
//        DisplayNextCampaign(newNext);
//        } else {
//        updateWidgetRemoteView();
//        updateCampaignData(false, campaignInformation.getId(), "views_number", 1);
//        int newNext = next + 1;
//        DisplayNextCampaign(newNext);
//        }
//        }
