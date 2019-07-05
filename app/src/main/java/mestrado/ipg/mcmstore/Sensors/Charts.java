package mestrado.ipg.mcmstore.Sensors;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.charts.Cartesian3d;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mestrado.ipg.mcmstore.domain.Record;

public class Charts extends Service implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;

    private static List<Record> RECORDS =  new ArrayList<>();
    private static AnyChartView ANYCHARTVIEW;
    private static int TYPE = 0;
    private final int CARTESIAN = 0;
    private final int LINES = 1;
    private static Cartesian cartesian;
    private static Cartesian3d cartesian3d;

    public static void drawChart(List<Record> records,
                                 AnyChartView anyChartView,
                                 int type){
        RECORDS = records;
        ANYCHARTVIEW = anyChartView;
        TYPE = type;
        List<DataEntry> data = new ArrayList<>();
        int counter;
        if(records.size() > 0) {
            for (counter = records.size() - 1; counter >= 0; counter--) {
                Record record = records.get(counter);
                data.add(new ValueDataEntry(record.getRecordDate() != null ?
                        convertToTime(record.getRecordDate()) : "", record.getValue()));
            }
        } else {
            for (counter = 0; counter < 2; counter++) {
                data.add(new ValueDataEntry("Sem dados", 0));
            }
        }
        anyChartView.invalidate();
        anyChartView.getRootView().invalidate();
        anyChartView.setHorizontalScrollBarEnabled(true);
        anyChartView.setVerticalScrollBarEnabled(true);
        anyChartView.setZoomEnabled(true);

        switch(type) {
            case 1:
                cartesian = AnyChart.line();
                if(data.size() > 2) {
                    cartesian.xAxis(0).labels().width(60);
                }
                cartesian.data(data);
                anyChartView.setChart(cartesian);
                break;
            default:
                cartesian3d = AnyChart.area3d();
                cartesian3d.animation().duration(3000);
                cartesian3d.zAngle(45);
                if(data.size() > 2) {
                    cartesian3d.xAxis(0).labels().width(60);
                }
                cartesian3d.data(data);
                anyChartView.setChart(cartesian3d);
                break;

        }

    }

    public static List<Record> parseObject(String data) {
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
            Log.e(ChartTemperature.class.toString(), e.getMessage());
        }
        return records;
    }

    private static Record parseRecord(JSONObject json) throws JSONException{
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

    private static Date convertIsoStringToDate(String date){
        try {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            return format.parse(date);
        } catch(ParseException ex) {
            Log.e(ChartTemperature.class.toString(), ex.getMessage());
        }
        return null;
    }

    private static String convertToTime(Date date){
        try {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            format = new SimpleDateFormat("HH:mm:ss");
            return format.format(date);
        } catch(Exception ex) {
            Log.e(ChartTemperature.class.toString(), ex.getMessage());
        }
        return null;
    }

    public static String convertDateToString(Date date){
        try {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            format = new SimpleDateFormat("dd/MM/yyyy");
            return format.format(date);
        } catch(Exception ex) {
            Log.e(ChartTemperature.class.toString(), ex.getMessage());
        }
        return null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer,
                SensorManager.SENSOR_DELAY_UI, new Handler());
        return START_STICKY;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        mAccelLast = mAccelCurrent;
        mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
        float delta = mAccelCurrent - mAccelLast;
        mAccel = mAccel * 0.9f + delta;
        if (mAccel > 25) {
            if(TYPE == CARTESIAN) {
                drawChart(RECORDS, ANYCHARTVIEW, LINES);
            } else {
                drawChart(RECORDS, ANYCHARTVIEW, CARTESIAN);
            }
        }
    }

    public static Double getMaximum(List<Record> records){
        Double auxiliar = 0.0;
        for(Record record : records) {
            if(record.getValue() >= auxiliar) {
                auxiliar = record.getValue();
            }
        }
        return auxiliar;
    }

    public static Double getMedia(List<Record> records){
        Double auxiliar = 0.0;
        for(Record record : records) {
            auxiliar += record.getValue();
        }
        if(auxiliar == 0.0) {
            return auxiliar;
        }
        return auxiliar / records.size();
    }

    public static Double getMinimum(List<Record> records){
        Double auxiliar = 1000000.0;
        for(Record record : records) {
            if(record.getValue() <= auxiliar) {
                auxiliar = record.getValue();
            }
        }
        if (auxiliar == 1000000.0) {
            return 0.0;
        }
        return auxiliar;
    }
}
