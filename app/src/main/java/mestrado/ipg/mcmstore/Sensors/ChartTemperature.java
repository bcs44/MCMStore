package mestrado.ipg.mcmstore.Sensors;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mestrado.ipg.mcmstore.Globals.User;
import mestrado.ipg.mcmstore.PrincipalActivity;
import mestrado.ipg.mcmstore.R;
import mestrado.ipg.mcmstore.Services.BackgroundGetServiceAuth;
import mestrado.ipg.mcmstore.domain.Record;
import mestrado.ipg.mcmstore.domain.TypeSensor;

public class ChartTemperature extends AppCompatActivity {
    private User user = User.getInstance();
    private List<Record> recordsTemperature = new ArrayList<>();
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent intent;
            switch (item.getItemId()) {
                case R.id.nav_temperature:
                    return true;
                case R.id.nav_humidity:
                    intent = new Intent(ChartTemperature.this, ChartHumidity.class);
                    startActivity(intent);
                    finish();
                    return true;
                case R.id.nav_gas:
                    intent = new Intent(ChartTemperature.this, ChartGas.class);
                    startActivity(intent);
                    finish();
                    return true;
                case R.id.nav_luminosity:
                    intent = new Intent(ChartTemperature.this, ChartLuminosity.class);
                    startActivity(intent);
                    finish();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charts_temperature);
        BottomNavigationView navView = findViewById(R.id.nav_view_temperature);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navView.setSelectedItemId(R.id.nav_temperature);
        registerReceiver();
        getDayRecords();
        addToolBarAction();
        initiateChartsService();
    }

    private void initiateChartsService(){

        Intent intent = new Intent(this, Charts.class);
        startService(intent);
    }

    private void addToolBarAction(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(ChartTemperature.this, PrincipalActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void getDayRecords(){
        new sendGet().execute();
    }



    private class sendGet extends AsyncTask<HashMap, HashMap, String> {

        HashMap<String, String> params = new HashMap<>();

        @Override
        protected String doInBackground(HashMap... args) {
            Date eDate = new Date(System.currentTimeMillis());
            SimpleDateFormat sdf;
            sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            String endDate = sdf.format(eDate);
            Date sDate = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 6);
            String startDate = sdf.format(sDate);
            startTemperatureChart(startDate, endDate);
            return "done";
        }
    }

    private void startTemperatureChart(String startDate, String endDate) {
        HashMap<String, String> params = new HashMap<>();
        String urlTemp = "https://bd.ipg.pt:5500/ords/bda_1701887/record/interval/type/" + TypeSensor.Temperature.getValue() + "/townhouse/" + user.getTownhouse_id();
        String _uriTemp = "/record/interval/type/" + TypeSensor.Temperature.getValue() + "/townhouse/" + user.getTownhouse_id();
        Intent intent = new Intent(ChartTemperature.this, BackgroundGetServiceAuth.class);
        intent.putExtra("urlStrg", urlTemp);
        intent.putExtra("_uri", _uriTemp);
        intent.putExtra("wherefrom", "Charts");
        intent.putExtra("sensorType", TypeSensor.Temperature.toString());
        intent.putExtra("end", endDate);
        intent.putExtra("start", startDate);
        startService(intent);
    }

    private void registerReceiver() {
        BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String data = intent.getStringExtra("data");
                String sensorType = intent.getStringExtra("sensorType");
                if (sensorType != null) {
                    if (sensorType.equals(TypeSensor.Temperature.toString())) {
                        recordsTemperature = Charts.parseObject(data);
                        AnyChartView anyChartView = (AnyChartView) findViewById(R.id.any_chart_view_temperature);
                        if(recordsTemperature.size() == 0) {
                            Button button = findViewById(R.id.button_details_temperature);
                            button.setText("Sem dados");
                            button.setEnabled(false);
                        }
                        Charts.drawChart(recordsTemperature, anyChartView, 0);
                    }
                }
                context.stopService(new Intent(context, BackgroundGetServiceAuth.class));
                intent.getBundleExtra("Location");
            }
        };

        LocalBroadcastManager.getInstance(ChartTemperature.this).registerReceiver(
                mMessageReceiver, new IntentFilter("ServiceDayRecords"));
    }

}
