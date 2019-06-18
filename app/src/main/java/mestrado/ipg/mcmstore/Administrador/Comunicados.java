package mestrado.ipg.mcmstore.Administrador;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import mestrado.ipg.mcmstore.Globals.User;
import mestrado.ipg.mcmstore.R;
import mestrado.ipg.mcmstore.Services.BackgroundGetServiceAuth;
import mestrado.ipg.mcmstore.Services.BackgroundPostServiceAuth;

public class Comunicados extends AppCompatActivity {

    EditText tituloET, descET;
    Button enviarBtn;
    User user = User.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comunicados);

        tituloET = findViewById(R.id.titulo);
        descET = findViewById(R.id.desc);
        enviarBtn = findViewById(R.id.enviar);

        enviarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData(String.valueOf(tituloET.getText()), String.valueOf(descET.getText()));
            }
        });

        registerReceiver();
    }

    private void sendData(String title, String desc) {

        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        HashMap<String, String> params = new HashMap<>();
        String url = "https://bd.ipg.pt:5500/ords/bda_1701887/communication/insert";
        String _uri = "/communication/insert";
        params.put("urlStr", url);
        params.put("_uri", _uri);
        params.put("title", title);
        params.put("description", desc);
        params.put("registry_date", format.format(date));
        params.put("user_id", user.getUser_id());
        params.put("wherefrom", "postComunicado");

        new sendPost().execute(params);
    }

    private class sendPost extends AsyncTask<HashMap, HashMap, String> {

        @Override
        protected String doInBackground(HashMap... args) {

            HashMap<String, String> hashMap = args[0];
            Intent intent = new Intent(Comunicados.this, BackgroundPostServiceAuth.class);
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

                if (wherefrom.equals("postComunicado")) {
                    context.stopService(new Intent(context, BackgroundGetServiceAuth.class));

                    AlertDialog.Builder dialogo = new
                            AlertDialog.Builder(Comunicados.this);
                    dialogo.setTitle("Aviso");
                    dialogo.setMessage("Comunicado Criado");
                    dialogo.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    dialogo.show();
                }

                intent.getBundleExtra("Location");
                Log.d("1233", "BCR");
            }
        };

        LocalBroadcastManager.getInstance(Comunicados.this).registerReceiver(
                mMessageReceiver, new IntentFilter("ServiceComunicados"));
    }
}
