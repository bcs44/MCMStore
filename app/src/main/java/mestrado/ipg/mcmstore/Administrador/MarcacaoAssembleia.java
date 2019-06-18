package mestrado.ipg.mcmstore.Administrador;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import mestrado.ipg.mcmstore.Globals.Place;
import mestrado.ipg.mcmstore.Globals.User;
import mestrado.ipg.mcmstore.Helpers.SpinAdapter;
import mestrado.ipg.mcmstore.R;
import mestrado.ipg.mcmstore.Services.BackgroundGetServiceAuth;
import mestrado.ipg.mcmstore.Services.BackgroundPostServiceAuth;

public class MarcacaoAssembleia extends AppCompatActivity {

    Calendar myCalendarInitial = Calendar.getInstance();
    Button saveDate;
    EditText etInitialDate, etInitialTime, etDesc, etTitle;
    Spinner spinnerPlaces;
    String placeDesc, placeId;
    User user = User.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marcacao_assembleia);


        saveDate = findViewById(R.id.saveDateBTN);
        etInitialDate = findViewById(R.id.initialDate);
        etInitialTime = findViewById(R.id.initialTime);
        etDesc = findViewById(R.id.desc);
        etTitle = findViewById(R.id.title);
        spinnerPlaces = findViewById(R.id.spinner1);

        registerReceiver();
        getPlaces();


        final DatePickerDialog.OnDateSetListener initialDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendarInitial.set(Calendar.YEAR, year);
                myCalendarInitial.set(Calendar.MONTH, monthOfYear);
                myCalendarInitial.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        etInitialDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(MarcacaoAssembleia.this, initialDate, myCalendarInitial
                        .get(Calendar.YEAR), myCalendarInitial.get(Calendar.MONTH),
                        myCalendarInitial.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        etInitialTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(MarcacaoAssembleia.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String toET = selectedHour + ":" + selectedMinute;
                        etInitialTime.setText(toET);
                        myCalendarInitial.set(Calendar.HOUR_OF_DAY, selectedHour);
                        myCalendarInitial.set(Calendar.MINUTE, selectedMinute);
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        saveDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEventToCalendar();

            }
        });


    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        etInitialDate.setText(sdf.format(myCalendarInitial.getTime()));

    }

    private void registerReceiver() {

        BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String data = intent.getStringExtra("data");
                String wherefrom = intent.getStringExtra("wherefrom");

                switch (wherefrom) {
                    case "getPlacesToMarcAssembleia":
                        context.stopService(new Intent(context, BackgroundGetServiceAuth.class));
                        dealWithSpinners(data);
                        break;
                    case "postAssembleia":
                        context.stopService(new Intent(context, BackgroundGetServiceAuth.class));
                        Toast.makeText(MarcacaoAssembleia.this, "Assembleia Marcada", Toast.LENGTH_LONG).show();
                        etInitialDate.setText("");
                        etInitialTime.setText("");
                        etDesc.setText("");
                        etTitle.setText("");
                        break;
                }


                intent.getBundleExtra("Location");

            }
        };

        LocalBroadcastManager.getInstance(MarcacaoAssembleia.this).registerReceiver(
                mMessageReceiver, new IntentFilter("ServiceMarcAssembleia"));

    }

    public void dealWithSpinners(String data) {

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

        adapter = new SpinAdapter(MarcacaoAssembleia.this,
                android.R.layout.simple_spinner_item,
                places);

        spinnerPlaces.setAdapter(adapter);
        spinnerPlaces.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            int checkTemp = 0;

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                if (++checkTemp > 1) {
                    Place place = adapter.getItem(position);
                    if (place != null) {
                        placeDesc = place.getDesc();
                        placeId = place.getId();
                        Toast.makeText(MarcacaoAssembleia.this, "ID: " + place.getId() + "\nDesc: " + place.getDesc(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapter) {
            }
        });

    }


    private void getPlaces() {

        Intent intent = new Intent(MarcacaoAssembleia.this, BackgroundGetServiceAuth.class);
        intent.putExtra("urlStrg", "https://bd.ipg.pt:5500/ords/bda_1701887/place/all");
        intent.putExtra("_uri", "/place/all");
        intent.putExtra("wherefrom", "getPlacesToMarcAssembleia");
        startService(intent);

    }


    private void addEventToCalendar() {

        ContentResolver cr = this.getContentResolver();
        ContentValues cv = new ContentValues();

        cv.put(CalendarContract.Events.TITLE, String.valueOf(etTitle.getText()));
        cv.put(CalendarContract.Events.DESCRIPTION, String.valueOf(etDesc.getText()));
        cv.put(CalendarContract.Events.DTSTART, myCalendarInitial.getTimeInMillis());
        cv.put(CalendarContract.Events.DTEND, myCalendarInitial.getTimeInMillis() + 3600000);
        cv.put(CalendarContract.Events.CALENDAR_ID, 1);
        cv.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MarcacaoAssembleia.this,
                    new String[]{Manifest.permission.WRITE_CALENDAR},
                    1);
        }
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, cv);
        Toast.makeText(this, "Inserido" + uri, Toast.LENGTH_LONG).show();
        sendData();
    }

    private void sendData() {

        HashMap<String, String> params = new HashMap<>();
        String url = "https://bd.ipg.pt:5500/ords/bda_1701887/meeting/insert";
        String _uri = "/meeting/insert";
        params.put("urlStr", url);
        params.put("_uri", _uri);



        Date date = new Date(myCalendarInitial.getTimeInMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        params.put("meeting_date",  format.format(date));
        params.put("place_id", placeId);
        params.put("description", String.valueOf(etDesc.getText()));
        params.put("title", String.valueOf(etTitle.getText()));
        params.put("user_id", user.getUser_id());

        params.put("wherefrom", "postAssembleia");

        new sendPost().execute(params);


    }

    private class sendPost extends AsyncTask<HashMap, HashMap, String> {

        @Override
        protected String doInBackground(HashMap... args) {

            HashMap<String, String> hashMap = args[0];

            Intent intent = new Intent(MarcacaoAssembleia.this, BackgroundPostServiceAuth.class);
            intent.putExtra("ParamsMAP", hashMap);
            startService(intent);

            return "done";
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    addEventToCalendar();
                }
            }
        }
    }

}
