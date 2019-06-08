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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import mestrado.ipg.mcmstore.Globals.User;
import mestrado.ipg.mcmstore.PrincipalActivity;
import mestrado.ipg.mcmstore.R;
import mestrado.ipg.mcmstore.ServiceSendToBDAuth;
import mestrado.ipg.mcmstore.Services.BackgroundGetService;


public class LoginActivity extends AppCompatActivity{

    private EditText etUsername, etPassword;
    Button lgnButn, login_rgt;
    User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.user);
        etPassword= findViewById(R.id.password);
        lgnButn= findViewById(R.id.login_btn);
        login_rgt = findViewById(R.id.login_rgt);

        lgnButn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                login(String.valueOf(etUsername.getText()), String.valueOf(etPassword.getText()));
                Toast.makeText(LoginActivity.this, "Username = " + String.valueOf(etUsername.getText()) + "Password=" + String.valueOf(etPassword   .getText()), Toast.LENGTH_LONG).show();
            }
        });


        login_rgt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(LoginActivity.this, Registar.class);
                startActivity(myIntent);
            }
        });

        registerReceiver();


    }

    private void login(String username, String password) {

        String url = "https://bd.ipg.pt:5500/ords/bda_1701887/user/login";
        Intent intent = new Intent(LoginActivity.this, ServiceSendToBDAuth.class);
        intent.putExtra("urlStrg", url);
        intent.putExtra("wherefrom", "login");
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
                loginTerminado(data,username);
                context.stopService(new Intent(context, BackgroundGetService.class));
                intent.getBundleExtra("Location");
            }
        };

        LocalBroadcastManager.getInstance(LoginActivity.this).registerReceiver(
                mMessageReceiver, new IntentFilter("ServiceLogin"));

    }

    private void loginTerminado(String data, String username) {

        JSONObject json;
        String api_key;
        String email;

        try {
            json = new JSONObject(data);
            api_key = json.getString("api-key");
            email = json.getString("email");

            user.setApi_key(api_key);
            user.setUsername(username);
            user.setEmail(email);

            Intent myIntent = new Intent(LoginActivity.this, PrincipalActivity.class);
            startActivity(myIntent);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}

