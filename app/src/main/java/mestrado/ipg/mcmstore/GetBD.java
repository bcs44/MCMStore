package mestrado.ipg.mcmstore;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import mestrado.ipg.mcmstore.Sensors.ConfigSensors;

public class GetBD extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //é Sempre recebido o URL e a Atividade para a qual vai, depois de feito o GET. Tambem a funcao para qual vai depois
        Intent intent = getIntent();
        String urlStrg = intent.getStringExtra("urlStrg");
        String activity = intent.getStringExtra("activity");
        String metodo = intent.getStringExtra("metodo");

        new sendGet().execute(urlStrg, activity, metodo);

    }


    public static class sendGet extends AsyncTask<String, String, HashMap<String, String>> {

        @Override
        protected HashMap<String, String> doInBackground(String... args) {


            String stringURL = args[0];
            //  String activity = args[1];
            //  String metodo = args[2];
            disableHttpsVerify(null);
            BufferedReader bis = null;
            InputStream in = null;
            HashMap<String, String> params = new HashMap<>();

            try {
                URL url = new URL(stringURL);
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setRequestMethod("GET");
                StringBuilder sb = new StringBuilder();
                String str = sb.toString();

                connection.connect();
                in = connection.getInputStream();
                bis = new BufferedReader(new InputStreamReader(in));
                sb.setLength(0);
                while ((str = bis.readLine()) != null) {
                    sb.append(str);
                }

                params.put("data", sb.toString());

                //    params.put("activity", activity);
                //   params.put("metodo", metodo);
                return params;

            } catch (Exception e) {
                return params;
            } finally {
                try {
                    if (bis != null) {
                        bis.close();
                    }
                    if (in != null) {
                        in.close();
                    }
                } catch (Exception x) {
                }
            }
        }

        @Override
        protected void onPostExecute(HashMap<String, String> hashMap) {
            super.onPostExecute(hashMap);

            String data = "";
            //    String activity = "";
            //   String metodo = "";

            for (Map.Entry<String, String> entry : hashMap.entrySet()) {
                if (entry.getKey().equals("data")) {
                    data = entry.getValue();
                }
           /*     else if(entry.getKey().equals("activity")){
                    activity = entry.getValue();
                }
                else if(entry.getKey().equals("metodo")){
                    metodo = entry.getValue();
                }*/
            }

            Class c = null;

            // c = Class.forName(activity);
            //  Intent myIntent = new Intent(GetBD.this, c);
            //myIntent.putExtra("data", data);
            // myIntent.putExtra("metodo", metodo);
            // startActivity(myIntent);

            new ConfigSensors().dealWithSpinners(data);


        }
    }

    private static void disableHttpsVerify(Object o) {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                    // Not implemented
                }

                @Override
                public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                    // Not implemented
                }
            }};
            SSLContext sc = SSLContext.getInstance("TLS");

            sc.init(null, trustAllCerts, new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            Log.d("disableHttpsVerify", e.toString());
        }

    }
}
