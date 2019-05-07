package mestrado.ipg.mcmstore;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


public class PrincipalActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer =findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.sens_conf) {
            // Configuração de Sensores
        } else if (id == R.id.sens_graph) {
            //graficos sensores

        } else if (id == R.id.sens_switch) {
            //Switch de sensores

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
        }
        else if (id == R.id.cond_fich) {
            //Ficheiros de condominio
        }
        else if (id == R.id.cond_regras) {
            //Regras de condominio
        }
        else if (id == R.id.cond_manu) {
            //Pedido de Manutenção
        }
        else if (id == R.id.cond_res) {
            //Reservas de espaço
        }
        else if (id == R.id.admin_marc) {
            Intent myIntent = new Intent(PrincipalActivity.this, MarcacaoAssembleia.class);
            startActivity(myIntent);
        }
        else if (id == R.id.admin_comuni) {
            //Comunicados de condominio
        }
        else if (id == R.id.admin_res) {
            //Reservas
        }
        else if (id == R.id.admin_ficheiro) {
            Intent myIntent = new Intent(PrincipalActivity.this, PostBD.class);
            startActivity(myIntent);

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
