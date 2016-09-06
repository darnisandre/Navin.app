package guilherme.krzisch.com.mybeaconclient;

import android.app.Application;
import android.content.Context;

import java.util.List;

import guilherme.krzisch.com.mybeaconclient.mybeaconframework.BasicModule.MyBeaconFacade;
import guilherme.krzisch.com.mybeaconclient.view.util.TTSManager;
import navin.dto.RouteDTO;
import navin.util.InternalCache;
import navin.util.RestClient;

public class MyApp extends Application {
    private static final String BAASBOX_URL = "put_here_server_url";
    private static final int PORT = 80;
    private static final String APP_CODE = "put_here_app_code";
    private static final String BEACON_RANGE_IDENTIFIER = "b9407f30f5f8466eaff925556b57fe6d";
    private static final String UUID = "b9407f30f5f8466eaff925556b57fe6d";
    private static final String restUrl = "http://ec2-52-67-89-114.sa-east-1.compute.amazonaws.com:8080/rest-webservice/rest/";

    // App context
    private static Context context;
    private static TTSManager ttsManager;
    private static InternalCache internalCache;
    private static List<RouteDTO> rLst;

    public static Context getAppContext() {
        return MyApp.context;
    }
    public static TTSManager getAppTTS() {
        return MyApp.ttsManager;
    }

    public static InternalCache getInternalCache(){ return internalCache;}
    public static List<RouteDTO> getRoutes(){ return rLst;}
    public static void setRoutes(List<RouteDTO> r){ rLst = r;}


    @Override
    public void onCreate() {
        ttsManager = new TTSManager();
        ttsManager.init(this);
        super.onCreate();
        MyApp.context = getApplicationContext();
        internalCache = new InternalCache(restUrl,context);
        MyBeaconFacade.initFramework(this, false, BAASBOX_URL, PORT, APP_CODE, BEACON_RANGE_IDENTIFIER, UUID);
    }
}
