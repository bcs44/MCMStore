package mestrado.ipg.mcmstore.Condominio;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import mestrado.ipg.mcmstore.Administrador.MarcacaoAssembleia;
import mestrado.ipg.mcmstore.Globals.Meeting;
import mestrado.ipg.mcmstore.Globals.Place;
import mestrado.ipg.mcmstore.Globals.User;
import mestrado.ipg.mcmstore.Helpers.SpinAdapter;
import mestrado.ipg.mcmstore.R;
import mestrado.ipg.mcmstore.Services.BackgroundGetServiceAuth;


public class CalendarActivity extends AppCompatActivity {

    User user = User.getInstance();
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        textView = findViewById(R.id.output);

        registerReceiver();
        getMeetings();

    }

    private void getMeetings() {
        Intent intent = new Intent(CalendarActivity.this, BackgroundGetServiceAuth.class);
        intent.putExtra("urlStrg", "https://bd.ipg.pt:5500/ords/bda_1701887/meeting/townhouse/" + user.getTownhouse_id());
        intent.putExtra("_uri", "/meeting/townhouse/" + user.getTownhouse_id());
        intent.putExtra("wherefrom", "getMeetingsToCalendar");
        startService(intent);

    }

    private void registerReceiver() {

        BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String data = intent.getStringExtra("data");
                String wherefrom = intent.getStringExtra("wherefrom");

                if (wherefrom.equals("getMeetingsToCalendar")) {
                    context.stopService(new Intent(context, BackgroundGetServiceAuth.class));
                    dealWithCalendar(data);
                }

                intent.getBundleExtra("Location");
                Log.d("1233", "BCR");
            }
        };

        LocalBroadcastManager.getInstance(CalendarActivity.this).registerReceiver(
                mMessageReceiver, new IntentFilter("ServiceCalendar"));

    }

    private void dealWithCalendar(String data) {

        JSONObject json;
        JSONArray array;
        Meeting[] meetings = new Meeting[0];

        try {
            json = new JSONObject(data);
            array = json.getJSONArray("response");
            meetings = new Meeting[array.length()];

            for (int i = 0; i < array.length(); ++i) {
                json = array.getJSONObject(i);
                if (json != null) {
                    meetings[i] = new Meeting();
                    meetings[i].setData(json.getString("meeting_date"));
                    meetings[i].setDescricao(json.getString("description"));
                    meetings[i].setTitulo(json.getString("title"));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Uri uri = null;
        for (int i=0; i<meetings.length; i++){

            Calendar calendar = GregorianCalendar.getInstance();
            try {
                String iso8601string = meetings[i].getData();
                String s = iso8601string.replace("Z", "+00:00");
                s = s.substring(0, 22) + s.substring(23);  // to get rid of the ":"
                Date date = null;
                date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(s);
                calendar.setTime(date);
                System.out.println(calendar.getTimeInMillis());

            } catch (IndexOutOfBoundsException e) {
                System.out.println(e);
            } catch (ParseException e) {
                e.printStackTrace();
            }


            ContentResolver cr = this.getContentResolver();
            ContentValues cv = new ContentValues();

            cv.put(CalendarContract.Events.TITLE, meetings[i].getTitulo());
            cv.put(CalendarContract.Events.DESCRIPTION, meetings[i].getDescricao());
            cv.put(CalendarContract.Events.DTSTART, calendar.getTimeInMillis());
            cv.put(CalendarContract.Events.DTEND, calendar.getTimeInMillis() + 3600000);
            cv.put(CalendarContract.Events.CALENDAR_ID, 1);
            cv.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(CalendarActivity.this,
                        new String[]{Manifest.permission.WRITE_CALENDAR},
                        1);
            }
            uri = cr.insert(CalendarContract.Events.CONTENT_URI, cv);
            Toast.makeText(this, "Inserido" + uri, Toast.LENGTH_LONG).show();


        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        startActivity(intent);


    }


}

