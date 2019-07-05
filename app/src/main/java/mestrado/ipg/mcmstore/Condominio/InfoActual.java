package mestrado.ipg.mcmstore.Condominio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mestrado.ipg.mcmstore.Globals.User;
import mestrado.ipg.mcmstore.R;
import mestrado.ipg.mcmstore.Sensors.ChartTemperature;
import mestrado.ipg.mcmstore.Sensors.Charts;
import mestrado.ipg.mcmstore.Sensors.DetailsLuminosity;
import mestrado.ipg.mcmstore.Sensors.DetailsTemperature;
import mestrado.ipg.mcmstore.Services.BackgroundGetServiceAuth;
import mestrado.ipg.mcmstore.domain.Record;
import mestrado.ipg.mcmstore.domain.SensorState;
import mestrado.ipg.mcmstore.domain.TypeSensor;

public class InfoActual extends AppCompatActivity {

    User user = User.getInstance();
    List<SensorState> sensorStates = new ArrayList<>();
    private TextView textViewValueNumberSensors;
    private TextView textViewValueActiveSensors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_actual);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textViewValueActiveSensors = findViewById(R.id.textViewValueActiveSensors);
        textViewValueNumberSensors = findViewById(R.id.textViewValueSensorNumber);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        registerReceiver();
        getInfo();
    }

    public void getInfo(){
        new InfoActual.sendGet().execute();
    }


    private class sendGet extends AsyncTask<HashMap, HashMap, String> {

        HashMap<String, String> params = new HashMap<>();

        @Override
        protected String doInBackground(HashMap... args) {
            checkState();
            return "done";
        }
    }

    private void checkState() {
        HashMap<String, String> params = new HashMap<>();
        String urlTemp = "https://bd.ipg.pt:5500/ords/bda_1701887/activesensor/townhouse/" + user.getTownhouse_id();
        String _uriTemp = "/activesensor/townhouse/" + user.getTownhouse_id();
        Intent intent = new Intent(InfoActual.this, BackgroundGetServiceAuth.class);
        intent.putExtra("urlStrg", urlTemp);
        intent.putExtra("_uri", _uriTemp);
        intent.putExtra("wherefrom", "InfoAtual");
        startService(intent);
    }

    private void registerReceiver() {
        BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String data = intent.getStringExtra("data");
                sensorStates = parseObject(data);
                textViewValueNumberSensors.setText("" + sensorStates.size());
                int count = 0;
                for(SensorState st : sensorStates) {
                    if(st.getState() == 1) {
                        count++;
                    }
                }
                textViewValueActiveSensors.setText("" + count);
                Map<String, List<SensorState>> maspp = createMapsByPlace();
                for(Map.Entry<String, List<SensorState>> entry : maspp.entrySet()){
                    addTableRow(entry.getKey(), entry.getValue());
                }
                context.stopService(new Intent(context, BackgroundGetServiceAuth.class));
                intent.getBundleExtra("Location");
            }
        };

        LocalBroadcastManager.getInstance(InfoActual.this).registerReceiver(
                mMessageReceiver, new IntentFilter("ServiceGlobalInfo"));
    }


    public static List<SensorState> parseObject(String data) {
        List<SensorState> sensorStateArray = new ArrayList<>();
        try {
            JSONObject json = new JSONObject(data);
            JSONArray jArray = (JSONArray) json.get("response");
            int counter;
            for(counter = 0; counter < jArray.length(); counter++) {
                json = (JSONObject) jArray.get(counter);
                Integer activeSensorId = (Integer) json.get("active_sensor_id");
                Integer sensorId = (Integer) json.get("sensor_id");
                String identification = (String) json.get("identification");
                String description = (String) json.get("description");
                Double latitude = 0.0;
                Double longitude = 0.0;

                try {
                    latitude = (Double) json.get("latitude");
                }catch(ClassCastException ex){}

                try {
                    longitude = (Double) json.get("longitude");
                }catch(ClassCastException ex){ }

                Integer state  = (Integer) json.get("state");
                Integer placeId = (Integer) json.get("place_id");
                String place = (String) json.get("place");
                String type = (String) json.get("type");
                SensorState sensorState = new SensorState(activeSensorId, sensorId, identification,
                        description, latitude, longitude, state, placeId, place, type);
                sensorStateArray.add(sensorState);

            }
        } catch (JSONException e) {
            sensorStateArray = new ArrayList<>();
            Log.e(ChartTemperature.class.toString(), e.getMessage());
        }
        return sensorStateArray;
    }

    private Map<String, List<SensorState>> createMapsByPlace(){
        Map<String, List<SensorState>> masp = new HashMap<>();
        for (SensorState st : sensorStates) {
            if(masp.get(st.getPlace()) != null){
                masp.get(st.getPlace()).add(st);
                continue;
            }
            List<SensorState> lstStates = new ArrayList<>();
            masp.put(st.getPlace(), new ArrayList<>());
            masp.get(st.getPlace()).add(st);
        }
        return masp;
    }

    private SensorState hasSensorType(List<SensorState> sensorByPlace, TypeSensor type) {
        for(SensorState st : sensorByPlace) {
            if(st.getType().toLowerCase().equals(type.getValue().toLowerCase())){
                return st;
            }
        }
        return null;
    }


    private void addTableRow(String namePlace, List<SensorState> sensorByPlace){
        TableLayout tl = (TableLayout) findViewById(R.id.simpleTableLayout);
        TableRow tr = new TableRow(this);
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        TableRow.LayoutParams params = new TableRow.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT,1.0f);

        TextView textTitle = new TextView(this);
        textTitle.setText(namePlace);
        textTitle.setLayoutParams(params);
        textTitle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textTitle.setPadding(10,40,10,40);
        textTitle.setTextColor(Color.parseColor("#000000"));
        textTitle.setTextSize(12);
        tr.addView(textTitle);

        TextView text = new TextView(this);
        SensorState state = hasSensorType(sensorByPlace, TypeSensor.Temperature);
        if(state != null) {
            if(state.getState() == 1) {
                text.setText("V");
            } else {
                text.setText("O");
            }
        } else {
            text.setText("X");
        }
        configureTextView(text, params);
        tr.addView(text);

        text = new TextView(this);
        state = hasSensorType(sensorByPlace, TypeSensor.Humidity);
        if(state != null) {
            if(state.getState() == 1) {
                text.setText("V");
            } else {
                text.setText("O");
            }
        } else {
            text.setText("X");
        }
        configureTextView(text, params);
        tr.addView(text);

        text = new TextView(this);
        state = hasSensorType(sensorByPlace, TypeSensor.Luminosity);
        if(state != null) {
            if(state.getState() == 1) {
                text.setText("V");
            } else {
                text.setText("O");
            }
        } else {
            text.setText("X");
        }
        configureTextView(text, params);
        tr.addView(text);

        text = new TextView(this);
        state = hasSensorType(sensorByPlace, TypeSensor.Gas);
        if(state != null) {
            if(state.getState() == 1) {
                text.setText("V");
            } else {
                text.setText("O");
            }
        } else {
            text.setText("X");
        }
        configureTextView(text, params);
        tr.addView(text);

        tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
    }

    private void configureTextView(TextView text, TableRow.LayoutParams params){
        text.setLayoutParams(params);
        text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        text.setPadding(10,40,10,40);
        text.setTextColor(Color.parseColor("#000000"));
        text.setTextSize(12);
    }

//                <TextView
//
//    android:id="@+id/simpleTextTitleHumidity"
//    android:layout_width="wrap_content"
//    android:layout_height="wrap_content"
//    android:background="#FF0000"
//    android:paddingStart="10dp"
//    android:paddingLeft="10dp"
//    android:paddingTop="20dp"
//    android:paddingEnd="10dp"
//    android:paddingRight="10dp"
//    android:paddingBottom="20dp"
//    android:text="@string/Luminosity"
//    android:textAlignment="center"
//    android:textColor="#000"
//    android:textSize="12sp" />
//
//                <TextView
//
//    android:id="@+id/simpleTextTitleLuminosity"
//    android:layout_width="wrap_content"
//    android:layout_height="wrap_content"
//    android:background="#FF0000"
//    android:paddingStart="10dp"
//    android:paddingLeft="10dp"
//    android:paddingTop="20dp"
//    android:paddingEnd="10dp"
//    android:paddingRight="10dp"
//    android:paddingBottom="20dp"
//    android:text="Humidade"
//    android:textAlignment="center"
//    android:textColor="#000"
//    android:textSize="12sp" />
//
//                <TextView
//
//    android:id="@+id/simpleTextTitleGas"
//    android:layout_width="wrap_content"
//    android:layout_height="wrap_content"
//    android:background="#FF0000"
//    android:paddingStart="10dp"
//    android:paddingLeft="10dp"
//    android:paddingTop="20dp"
//    android:paddingEnd="10dp"
//    android:paddingRight="10dp"
//    android:paddingBottom="20dp"
//    android:text="@string/Gas"
//    android:textAlignment="center"
//    android:textColor="#000"
//    android:textSize="12sp" />
//
//            </TableRow>

}
