package com.howinai.ireserveavailability;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;


public class MainActivity extends ActionBarActivity {

    TextView tv, bg;
    //ListView modelList;
    EditText interval;
    Button update, visit;
    ListView list;
    //int shopList[] = {R.id.list_shop1, R.id.list_shop2, R.id.list_shop3};
    HashMap<String, String> map;
    static String IntervalString = "com.howinai.ireserveavailability.interval";
    int tvIDs[] = {R.id.name_tv, R.id.cwb_tv, R.id.fes_tv, R.id.ifc_tv};
    String shops[] = {"R409", "R485", "R428"};
    String models[] = {"MGAF2ZP/A",
            "MG492ZP/A",
            "MGAC2ZP/A",
            "MGA92ZP/A",
            "MG4F2ZP/A",
            "MG472ZP/A",
            "MG4A2ZP/A",
            "MGAK2ZP/A",
            "MGAA2ZP/A",
            "MG4J2ZP/A",
            "MGAJ2ZP/A",
            "MG4H2ZP/A",
            "MGAE2ZP/A",
            "MG4E2ZP/A",
            "MG482ZP/A",
            "MGAH2ZP/A",
            "MG4C2ZP/A",
            "MGA82ZP/A"};

    String phones[] = {"iP6-Plus Gold 128GB",
            "iP6 Gold 16GB",
            "iP6-Plus Black 128GB",
            "iP6-Plus Silver 16GB",
            "iP6 Black 64GB",
            "iP6 Black 16GB",
            "iP6 Black 128GB",
            "iP6-Plus Gold 64GB",
            "iP6-Plus Gold 16GB",
            "iP6 Gold 64GB",
            "iP6-Plus Silver 64GB",
            "iP6 Silver 16GB",
            "iP6-Plus Silver 128GB",
            "iP6 Gold 128GB",
            "iP6 Silver 64GB",
            "iP6-Plus Black 64GB",
            "iP6 Silver 128GB",
            "iP6-Plus Black 16GB"
    };

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
    }

    @Override
    protected void onResume(){
        super.onResume();

        resetList();
    }

    @Override
    public void onConfigurationChanged(Configuration config){
        super.onConfigurationChanged(config);

        //Log.d("Configuration", "changed!!!");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    public void init(){
        map = new HashMap<String, String>();
        for(int i = 0; i < models.length; i++){
            map.put(phones[i], models[i]);
        }

        Arrays.sort(phones);
        

        //MyArrayAdapter adapter = new MyArrayAdapter(this, phones);
        //modelList.setAdapter(adapter);

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

    public void resetList(){
        (new GetAvailability()).execute();
    }

    public void setJson(JSONObject json){
        try {
            long time = json.getLong("updated");
            tv.setText(convertTime(time));
            String avails[][] = new String[models.length][shops.length + 1];
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
            //Log.d(tvs[0].getText().toString(), values[position][0]);
            if(MainActivity.this.getSharedPreferences("favourite", 0).getBoolean(values[position][0], false)){
                view.setBackgroundColor(Color.CYAN);
            }

            return view;
        }
    }

    public class GetAvailability extends AsyncTask<String, String , String>{
        JSONParser jsonParser;
        JSONObject json;
        String URL_iphone = "https://reserve.cdn-apple.com/HK/zh_HK/reserve/iPhone/availability.json";
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
