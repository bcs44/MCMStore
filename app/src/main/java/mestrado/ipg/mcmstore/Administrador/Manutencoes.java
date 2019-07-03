package mestrado.ipg.mcmstore.Administrador;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import mestrado.ipg.mcmstore.Globals.Manutencao;
import mestrado.ipg.mcmstore.Globals.User;
import mestrado.ipg.mcmstore.PrincipalActivity;
import mestrado.ipg.mcmstore.R;
import mestrado.ipg.mcmstore.Services.BackgroundGetServiceAuth;

public class Manutencoes extends AppCompatActivity {

    EditText dateET;
    TextView DescET, LocalET, UserET;
    User user = User.getInstance();
    int manutencao = 0;
    Manutencao[] manutencaos = new Manutencao[0];
    Button aceitar, rejeitar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manutencoes);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Manutencoes.this, PrincipalActivity.class);
                startActivity(intent);
            }
        });

        dateET = findViewById(R.id.date);
        DescET = findViewById(R.id.txtDesc);
        LocalET = findViewById(R.id.txtLocal);
        UserET = findViewById(R.id.txtUsername);
        aceitar = findViewById(R.id.btnAceitar);
        rejeitar = findViewById(R.id.btnRejeitar);

        aceitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manutencao = manutencao + 1;
                dealWithEditText();
            }
        });

        rejeitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manutencao = manutencao + 1;
                dealWithEditText();
            }
        });


        registerReceiver();
        getManutencoes();
    }

    private void getManutencoes() {

        Intent intent = new Intent(Manutencoes.this, BackgroundGetServiceAuth.class);
        intent.putExtra("urlStrg", "https://bd.ipg.pt:5500/ords/bda_1701887/maintenance/townhouse/" + user.getTownhouse_id());
        intent.putExtra("_uri", "/maintenance/townhouse/" + user.getTownhouse_id());
        intent.putExtra("wherefrom", "getManutencao");
        startService(intent);

    }

    private void registerReceiver() {

        BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String data = intent.getStringExtra("data");
                String wherefrom = intent.getStringExtra("wherefrom");

                switch (wherefrom) {
                    case "getManutencao":
                        context.stopService(new Intent(context, BackgroundGetServiceAuth.class));
                        dealWithManut(data);
                        break;
                }
                intent.getBundleExtra("Location");
            }
        };

        LocalBroadcastManager.getInstance(Manutencoes.this).registerReceiver(
                mMessageReceiver, new IntentFilter("ServiceManutencoesAdmin"));
    }

    private void dealWithManut(String data) {

        JSONObject json;
        JSONArray array;


        try {
            json = new JSONObject(data);
            array = json.getJSONArray("response");
            manutencaos = new Manutencao[array.length()];

            for (int i = 0; i < array.length(); ++i) {
                json = array.getJSONObject(i);
                if (json != null) {
                    manutencaos[i] = new Manutencao();
                    manutencaos[i].setMaintenance_id(json.getString("maintenance_id"));
                    manutencaos[i].setMaintenance_date(json.getString("maintenance_date"));
                    manutencaos[i].setDescription(json.getString("description"));
                    manutencaos[i].setPlace_id(json.getString("place_id"));
                    manutencaos[i].setAddress(json.getString("address"));
                    manutencaos[i].setFloor(json.getString("floor"));
                    manutencaos[i].setDoor(json.getString("door"));
                    manutencaos[i].setUser_id(json.getString("user_id"));
                    manutencaos[i].setUsername(json.getString("username"));
                    manutencaos[i].setEmail(json.getString("email"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        dealWithEditText();

    }

    private void dealWithEditText() {

        dateET.setText(manutencaos[manutencao].getMaintenance_date());
        DescET.setText(manutencaos[manutencao].getDescription());
        LocalET.setText(manutencaos[manutencao].getPlace_id());
        UserET.setText(manutencaos[manutencao].getUsername());

    }

}
