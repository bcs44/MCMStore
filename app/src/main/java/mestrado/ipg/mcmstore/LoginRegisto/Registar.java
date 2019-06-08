package mestrado.ipg.mcmstore.LoginRegisto;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import mestrado.ipg.mcmstore.Administrador.Comunicados;
import mestrado.ipg.mcmstore.Condominio.ChatCondominio;
import mestrado.ipg.mcmstore.Globals.User;
import mestrado.ipg.mcmstore.PrincipalActivity;
import mestrado.ipg.mcmstore.R;
import mestrado.ipg.mcmstore.Sensors.ConfigSensors;
import mestrado.ipg.mcmstore.ServiceSendToBDAuth;
import mestrado.ipg.mcmstore.Services.BackgroundGetService;

public class Registar extends AppCompatActivity {

    Button login_rgt;
    EditText userET, passET;
    User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registar);


        login_rgt = findViewById(R.id.login_rgt);
        userET = findViewById(R.id.user);
        passET = findViewById(R.id.password);

        login_rgt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registo(String.valueOf(userET.getText()), String.valueOf(passET.getText()));
            }
        });

        registerReceiver();

    }

    private void registo(String username, String password) {

        String url = "https://bd.ipg.pt:5500/ords/bda_1701887/user/insert";
        Intent intent = new Intent(Registar.this, ServiceSendToBDAuth.class);
        intent.putExtra("urlStrg", url);
        intent.putExtra("wherefrom", "registo");
        intent.putExtra("username", username);
        intent.putExtra("password", password);

        startService(intent);

    }

    private void registerReceiver() {

        BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String data = intent.getStringExtra("data");
                String username = intent.getStringExtra("username");
                registoTerminado(data,username);
                context.stopService(new Intent(context, BackgroundGetService.class));
                intent.getBundleExtra("Location");
                Log.d("1233", "BCR");
            }
        };

        LocalBroadcastManager.getInstance(Registar.this).registerReceiver(
                mMessageReceiver, new IntentFilter("ServiceRegisto"));

    }

    private void registoTerminado(String data, String username) {

        JSONObject json;
        String api_key;


        try {
            json = new JSONObject(data);
            api_key = json.getString("api-key");


            user.setApi_key(api_key);
            user.setUsername(username);


            Intent myIntent = new Intent(Registar.this, PrincipalActivity.class);
            startActivity(myIntent);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

}
