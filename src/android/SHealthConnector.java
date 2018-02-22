package com.wopo.plugin;

import com.samsung.android.sdk.healthdata.HealthConnectionErrorResult;
import com.samsung.android.sdk.healthdata.HealthConstants;
import com.samsung.android.sdk.healthdata.HealthDataService;
import com.samsung.android.sdk.healthdata.HealthDataStore;
import com.samsung.android.sdk.healthdata.HealthPermissionManager;
import com.samsung.android.sdk.healthdata.HealthPermissionManager.PermissionKey;
import com.samsung.android.sdk.healthdata.HealthPermissionManager.PermissionResult;
import com.samsung.android.sdk.healthdata.HealthPermissionManager.PermissionType;
import com.samsung.android.sdk.healthdata.HealthResultHolder;

import com.samsung.android.simplehealth.DataReporter;

import android.util.Log;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.app.Activity;

public class SHealthConnector {

    private HealthDataStore mStore;
    private Set<PermissionKey> mKeySet;
    private DataReporter mReporter;

    String APP_TAG = "CordovaSHealthPlugin";

    Activity activity;
    CallbackContext callbackContext;
    CallbackContext observerCallbackContext;

    /** Default Constructor.
     *
     * @param pActivity         Activity of the cordova application
     * @param pCallbackContext  Object holding callback functions
     */
    public SHealthConnector(Activity pActivity, CallbackContext pCallbackContext){
        this.activity = pActivity;
        this.callbackContext = pCallbackContext;

        mKeySet = new HashSet<PermissionKey>();
        mKeySet.add(new PermissionKey(HealthConstants.StepCount.HEALTH_DATA_TYPE, PermissionType.READ));
        // mKeySet.add(new PermissionKey(HealthConstants.Exercise.HEALTH_DATA_TYPE, PermissionType.READ));
        mKeySet.add(new PermissionKey(HealthConstants.Sleep.HEALTH_DATA_TYPE, PermissionType.READ));
        // mKeySet.add(new PermissionKey("com.samsung.shealth.step_daily_trend", PermissionType.READ));
        // mKeySet.add(new PermissionKey(HealthConstants.SleepStage.HEALTH_DATA_TYPE, PermissionType.READ));
        // mKeySet.add(new PermissionKey(HealthConstants.FoodIntake.HEALTH_DATA_TYPE, PermissionType.READ));
        // mKeySet.add(new PermissionKey(HealthConstants.WaterIntake.HEALTH_DATA_TYPE, PermissionType.READ));
        // mKeySet.add(new PermissionKey(HealthConstants.CaffeineIntake.HEALTH_DATA_TYPE, PermissionType.READ));
        // mKeySet.add(new PermissionKey(HealthConstants.HeartRate.HEALTH_DATA_TYPE, PermissionType.READ));
        // mKeySet.add(new PermissionKey(HealthConstants.BodyTemperature.HEALTH_DATA_TYPE, PermissionType.READ));
        // mKeySet.add(new PermissionKey(HealthConstants.BloodPressure.HEALTH_DATA_TYPE, PermissionType.READ));
        // mKeySet.add(new PermissionKey(HealthConstants.BloodGlucose.HEALTH_DATA_TYPE, PermissionType.READ));
        // mKeySet.add(new PermissionKey(HealthConstants.OxygenSaturation.HEALTH_DATA_TYPE, PermissionType.READ));
        // mKeySet.add(new PermissionKey(HealthConstants.HbA1c.HEALTH_DATA_TYPE, PermissionType.READ));
        // mKeySet.add(new PermissionKey(HealthConstants.AmbientTemperature.HEALTH_DATA_TYPE, PermissionType.READ));
        // mKeySet.add(new PermissionKey(HealthConstants.UvExposure.HEALTH_DATA_TYPE, PermissionType.READ));
    }

    /** Set callback context
     */
     public void setCallbackContext(CallbackContext pCallbackContext) {
         this.callbackContext = pCallbackContext;
         if(mReporter != null){
             mReporter.setCallbackContext(pCallbackContext);
         }
     }

     /** Set callback context for observer
     */
     public void setObserverCallbackContext(CallbackContext pCallbackContext) {
         this.observerCallbackContext = pCallbackContext;
         if(mReporter != null){
             mReporter.setObserverCallbackContext(pCallbackContext);
         }
     }

    /** Connects the plugin to S Health
     *
     */
    public void connect() {

        HealthDataService healthDataService = new HealthDataService();
        try {
            healthDataService.initialize(activity.getApplicationContext());
        } catch (Exception e) {
            Log.e(APP_TAG, "healthDataService.initialize - " + e.toString());
            e.printStackTrace();

            PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "{\"TYPE\":\"ERROR\",\"MESSAGE\":\"Could not successfully connect with SHealth\"}");
            //pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
        }

        // Create a HealthDataStore instance and set its listener
        mStore = new HealthDataStore(activity.getApplicationContext(), mConnectionListener);
        // Request the connection to the health data store
        mStore.connectService();
    }

    /** Callback for callHealthPermissionManager
     *
     */
    private final HealthResultHolder.ResultListener<PermissionResult> mRequestPermissionCallback = new HealthResultHolder.ResultListener<PermissionResult>() {
        @Override
        public void onResult(PermissionResult result) {
            Log.d(APP_TAG, "Permission callback is received.");
            Map<PermissionKey, Boolean> resultMap = result.getResultMap();

            if (resultMap.containsValue(Boolean.FALSE)) {
                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, "{\"TYPE\":\"ERROR\",\"MESSAGE\":\"Permission has not been acquired.\"}");
                //pluginResult.setKeepCallback(true);
                callbackContext.sendPluginResult(pluginResult);
            } else {
                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, "{\"TYPE\":\"SUCCESS\",\"MESSAGE\":\"Permission has been acquired.\"}");
                //pluginResult.setKeepCallback(true);
                callbackContext.sendPluginResult(pluginResult);
            }
        }
    };

    /** Opens the permission manager for S Health
     *
     */
    public void callHealthPermissionManager() {
        HealthPermissionManager pmsManager = new HealthPermissionManager(mStore);
        try {
            // Show user permission UI for allowing user to change options
            pmsManager.requestPermissions(mKeySet, activity).setResultListener(mRequestPermissionCallback);
        } catch (Exception e) {
            Log.e(APP_TAG, e.getClass().getName() + " - " + e.getMessage());
            Log.e(APP_TAG, "Permission setting fails.");

            PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "{\"TYPE\":\"ERROR\",\"MESSAGE\":\"Not successfully connected with SHealth\"}");
            //pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
        }
    }

    public void checkPermissionHasBeenAcquired() {
        HealthPermissionManager pmsManager = new HealthPermissionManager(mStore);
        try {
            // Check whether the permissions that this application needs are acquired
            Map<PermissionKey, Boolean> resultMap = pmsManager.isPermissionAcquired(mKeySet);

            if (resultMap.containsValue(Boolean.FALSE)) {
                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, "{\"TYPE\":\"ERROR\",\"MESSAGE\":\"Permission has not been acquired.\"}");
                //pluginResult.setKeepCallback(true);
                callbackContext.sendPluginResult(pluginResult);
            } else {
                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, "{\"TYPE\":\"SUCCESS\",\"MESSAGE\":\"Permission has been acquired.\"}");
                //pluginResult.setKeepCallback(true);
                callbackContext.sendPluginResult(pluginResult);
            }
        } catch (Exception e) {
            Log.e(APP_TAG, e.getClass().getName() + " - " + e.getMessage());
            Log.e(APP_TAG, "Permission check fails.");

            PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "{\"TYPE\":\"ERROR\",\"MESSAGE\":\"Not successfully connected with SHealth\"}");
            //pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
        }
    }

    /** Starts the database query for S Health
     *
     * @param startTime     Earliest time of measurement
     * @param endTime      Latest time of measurement
     */
    public void startReporter(String hcHDT, long startTime, long endTime) {
        if(mReporter != null){
            mReporter.start(hcHDT, startTime,endTime);
        } else {
            Log.e(APP_TAG, "mReporter == null");

            PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "{\"TYPE\":\"ERROR\",\"MESSAGE\":\"Not successfully connected with SHealth\"}");
            //pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
        }
    }

    /** Start observer
     */
    public void startObserver(String healthDataType) {
        if(mReporter != null){
            mReporter.startObserver(healthDataType);
        } else {
            Log.e(APP_TAG, "mReporter == null");

            PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "{\"TYPE\":\"ERROR\",\"MESSAGE\":\"Not successfully connected with SHealth\"}");
            //pluginResult.setKeepCallback(true);
            if (observerCallbackContext != null) {
                observerCallbackContext.sendPluginResult(pluginResult);
            }
        }
    }

    /** Stop the observer
     */
    public void stopObserver() {
        if(mReporter != null){
            mReporter.removeObserver();
        } else {
            Log.e(APP_TAG, "mReporter == null");
        }

        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, "{\"TYPE\":\"SUCCESS\",\"MESSAGE\":\"Observer has been stopped.\"}");
        //pluginResult.setKeepCallback(true);
        callbackContext.sendPluginResult(pluginResult);
    }

    /** Disconnect health store service
     */
    public void disconnectService() {
        if (mStore != null) {
            mStore.disconnectService();
        }
        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, "{\"TYPE\":\"SUCCESS\",\"MESSAGE\":\"Health Store service has been disconnected.\"}");
        //pluginResult.setKeepCallback(true);
        callbackContext.sendPluginResult(pluginResult);
    }

    /** Callback object for {@link HealthDataStore}
     *
     */
    private final HealthDataStore.ConnectionListener mConnectionListener = new HealthDataStore.ConnectionListener() {

        @Override
        public void onConnected() {
            Log.d(APP_TAG, "Health data service is connected.");
            HealthPermissionManager pmsManager = new HealthPermissionManager(mStore);
            mReporter = new DataReporter(mStore, activity, callbackContext);

            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, "{\"TYPE\":\"SUCCESS\",\"MESSAGE\":\"Health data service is connected.\"}");
            //pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
        }

        @Override
        public void onConnectionFailed(HealthConnectionErrorResult error) {
            Log.d(APP_TAG, "Health data service is not available.");
            PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "{\"TYPE\":\"ERROR\",\"MESSAGE\":\"Health data service is not available.\"}");
            //pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
        }

        @Override
        public void onDisconnected() {
            Log.d(APP_TAG, "Health data service is disconnected.");
        }
    };

    /**  Returns the name of the class
     *
     * @return Name of class
     */
    @Override
    public String toString() {
        return "SHealthConnector";
    }
}