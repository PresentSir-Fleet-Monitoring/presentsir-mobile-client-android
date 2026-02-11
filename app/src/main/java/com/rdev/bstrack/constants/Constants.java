package com.rdev.bstrack.constants;

public class Constants {

    // URLs
    public static String APP_CONFIG_URL = "https://raw.githubusercontent.com/ranjit485/Terminal/master/";
    public static String SERVER_URL = "https://presentsir-server.onrender.com/";
    public static String WEBSOCKET_URL = "wss://presentsir-server.onrender.com/ps/ws";

    // OneSignal
    public static String ONESIGNAL_APP_ID = "f841a344-67bc-4525-966e-f5276babe410";

    // MapMyIndia
    public static String MAP_MY_INDIA_API_KEY = "3ec46d49ebb7fb8f074ed19cc8c82a46";
    public static String MAP_MY_INDIA_CLIENT_ID = "96dHZVzsAuuTBo7GfPQrzQvo9XooFPXXQt142wwak2QqpWblt76X0HUouokzm5ARAYJO_QFuUA6BmA8fwbYX8g==";
    public static String MAP_MY_INDIA_CLIENT_SECRET = "lrFxI-iSEg-bQ0D4XsKrrv-2ARAKnqm-jzq0FmeRnBnJkadU6QBiVzTjVSloEoNGBpCeXup396xYoewW5O4qKjd5uozDK_IO";

    // App UI Config
    public static int REMINDER_METER = 100;
    public static String buyMeCoffeeButtonVisible = "NO";
    public static String shareLocationButtonVisibleToEveryone = "NO";
    public static String shareLocationButtonVisibleToUser = "NO";
    public static String shareLocationButtonVisibleToDriver = "YES";
    public static String shareLocationButtonVisibleToAdmin = "NO";

    public static String changeBusButtonVisibleToEveryone = "YES";
    public static String changeBusButtonVisibleToUser = "YES";
    public static String changeBusButtonVisibleToDriver = "NO";
    public static String changeBusButtonVisibleToAdmin = "NO";

    public static String developerName = "Ranjit";

    // Getters and Setters
    public static String getBuyMeCoffeeButtonVisible() { return buyMeCoffeeButtonVisible; }
    public static void setBuyMeCoffeeButtonVisible(String value) { buyMeCoffeeButtonVisible = value; }

    public static String getShareLocationButtonVisibleToEveryone() { return shareLocationButtonVisibleToEveryone; }
    public static void setShareLocationButtonVisibleToEveryone(String value) { shareLocationButtonVisibleToEveryone = value; }

    public static String getShareLocationButtonVisibleToUser() { return shareLocationButtonVisibleToUser; }
    public static void setShareLocationButtonVisibleToUser(String value) { shareLocationButtonVisibleToUser = value; }

    public static String getShareLocationButtonVisibleToDriver() { return shareLocationButtonVisibleToDriver; }
    public static void setShareLocationButtonVisibleToDriver(String value) { shareLocationButtonVisibleToDriver = value; }

    public static String getShareLocationButtonVisibleToAdmin() { return shareLocationButtonVisibleToAdmin; }
    public static void setShareLocationButtonVisibleToAdmin(String value) { shareLocationButtonVisibleToAdmin = value; }

    public static String getChangeBusButtonVisibleToEveryone() { return changeBusButtonVisibleToEveryone; }
    public static void setChangeBusButtonVisibleToEveryone(String value) { changeBusButtonVisibleToEveryone = value; }

    public static String getChangeBusButtonVisibleToUser() { return changeBusButtonVisibleToUser; }
    public static void setChangeBusButtonVisibleToUser(String value) { changeBusButtonVisibleToUser = value; }

    public static String getChangeBusButtonVisibleToDriver() { return changeBusButtonVisibleToDriver; }
    public static void setChangeBusButtonVisibleToDriver(String value) { changeBusButtonVisibleToDriver = value; }

    public static String getChangeBusButtonVisibleToAdmin() { return changeBusButtonVisibleToAdmin; }
    public static void setChangeBusButtonVisibleToAdmin(String value) { changeBusButtonVisibleToAdmin = value; }

    public static String getDeveloperName() { return developerName; }
    public static void setDeveloperName(String value) { developerName = value; }

    public static String getAppConfigUrl() { return APP_CONFIG_URL; }
    public static void setAppConfigUrl(String value) { APP_CONFIG_URL = value; }

    public static String getServerUrl() { return SERVER_URL; }
    public static void setServerUrl(String value) { SERVER_URL = value; }

    public static String getWebsocketUrl() { return WEBSOCKET_URL; }
    public static void setWebsocketUrl(String value) { WEBSOCKET_URL = value; }

    public static String getOnesignalAppId() { return ONESIGNAL_APP_ID; }
    public static void setOnesignalAppId(String value) { ONESIGNAL_APP_ID = value; }

    public static String getMapMyIndiaApiKey() { return MAP_MY_INDIA_API_KEY; }
    public static void setMapMyIndiaApiKey(String value) { MAP_MY_INDIA_API_KEY = value; }

    public static String getMapMyIndiaClientId() { return MAP_MY_INDIA_CLIENT_ID; }
    public static void setMapMyIndiaClientId(String value) { MAP_MY_INDIA_CLIENT_ID = value; }

    public static String getMapMyIndiaClientSecret() { return MAP_MY_INDIA_CLIENT_SECRET; }
    public static void setMapMyIndiaClientSecret(String value) { MAP_MY_INDIA_CLIENT_SECRET = value; }

    public static int getReminderMeter() { return REMINDER_METER; }
    public static void setReminderMeter(int value) { REMINDER_METER = value; }
}
