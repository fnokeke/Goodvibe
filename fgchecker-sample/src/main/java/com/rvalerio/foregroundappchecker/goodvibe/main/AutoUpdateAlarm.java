package com.rvalerio.foregroundappchecker.goodvibe.main;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import com.rvalerio.foregroundappchecker.goodvibe.fcm.AppJobService;
import com.rvalerio.foregroundappchecker.goodvibe.helper.AlarmHelper;
import com.rvalerio.foregroundappchecker.goodvibe.helper.DateHelper;

import java.util.Calendar;

public class AutoUpdateAlarm extends BroadcastReceiver {
    private static AutoUpdateAlarm mInstance;

    public static AutoUpdateAlarm getInstance() {
        if (mInstance == null) {
            mInstance = new AutoUpdateAlarm();
        }
        return mInstance;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
//        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
//        wl.acquire();
//        wl.release();
        performUpdateAndSyncToServer(context);
    }


    public void setAlarmForPeriodicUpdate(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AutoUpdateAlarm.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), getVariableHourInterval(12, 24), pi);
    }

    private int getVariableHourInterval(int lowerHour, int upperHour) {
        int hourInMillis = 60 * 60 * 1000;
        return DateHelper.getRandomInt(lowerHour * hourInMillis, upperHour * hourInMillis);
    }

    public static void performUpdateAndSyncToServer(Context context) {
        ForegroundToastService.startMonitoring(context);
        AppJobService.updateServerThroughFirebaseJob(context);
    }

    public void cancelAlarm(Context context) {
        Intent intent = new Intent(context, AutoUpdateAlarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    public static long getMillisUntilTriggerTime(int hourOf24HourDay) {
        Calendar triggerAt = Calendar.getInstance();
        triggerAt.setTimeInMillis(System.currentTimeMillis());
        triggerAt.set(Calendar.HOUR_OF_DAY, hourOf24HourDay);
        triggerAt.set(Calendar.MINUTE, 0);
        triggerAt.set(Calendar.SECOND, 0);
        Calendar now = Calendar.getInstance();
        if (triggerAt.before(now)) triggerAt.add(Calendar.DAY_OF_MONTH, 1);
        return triggerAt.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
    }

}