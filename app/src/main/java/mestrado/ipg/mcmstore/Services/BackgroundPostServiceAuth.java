package mestrado.ipg.mcmstore.Services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import mestrado.ipg.mcmstore.Globals.User;

public class BackgroundPostServiceAuth extends Service {
    private static final String HMAC_SHA_ALGORITHM = "HmacSHA512";
    public BackgroundPostServiceAuth() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        Log.i("ccc", "onBind");
        throw new UnsupportedOperationException("Not yet implemented");

    }

    @Override
    public void onCreate() {
        Log.i("ccc", "onCreate");
        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        HashMap<String, String> hashMap = (HashMap<String, String>)intent.getSerializableExtra("ParamsMAP");
        new sendPost().execute(hashMap);

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("ccc", "onDestroy");
    }

    public class sendPost extends  AsyncTask<HashMap, HashMap, HashMap> {

        @Override
        protected HashMap doInBackground(HashMap... args) {


            String username = "";
            String password= "";
            String apiKey = "";
            User user = User.getInstance();
            if(!user.getUsername().equals("")){
                username = user.getUsername();
                password = user.getPassword();
                apiKey = user.getApi_key();
            }

            HashMap<String, String> hashMap = args[0];

            String stringURL = "";
            String _uri = "";
            String wherefrom = "";


            for(Map.Entry<String, String> entry : hashMap.entrySet()) {
                if (entry.getKey().equals("urlStr")) {
                    stringURL = entry.getValue();
                }
                else if(entry.getKey().equals("wherefrom")) {
                    wherefrom = entry.getValue();
                }
                else if(entry.getKey().equals("_uri")) {
                    _uri = entry.getValue();
                }
            }

            hashMap.remove("urlStr");
            hashMap.remove("wherefrom");
            hashMap.remove("_uri");


            Calendar cal = Calendar.getInstance();
            long nonce = cal.getTimeInMillis();
            Map<String, String> headers = null;
            try {
                headers = createHeaders(_uri, null, nonce, username, password,
                        apiKey);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            String query = null;
            if (query == null) {
                query = "";
            }
            query += "?nonce=" + nonce;
            try {
                allowSSLCertificates();

                URL url = new URL(stringURL + query);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Accept", "*/*");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    headers.entrySet().forEach(header ->{
                        urlConnection.setRequestProperty(header.getKey(), header.getValue());
                    });
                }
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setReadTimeout(60 * 1000);
                urlConnection.setConnectTimeout(60 * 1000);

                BufferedReader bis = null;
                InputStream in = null;
                OutputStream out = null;
                out = urlConnection.getOutputStream();


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

                urlConnection.connect();
                in = urlConnection.getInputStream();
                bis = new BufferedReader(new InputStreamReader(in));
                sb.setLength(0);
                while((str = bis.readLine()) != null) {
                    sb.append(str);
                }

                Intent intent = null;
                if (wherefrom.equals("registo")) {
                    intent = new Intent("ServiceRegisto");
                }
                else if (wherefrom.equals("login")) {
                    intent = new Intent("ServiceLogin");
                }
                else if (wherefrom.equals("PostConfigSensors")) {
                    intent = new Intent("ServiceConfigSensors");
                }

                intent.putExtra("data", sb.toString());
                intent.putExtra("hashParams", hashMap);
                intent.putExtra("wherefrom", wherefrom);

                Bundle b = new Bundle();
                intent.putExtra("Location", b);
                LocalBroadcastManager.getInstance(BackgroundPostServiceAuth.this).sendBroadcast(intent);


            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            HashMap<String, String> params = new HashMap<>();

            return  params;
        }

        @Override
        protected void onPostExecute(HashMap hashMap) {
            super.onPostExecute(hashMap);

        }
    }

    private static void allowSSLCertificates() throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
            public void checkClientTrusted(X509Certificate[] certs, String authType) { }
            public void checkServerTrusted(X509Certificate[] certs, String authType) { }

        } };
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) { return true; }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
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


    public static Map<String, String> createHeaders(String URI, String params, long nonce, String username, String password, String apiKey)
            throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        String sParamsNonce = "";
        if (params != null && !params.isEmpty()) {
            sParamsNonce = params + "&nonce=" + nonce;
        } else {
            sParamsNonce = "nonce=" + nonce;
        }

        // prepare path
        byte[] urlBytes = new byte[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            urlBytes = URI.getBytes(StandardCharsets.US_ASCII);
        }
        byte[] paramsNonceBytes = hash256(nonce + sParamsNonce);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        outputStream.write(urlBytes);
        outputStream.write(paramsNonceBytes);
        byte[] finalArray = outputStream.toByteArray();

        // ket key username + password -> same database
        String secret = buildSecret(username, password);

        // calculate and prepare headers map
        String apiSign = calculateHMAC(finalArray, secret);
        Map<String, String> headers = new HashMap<>();
        headers.put("api-sign", apiSign);
        headers.put("api-key", apiKey);
        return headers;
    }

    public static String buildSecret(String username, String password) throws NoSuchAlgorithmException {
        byte[] encodedHash = hash256(username + password);
        String originalInput = bytesToHex(encodedHash);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Base64.getEncoder().encodeToString(originalInput.toUpperCase().getBytes());
        }
        else{
            return "";
        }
    }

    private static String calculateHMAC(byte[] data, String secret)
            throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] decoded_key = new byte[0];
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            decoded_key = Base64.getDecoder().decode(secret.getBytes());
        }
        SecretKeySpec signingKey = new SecretKeySpec(decoded_key, HMAC_SHA_ALGORITHM);
        Mac mac = Mac.getInstance(HMAC_SHA_ALGORITHM);
        mac.init(signingKey);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Base64.getEncoder().encodeToString(mac.doFinal(data));
        }
        else {
            return "";
        }
    }

    private static byte[] hash256(String secret) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return digest.digest(secret.getBytes(StandardCharsets.UTF_8));
        }
        else {

            return null;
        }
    }

    private static byte[] hash512(String secret) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return digest.digest(secret.getBytes(StandardCharsets.UTF_8));
        }
        else {
            return null;
        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }



}

