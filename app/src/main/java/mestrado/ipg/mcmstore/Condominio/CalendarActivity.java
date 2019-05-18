package mestrado.ipg.mcmstore.Condominio;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import mestrado.ipg.mcmstore.R;


public class CalendarActivity extends AppCompatActivity {


   TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        textView = findViewById(R.id.output);

        String urlStr = "https://bd.ipg.pt:5500/ords/bda_1701887/access/accessbyuserid";


        new sendPost().execute(urlStr);

    }



    private class sendPost extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {

            String d = strings[0];
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("user_id", "");
            disableHttpsVerify(null);
            BufferedReader bis = null;
            InputStream in = null;
            OutputStream out = null;
            try {
                URL url = new URL(d);
                HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestMethod("POST");
                out = connection.getOutputStream();

                StringBuilder sb = new StringBuilder();
                for(Map.Entry<String, String> entry : params.entrySet()) {
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
                return sb.toString();
            } catch (Exception e) {
                return "";
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

