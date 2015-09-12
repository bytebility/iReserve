package com.howinai.ireserveavailability;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Array;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;


public class MainActivity extends ActionBarActivity {

    boolean isDebug = false;
    TextView tv, bg;
    EditText interval;
    Button update, visit;
    ListView list;
    HashMap<String, String> map;
    private static final Map<String, String> modelToSlugStaticMap;
    static String IntervalString = "com.howinai.ireserveavailability.interval";
    int tvIDs[] = {R.id.name_tv, R.id.cwb_tv, R.id.fes_tv, R.id.ifc_tv, R.id.canton_tv};
    String shops[] = {"R409", "R485", "R428", "R499"};
    String models[] = {"MKQM2ZP/A",
            "MKQL2ZP/A",
            "MKQK2ZP/A",
            "MKQJ2ZP/A",
            "MKQR2ZP/A",
            "MKQQ2ZP/A",
            "MKQP2ZP/A",
            "MKQN2ZP/A",
            "MKQW2ZP/A",
            "MKQV2ZP/A",
            "MKU52ZP/A",
            "MKU32ZP/A",
            "MKU22ZP/A",
            "MKU12ZP/A",
            "MKU92ZP/A",
            "MKU82ZP/A",
            "MKU72ZP/A",
            "MKU62ZP/A",
            "MKUG2ZP/A",
            "MKUF2ZP/A",
            "MKUE2ZP/A",
            "MKUD2ZP/A"};

    String phones[] = {"iPhone 6s 16GB 玫瑰金色",
            "iPhone 6s 16GB 金色",
            "iPhone 6s 16GB 銀色",
            "iPhone 6s 16GB 太空灰",
            "iPhone 6s 64GB 玫瑰金色",
            "iPhone 6s 64GB 金色",
            "iPhone 6s 64GB 銀色",
            "iPhone 6s 64GB 太空灰",
            "iPhone 6s 128GB 玫瑰金色",
            "iPhone 6s 128GB 金色",
            "iPhone 6s Plus 16GB 玫瑰金色",
            "iPhone 6s Plus 16GB 金色",
            "iPhone 6s Plus 16GB 銀色",
            "iPhone 6s Plus 16GB 太空灰",
            "iPhone 6s Plus 64GB 玫瑰金色",
            "iPhone 6s Plus 64GB 金色",
            "iPhone 6s Plus 64GB 銀色",
            "iPhone 6s Plus 64GB 大空灰",
            "iPhone 6s Plus 128GB 玫瑰金色",
            "iPhone 6s Plus 128GB 金色",
            "iPhone 6s Plus 128GB 銀色",
            "iPhone 6s Plus 128GB 大空灰"};

    static {
        Map<String, String> modelToSlugMap = new HashMap<>();
        modelToSlugMap.put("MKQM2ZP/A", "iPhone 6s 16GB 玫瑰金色");
        modelToSlugMap.put("MKQL2ZP/A", "iPhone 6s 16GB 金色");
        modelToSlugMap.put("MKQK2ZP/A", "iPhone 6s 16GB 銀色");
        modelToSlugMap.put("MKQJ2ZP/A", "iPhone 6s 16GB 太空灰");
        modelToSlugMap.put("MKQR2ZP/A", "iPhone 6s 64GB 玫瑰金色");
        modelToSlugMap.put("MKQQ2ZP/A", "iPhone 6s 64GB 金色");
        modelToSlugMap.put("MKQP2ZP/A", "iPhone 6s 64GB 銀色");
        modelToSlugMap.put("MKQN2ZP/A", "iPhone 6s 64GB 太空灰");
        modelToSlugMap.put("MKQW2ZP/A", "iPhone 6s 128GB 玫瑰金色");
        modelToSlugMap.put("MKQV2ZP/A", "iPhone 6s 128GB 金色");
        modelToSlugMap.put("MKU52ZP/A", "iPhone 6s Plus 16GB 玫瑰金色");
        modelToSlugMap.put("MKU32ZP/A", "iPhone 6s Plus 16GB 金色");
        modelToSlugMap.put("MKU22ZP/A", "iPhone 6s Plus 16GB 銀色");
        modelToSlugMap.put("MKU12ZP/A", "iPhone 6s Plus 16GB 太空灰");
        modelToSlugMap.put("MKU92ZP/A", "iPhone 6s Plus 64GB 玫瑰金色");
        modelToSlugMap.put("MKU82ZP/A", "iPhone 6s Plus 64GB 金色");
        modelToSlugMap.put("MKU72ZP/A", "iPhone 6s Plus 64GB 銀色");
        modelToSlugMap.put("MKU62ZP/A", "iPhone 6s Plus 64GB 大空灰");
        modelToSlugMap.put("MKUG2ZP/A", "iPhone 6s Plus 128GB 玫瑰金色");
        modelToSlugMap.put("MKUF2ZP/A", "iPhone 6s Plus 128GB 金色");
        modelToSlugMap.put("MKUE2ZP/A", "iPhone 6s Plus 128GB 銀色");
        modelToSlugMap.put("MKUD2ZP/A", "iPhone 6s Plus 128GB 大空灰");
        modelToSlugStaticMap = Collections.unmodifiableMap(modelToSlugMap);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        update = (Button) this.findViewById(R.id.update);
        visit = (Button) this.findViewById(R.id.visit);
        interval = (EditText) this.findViewById(R.id.interval);
        bg = (TextView) this.findViewById(R.id.tv_bg);
        tv = (TextView) this.findViewById(R.id.tv_main);
        init();
        startService(new Intent(MainActivity.this, MyService.class));

        // init jPush
        JPushInterface.setDebugMode(isDebug);
        JPushInterface.init(this);
    }

    @Override
    protected void onResume(){
        super.onResume();

        resetList();
    }

    @Override
    public void onConfigurationChanged(Configuration config){
        super.onConfigurationChanged(config);

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    public void init(){
        map = new HashMap<String, String>();
        for(int i = 0; i < models.length; i++) {
            map.put(phones[i], models[i]);
        }

        Arrays.sort(phones);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int inter;

                try{
                    inter = Integer.parseInt(interval.getText().toString());
                    Intent in = new Intent();
                    in.setAction(IntervalString);
                    if(inter < 10)
                        inter = 10;
                    else if(inter > 3600)
                        inter = 3600;
                    in.putExtra("interval", inter);

                    SharedPreferences.Editor editor = MainActivity.this.getSharedPreferences("iReserve_values", 0)
                            .edit();
                    editor.putInt("interval", inter);
                    editor.commit();

                    MainActivity.this.sendBroadcast(in);
                }catch(NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });

        visit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://reserve.cdn-apple.com/HK/zh_HK/reserve/iPhone/availability";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        this.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String str = "";
                str += intent.getStringExtra("bgtime");
                str += " (" + String.valueOf(intent.getIntExtra("count", 1)) + "次)";
                str += "\n(更新時間：" + String.valueOf(intent.getIntExtra("interval", -1)) + ")";

                bg.setText(str);
            }
        }, new IntentFilter(MyService.ACTION_BGTIME));
    }

    public void resetList() {
        (new GetAvailability()).execute();
    }

    public void setJson(JSONObject json) {
        try {
            long time = json.getLong("updated");
            tv.setText(convertTime(time));
            String avails[][] = new String[models.length][shops.length + 1];
            String availsWithStaticMap[][] = new String[models.length][shops.length + 1];
            for(int j = 0; j < shops.length; j++){
                JSONObject root = json.getJSONObject(shops[j]);

                for(int i = 0; i < models.length; i++){
                    String available = root.getString(map.get(phones[i]));
                    avails[i][j + 1] = available;
                }
            }

            for(int i = 0; i < models.length; i++){
                avails[i][0] = phones[i];
            }

            MyBaseAdapter adapter = new MyBaseAdapter(this, avails);
            list = (ListView) this.findViewById(R.id.list);
            list.setAdapter(adapter);

            list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    if (MainActivity.this.getSharedPreferences("favourite", 0).getBoolean(((String[])list.getItemAtPosition(position))[0], false)){
                        view.setBackgroundColor(Color.TRANSPARENT);
                        SharedPreferences.Editor editor = MainActivity.this.getSharedPreferences("favourite", 0).edit();
                        editor.putBoolean(((String[])list.getItemAtPosition(position))[0], false);
                        editor.commit();
                    }else{
                        view.setBackgroundColor(Color.CYAN);
                        SharedPreferences.Editor editor = MainActivity.this.getSharedPreferences("favourite", 0).edit();
                        editor.putBoolean(((String[])list.getItemAtPosition(position))[0], true);
                        editor.commit();
                    }


                    return false;
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String convertTime(long time){
        Date date = new Date(time);
        Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    public class MyBaseAdapter extends BaseAdapter{

        LayoutInflater myInflater;
        String[][] values;
        Context con;

        public MyBaseAdapter(Context con, String[][] results){
            myInflater = LayoutInflater.from(con);
            values = results;
            this.con = con;
        }

        @Override
        public int getCount() {
            return values.length;
        }

        @Override
        public Object getItem(int position) {
            return values[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView != null){
                //    return convertView;
            }
            View view = myInflater.inflate(R.layout.list_item, parent, false);

            TextView tvs[] = new TextView[values[0].length];
            for(int i = 0; i < tvs.length; i++){
                tvs[i] = (TextView) view.findViewById(tvIDs[i]);
                tvs[i].setText(values[position][i]);
                if(tvs[i].getText().toString().contains("true")){
                    tvs[i].setBackgroundColor(Color.YELLOW);
                    tvs[0].setTextAppearance(myInflater.getContext(), R.style.boldText);
                }

            }
            if(MainActivity.this.getSharedPreferences("favourite", 0).getBoolean(values[position][0], false)){
                view.setBackgroundColor(Color.CYAN);
            }

            return view;
        }
    }

    public class GetAvailability extends AsyncTask<String, String , String>{
        JSONParser jsonParser;
        JSONObject json;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            jsonParser = new JSONParser();
        }

        @Override
        protected String doInBackground(String... params) {
            json = jsonParser.makeHttpRequest(MyContract.URL_iphone, "GET", null);

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            setJson(json);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            resetList();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
