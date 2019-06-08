package mestrado.ipg.mcmstore.Sensors;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import mestrado.ipg.mcmstore.Globals.Place;
import mestrado.ipg.mcmstore.Helpers.SpinAdapter;
import mestrado.ipg.mcmstore.R;
import mestrado.ipg.mcmstore.Services.BackgroundGetService;
import mestrado.ipg.mcmstore.Services.BackgroundPostService;

public class SensorSwitch extends AppCompatActivity {

    Switch switchTemp, switchHum, switchCo, switchLum;
    String placeDesc, placeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_switch);


        // get all places
        // get all sensor from place
        // switch sensor

        registerReceiver();
        getPlaces();

        switchTemp = findViewById(R.id.switchTemp);
        switchHum = findViewById(R.id.switchHum);
        switchCo = findViewById(R.id.switchCo);
        switchLum = findViewById(R.id.switchLum);

        switchTemp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String x = String.valueOf(isChecked);
                Toast.makeText(SensorSwitch.this, "isChecked switch1 " + x, Toast.LENGTH_LONG).show();

                getSensorId("Temperatura", isChecked);
            }
        });

        switchHum.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String x = String.valueOf(isChecked);
                Toast.makeText(SensorSwitch.this, "isChecked switch2 " + x, Toast.LENGTH_LONG).show();

                getSensorId("Humidade", isChecked);


            }
        });
        switchCo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String x = String.valueOf(isChecked);
                Toast.makeText(SensorSwitch.this, "isChecked switch3 " + x, Toast.LENGTH_LONG).show();

                getSensorId("CO2", isChecked);
            }
        });
        switchLum.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String x = String.valueOf(isChecked);
                Toast.makeText(SensorSwitch.this, "isChecked switch4 " + x, Toast.LENGTH_LONG).show();

                getSensorId("Luminosidade", isChecked);

            }
        });

    }

    private void getSensorId(String sensorType, Boolean isChecked) {

        String url = "https://bd.ipg.pt:5500/ords/bda_1701887/sensor/place/" + placeId + "/type/" + sensorType;
        Intent intent = new Intent(SensorSwitch.this, BackgroundGetService.class);
        intent.putExtra("urlStrg", url);
        intent.putExtra("sensorType", sensorType);
        intent.putExtra("whereto", "dealWithSensorID");
        startService(intent);


    }

    private void registerReceiver() {

        BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String data = intent.getStringExtra("data");
                String whereto = intent.getStringExtra("whereto");

                if (whereto.equals("dealWithSpinners")) {
                    context.stopService(new Intent(context, BackgroundGetService.class));
                    dealWithSpinner(data);
                }

                intent.getBundleExtra("Location");
                Log.d("1233", "BCR");
            }
        };

        LocalBroadcastManager.getInstance(SensorSwitch.this).registerReceiver(
                mMessageReceiver, new IntentFilter("GetSevice"));

    }


    private void getPlaces() {

        Intent intent = new Intent(SensorSwitch.this, BackgroundGetService.class);
        intent.putExtra("urlStrg", "https://bd.ipg.pt:5500/ords/bda_1701887/place/all");
        intent.putExtra("whereto", "dealWithSpinners");
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

                    //    getSensorID("Temperatura", placeIdTemp);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapter) {
            }
        });



    }



}

