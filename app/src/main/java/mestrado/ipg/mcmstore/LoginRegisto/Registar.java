package mestrado.ipg.mcmstore.LoginRegisto;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import mestrado.ipg.mcmstore.Administrador.Reservas;
import mestrado.ipg.mcmstore.Globals.User;
import mestrado.ipg.mcmstore.PrincipalActivity;
import mestrado.ipg.mcmstore.R;
import mestrado.ipg.mcmstore.Services.BackgroundPostServiceAuth;
import mestrado.ipg.mcmstore.Services.BackgroundGetService;

public class Registar extends AppCompatActivity {

    Button login_rgt;
    EditText userET, passET, emailET;
    User user = User.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registar);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(Registar.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        login_rgt = findViewById(R.id.login_rgt);
        userET = findViewById(R.id.user);
        emailET = findViewById(R.id.email);
        passET = findViewById(R.id.password);

        login_rgt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registo(String.valueOf(userET.getText()), String.valueOf(emailET.getText()), String.valueOf(passET.getText()));
            }
        });

        registerReceiver();
    }

    private void registo(String username, String email, String password) {

        String url = "https://bd.ipg.pt:5500/ords/bda_1701887/user/insert";
        Intent intent = new Intent(Registar.this, BackgroundPostServiceAuth.class);
        intent.putExtra("urlStrg", url);
        intent.putExtra("wherefrom", "registo");
        intent.putExtra("username", username);
        intent.putExtra("email", email);
        intent.putExtra("password", password);

        startService(intent);
    }

    private void registerReceiver() {

        BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String data = intent.getStringExtra("data");
                String username = intent.getStringExtra("username");
                String password = intent.getStringExtra("password");
                String email = intent.getStringExtra("email");

                registoTerminado(data, username, email, password);
                context.stopService(new Intent(context, BackgroundGetService.class));
                intent.getBundleExtra("Location");
                Log.d("1233", "BCR");
            }
        };

        LocalBroadcastManager.getInstance(Registar.this).registerReceiver(
                mMessageReceiver, new IntentFilter("ServiceRegisto"));
    }

    private void registoTerminado(String data, String username, String email, String password) {

        JSONObject json;
        String api_key;
        String townhouse_id;

        try {
            json = new JSONObject(data);
            api_key = json.getString("api-key");
            townhouse_id = json.getString("townhouse_id");
            user.setApi_key(api_key);
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(password);
            user.setTownhouse_id(townhouse_id);

            Intent myIntent = new Intent(Registar.this, PrincipalActivity.class);
            startActivity(myIntent);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
