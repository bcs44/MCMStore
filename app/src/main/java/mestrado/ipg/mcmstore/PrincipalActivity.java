package mestrado.ipg.mcmstore;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import mestrado.ipg.mcmstore.Sensors.ConfigSensors;
import mestrado.ipg.mcmstore.Sensors.SensorSwitch;
import mestrado.ipg.mcmstore.Services.BackgroundGetServiceAuth;


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
        fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });

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
        // se existe, criar nova activity, para mostrar o comunicado

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
                    communication[i] = new Communication();
                    communication[i].setCommunication_id(json.getString("communication_id"));
                    communication[i].setTitle(json.getString("title"));
                    communication[i].setDescription(json.getString("description"));
                    communication[i].setRegistry_date(json.getString("registry_date"));
                    communication[i].setUser_id(json.getString("user_id"));
                    communication[i].setConfirmation(json.getString("confirmation"));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        fabText = findViewById(R.id.fabText);
            fabText.setText(String.valueOf(communication.length));





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

        if (id == R.id.sens_conf) {
            Intent myIntent = new Intent(PrincipalActivity.this, ConfigSensors.class);
            startActivity(myIntent);

        } else if (id == R.id.sens_graph) {
            //graficos sensores

        } else if (id == R.id.sens_switch) {
            Intent myIntent = new Intent(PrincipalActivity.this, SensorSwitch.class);
            startActivity(myIntent);

        } else if (id == R.id.sens_info) {
            //Informação de Sensores

        } else if (id == R.id.cond_chat) {
            //Chat Condomonio

            Intent myIntent = new Intent(PrincipalActivity.this, ChatCondominio.class);
            startActivity(myIntent);

        } else if (id == R.id.cond_cal) {
            //Calendario de condominio
            Intent myIntent = new Intent(PrincipalActivity.this, CalendarActivity.class);
            startActivity(myIntent);

        } else if (id == R.id.cond_fich) {
            //Ficheiros de condominio

            Intent myIntent = new Intent(PrincipalActivity.this, Registar.class);
            startActivity(myIntent);

        } else if (id == R.id.cond_regras) {
            //Regras de condominio
        } else if (id == R.id.cond_manu) {
            Intent myIntent = new Intent(PrincipalActivity.this, PedidoManutencao.class);
            startActivity(myIntent);
        } else if (id == R.id.cond_res) {
            //Reservas de espaço

            Intent myIntent = new Intent(PrincipalActivity.this, PedidoReserva.class);
            startActivity(myIntent);


        } else if (id == R.id.admin_marc) {
            Intent myIntent = new Intent(PrincipalActivity.this, MarcacaoAssembleia.class);
            startActivity(myIntent);
        } else if (id == R.id.admin_comuni) {
            //Comunicados de condominio


            Intent myIntent = new Intent(PrincipalActivity.this, Comunicados.class);
            startActivity(myIntent);

        } else if (id == R.id.admin_res) {
            //Reservas

            Intent myIntent = new Intent(PrincipalActivity.this, Reservas.class);
            startActivity(myIntent);


        } else if (id == R.id.admin_man) {
            //Reservas

            Intent myIntent = new Intent(PrincipalActivity.this, Manutencoes.class);
            startActivity(myIntent);

        } else if (id == R.id.admin_ficheiro) {
            Intent myIntent = new Intent(PrincipalActivity.this, Ficheiros.class);
            startActivity(myIntent);

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
