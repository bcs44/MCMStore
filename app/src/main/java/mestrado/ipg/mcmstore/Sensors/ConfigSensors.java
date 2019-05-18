package mestrado.ipg.mcmstore.Sensors;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import mestrado.ipg.mcmstore.R;
import mestrado.ipg.mcmstore.Services.BackgroundGetService;
import mestrado.ipg.mcmstore.Services.BackgroundPostService;
import mestrado.ipg.mcmstore.Helpers.SpinAdapter;

public class ConfigSensors extends AppCompatActivity {

    Button save;

    EditText minTemp, maxTemp, minHum, maxHum, minCo, maxCo, minLum, maxLum;
    String placeDescTemp, placeIdTemp, placeDescHum, placeIdHum, placeDescCo, placeIdCo, placeDescLum, placeIdLum;
    HashMap<String, String> paramsTOSEND = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_sensors);

        save = findViewById(R.id.saveConfigData);
        minTemp = findViewById(R.id.MinValueSensorTemp);
        maxTemp = findViewById(R.id.MaxValueSensorTemp);
        minHum = findViewById(R.id.MinValueSensorHum);
        maxHum = findViewById(R.id.MaxValueSensorHum);
        minCo = findViewById(R.id.MinValueSensorCO2);
        maxCo = findViewById(R.id.MaxValueSensorCO2);
        minLum = findViewById(R.id.MinValueSensorLum);
        maxLum = findViewById(R.id.MaxValueSensorLum);

        //1) get places - escolho
        //2) get sensor por type e place
        //3) post sensor com config (Max e Min)

        registerReceiver();
        getPlaces();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String TempID = "";
                String HumID = "";
                String CoID = "";
                String LumID = "";

                for (Map.Entry<String, String> entry : paramsTOSEND.entrySet()) {
                    switch (entry.getKey()) {
                        case "Temperatura":
                            TempID = entry.getValue();
                            break;
                        case "Humidade":
                            HumID = entry.getValue();
                            break;
                        case "CO2":
                            CoID = entry.getValue();
                            break;
                        case "Luminosidade":
                            LumID = entry.getValue();
                            break;
                    }
                }

                HashMap<String, String> params = new HashMap<>();
                params.put("urlStr", "https://bd.ipg.pt:5500/ords/bda_1701887/confs/update");
                params.put("id", TempID);
                params.put("min", String.valueOf(minTemp.getText()));
                params.put("max", String.valueOf(maxTemp.getText()));

                new sendPost().execute(params);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                params = new HashMap<>();
                params.put("urlStr", "https://bd.ipg.pt:5500/ords/bda_1701887/confs/update");
                params.put("id", HumID);
                params.put("min", String.valueOf(minHum.getText()));
                params.put("max", String.valueOf(maxHum.getText()));

                new sendPost().execute(params);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                params = new HashMap<>();
                params.put("urlStr", "https://bd.ipg.pt:5500/ords/bda_1701887/confs/update");
                params.put("id", CoID);
                params.put("min", String.valueOf(minCo.getText()));
                params.put("max", String.valueOf(maxCo.getText()));

                new sendPost().execute(params);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                params = new HashMap<>();
                params.put("urlStr", "https://bd.ipg.pt:5500/ords/bda_1701887/confs/update");
                params.put("id", LumID);
                params.put("min", String.valueOf(minLum.getText()));
                params.put("max", String.valueOf(maxLum.getText()));

                new sendPost().execute(params);

            }
        });
    }

    private void registerReceiver() {

        BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String data = intent.getStringExtra("data");
                String whereto = intent.getStringExtra("whereto");

                if (whereto.equals("dealWithSpinners")) {
                    context.stopService(new Intent(context, BackgroundGetService.class));
                    dealWithSpinners(data);
                } else if (whereto.equals("dealWithSensorID")) {
                    String sensorType = intent.getStringExtra("sensorType");
                    context.stopService(new Intent(context, BackgroundGetService.class));
                    dealWithSensorID(data, sensorType);
                }

                intent.getBundleExtra("Location");
                Log.d("1233", "BCR");
            }
        };

        LocalBroadcastManager.getInstance(ConfigSensors.this).registerReceiver(
                mMessageReceiver, new IntentFilter("GetSevice"));

    }

    private void dealWithSensorID(String data, String sensorType) {

        JSONObject json;
        JSONArray array;

        try {
            json = new JSONObject(data);
            array = json.getJSONArray("items");

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

            Intent intent = new Intent(ConfigSensors.this, BackgroundPostService.class);
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
            array = json.getJSONArray("items");
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

                        getSensorID("Humidade", placeIdTemp);
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
                        getSensorID("CO2", placeIdTemp);
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
                        getSensorID("Luminosidade", placeIdTemp);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapter) {
            }
        });
    }

    private void getSensorID(String sensorType, String placeId) {

        String url = "https://bd.ipg.pt:5500/ords/bda_1701887/sensor/place/" + placeId + "/type/" + sensorType;
        Intent intent = new Intent(ConfigSensors.this, BackgroundGetService.class);
        intent.putExtra("urlStrg", url);
        intent.putExtra("sensorType", sensorType);
        intent.putExtra("whereto", "dealWithSensorID");
        startService(intent);

    }

    private void getPlaces() {

        Intent intent = new Intent(ConfigSensors.this, BackgroundGetService.class);
        intent.putExtra("urlStrg", "https://bd.ipg.pt:5500/ords/bda_1701887/place/all");
        intent.putExtra("whereto", "dealWithSpinners");
        startService(intent);

    }
}
