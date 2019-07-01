package mestrado.ipg.mcmstore.Condominio;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import mestrado.ipg.mcmstore.Administrador.Reservas;
import mestrado.ipg.mcmstore.Globals.Place;
import mestrado.ipg.mcmstore.Globals.User;
import mestrado.ipg.mcmstore.Helpers.SpinAdapter;
import mestrado.ipg.mcmstore.PrincipalActivity;
import mestrado.ipg.mcmstore.R;
import mestrado.ipg.mcmstore.Services.BackgroundGetServiceAuth;
import mestrado.ipg.mcmstore.Services.BackgroundPostServiceAuth;

public class PedidoReserva extends AppCompatActivity {

    String placeId;
    EditText dateET, timeET, descET;
    Calendar myCalendar = Calendar.getInstance();
    Button sendPost;
    User user = User.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido_reserva);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(PedidoReserva.this, PrincipalActivity.class);
                startActivity(intent);
            }
        });

        dateET = findViewById(R.id.reservDate);
        timeET = findViewById(R.id.reservTime);
        descET = findViewById(R.id.desc);
        sendPost = findViewById(R.id.senPostReservation);

        registerReceiver();
        getPlaces();

        final DatePickerDialog.OnDateSetListener initialDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "MM/dd/yy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                dateET.setText(sdf.format(myCalendar.getTime()));
            }
        };

        dateET.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DatePickerDialog mDatePicker;
                new DatePickerDialog(PedidoReserva.this, initialDate, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        timeET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(PedidoReserva.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String toET = selectedHour + ":" + selectedMinute;
                        timeET.setText(toET);
                        myCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                        myCalendar.set(Calendar.MINUTE, selectedMinute);
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });


        sendPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> params = new HashMap<>();

                //TODO
                String x = "?";

                String url = "https://bd.ipg.pt:5500/ords/bda_1701887/reservation/insert";
                String _uri = "/reservation/insert";
                params.put("urlStr", url);
                params.put("_uri", _uri);
                params.put("wherefrom", "PostPedidoReserva");

                params.put("user_id", user.getUser_id());

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                Date reservation_date = new Date();
                Date date = new Date(myCalendar.getTimeInMillis());

                params.put("description", String.valueOf(descET.getText()));
                params.put("place_id", placeId);
                params.put("start_date", format.format(date));
                params.put("end_date", format.format(date));
                params.put("reservation_date", format.format(reservation_date));


                new sendPost().execute(params);
            }
        });
    }

    private class sendPost extends AsyncTask<HashMap, HashMap, String> {

        @Override
        protected String doInBackground(HashMap... args) {
            HashMap<String, String> hashMap = args[0];
            Intent intent = new Intent(PedidoReserva.this, BackgroundPostServiceAuth.class);
            intent.putExtra("ParamsMAP", hashMap);
            startService(intent);
            return "done";
        }
    }

    private void registerReceiver() {

        BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String data = intent.getStringExtra("data");
                String wherefrom = intent.getStringExtra("wherefrom");

                if (wherefrom.equals("getPlacesToPedidoReserva")) {
                    context.stopService(new Intent(context, BackgroundGetServiceAuth.class));
                    dealWithSpinner(data);
                }
                else if (wherefrom.equals("PostPedidoReserva")) {
                    context.stopService(new Intent(context, BackgroundGetServiceAuth.class));
                    AlertDialog.Builder dialogo = new
                            AlertDialog.Builder(PedidoReserva.this);
                    dialogo.setTitle("Aviso");
                    dialogo.setMessage("Pedido de Reserva Enviado");
                    dialogo.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dateET.setText("");
                            timeET.setText("");
                            descET.setText("");
                            dialog.dismiss();
                        }
                    });
                    dialogo.show();
                }

                intent.getBundleExtra("Location");
                Log.d("1233", "BCR");
            }
        };

        LocalBroadcastManager.getInstance(PedidoReserva.this).registerReceiver(
                mMessageReceiver, new IntentFilter("ServicePedidoReserva"));
    }

    private void getPlaces() {

        Intent intent = new Intent(PedidoReserva.this, BackgroundGetServiceAuth.class);
        intent.putExtra("urlStrg", "https://bd.ipg.pt:5500/ords/bda_1701887/place/all");
        intent.putExtra("_uri", "/place/all");
        intent.putExtra("wherefrom", "getPlacesToPedidoReserva");
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

        adapter = new SpinAdapter(PedidoReserva.this,
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
                        placeId = place.getId();
                        Toast.makeText(PedidoReserva.this, "ID: " + place.getId() + "\nDesc: " + place.getDesc(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapter) {
            }
        });
    }
}
