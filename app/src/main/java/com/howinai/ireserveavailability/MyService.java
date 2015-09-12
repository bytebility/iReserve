package com.howinai.ireserveavailability;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by PETEroid on 18/12/14.
 */
public class MyService extends Service {

    final int NOTIFICATION = R.string.app_name;
    NotificationManager notice;
    Handler handler;
    Runnable runnable;
    int interval;
    static int count;
    static String ACTION_BGTIME = "com.howinai.ireserveavailability.bgtime";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent in, int flags, int startId){
        //super.onStartCommand(in, flags, startId);
        interval = this.getSharedPreferences("iReserve_values", 0).getInt("interval", 60);
        count = 0;
        notice = (NotificationManager)this.getSystemService(NOTIFICATION_SERVICE);

        (new GetAvailability()).execute();
        handler = new Handler();
        runnable = new Runnable(){

            @Override
            public void run() {
                Calendar calendar = GregorianCalendar.getInstance();
                calendar.setTime(new Date());
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                if(hour >= 8 && hour <= 20){
                    (new GetAvailability()).execute();
                }else{
                    Log.d("Sleeping", "zZzzZZzzzZZZZzzzzZZZZ");
                }
                handler.postDelayed(runnable, interval*1000);
            }
        };
        handler.postDelayed(runnable, 0);

        this.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                interval = MyService.this.getSharedPreferences("iReserve_values", 0).getInt("interval", 60);
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, 0);
                showNoti("設定新間距", "依家間距係" + String.valueOf(interval) + "秒");
            }
        }, new IntentFilter(MainActivity.IntervalString));

        return Service.START_STICKY;
    }

    private void showNoti(String title, String content){
        //Notification notification = new Notification(R.drawable.ireserve, content, System.currentTimeMillis());
        //notification.setLatestEventInfo(this, title, content, PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0));
        NotificationCompat.Builder mbuilder = new NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.ireserve)
                .setVibrate(new long[]{0, 500, 600, 500})
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setLights(Color.GREEN, 200, 700)
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0));

        Notification notification = mbuilder.build();

        notice.notify(NOTIFICATION, notification);
    }

    public class GetAvailability extends AsyncTask<String, String , String> {
        JSONParser jsonParser;
        JSONObject json;
        String URL_iphone = "https://reserve.cdn-apple.com/HK/zh_HK/reserve/iPhone/availability.json";
        //String URL_iphone = "http://i.cs.hku.hk/~kfchow/iphone/iphone.php";
        //String URL_iphone = "http://i.cs.hku.hk/~kfchow/iphone/avail.json";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            jsonParser = new JSONParser();
        }

        @Override
        protected String doInBackground(String... params) {
            json = jsonParser.makeHttpRequest(URL_iphone, "GET", null);
            //Log.d("json", json.toString());

            return null;
        }

        public String convertTime(long time){
            Date date = new Date(time);
            Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return format.format(date);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                //boolean isAvail = json.getBoolean("availability");
                long time = json.getLong("updated");
                //String timestamp = convertTime(time);
                String timestamp = convertTime(System.currentTimeMillis());
                if(json.toString().contains("true")){
                    showNoti("有貨番!", timestamp);
                }
                Intent in = new Intent();
                in.putExtra("bgtime", timestamp);
                in.putExtra("count", ++count);
                in.putExtra("interval", interval);
                in.setAction(ACTION_BGTIME);
                sendBroadcast(in);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
