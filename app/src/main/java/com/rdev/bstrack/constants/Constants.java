package com.rdev.bstrack.constants;

public class Constants {
    public static String APP_CONFIG_URL ="https://raw.githubusercontent.com/ranjit485/Terminal/master/";

    public static String SERVER_URL="https://ca66-2409-4042-d06-485-7d7b-a41a-af9b-e4cd.ngrok-free.app/";
//    public static String DEVELOPER_NAME="Ranjit";
    public static String WEBSOCKET_URL="wss://ca66-2409-4042-d06-485-7d7b-a41a-af9b-e4cd.ngrok-free.app/ps/ws";
    public static String ONESIGNAL_APP_ID="f841a344-67bc-4525-966e-f5276babe410";

    public static int REMINDER_METER=100;
    public static String BUY_ME_COFFEE_BUTTON_VISIBLE="NO";

//    public static String SERVER_URL;

    public static String DEVELOPER_NAME ="Ranjit";
//    public static String WEBSOCKET_URL;
//    public static String ONESIGNAL_APP_ID;

//    public static String MAP_MY_INDIA_API_KEY;
//    public static String MAP_MY_INDIA_CLIENT_ID;
//    public static String MAP_MY_INDIA_CLIENT_SERCRET;

    public static String MAP_MY_INDIA_API_KEY="3ec46d49ebb7fb8f074ed19cc8c82a46";
    public static String MAP_MY_INDIA_CLIENT_ID="96dHZVzsAuuTBo7GfPQrzQvo9XooFPXXQt142wwak2QqpWblt76X0HUouokzm5ARAYJO_QFuUA6BmA8fwbYX8g==";
    public static String MAP_MY_INDIA_CLIENT_SERCRET="lrFxI-iSEg-bQ0D4XsKrrv-2ARAKnqm-jzq0FmeRnBnJkadU6QBiVzTjVSloEoNGBpCeXup396xYoewW5O4qKjd5uozDK_IO";

    public static String getBuyMeCoffeeButtonVisible() {
        return BUY_ME_COFFEE_BUTTON_VISIBLE;
    }

    public static void setBuyMeCoffeeButtonVisible(String buyMeCoffeeButtonVisible) {
        BUY_ME_COFFEE_BUTTON_VISIBLE = buyMeCoffeeButtonVisible;
    }

    public static String getDeveloperName() {
        return DEVELOPER_NAME;
    }

    public static void setDeveloperName(String developerName) {
        DEVELOPER_NAME = developerName;
    }

    public static String getAppConfigUrl() {
        return APP_CONFIG_URL;
    }

    public static void setAppConfigUrl(String appConfigUrl) {
        APP_CONFIG_URL = appConfigUrl;
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

    public static String getMapMyIndiaClientSercret() {
        return MAP_MY_INDIA_CLIENT_SERCRET;
    }

    public static void setMapMyIndiaClientSercret(String mapMyIndiaClientSercret) {
        MAP_MY_INDIA_CLIENT_SERCRET = mapMyIndiaClientSercret;
    }

    public static String getOnesignalAppId() {
        return ONESIGNAL_APP_ID;
    }

    public static void setOnesignalAppId(String onesignalAppId) {
        ONESIGNAL_APP_ID = onesignalAppId;
    }

    public static String getWebsocketUrl() {
        return WEBSOCKET_URL;
    }

    public static void setWebsocketUrl(String websocketUrl) {
        WEBSOCKET_URL = websocketUrl;
    }

    public static String getServerUrl() {
        return SERVER_URL;
    }

    public static void setServerUrl(String serverUrl) {
        SERVER_URL = serverUrl;
    }

    public static int getReminderMeter() {
        return REMINDER_METER;
    }

    public static void setReminderMeter(int reminderMeter) {
        REMINDER_METER = reminderMeter;
    }
}
