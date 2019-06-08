package mestrado.ipg.mcmstore.Administrador;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import mestrado.ipg.mcmstore.R;
import mestrado.ipg.mcmstore.Services.BackgroundPostServiceAuth;

public class Comunicados extends AppCompatActivity {


    EditText tituloET, descET;
    Button enviarBtn;

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
                sendToBD(String.valueOf(tituloET.getText()), String.valueOf(descET.getText()));
            }
        });

    }

    private void sendToBD(String title, String desc) {


        String url = "https://bd.ipg.pt:5500/ords/bda_1701887/place/insert";
        Intent intent = new Intent(Comunicados.this, BackgroundPostServiceAuth.class);
        intent.putExtra("urlStrg", url);
        intent.putExtra("DESCRIPTION", title);
        intent.putExtra("TITLE", desc);
        startService(intent);


    }
}
