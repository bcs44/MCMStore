package mestrado.ipg.mcmstore;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MarcacaoAssembleia extends AppCompatActivity {
    Calendar myCalendarInitial = Calendar.getInstance();
    Calendar myCalendarFinal = Calendar.getInstance();
    Button saveDate;
    EditText etInitialDate, etFinalDate, etInitialTime, etFinalTime, etDesc, etTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marcacao_assembleia);

        saveDate = findViewById(R.id.saveDateBTN);
        etInitialDate= findViewById(R.id.initialDate);
        etFinalDate= findViewById(R.id.finalDate);
        etInitialTime= findViewById(R.id.initialTime);
        etFinalTime= findViewById(R.id.finalTime);
        etDesc= findViewById(R.id.desc);
        etTitle= findViewById(R.id.title);

        final DatePickerDialog.OnDateSetListener initialDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendarInitial.set(Calendar.YEAR, year);
                myCalendarInitial.set(Calendar.MONTH, monthOfYear);
                myCalendarInitial.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel("initial");
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
                        etInitialTime.setText( selectedHour + ":" + selectedMinute);
                        myCalendarInitial.set(Calendar.HOUR_OF_DAY, selectedHour);
                        myCalendarInitial.set(Calendar.MINUTE, selectedMinute);
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        final DatePickerDialog.OnDateSetListener finalDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendarFinal.set(Calendar.YEAR, year);
                myCalendarFinal.set(Calendar.MONTH, monthOfYear);
                myCalendarFinal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel("final");
            }
        };

        etFinalDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(MarcacaoAssembleia.this, finalDate, myCalendarFinal
                        .get(Calendar.YEAR), myCalendarFinal.get(Calendar.MONTH),
                        myCalendarFinal.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        etFinalTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(MarcacaoAssembleia.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        etFinalTime.setText( selectedHour + ":" + selectedMinute);
                        myCalendarFinal.set(Calendar.HOUR_OF_DAY, selectedHour);
                        myCalendarFinal.set(Calendar.MINUTE, selectedMinute);
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
            }
        });
    }

    private void updateLabel(String label) {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

        if(label.equals("initial")){
            etInitialDate.setText(sdf.format(myCalendarInitial.getTime()));
        }
        else if (label.equals("final")) {
            etFinalDate.setText(sdf.format(myCalendarFinal.getTime()));
        }
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
        cv.put(CalendarContract.Events.DTEND, myCalendarFinal.getTimeInMillis());
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

}

//TODO - adicionar as datas na BD