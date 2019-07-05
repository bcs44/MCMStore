package mestrado.ipg.mcmstore.Sensors;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.anychart.AnyChartView;

import java.util.Date;
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

public class ChartHumidity extends AppCompatActivity {
    private User user = User.getInstance();
    private List<Record> recordsHumidity = new ArrayList<>();
    private Date startDate;
    private Date endDate;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_temperature:
                    Intent intent = new Intent(ChartHumidity.this, ChartTemperature.class);
                    startActivity(intent);
                    finish();
                    return true;
                case R.id.nav_humidity:
                    return true;
                case R.id.nav_gas:
                    intent = new Intent(ChartHumidity.this, ChartGas.class);
                    startActivity(intent);
                    finish();
                    return true;
                case R.id.nav_luminosity:
                    intent = new Intent(ChartHumidity.this, ChartLuminosity.class);
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
        setContentView(R.layout.activity_chart_humidity);
        BottomNavigationView navView = findViewById(R.id.nav_view_humidity);
        navView.setSelectedItemId(R.id.nav_humidity);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        registerReceiver();
        getDayRecords();
        addToolBarAction();
    }

    private void addToolBarAction(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getDayRecords(){
        new sendGet().execute();
    }

    public void getDetails(View view) {
        Intent intent = new Intent(ChartHumidity.this, DetailsHumidity.class);
        intent.putExtra("startDate", Charts.convertDateToString(startDate));
        intent.putExtra("finalDate", Charts.convertDateToString(endDate));
        intent.putExtra("maximumValue", Charts.getMaximum(recordsHumidity));
        intent.putExtra("minimumValue", Charts.getMinimum(recordsHumidity));
        intent.putExtra("mediaValue", Charts.getMedia(recordsHumidity));
        intent.putExtra("actualValue", recordsHumidity.size() > 0 ? recordsHumidity.get(0).getValue() : 0.0);
        startActivity(intent);
    }


    private class sendGet extends AsyncTask<HashMap, HashMap, String> {

        HashMap<String, String> params = new HashMap<>();

        @Override
        protected String doInBackground(HashMap... args) {
            endDate = new Date(System.currentTimeMillis());
            SimpleDateFormat sdf;
            sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            String endSDate = sdf.format(endDate);
            startDate = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 6);
            String startSDate = sdf.format(startDate);
            startTemperatureChart(startSDate, endSDate);
            return "done";
        }
    }

    private void startTemperatureChart(String startDate, String endDate) {
        HashMap<String, String> params = new HashMap<>();
        String urlTemp = "https://bd.ipg.pt:5500/ords/bda_1701887/record/interval/type/" + TypeSensor.Humidity.getValue() + "/townhouse/" + user.getTownhouse_id();
        String _uriTemp = "/record/interval/type/" + TypeSensor.Humidity.getValue() + "/townhouse/" + user.getTownhouse_id();
        Intent intent = new Intent(ChartHumidity.this, BackgroundGetServiceAuth.class);
        intent.putExtra("urlStrg", urlTemp);
        intent.putExtra("_uri", _uriTemp);
        intent.putExtra("wherefrom", "Charts");
        intent.putExtra("sensorType", TypeSensor.Humidity.toString());
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
                    if (sensorType.equals(TypeSensor.Humidity.toString())) {
                        recordsHumidity = Charts.parseObject(data);
                        AnyChartView anyChartView = (AnyChartView) findViewById(R.id.any_chart_view_humidity);
                        if(recordsHumidity.size() == 0) {
                            Button button = findViewById(R.id.button_details_humidity);
                            button.setText("Sem dados");
                            button.setEnabled(false);
                        }
                        Charts.drawChart(recordsHumidity, anyChartView, 0);
                    }
                }
                context.stopService(new Intent(context, BackgroundGetServiceAuth.class));
                intent.getBundleExtra("Location");
            }
        };

        LocalBroadcastManager.getInstance(ChartHumidity.this).registerReceiver(
                mMessageReceiver, new IntentFilter("ServiceDayRecords"));
    }



}
