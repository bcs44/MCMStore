package mestrado.ipg.mcmstore;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import mestrado.ipg.mcmstore.Administrador.Comunicados;
import mestrado.ipg.mcmstore.Administrador.Ficheiros;
import mestrado.ipg.mcmstore.Administrador.Manutencoes;
import mestrado.ipg.mcmstore.Administrador.Reservas;
import mestrado.ipg.mcmstore.Condominio.CalendarActivity;
import mestrado.ipg.mcmstore.Condominio.ChatCondominio;
import mestrado.ipg.mcmstore.Administrador.MarcacaoAssembleia;
import mestrado.ipg.mcmstore.Condominio.PedidoManutencao;
import mestrado.ipg.mcmstore.Condominio.PedidoReserva;
import mestrado.ipg.mcmstore.Globals.Communication;
import mestrado.ipg.mcmstore.Globals.User;
import mestrado.ipg.mcmstore.LoginRegisto.Registar;
import mestrado.ipg.mcmstore.Sensors.ChartTemperature;
import mestrado.ipg.mcmstore.Sensors.ConfigSensors;
import mestrado.ipg.mcmstore.Sensors.SensorSwitch;
import mestrado.ipg.mcmstore.Sensors.ShakeDetector;
import mestrado.ipg.mcmstore.Services.BackgroundGetServiceAuth;
import mestrado.ipg.mcmstore.Services.BackgroundPostServiceAuth;


public class PrincipalActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView emailET, usernameET;
    User user = User.getInstance();
    FloatingActionButton fab;
    TextView fabText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Comunicados>>
        getComunicados();
        registerReceiver();
        //Comunicados<<


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);

        emailET = headerView.findViewById(R.id.textView);
        usernameET = headerView.findViewById(R.id.username);
        usernameET.setText(user.getUsername());
        emailET.setText(user.getEmail());


    }

    private void getComunicados() {

        Intent intent = new Intent(PrincipalActivity.this, BackgroundGetServiceAuth.class);
        intent.putExtra("urlStrg", "https://bd.ipg.pt:5500/ords/bda_1701887/communicationpartaker/user/" + user.getUser_id());
        intent.putExtra("_uri", "/communicationpartaker/user/" + user.getUser_id());
        intent.putExtra("wherefrom", "getComunicadosPrincipalAct");
        startService(intent);
    }

    private void registerReceiver() {

        BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String data = intent.getStringExtra("data");
                String wherefrom = intent.getStringExtra("wherefrom");

                switch (wherefrom) {
                    case "getComunicadosPrincipalAct":
                        context.stopService(new Intent(context, BackgroundGetServiceAuth.class));
                        dealWithComunicados(data);
                        break;
                }
                intent.getBundleExtra("Location");
            }
        };

        LocalBroadcastManager.getInstance(PrincipalActivity.this).registerReceiver(
                mMessageReceiver, new IntentFilter("ServicePrincipalActvivity"));
    }

    private void dealWithComunicados(String data) {

        //verificar se existe.
        // se existe, mostrar o comunicado

        JSONObject json;
        JSONArray array;
        Communication[] communication = new Communication[0];

        try {
            json = new JSONObject(data);
            array = json.getJSONArray("response");
            communication = new Communication[array.length()];

            for (int i = 0; i < array.length(); ++i) {
                json = array.getJSONObject(i);
                if (json != null) {
                    if (json.getString("confirmation").equals("0")) {
                        communication[i] = new Communication();
                        communication[i].setCommunication_id(json.getString("communication_id"));
                        communication[i].setTitle(json.getString("title"));
                        communication[i].setDescription(json.getString("description"));
                        communication[i].setRegistry_date(json.getString("registry_date"));
                        communication[i].setUser_id(json.getString("user_id"));
                        communication[i].setConfirmation(json.getString("confirmation"));
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        fab = findViewById(R.id.fab);

        if (communication.length > 0) {
            Communication[] finalCommunication = communication;
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Tem " + finalCommunication.length + " comunicados por ler!", Snackbar.LENGTH_LONG)
                            .setAction("Abrir", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    abrirComunicados(finalCommunication);
                                }
                            }).show();
                }
            });
        } else {
            fab.hide();
        }
    }

    private void abrirComunicados(Communication[] finalCommunication) {


        for (int i = 0; i < finalCommunication.length; i++) {

            AlertDialog.Builder dialogo = new
                    AlertDialog.Builder(PrincipalActivity.this);
            dialogo.setTitle(finalCommunication[i].getTitle());
            dialogo.setMessage(finalCommunication[i].getDescription());
            dialogo.setNeutralButton("Confirmar Receção", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //confirmar
                    dialog.dismiss();
                }
            });
            dialogo.show();
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        Intent myIntent = null;
        if (id == R.id.sens_conf) {
            myIntent = new Intent(PrincipalActivity.this, ConfigSensors.class);
        } else if (id == R.id.sens_graph) {
            myIntent = new Intent(PrincipalActivity.this, ChartTemperature.class);
        } else if (id == R.id.sens_switch) {
            myIntent = new Intent(PrincipalActivity.this, SensorSwitch.class);
        } else if (id == R.id.sens_info) {
            //Informação de Sensores

        } else if (id == R.id.cond_chat) {
            myIntent = new Intent(PrincipalActivity.this, ChatCondominio.class);
        } else if (id == R.id.cond_cal) {
            myIntent = new Intent(PrincipalActivity.this, CalendarActivity.class);
        } else if (id == R.id.cond_fich) {
            myIntent = new Intent(PrincipalActivity.this, Registar.class);
        } else if (id == R.id.cond_regras) {
            //Regras de condominio

        } else if (id == R.id.cond_manu) {
            myIntent = new Intent(PrincipalActivity.this, PedidoManutencao.class);
        } else if (id == R.id.cond_res) {
            myIntent = new Intent(PrincipalActivity.this, PedidoReserva.class);
        } else if (id == R.id.admin_marc) {
            myIntent = new Intent(PrincipalActivity.this, MarcacaoAssembleia.class);
        } else if (id == R.id.admin_comuni) {
            myIntent = new Intent(PrincipalActivity.this, Comunicados.class);
        } else if (id == R.id.admin_res) {
            myIntent = new Intent(PrincipalActivity.this, Reservas.class);
        } else if (id == R.id.admin_man) {
            myIntent = new Intent(PrincipalActivity.this, Manutencoes.class);
        } else if (id == R.id.admin_ficheiro) {
            myIntent = new Intent(PrincipalActivity.this, Ficheiros.class);
        } else if (id == R.id.logout) {
            getSharedPreferences("Login", MODE_PRIVATE).edit().clear().commit();
            finishAffinity();
            System.exit(0);
        }
        if(myIntent != null) {
            startActivity(myIntent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
