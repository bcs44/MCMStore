package mestrado.ipg.mcmstore.Administrador;


import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import mestrado.ipg.mcmstore.Globals.FileGlobal;
import mestrado.ipg.mcmstore.Globals.User;
import mestrado.ipg.mcmstore.R;
import mestrado.ipg.mcmstore.Services.BackgroundPostServiceAuth;

public class Ficheiros extends AppCompatActivity {

    EditText et;
    User user = User.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ficheiros);

        et = findViewById(R.id.file);

        et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentPDF = new Intent(Intent.ACTION_GET_CONTENT);
                intentPDF.setType("application/pdf");
                intentPDF.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(intentPDF, "Select Picture"), 12);
            }
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 12 && resultCode == RESULT_OK && data != null) {

            Uri uri = data.getData();
            String base64String = convertFileToByteArray(uri);

            if (base64String != null) {
                Log.d("Assignment", "Base64 String : --> " + base64String);
                sendData(base64String);
            }

        }
    }

    private void sendData(String base64String) {
        FileGlobal fileGlobal = FileGlobal.getInstance();

        HashMap<String, String> params = new HashMap<>();
        String url = "https://bd.ipg.pt:5500/ords/bda_1701887/file/post";
        String _uri = "/file/post";
        params.put("urlStr", url);
        params.put("_uri", _uri);
        //  params.put("file_base64", base64String);
        //  params.put("file_type", "ATA");
        params.put("townhouse_id", user.getTownhouse_id());
        params.put("wherefrom", "postNewFile");

        fileGlobal.setBase64(base64String);
        fileGlobal.setType("ATA");

        new sendPost().execute(params);
    }

    private class sendPost extends AsyncTask<HashMap, HashMap, String> {

        @Override
        protected String doInBackground(HashMap... args) {

            HashMap<String, String> hashMap = args[0];
            Intent intent = new Intent(Ficheiros.this, BackgroundPostServiceAuth.class);
            intent.putExtra("ParamsMAP", hashMap);
            startService(intent);
            return "done";
        }
    }

    public String convertFileToByteArray(Uri uri) {
        byte[] byteArray = null;
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024 * 11];
            int bytesRead = 0;

            while ((bytesRead = inputStream.read(b)) != -1) {
                bos.write(b, 0, bytesRead);
            }

            byteArray = bos.toByteArray();

            Log.e("Byte array", ">" + byteArray);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return Base64.encodeToString(byteArray, Base64.NO_WRAP);
    }
}
