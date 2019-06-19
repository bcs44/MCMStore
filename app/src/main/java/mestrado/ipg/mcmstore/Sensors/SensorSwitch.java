package mestrado.ipg.mcmstore.Sensors;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import mestrado.ipg.mcmstore.Administrador.Reservas;
import mestrado.ipg.mcmstore.Globals.ActiveSensor;
import mestrado.ipg.mcmstore.Globals.Place;
import mestrado.ipg.mcmstore.Globals.Sensor;
import mestrado.ipg.mcmstore.Helpers.SpinAdapter;
import mestrado.ipg.mcmstore.PrincipalActivity;
import mestrado.ipg.mcmstore.R;
import mestrado.ipg.mcmstore.Services.BackgroundGetService;
import mestrado.ipg.mcmstore.Services.BackgroundGetServiceAuth;
import mestrado.ipg.mcmstore.Services.BackgroundPostService;
import mestrado.ipg.mcmstore.Services.BackgroundPostServiceAuth;

public class SensorSwitch extends AppCompatActivity {

    Switch switchTemp, switchHum, switchCo, switchLum;
    String placeDesc, placeId;
    Boolean onFirstCall;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_switch);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(SensorSwitch.this, PrincipalActivity.class);
                startActivity(intent);
            }
        });

        registerReceiver();
        getPlaces();
        onFirstCall = true;

        switchTemp = findViewById(R.id.switchTemp);
        switchHum = findViewById(R.id.switchHum);
        switchCo = findViewById(R.id.switchCo);
        switchLum = findViewById(R.id.switchLum);

        switchTemp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String x = String.valueOf(isChecked);
                Toast.makeText(SensorSwitch.this, "isChecked switch1 " + x, Toast.LENGTH_LONG).show();

                if (!onFirstCall) {
                    sendData("Temperatura", isChecked);
                }
            }
        });

        switchHum.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String x = String.valueOf(isChecked);
                Toast.makeText(SensorSwitch.this, "isChecked switch2 " + x, Toast.LENGTH_LONG).show();
                if (!onFirstCall) {
                    sendData("Humidade", isChecked);
                }
            }
        });
        switchCo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String x = String.valueOf(isChecked);
                Toast.makeText(SensorSwitch.this, "isChecked switch3 " + x, Toast.LENGTH_LONG).show();

                if (!onFirstCall) {
                    sendData("CO2", isChecked);
                }
            }
        });
        switchLum.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String x = String.valueOf(isChecked);
                Toast.makeText(SensorSwitch.this, "isChecked switch4 " + x, Toast.LENGTH_LONG).show();

                if (!onFirstCall) {
                    sendData("Luminosidade", isChecked);
                }
            }
        });

    }

    private void getActiveSensors() {

        String url = "https://bd.ipg.pt:5500/ords/bda_1701887/activesensor/place/" + placeId;
        String _uri = "/activesensor/place/" + placeId;
        Intent intent = new Intent(SensorSwitch.this, BackgroundGetServiceAuth.class);
        intent.putExtra("urlStrg", url);
        intent.putExtra("_uri", _uri);
        intent.putExtra("wherefrom", "getActiveSensors");
        startService(intent);

    }

    private void registerReceiver() {

        BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String data = intent.getStringExtra("data");
                String wherefrom = intent.getStringExtra("wherefrom");

                if (wherefrom.equals("getPlacesToSensorSwitch")) {
                    context.stopService(new Intent(context, BackgroundGetServiceAuth.class));
                    dealWithSpinner(data);
                } else if (wherefrom.equals("getActiveSensors")) {
                    context.stopService(new Intent(context, BackgroundGetServiceAuth.class));
                    dealWithActiveSensors(data);
                } else if (wherefrom.equals("postActiveSensor")) {
                    context.stopService(new Intent(context, BackgroundGetServiceAuth.class));
                    AlertDialog.Builder dialogo = new
                            AlertDialog.Builder(SensorSwitch.this);
                    dialogo.setTitle("Aviso");
                    dialogo.setMessage("Estado do Sensor Alterado");
                    dialogo.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    dialogo.show();
                }

                intent.getBundleExtra("Location");
                Log.d("1233", "BCR");
            }
        };

        LocalBroadcastManager.getInstance(SensorSwitch.this).registerReceiver(
                mMessageReceiver, new IntentFilter("ServiceSensorSwitch"));
    }

    private void dealWithActiveSensors(String data) {

        JSONObject json;
        JSONArray array;
        ActiveSensor[] activeSensor = new ActiveSensor[0];

        try {
            json = new JSONObject(data);
            array = json.getJSONArray("response");
            activeSensor = new ActiveSensor[array.length()];

            for (int i = 0; i < array.length(); ++i) {
                json = array.getJSONObject(i);
                if (json != null) {
                    activeSensor[i] = new ActiveSensor();
                    activeSensor[i].setSensor_id(json.getString("sensor_id"));
                    activeSensor[i].setTownhouse_id(json.getString("townhouse_id"));
                    activeSensor[i].setState(json.getString("state"));
                    activeSensor[i].setSensorTypeDes(json.getString("description"));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        int i = 0;

        while (i < activeSensor.length) {

            if (activeSensor[i].getSensorTypeDes().equals("Temperatura")) {
                switchTemp.setChecked(activeSensor[i].getState().equals("1"));
            } else if (activeSensor[i].getSensorTypeDes().equals("Humidade")) {
                switchHum.setChecked(activeSensor[i].getState().equals("1"));
            } else if (activeSensor[i].getSensorTypeDes().equals("Luminosidade")) {
                switchLum.setChecked(activeSensor[i].getState().equals("1"));
            } else if (activeSensor[i].getSensorTypeDes().equals("CO2")) {
                switchCo.setChecked(activeSensor[i].getState().equals("1"));
            }
            i++;
        }

        onFirstCall = false;
    }

    private void sendData(String sensorType, Boolean isChecked) {

        HashMap<String, String> params = new HashMap<>();
        String url = "";
        String _uri = "";
        if (isChecked) {

            url = "https://bd.ipg.pt:5500/ords/bda_1701887/activesensor/on/place/" + placeId + "/type/" + sensorType;
            _uri = "/activesensor/on/place/" + placeId + "/type/" + sensorType;

        } else {
            url = "https://bd.ipg.pt:5500/ords/bda_1701887/activesensor/off/place/" + placeId + "/type/" + sensorType;
            _uri = "/activesensor/off/place/" + placeId + "/type/" + sensorType;
        }
        params.put("urlStr", url);
        params.put("_uri", _uri);
        params.put("place", placeId);
        params.put("type", sensorType);
        params.put("wherefrom", "postActiveSensor");

        new sendPost().execute(params);
    }


    private class sendPost extends AsyncTask<HashMap, HashMap, String> {

        @Override
        protected String doInBackground(HashMap... args) {

            HashMap<String, String> hashMap = args[0];
            Intent intent = new Intent(SensorSwitch.this, BackgroundPostServiceAuth.class);
            intent.putExtra("ParamsMAP", hashMap);
            startService(intent);
            return "done";
        }
    }

    private void getPlaces() {

        Intent intent = new Intent(SensorSwitch.this, BackgroundGetServiceAuth.class);
        intent.putExtra("urlStrg", "https://bd.ipg.pt:5500/ords/bda_1701887/place/all");
        intent.putExtra("_uri", "/place/all");
        intent.putExtra("wherefrom", "getPlacesToSensorSwitch");
        startService(intent);
    }

    public void dealWithSpinner(String data) {
        Spinner spinnerPlace;
        spinnerPlace = findViewById(R.id.spinnerPlace);
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

        adapter = new SpinAdapter(SensorSwitch.this,
                android.R.layout.simple_spinner_item,
                places);

        spinnerPlace.setAdapter(adapter);
        spinnerPlace.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            int checkTemp = 0;

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                if (++checkTemp > 1) {
                    Place place = adapter.getItem(position);
                    if (place != null) {
                        placeDesc = place.getDesc();
                        placeId = place.getId();
                        Toast.makeText(SensorSwitch.this, "ID: " + place.getId() + "\nDesc: " + place.getDesc(),
                                Toast.LENGTH_SHORT).show();

                        getActiveSensors();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapter) {
            }
        });
    }
}

