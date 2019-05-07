package mestrado.ipg.mcmstore;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MarcacaoAssembleia extends AppCompatActivity {
    Calendar myCalendarInitial = Calendar.getInstance();
    Button saveDate;
    EditText etInitialDate, etInitialTime, etDesc, etTitle;
    Spinner spinner1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marcacao_assembleia);

        saveDate = findViewById(R.id.saveDateBTN);
        etInitialDate = findViewById(R.id.initialDate);
        etInitialTime = findViewById(R.id.initialTime);
        etDesc = findViewById(R.id.desc);
        etTitle = findViewById(R.id.title);
        spinner1 = findViewById(R.id.spinner1);


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
                addEvent();
                sendToBD();
            }
        });

        Intent intent = getIntent();
        String data = intent.getStringExtra("data");
        if (data != null) {
            CreateSpinner(data);
        } else {
            getPlaces();
        }


    }

    private void sendToBD() {

        HashMap<String, String> params = new HashMap<>();
        params.put("urlStr", "https://bd.ipg.pt:5500/ords/bda_1701887/meeting/post");
        params.put("user_id", "1");
        params.put("meeting_date", "2019/05/10 21:02:44");
        params.put("place_id", "1");
        params.put("description", "1");
        params.put("alternative_place", "1");
        params.put("alternative_date", "2019/05/12 23:10:00");


        Intent myIntent = new Intent(MarcacaoAssembleia.this, PostBD.class);
        myIntent.putExtra("ParamsMAP", params);
        startActivity(myIntent);


    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        etInitialDate.setText(sdf.format(myCalendarInitial.getTime()));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    addEvent();
                }
            }
        }
    }

    private void addEvent() {

        ContentResolver cr = this.getContentResolver();
        ContentValues cv = new ContentValues();

        cv.put(CalendarContract.Events.TITLE, String.valueOf(etTitle.getText()));
        cv.put(CalendarContract.Events.DESCRIPTION, String.valueOf(etDesc.getText()));
        //cv.put(CalendarContract.Events.EVENT_LOCATION, "location");
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
    }


    private void getPlaces() {

        Intent myIntent = new Intent(MarcacaoAssembleia.this, GetBD.class);
        myIntent.putExtra("urlStrg", "https://bd.ipg.pt:5500/ords/bda_1701887/place/all");
        myIntent.putExtra("activity", "mestrado.ipg.mcmstore.MarcacaoAssembleia");
        startActivity(myIntent);


    }

    private void CreateSpinner(String data) {


        ArrayList<String> list = new ArrayList<>();
        JSONObject json;
        JSONArray array;
        List<String> descriptions = null;
        try {
            json = new JSONObject(data);
            array = json.getJSONArray("items");

            if (array != null) {
                descriptions = new ArrayList<>(array.length());
            }

            if (array != null) {
                for (int i = 0; i < array.length(); ++i) {
                    json = array.getJSONObject(i);
                    if (json != null) {
                        descriptions.add(json.getString("description"));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (descriptions != null) {
            list.addAll(descriptions);
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(dataAdapter);

        System.out.println(data);

    }

}

