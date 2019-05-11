package mestrado.ipg.mcmstore;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class ConfigSensors extends AppCompatActivity {

    Button save;

    EditText minTemp, maxTemp, minHum, maxHum, minCo, maxCo, minLum, maxLum;
    Spinner spinnerTemp, spinnerHum, spinnerCo, spinnerLum;
    String placeDescTemp, placeIdTemp, placeDescHum, placeIdHum, placeDescCo,placeIdCo,  placeDescLum, placeIdLum;

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


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                HashMap<String, String> params = new HashMap<>();
                params.put("urlStr", "https://bd.ipg.pt:5500/ords/bda_1701887/confs/update");
                params.put("id", "1");
                params.put("min", String.valueOf(minTemp.getText()));
                params.put("max", String.valueOf(maxTemp.getText()));

                new sendPost().execute(params);




                params = new HashMap<>();
                params.put("urlStr", "https://bd.ipg.pt:5500/ords/bda_1701887/confs/update");
                params.put("id", "42");
                params.put("min", String.valueOf(minHum.getText()));
                params.put("max", String.valueOf(maxHum.getText()));

                new sendPost().execute(params);

                params = new HashMap<>();
                params.put("urlStr", "https://bd.ipg.pt:5500/ords/bda_1701887/confs/update");
                params.put("id", "62");
                params.put("min", String.valueOf(minCo.getText()));
                params.put("max", String.valueOf(maxCo.getText()));

                new sendPost().execute(params);

                params = new HashMap<>();
                params.put("urlStr", "https://bd.ipg.pt:5500/ords/bda_1701887/confs/update");
                params.put("id", "21");
                params.put("min", String.valueOf(minLum.getText()));
                params.put("max", String.valueOf(maxLum.getText()));

                new sendPost().execute(params);

            }
        });



        Intent intent = getIntent();
        String data = intent.getStringExtra("data");
        if (data != null) {
            dealWithSpinners(data);
            //CreateSpinner(data);
        } else {
            getPlaces();
        }


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



    private void dealWithSpinners(String data) {

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

        spinnerTemp.setAdapter(adapter); // Set the custom adapter to the spinner
        // You can create an anonymous listener to handle the event when is selected an spinner item
        spinnerTemp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                // Here you get the current item (a User object) that is selected by its position
                Place place = adapter.getItem(position);
                placeDescTemp = place.getDesc();
                placeIdTemp = place.getId();
                // Here you can do the action you want to...
                Toast.makeText(ConfigSensors.this, "ID: " + place.getId() + "\nDesc: " + place.getDesc(),
                        Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapter) {  }
        });
        spinnerHum.setAdapter(adapter); // Set the custom adapter to the spinner
        // You can create an anonymous listener to handle the event when is selected an spinner item
        spinnerHum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                // Here you get the current item (a User object) that is selected by its position
                Place place = adapter.getItem(position);
                placeDescHum = place.getDesc();
                placeIdHum = place.getId();
                // Here you can do the action you want to...
                Toast.makeText(ConfigSensors.this, "ID: " + place.getId() + "\nDesc: " + place.getDesc(),
                        Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapter) {  }
        });
        spinnerCo.setAdapter(adapter); // Set the custom adapter to the spinner
        // You can create an anonymous listener to handle the event when is selected an spinner item
        spinnerCo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                // Here you get the current item (a User object) that is selected by its position
                Place place = adapter.getItem(position);
                // Here you can do the action you want to...
                placeDescCo = place.getDesc();
                placeIdCo = place.getId();
                Toast.makeText(ConfigSensors.this, "ID: " + place.getId() + "\nDesc: " + place.getDesc(),
                        Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapter) {  }
        });
        spinnerLum.setAdapter(adapter); // Set the custom adapter to the spinner
        // You can create an anonymous listener to handle the event when is selected an spinner item
        spinnerLum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                // Here you get the current item (a User object) that is selected by its position
                Place place = adapter.getItem(position);
                // Here you can do the action you want to...

                placeDescLum = place.getDesc();
                placeIdLum = place.getId();
                Toast.makeText(ConfigSensors.this, "ID: " + place.getId() + "\nDesc: " + place.getDesc(),
                        Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapter) {  }
        });
    }




    private void getPlaces() {
        Intent myIntent = new Intent(ConfigSensors.this, GetBD.class);
        myIntent.putExtra("urlStrg", "https://bd.ipg.pt:5500/ords/bda_1701887/place/all");
        myIntent.putExtra("activity", "mestrado.ipg.mcmstore.ConfigSensors");
        startActivity(myIntent);
    }



}
