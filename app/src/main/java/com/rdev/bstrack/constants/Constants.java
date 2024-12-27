package com.rdev.bstrack.constants;

public class Constants {

    // URLs
    public static String APP_CONFIG_URL = "https://raw.githubusercontent.com/ranjit485/Terminal/master/";
    public static String SERVER_URL = "https://4712-106-210-237-147.ngrok-free.app/";
    public static String WEBSOCKET_URL = "wss://4712-106-210-237-147.ngrok-free.app/ps/ws";

    // OneSignal Configuration
    public static String ONESIGNAL_APP_ID = "f841a344-67bc-4525-966e-f5276babe410";

    // MapMyIndia Configuration
    public static String MAP_MY_INDIA_API_KEY = "3ec46d49ebb7fb8f074ed19cc8c82a46";
    public static String MAP_MY_INDIA_CLIENT_ID = "96dHZVzsAuuTBo7GfPQrzQvo9XooFPXXQt142wwak2QqpWblt76X0HUouokzm5ARAYJO_QFuUA6BmA8fwbYX8g==";
    public static String MAP_MY_INDIA_CLIENT_SECRET = "lrFxI-iSEg-bQ0D4XsKrrv-2ARAKnqm-jzq0FmeRnBnJkadU6QBiVzTjVSloEoNGBpCeXup396xYoewW5O4qKjd5uozDK_IO";

    // App Configuration
    public static int REMINDER_METER = 100;
    public static String buyMeCoffeeButtonVisible = "NO";
    public static String developerName = "Ranjit";

    // Getters and Setters
    public static String getBuyMeCoffeeButtonVisible() {
        return buyMeCoffeeButtonVisible;
    }

    public static void setBuyMeCoffeeButtonVisible(String buyMeCoffeeButtonVisible) {
        Constants.buyMeCoffeeButtonVisible = buyMeCoffeeButtonVisible;
    }

    public static String getDeveloperName() {
        return developerName;
    }

    public static void setDeveloperName(String developerName) {
        Constants.developerName = developerName;
    }

    public static String getAppConfigUrl() {
        return APP_CONFIG_URL;
    }

    public static void setAppConfigUrl(String appConfigUrl) {
        APP_CONFIG_URL = appConfigUrl;
    }

    public static String getServerUrl() {
        return SERVER_URL;
    }

    public static void setServerUrl(String serverUrl) {
        SERVER_URL = serverUrl;
    }

    public static String getWebsocketUrl() {
        return WEBSOCKET_URL;
    }

    public static void setWebsocketUrl(String websocketUrl) {
        WEBSOCKET_URL = websocketUrl;
    }

    public static String getOnesignalAppId() {
        return ONESIGNAL_APP_ID;
    }

    public static void setOnesignalAppId(String onesignalAppId) {
        ONESIGNAL_APP_ID = onesignalAppId;
    }

    public static String getMapMyIndiaApiKey() {
        return MAP_MY_INDIA_API_KEY;
    }

    public static void setMapMyIndiaApiKey(String mapMyIndiaApiKey) {
        MAP_MY_INDIA_API_KEY = mapMyIndiaApiKey;
    }

    public static String getMapMyIndiaClientId() {
        return MAP_MY_INDIA_CLIENT_ID;
    }

    public static void setMapMyIndiaClientId(String mapMyIndiaClientId) {
        MAP_MY_INDIA_CLIENT_ID = mapMyIndiaClientId;
    }

    public static String getMapMyIndiaClientSecret() {
        return MAP_MY_INDIA_CLIENT_SECRET;
    }

    public static void setMapMyIndiaClientSecret(String mapMyIndiaClientSecret) {
        MAP_MY_INDIA_CLIENT_SECRET = mapMyIndiaClientSecret;
    }

    public static int getReminderMeter() {
        return REMINDER_METER;
    }

    public static void setReminderMeter(int reminderMeter) {
        REMINDER_METER = reminderMeter;
    }
}
