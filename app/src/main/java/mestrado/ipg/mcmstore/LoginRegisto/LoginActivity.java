package mestrado.ipg.mcmstore.LoginRegisto;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import mestrado.ipg.mcmstore.R;


public class LoginActivity extends AppCompatActivity{

    private EditText etUsername, etPassword;
    Button lgnButn, login_rgt;

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
                login(String.valueOf(etUsername.getText()), String.valueOf(etPassword   .getText()));
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



    }

    private void login(String userName, String password) {





    }


}

