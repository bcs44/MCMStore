package mestrado.ipg.mcmstore.Sensors;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import mestrado.ipg.mcmstore.Globals.Place;
import mestrado.ipg.mcmstore.PrincipalActivity;
import mestrado.ipg.mcmstore.R;
import mestrado.ipg.mcmstore.Services.BackgroundGetServiceAuth;
import mestrado.ipg.mcmstore.Helpers.SpinAdapter;
import mestrado.ipg.mcmstore.Services.BackgroundPostServiceAuth;

public class ConfigSensors extends AppCompatActivity {

    Button saveTemp, saveHum, saveCO2, saveLum;
    EditText minTemp, maxTemp, minHum, maxHum, minCo, maxCo, minLum, maxLum;
    String placeDescTemp, placeIdTemp, placeDescHum, placeIdHum, placeDescCo, placeIdCo, placeDescLum, placeIdLum;
    HashMap<String, String> paramsTOSEND = new HashMap<>();


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_sensors);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(ConfigSensors.this, PrincipalActivity.class);
                startActivity(intent);
            }
        });

        saveTemp = findViewById(R.id.saveTemp);
        saveHum = findViewById(R.id.saveHum);
        saveCO2 = findViewById(R.id.saveCO2);
        saveLum = findViewById(R.id.saveLum);
        minTemp = findViewById(R.id.MinValueSensorTemp);
        maxTemp = findViewById(R.id.MaxValueSensorTemp);
        minHum = findViewById(R.id.MinValueSensorHum);
        maxHum = findViewById(R.id.MaxValueSensorHum);
        minCo = findViewById(R.id.MinValueSensorCO2);
        maxCo = findViewById(R.id.MaxValueSensorCO2);
        minLum = findViewById(R.id.MinValueSensorLum);
        maxLum = findViewById(R.id.MaxValueSensorLum);

        registerReceiver();
        getPlaces();

        saveTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String TempID = "";
                for (Map.Entry<String, String> entry : paramsTOSEND.entrySet()) {
                    switch (entry.getKey()) {
                        case "Temperatura":
                            TempID = entry.getValue();
                            break;
                    }
                }
                HashMap<String, String> params = new HashMap<>();
                String urlTemp = "https://bd.ipg.pt:5500/ords/bda_1701887/confsensor/update/sensor/" + TempID;
                String _uriTemp = "/confsensor/update/sensor/" + TempID;
                params.put("urlStr", urlTemp);
                params.put("_uri", _uriTemp);
                params.put("id", TempID);
                params.put("min", String.valueOf(minTemp.getText()));
                params.put("max", String.valueOf(maxTemp.getText()));
                params.put("wherefrom", "PostConfigSensors");

                new sendPost().execute(params);
            }
        });

        saveHum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String HumID = "";
                for (Map.Entry<String, String> entry : paramsTOSEND.entrySet()) {
                    switch (entry.getKey()) {
                        case "Humidade":
                            HumID = entry.getValue();
                            break;
                    }
                }
                HashMap<String, String> params = new HashMap<>();
                String urlHum = "https://bd.ipg.pt:5500/ords/bda_1701887/confsensor/update/sensor/" + HumID;
                String _uriHum = "/confsensor/update/sensor/" + HumID;
                params.put("urlStr", urlHum);
                params.put("_uri", _uriHum);
                params.put("id", HumID);
                params.put("min", String.valueOf(minHum.getText()));
                params.put("max", String.valueOf(maxHum.getText()));
                params.put("wherefrom", "PostConfigSensors");

                new sendPost().execute(params);
            }
        });

        saveCO2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String CoID = "";
                for (Map.Entry<String, String> entry : paramsTOSEND.entrySet()) {
                    switch (entry.getKey()) {
                        case "CO2":
                            CoID = entry.getValue();
                            break;
                    }
                }
                HashMap<String, String> params = new HashMap<>();
                String urlCO = "https://bd.ipg.pt:5500/ords/bda_1701887/confsensor/update/sensor/" + CoID;
                String _uriCO = "/confsensor/update/sensor/" + CoID;
                params.put("urlStr", urlCO);
                params.put("_uri", _uriCO);
                params.put("id", CoID);
                params.put("min", String.valueOf(minCo.getText()));
                params.put("max", String.valueOf(maxCo.getText()));
                params.put("wherefrom", "PostConfigSensors");

                new sendPost().execute(params);
            }
        });

        saveLum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String LumID = "";
                for (Map.Entry<String, String> entry : paramsTOSEND.entrySet()) {
                    switch (entry.getKey()) {
                        case "Luminosidade":
                            LumID = entry.getValue();
                            break;
                    }
                }
                HashMap<String, String> params = new HashMap<>();
                String urlLum = "https://bd.ipg.pt:5500/ords/bda_1701887/confsensor/update/sensor/" + LumID;
                String _uriLum = "/confsensor/update/sensor/" + LumID;
                params.put("urlStr", urlLum);
                params.put("_uri", _uriLum);
                params.put("id", LumID);
                params.put("min", String.valueOf(minLum.getText()));
                params.put("max", String.valueOf(maxLum.getText()));
                params.put("wherefrom", "PostConfigSensors");

                new sendPost().execute(params);
            }
        });
    }

    private void getPlaces() {

        Intent intent = new Intent(ConfigSensors.this, BackgroundGetServiceAuth.class);
        intent.putExtra("urlStrg", "https://bd.ipg.pt:5500/ords/bda_1701887/place/all");
        intent.putExtra("_uri", "/place/all");
        intent.putExtra("wherefrom", "getPlacesToConfSens");
        startService(intent);
    }

    private void getSensorID(String sensorType, String placeId) {

        String url = "https://bd.ipg.pt:5500/ords/bda_1701887/sensor/place/" + placeId + "/type/" + sensorType;
        String _uri = "/sensor/place/" + placeId + "/type/" + sensorType;
        Intent intent = new Intent(ConfigSensors.this, BackgroundGetServiceAuth.class);
        intent.putExtra("urlStrg", url);
        intent.putExtra("_uri", _uri);
        intent.putExtra("wherefrom", "getSensorIDToConfSens");
        intent.putExtra("sensorType", sensorType);
        startService(intent);
    }

    private void registerReceiver() {

        BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String data = intent.getStringExtra("data");
                String wherefrom = intent.getStringExtra("wherefrom");

                switch (wherefrom) {
                    case "getPlacesToConfSens":
                        context.stopService(new Intent(context, BackgroundGetServiceAuth.class));
                        dealWithSpinners(data);
                        break;
                    case "getSensorIDToConfSens":
                        String sensorType = intent.getStringExtra("sensorType");
                        context.stopService(new Intent(context, BackgroundGetServiceAuth.class));
                        dealWithSensorID(data, sensorType);
                        break;
                    case "PostConfigSensors":
                        context.stopService(new Intent(context, BackgroundGetServiceAuth.class));
                        break;
                }
                intent.getBundleExtra("Location");
            }
        };

        LocalBroadcastManager.getInstance(ConfigSensors.this).registerReceiver(
                mMessageReceiver, new IntentFilter("ServiceConfigSensors"));
    }

    private void dealWithSensorID(String data, String sensorType) {

        JSONObject json;
        JSONArray array;

        try {
            json = new JSONObject(data);
            array = json.getJSONArray("response");

            for (int i = 0; i < array.length(); ++i) {
                json = array.getJSONObject(i);
                if (json != null) {
                    paramsTOSEND.put(sensorType, json.getString("sensor_id"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class sendPost extends AsyncTask<HashMap, HashMap, String> {

        @Override
        protected String doInBackground(HashMap... args) {

            HashMap<String, String> hashMap = args[0];
            Intent intent = new Intent(ConfigSensors.this, BackgroundPostServiceAuth.class);
            intent.putExtra("ParamsMAP", hashMap);
            startService(intent);
            return "done";
        }
    }

    public void dealWithSpinners(String data) {

        Spinner spinnerTemp, spinnerHum, spinnerCo, spinnerLum;
        spinnerTemp = findViewById(R.id.spinnerTemp);
        spinnerHum = findViewById(R.id.spinnerHum);
        spinnerCo = findViewById(R.id.spinnerCO2);
        spinnerLum = findViewById(R.id.spinnerLum);
        final SpinAdapter adapter;

        JSONObject json;
        JSONArray array;
        Place[] places = new Place[0];

        try {
            json = new JSONObject(data);
            array = json.getJSONArray("response");
            places = new Place[array.length()];

            for (int i = 0; i < array.length(); ++i) {
                json = array.getJSONObject(i);
                if (json != null) {
                    places[i] = new Place();
                    places[i].setId(json.getString("place_id"));
                    places[i].setDesc(json.getString("description"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter = new SpinAdapter(ConfigSensors.this,
                android.R.layout.simple_spinner_item,
                places);

        spinnerTemp.setAdapter(adapter);
        spinnerTemp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            int checkTemp = 0;

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                if (++checkTemp > 1) {
                    Place place = adapter.getItem(position);
                    if (place != null) {
                        placeDescTemp = place.getDesc();
                        placeIdTemp = place.getId();
                        Toast.makeText(ConfigSensors.this, "ID: " + place.getId() + "\nDesc: " + place.getDesc(),
                                Toast.LENGTH_SHORT).show();

                        getSensorID("Temperatura", placeIdTemp);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapter) {
            }
        });
        spinnerHum.setAdapter(adapter);
        spinnerHum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            int checkHum = 0;

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                if (++checkHum > 1) {
                    Place place = adapter.getItem(position);
                    if (place != null) {
                        placeDescHum = place.getDesc();
                        placeIdHum = place.getId();
                        Toast.makeText(ConfigSensors.this, "ID: " + place.getId() + "\nDesc: " + place.getDesc(),
                                Toast.LENGTH_SHORT).show();

                        getSensorID("Humidade", placeIdHum);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapter) {
            }
        });
        spinnerCo.setAdapter(adapter);
        spinnerCo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            int checkCo = 0;

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                if (++checkCo > 1) {
                    Place place = adapter.getItem(position);
                    if (place != null) {
                        placeDescCo = place.getDesc();
                        placeIdCo = place.getId();
                        Toast.makeText(ConfigSensors.this, "ID: " + place.getId() + "\nDesc: " + place.getDesc(),
                                Toast.LENGTH_SHORT).show();
                        getSensorID("CO2", placeIdCo);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapter) {
            }
        });
        spinnerLum.setAdapter(adapter);
        spinnerLum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            int checkLum = 0;

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                if (++checkLum > 1) {
                    Place place = adapter.getItem(position);
                    if (place != null) {
                        placeDescLum = place.getDesc();
                        placeIdLum = place.getId();
                        Toast.makeText(ConfigSensors.this, "ID: " + place.getId() + "\nDesc: " + place.getDesc(),
                                Toast.LENGTH_SHORT).show();
                        getSensorID("Luminosidade", placeIdLum);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapter) {
            }
        });
    }

}
