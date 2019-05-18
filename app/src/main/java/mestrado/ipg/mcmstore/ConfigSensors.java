package mestrado.ipg.mcmstore;

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

import mestrado.ipg.mcmstore.service.BackgroundGetService;

public class ConfigSensors extends AppCompatActivity {

    Button save;

    EditText minTemp, maxTemp, minHum, maxHum, minCo, maxCo, minLum, maxLum;
    Spinner spinnerTemp, spinnerHum, spinnerCo, spinnerLum;
    String placeDescTemp, placeIdTemp, placeDescHum, placeIdHum, placeDescCo,placeIdCo,  placeDescLum, placeIdLum;
    Boolean clickable = false;
    private static String isRegisteredreceiver = "false";

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

                if (clickable) {

                    HashMap<String, String> params = new HashMap<>();
                    params.put("urlStr", "https://bd.ipg.pt:5500/ords/bda_1701887/confs/update");
                    params.put("id", "1");
                    params.put("min", String.valueOf(minTemp.getText()));
                    params.put("max", String.valueOf(maxTemp.getText()));


                    new GetBD.sendGet().execute("https://bd.ipg.pt:5500/ords/bda_1701887/confs/update", "mestrado.ipg.mcmstore.ConfigSensors", "cena");

                    new sendPost().execute(params);

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    params = new HashMap<>();
                    params.put("urlStr", "https://bd.ipg.pt:5500/ords/bda_1701887/confs/update");
                    params.put("id", "42");
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
                    params.put("id", "62");
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
                    params.put("id", "21");
                    params.put("min", String.valueOf(minLum.getText()));
                    params.put("max", String.valueOf(maxLum.getText()));

                    new sendPost().execute(params);

                }
                else{
                    Toast.makeText(ConfigSensors.this, "Preencha todos os campos necessários",
                            Toast.LENGTH_SHORT).show();

                }
            }

        });
    }



    private void registerReceiver() {

        BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {



                String data = intent.getStringExtra("data");
                String whereto = intent.getStringExtra("whereto");

                if (whereto.equals("dealWithSpinners")){
                    context.stopService(new Intent(context,BackgroundGetService.class));
                    dealWithSpinners(data);
                }
                Bundle b = intent.getBundleExtra("Location");
                Log.d("1233", "BCR");
            }
        };



      //  if (isRegisteredreceiver.equals("false")) {
            LocalBroadcastManager.getInstance(ConfigSensors.this).registerReceiver(
                    mMessageReceiver, new IntentFilter("GetSevice"));
          //  isRegisteredreceiver = "true";
        //}
    }



    private class sendPost extends AsyncTask<HashMap, HashMap, String> {

        @Override
        protected String doInBackground(HashMap... args) {

            HashMap<String, String> hashMap = args[0];

            Intent myIntent = new Intent(ConfigSensors.this, PostBD.class);
            myIntent.putExtra("ParamsMAP", hashMap);
            startActivity(myIntent);
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
                if(++checkTemp > 1) {
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
            public void onNothingSelected(AdapterView<?> adapter) {  }
        });
        spinnerHum.setAdapter(adapter);
        spinnerHum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
          int  checkHum = 0;
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                if(++checkHum > 1) {
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
            public void onNothingSelected(AdapterView<?> adapter) {  }
        });
        spinnerCo.setAdapter(adapter);
        spinnerCo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            int checkCo = 0;
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                if(++checkCo > 1) {
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
            public void onNothingSelected(AdapterView<?> adapter) {  }
        });
        spinnerLum.setAdapter(adapter);
        spinnerLum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            int checkLum = 0;
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                if(++checkLum > 1) {
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
            public void onNothingSelected(AdapterView<?> adapter) {  }
        });
    }

    private void getSensorID(String sensorType, String placeId) {
        Intent myIntent = new Intent(ConfigSensors.this, GetBD.class);
        String url = "https://bd.ipg.pt:5500/ords/bda_1701887/sensor/place/" + placeId + "/type/" + sensorType;
        myIntent.putExtra("urlStrg", url);
        myIntent.putExtra("activity", "mestrado.ipg.mcmstore.ConfigSensors");
        myIntent.putExtra("metodo", "getSensorID");
        startActivity(myIntent);
    }

    private void getPlaces() {

        Intent intent = new Intent(ConfigSensors.this, BackgroundGetService.class);
        intent.putExtra("urlStrg", "https://bd.ipg.pt:5500/ords/bda_1701887/place/all");
        intent.putExtra("whereto", "dealWithSpinners");
        startService(intent);

    }



}
