package mestrado.ipg.mcmstore.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

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

public class BackgroundGetService extends Service {

    public BackgroundGetService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.i("ccc", "onBind");
        throw new UnsupportedOperationException("Not yet implemented");

    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "Invoke background service onCreate method.", Toast.LENGTH_LONG).show();
        Log.i("ccc", "onCreate");
        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Invoke background service onStartCommand method.", Toast.LENGTH_LONG).show();

        String url =   intent.getStringExtra("urlStrg");
        String whereto =   intent.getStringExtra("whereto");
        String sensorType =   intent.getStringExtra("sensorType");

        new sendGet().execute(url, whereto, sensorType);
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Invoke background service onDestroy method.", Toast.LENGTH_LONG).show();
        Log.i("ccc", "onDestroy");
    }



    public class sendGet extends AsyncTask<String, String, HashMap<String, String>> {

        @Override
        protected HashMap<String, String> doInBackground(String... args) {


            String stringURL = args[0];
            String whereto = args[1];
            String sensorType = args[2];


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
                params.put("whereto", whereto);
                params.put("sensorType", sensorType);

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
            String whereto = "";
            String sensorType = "";

            for(Map.Entry<String, String> entry : hashMap.entrySet()) {
                if (entry.getKey().equals("data")) {
                    data = entry.getValue();

                }
                else if (entry.getKey().equals("whereto")) {
                    whereto = entry.getValue();
                }
                else if (entry.getKey().equals("sensorType")) {
                    sensorType = entry.getValue();
                }
            }

            Intent intent = new Intent("GetSevice");
            intent.putExtra("data", data);
            intent.putExtra("sensorType", sensorType);
            intent.putExtra("whereto", whereto);

            Bundle b = new Bundle();
            intent.putExtra("Location", b);
            LocalBroadcastManager.getInstance(BackgroundGetService.this).sendBroadcast(intent);

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
