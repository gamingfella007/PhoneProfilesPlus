package sk.henrichg.phoneprofilesplus;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;

public class DeviceIdleModeBroadcastReceiver extends BroadcastReceiver {

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
//        PPApplication.logE("[BROADCAST CALL] DeviceIdleModeBroadcastReceiver.onReceive","xxx");

        //CallsCounter.logCounter(context, "DeviceIdleModeBroadcastReceiver.onReceive", "DeviceIdleModeBroadcastReceiver_onReceive");

        final Context appContext = context.getApplicationContext();

        if (!PPApplication.getApplicationStarted(true))
            // application is not started
            return;

        if (Event.getGlobalEventsRunning()) {
            PowerManager powerManager = (PowerManager) appContext.getSystemService(Context.POWER_SERVICE);
            // isLightDeviceIdleMode() is @hide :-(
            if ((powerManager != null) && !powerManager.isDeviceIdleMode() /*&& !powerManager.isLightDeviceIdleMode()*/) {
                //PPApplication.logE("DeviceIdleModeBroadcastReceiver.onReceive","NOT in idle mode");
                PPApplication.startHandlerThread(/*"DeviceIdleModeBroadcastReceiver.onReceive"*/);
                final Handler handler = new Handler(PPApplication.handlerThread.getLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        PowerManager powerManager = (PowerManager) appContext.getSystemService(Context.POWER_SERVICE);
                        PowerManager.WakeLock wakeLock = null;
                        try {
                            if (powerManager != null) {
                                wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, PPApplication.PACKAGE_NAME + ":DeviceIdleModeBroadcastReceiver_onReceive");
                                wakeLock.acquire(10 * 60 * 1000);
                            }

//                            PPApplication.logE("[HANDLER CALL] PPApplication.startHandlerThread", "START run - from=DeviceIdleModeBroadcastReceiver.onReceive");

                            // start events handler
//                            PPApplication.logE("[EVENTS_HANDLER] DeviceIdleModeBroadcastReceiver.onReceive", "sensorType=SENSOR_TYPE_DEVICE_IDLE_MODE");
                            EventsHandler eventsHandler = new EventsHandler(appContext);
                            eventsHandler.handleEvents(EventsHandler.SENSOR_TYPE_DEVICE_IDLE_MODE);

                            //PPApplication.logE("****** EventsHandler.handleEvents", "END run - from=DeviceIdleModeBroadcastReceiver.onReceive");

                            // rescan
                            if (PhoneProfilesService.getInstance() != null) {
                                //PPApplication.logE("DeviceIdleModeBroadcastReceiver.onReceive", "rescan/reschedule workers");
                                boolean restart = false;
                                if (ApplicationPreferences.applicationEventLocationEnableScanning)
                                    restart = true;
                                else if (ApplicationPreferences.applicationEventWifiEnableScanning)
                                    restart = true;
                                else if (ApplicationPreferences.applicationEventBluetoothEnableScanning)
                                    restart = true;
                                else if (ApplicationPreferences.applicationEventMobileCellEnableScanning)
                                    restart = true;
                                else if (ApplicationPreferences.applicationEventOrientationEnableScanning)
                                    restart = true;
                                else if (ApplicationPreferences.applicationEventBackgroundScanningEnableScanning)
                                    restart = true;
                                if (restart) {
                                    PPApplication.restartAllScanners(appContext, false);
                                }
                            }
                            /*if (DatabaseHandler.getInstance(appContext).getTypeEventsCount(DatabaseHandler.ETYPE_MOBILE_CELLS, false) > 0) {
                                // rescan mobile cells
                                synchronized (PPApplication.phoneStateScannerMutex) {
                                    if ((PhoneProfilesService.getInstance() != null) && PhoneProfilesService.getInstance().isPhoneStateScannerStarted()) {
                                        PhoneProfilesService.getInstance().getPhoneStateScanner().rescanMobileCells();
                                    }
                                }
                            }*/

                            //PPApplication.logE("PPApplication.startHandlerThread", "END run - from=DeviceIdleModeBroadcastReceiver.onReceive");
                        } finally {
                            if ((wakeLock != null) && wakeLock.isHeld()) {
                                try {
                                    wakeLock.release();
                                } catch (Exception ignored) {}
                            }
                        }
                    }
                });
            }
            //else
            //    PPApplication.logE("DeviceIdleModeBroadcastReceiver.onReceive","in idle mode");
        }
    }
}
