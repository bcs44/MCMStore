package mestrado.ipg.mcmstore.Sensors;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import mestrado.ipg.mcmstore.Globals.User;
import mestrado.ipg.mcmstore.R;
import mestrado.ipg.mcmstore.Services.BackgroundGetServiceAuth;
import mestrado.ipg.mcmstore.domain.Record;
import mestrado.ipg.mcmstore.domain.TypeSensor;

public class DetailsLuminosity extends AppCompatActivity {

    private String startDate;
    private String finalDate;
    private Double mediaValue;
    private Double maximumValue;
    private Double minimumValue;
    private Double actualValue;
    private TextView actualView;
    private TextView minimaView;
    private TextView maximaView;
    private TextView mediaView;
    private EditText startDateText;
    private EditText finalDateText;
    User user = User.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        startDate = getIntent().getStringExtra("startDate");
        finalDate = getIntent().getStringExtra("finalDate");
        mediaValue = getIntent().getDoubleExtra("mediaValue", 0.0);
        maximumValue = getIntent().getDoubleExtra("maximumValue", 0.0);
        minimumValue = getIntent().getDoubleExtra("minimumValue", 0.0);
        actualValue = getIntent().getDoubleExtra("actualValue", 0.0);

        actualView = findViewById(R.id.textViewValueAtual);
        actualView.setText(String.format("%.2f",actualValue));
        minimaView = findViewById(R.id.textViewValueMinima);
        minimaView.setText(String.format("%.2f",minimumValue));
        maximaView = findViewById(R.id.textViewValueMaxima);
        maximaView.setText(String.format("%.2f",maximumValue));
        mediaView = findViewById(R.id.textViewValueMedia);
        mediaView.setText(String.format("%.2f", mediaValue));

        startDateText = findViewById(R.id.editTextStartDate);
        startDateText.setText(startDate);
        finalDateText = findViewById(R.id.editTextFinalDate);
        finalDateText.setText(finalDate);
    }

    public void getRecords(){
        new DetailsLuminosity.sendGet().execute();
    }

    public void getRecords(View view) {
        registerReceiver();
        getRecords();
    }

    private class sendGet extends AsyncTask<HashMap, HashMap, String> {

        HashMap<String, String> params = new HashMap<>();

        @Override
        protected String doInBackground(HashMap... args) {
            Date startDate = convertToDate(startDateText.getText().toString());
            if(startDate == null) {
                return "done";
            }
            Date endDate = convertToDate(finalDateText.getText().toString());
            if(endDate == null) {
                return "done";
            }
            SimpleDateFormat sdf;
            sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            String startDdate = sdf.format(startDate);
            String endSDate = sdf.format(endDate);
            startTemperatureDetails(startDdate, endSDate);
            return "done";
        }
    }

    private void startTemperatureDetails(String startDate, String endDate) {
        HashMap<String, String> params = new HashMap<>();
        String urlTemp = "https://bd.ipg.pt:5500/ords/bda_1701887/record/interval/type/" + TypeSensor.Luminosity.getValue() + "/townhouse/" + user.getTownhouse_id();
        String _uriTemp = "/record/interval/type/" + TypeSensor.Luminosity.getValue() + "/townhouse/" + user.getTownhouse_id();
        Intent intent = new Intent(DetailsLuminosity.this, BackgroundGetServiceAuth.class);
        intent.putExtra("urlStrg", urlTemp);
        intent.putExtra("_uri", _uriTemp);
        intent.putExtra("wherefrom", "Details");
        intent.putExtra("sensorType", TypeSensor.Luminosity.toString());
        intent.putExtra("end", endDate);
        intent.putExtra("start", startDate);
        startService(intent);
    }

    private void registerReceiver() {
        BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String data = intent.getStringExtra("data");
                String sensorType = intent.getStringExtra("sensorType");
                if (sensorType != null) {
                    if (sensorType.equals(TypeSensor.Luminosity.toString())) {
                        List<Record> records = Charts.parseObject(data);
                        mediaValue = Charts.getMedia(records);
                        maximumValue = Charts.getMaximum(records);
                        minimumValue = Charts.getMinimum(records);
                        if(records.size() > 0) {
                            actualValue = records.get(0).getValue();
                        } else {
                            actualValue = 0.0;
                        }
                        minimaView.setText(String.format("%.2f",minimumValue));
                        maximaView.setText(String.format("%.2f",maximumValue));
                        mediaView.setText(String.format("%.2f", mediaValue));
                    }
                }
                context.stopService(new Intent(context, BackgroundGetServiceAuth.class));
                intent.getBundleExtra("Location");
            }
        };

        LocalBroadcastManager.getInstance(DetailsLuminosity.this).registerReceiver(
                mMessageReceiver, new IntentFilter("ServiceDetailsRecords"));
    }

    private static Date convertToDate(String date){
        try {
            DateFormat format = new SimpleDateFormat("dd/MM/yyyy.HH-mm-ss");
            return format.parse(date);
        } catch(ParseException ex) {
            try {
                DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                return format.parse(date);
            } catch(ParseException e){

            }
        }
        return null;
    }


}
