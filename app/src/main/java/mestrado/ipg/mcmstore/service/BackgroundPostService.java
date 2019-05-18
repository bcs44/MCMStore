package mestrado.ipg.mcmstore.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class BackgroundPostService extends Service {
    public BackgroundPostService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
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

        HashMap<String, String> hashMap = (HashMap<String, String>)intent.getSerializableExtra("ParamsMAP");


        new sendPost().execute(hashMap);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Invoke background service onDestroy method.", Toast.LENGTH_LONG).show();
        Log.i("ccc", "onDestroy");
    }

    private class sendPost extends AsyncTask<HashMap, HashMap, HashMap> {

        @Override
        protected HashMap doInBackground(HashMap... args) {

            HashMap<String, String> hashMap = args[0];


            String stringURL = "";

            for(Map.Entry<String, String> entry : hashMap.entrySet()) {
                if (entry.getKey().equals("urlStr")) {
                    stringURL = entry.getValue();
                }
            }

            hashMap.remove("urlStr");

            disableHttpsVerify(null);
            BufferedReader bis = null;
            InputStream in = null;
            OutputStream out = null;
            try {
                URL url = new URL(stringURL);
                HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestMethod("POST");
                out = connection.getOutputStream();

                StringBuilder sb = new StringBuilder();

                for(Map.Entry<String, String> entry : hashMap.entrySet()) {
                    sb.append(entry.getKey());
                    sb.append('=');
                    sb.append(entry.getValue());
                    sb.append('&');
                }
                String str = sb.toString();
                byte[] data = str.substring(0, str.length() - 1).getBytes();
                out.write(data);

                connection.connect();
                in = connection.getInputStream();
                bis = new BufferedReader(new InputStreamReader(in));
                sb.setLength(0);
                while((str = bis.readLine()) != null) {
                    sb.append(str);
                }


                JSONObject jsonObj = new JSONObject(sb.toString());

                Iterator<String> iterator = jsonObj.keys();
                HashMap<String, String> map = new HashMap<>();

                while (iterator.hasNext()) {
                    String key = iterator.next();
                    String value = jsonObj.getString(key);
                    map.put(key, value);
                }

                return map;
            } catch (Exception e) {
                return hashMap;
            } finally {
                try {
                    if(bis != null) {
                        bis.close();
                    }
                    if(in != null) {
                        in.close();
                    }
                } catch (Exception x) {

                }
            }
        }

        @Override
        protected void onPostExecute(HashMap hashMap) {

            Log.i("onPostExecute", "onPostExecute FEITO");

            super.onPostExecute(hashMap);
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
