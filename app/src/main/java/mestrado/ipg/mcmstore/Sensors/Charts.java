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
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

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
import mestrado.ipg.mcmstore.R;
import mestrado.ipg.mcmstore.Services.BackgroundGetServiceAuth;
import mestrado.ipg.mcmstore.domain.Record;

public class Charts extends AppCompatActivity {
    private TextView mTextMessage;
    private User user = User.getInstance();
    private List<Record> recordsTemperature = new ArrayList<>();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charts);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        mTextMessage = findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        registerReceiver();
        getDayRecords();
    }

    private void getDayRecords(){
        new sendGet().execute();
    }

    private void drawCHart(){
        Cartesian cartesian = AnyChart.line();

        List<DataEntry> data = new ArrayList<>();
        for(Record record : recordsTemperature) {
            data.add(new ValueDataEntry(record.getRecordDate() != null ?
                    record.getRecordDate().toString() : "", record.getValue()));
        }

        cartesian.data(data);

        AnyChartView anyChartView = (AnyChartView) findViewById(R.id.any_chart_view);
        anyChartView.setChart(cartesian);
    }

    private class sendGet extends AsyncTask<HashMap, HashMap, String> {

        HashMap<String, String> params = new HashMap<>();

        @Override
        protected String doInBackground(HashMap... args) {
            Date eDate = new Date(System.currentTimeMillis());
            SimpleDateFormat sdf;
            sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            String endDate = sdf.format(eDate);
            Date sDate = new Date(System.currentTimeMillis() - 1000 * 60 * 60);
            String startDate = sdf.format(sDate);
            HashMap<String, String> params = new HashMap<>();
            String urlTemp = "https://bd.ipg.pt:5500/ords/bda_1701887/record/interval/type/temperatura/townhouse/" + user.getTownhouse_id();
            String _uriTemp = "/record/interval/type/temperatura/townhouse/" + user.getTownhouse_id();
            Intent intent = new Intent(Charts.this, BackgroundGetServiceAuth.class);
            intent.putExtra("urlStrg", urlTemp);
            intent.putExtra("_uri", _uriTemp);
            intent.putExtra("wherefrom", "Charts");
            intent.putExtra("end", endDate);
            intent.putExtra("start", startDate);
            startService(intent);
            return "done";
        }
    }

    private void registerReceiver() {
        BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String data = intent.getStringExtra("data");
                recordsTemperature = parseObject(data);
                drawCHart();
                context.stopService(new Intent(context, BackgroundGetServiceAuth.class));
                intent.getBundleExtra("Location");
            }
        };

        LocalBroadcastManager.getInstance(Charts.this).registerReceiver(
                mMessageReceiver, new IntentFilter("ServiceDayRecords"));
    }

    private List<Record> parseObject(String data) {
        List<Record> records = new ArrayList<>();
        try {
            JSONObject json = new JSONObject(data);
            JSONArray jArray = (JSONArray) json.get("response");
            int counter;
            for(counter = 0; counter < jArray.length(); counter++) {
                json = (JSONObject) jArray.get(counter);
                records.add(parseRecord(json));
            }

        } catch (JSONException e) {
            Log.e(Charts.class.toString(), e.getMessage());
        }
        return records;
    }

    private Record parseRecord(JSONObject json) throws JSONException{
        Double value;
        try {
            value = (Double)json.get("value");
        } catch (ClassCastException ex){
            value = ((Integer) json.get("value")).doubleValue();
        }
        String measureUnit = (String) json.get("measure_unit");
        String dateText = (String) json.get("record_date");
        Date date = convertIsoStringToDate(dateText);
        Integer placeId = (Integer) json.get("place_id");
        Record record = new Record(value, measureUnit, date, placeId);
        return record;
    }

    private Date convertIsoStringToDate(String date){
        try {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            return format.parse(date);
        } catch(ParseException ex) {
            Log.e(Charts.class.toString(), ex.getMessage());
        }
        return null;
    }

}
