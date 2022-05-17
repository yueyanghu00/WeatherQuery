package com.example.myapplication;

import androidx.annotation.LongDef;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private List<String> cityList = new ArrayList<>();
    private List<Forecast> showList = new ArrayList<>();
    private InformationAdapter adapter = null;
    public static final int LOCATION_CODE = 301;
    private LocationManager locationManager;
    private String locationProvider = null;
    private String localCity = "北京市";
    private Forecast localWeather = null;
    TextView textViewLocalCity;
    TextView textViewLocalWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonInputWeight = (Button)findViewById(R.id.button_input_city);
        Button sendRequest = (Button) findViewById(R.id.button_refresh);
        textViewLocalCity = (TextView)findViewById(R.id.local_city);
        textViewLocalWeather = (TextView)findViewById(R.id.local_weather);
        buttonInputWeight.setOnClickListener(this);
        sendRequest.setOnClickListener( this);

        initCity();  // 初始化informationList列表
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);  //找到RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);  //设置线性布局
        recyclerView.setLayoutManager(layoutManager);
        adapter = new InformationAdapter(showList,cityList,this);  //构建适配器
        recyclerView.setAdapter(adapter);  //设置RecyclerView的适配器

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new MyItemTouchCallBack(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
//        getLocation();

    }


    @Override
    public void onClick(View view){
        Log.d("hyy", "onClick: ");
        int id = view.getId();
        switch (id){
            case R.id.button_input_city:
                showInputCity();
                break;
            case R.id.button_refresh:
                sendRequestWithOkHttp();
            default:
        }
    }

    private void sendRequestWithOkHttp(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    List<Forecast> weatherList = new ArrayList<>();
                    for(String city:cityList) {
                        Log.d("hyy", "run: ");
                        OkHttpClient client = new OkHttpClient();
                        String mUrl = "https://restapi.amap.com/v3/weather/" +
                                "weatherInfo?key=641013cda71a1884cad8b4d20d06d736&extensions=all&city="+city;
                        Request request = new Request.Builder()
                                .url(mUrl)
                                .build();
                        Response response = client.newCall(request).execute();
                        String responseData = response.body().string();

                        Gson gson = new Gson();
                        Weather weather = gson.fromJson(responseData, Weather.class);
                        for(Forecast cast:weather.getForecasts()){
                            weatherList.add(cast);
                        }
                    }
                    Log.d("hyy", "run: size"+String.valueOf(weatherList.size()));
                    showResponse(weatherList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void showResponse(final List<Forecast> response){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int jud=0;
                textViewLocalCity.setText(localCity);
                textViewLocalWeather.setText(response.get(0).getWeatherShow());
                showList.clear();
                for(Forecast weather:response) {
                    if(jud==0){jud=1;continue;}
                    showList.add(weather);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("hyy_destroy", "onDestroy: ");
        save_city();
    }


    public void save_city(){
        FileOutputStream out = null;
        BufferedWriter writer = null;
        try{
            out = openFileOutput("citys", Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            int jud=0;
            for(String city : cityList){
                if(jud==0){jud=1; continue;}
                Log.d("save", "save_city: "+city);
                writer.write(city+"\n");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }finally{
            try{
                if(writer!=null){
                    writer.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public List<String> load_city(){
        FileInputStream in = null;
        BufferedReader reader = null;
        List<String> content = new ArrayList<>();
        content.add(localCity);
        try{
            in = openFileInput("citys");
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while((line = reader.readLine())!=null){
                Log.d("hyy", "load_city: "+line);
                content.add(line);
            }
        } catch (IOException e){
            e.printStackTrace();
        }finally {
            if (reader!=null){
                try{
                    reader.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        return content;
    }



    private void showInputCity() {
        final EditText editText = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this).
                setTitle("请输入城市").setView(editText)
                .setPositiveButton("完成", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d("showInputCity", "onClick: "+editText.getText().toString());
                        cityList.add(editText.getText().toString());
                        sendRequestWithOkHttp();
                       // adapter.notifyDataSetChanged();
                    }
                });
        builder.create().show();
    }

    private void initCity(){
        localCity = getLocation();
        Log.d("localCity", "initCity: "+localCity);
        List<String> citys = load_city();
        for(String line:citys){
            cityList.add(line);
        }
        sendRequestWithOkHttp();
        Log.d("hyy", "city: good");
    }

    private String getLocation(){
        //生成位置管理器实例
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Log.d("TAG", "getLocation: ");
        //2.获取GPS
        List<String> providers = locationManager.getProviders(true);

        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            //如果是GPS
            locationProvider = LocationManager.GPS_PROVIDER;
            Log.v("TAG", "定位方式GPS");
        }else {
            Toast.makeText(this, "没有可用的GPS", Toast.LENGTH_SHORT).show();
            return null;
        }
        String ret="";
        //判断是否已经有权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            //没有则请求权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_CODE);
        } else {
            //获取位置
            Location location = locationManager.getLastKnownLocation(locationProvider);
            if (location!=null){
                ret = getAddress(location);
            }
        }
        return ret;
    }



    //获取地址信息:城市、街道等信息
    private String getAddress(Location location) {
        List<Address> result = null;
        String ret = "";
        try {
            if (location != null) {
                Geocoder gc = new Geocoder(this, Locale.getDefault());
                result = gc.getFromLocation(location.getLatitude(),
                        location.getLongitude(), 1);

                for(Address add:result){
                    Log.d("TAG", "getAddress: "+add.getLocality());
                    ret=add.getLocality();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

}