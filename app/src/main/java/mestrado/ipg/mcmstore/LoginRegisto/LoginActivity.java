package mestrado.ipg.mcmstore.LoginRegisto;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import mestrado.ipg.mcmstore.Administrador.Reservas;
import mestrado.ipg.mcmstore.Globals.User;
import mestrado.ipg.mcmstore.PrincipalActivity;
import mestrado.ipg.mcmstore.R;
import mestrado.ipg.mcmstore.Services.BackgroundPostServiceAuth;
import mestrado.ipg.mcmstore.Services.BackgroundGetService;


public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    Button lgnButn, login_rgt;
    User user = User.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        lookForCredentials();

        etUsername = findViewById(R.id.user);
        etPassword = findViewById(R.id.password);
        lgnButn = findViewById(R.id.login_btn);
        login_rgt = findViewById(R.id.login_rgt);

        lgnButn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                login(String.valueOf(etUsername.getText()), String.valueOf(etPassword.getText()));
                Toast.makeText(LoginActivity.this, "Username = " + String.valueOf(etUsername.getText()) + "Password=" + String.valueOf(etPassword.getText()), Toast.LENGTH_LONG).show();
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

        HashMap<String, String> params = new HashMap<>();
        params.put("urlStr", "https://bd.ipg.pt:5500/ords/bda_1701887/user/login");
        params.put("_uri", "/user/login");
        params.put("wherefrom", "login");
        params.put("username", username);
        params.put("password", password);

        Intent intent = new Intent(LoginActivity.this, BackgroundPostServiceAuth.class);
        intent.putExtra("ParamsMAP", params);
        startService(intent);
    }

    private void registerReceiver() {

        BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String data = intent.getStringExtra("data");
                HashMap<String, String> hashParams = (HashMap<String, String>) intent.getSerializableExtra("hashParams");
                String username = null;
                String password = null;

                for (Map.Entry<String, String> entry : hashParams.entrySet()) {
                    if (entry.getKey().equals("username")) {
                        username = entry.getValue();
                    } else if (entry.getKey().equals("password")) {
                        password = entry.getValue();
                    }
                }

                loginTerminado(data, username, password);
                context.stopService(new Intent(context, BackgroundGetService.class));
                intent.getBundleExtra("Location");
            }
        };

        LocalBroadcastManager.getInstance(LoginActivity.this).registerReceiver(
                mMessageReceiver, new IntentFilter("ServiceLogin"));
    }

    private void loginTerminado(String data, String username, String password) {
        JSONObject json;
        String api_key;
        String email;
        String townhouse_id;
        String user_id;

        try {
            json = new JSONObject(data);
            api_key = json.getString("api-key");
            email = json.getString("email");
            townhouse_id = json.getString("townhouse_id");
            user_id = json.getString("user_id");
            if(api_key != null && email != null && townhouse_id != null && user_id != null) {
                user.setApi_key(api_key);
                user.setUser_id(user_id);
                user.setUsername(username);
                user.setEmail(email);
                user.setPassword(password);
                user.setTownhouse_id(townhouse_id);

                saveCredentials();

                Intent myIntent = new Intent(LoginActivity.this, PrincipalActivity.class);
                startActivity(myIntent);
            } else {
                Toast.makeText(this, "Login não foi concluido com sucesso.", Toast.LENGTH_LONG);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveCredentials() {
        SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("api-key", user.getApi_key());
        ed.putString("email", user.getEmail());
        ed.putString("townhouse_id", user.getTownhouse_id());
        ed.putString("user_id", user.getUser_id());
        ed.putString("username", user.getUsername());
        ed.putString("password", user.getPassword());
        ed.commit();
    }

    private void lookForCredentials() {
        SharedPreferences options = this.getSharedPreferences("Login", MODE_PRIVATE);
        if (options != null) {
            String apiKey = options.getString("api-key", null);
            String email = options.getString("email", null);
            String townhouseId = options.getString("townhouse_id", null);
            String userId = options.getString("user_id", null);
            String userName = options.getString("username", null);
            String password = options.getString("password", null);
            boolean condition = true;
            if (apiKey == null || email == null || townhouseId == null ||
                    userId == null || userName == null || password == null) {
                return;
            }
            user.setApi_key(apiKey);
            user.setUser_id(userId);
            user.setUsername(userName);
            user.setEmail(email);
            user.setPassword(password);
            user.setTownhouse_id(townhouseId);
            Intent myIntent = new Intent(LoginActivity.this, PrincipalActivity.class);
            startActivity(myIntent);
            finish();
        }
    }
}

