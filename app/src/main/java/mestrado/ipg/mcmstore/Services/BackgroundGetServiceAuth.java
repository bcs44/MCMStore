package mestrado.ipg.mcmstore.Services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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

public class BackgroundGetServiceAuth extends Service {
    private static final String HMAC_SHA_ALGORITHM = "HmacSHA512";

    public BackgroundGetServiceAuth() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String url = intent.getStringExtra("urlStrg");
        String _uri = intent.getStringExtra("_uri");
        String wherefrom = intent.getStringExtra("wherefrom");
        HashMap<String, String> params = new HashMap<>();

        params.put("url", url);
        params.put("_uri", _uri);
        params.put("wherefrom", wherefrom);

        if (wherefrom.equals("getSensorIDToConfSens")) {
            String sensorType = intent.getStringExtra("sensorType");
            params.put("sensorType", sensorType);
        }


        new BackgroundGetServiceAuth.sendGet().execute(params);


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("ccc", "onDestroy");
    }

    public class sendGet extends AsyncTask<HashMap<String, String>, HashMap, HashMap<String, String>> {

        @Override
        protected HashMap doInBackground(HashMap... args) {

            User user = User.getInstance();
            HashMap<String, String> hashMap = args[0];

            String stringURL = "";
            String _uri = "";
            String wherefrom = "";
            String sensorType = "";

            for (Map.Entry<String, String> entry : hashMap.entrySet()) {
                if (entry.getKey().equals("url")) {
                    stringURL = entry.getValue();
                } else if (entry.getKey().equals("wherefrom")) {
                    wherefrom = entry.getValue();
                } else if (entry.getKey().equals("_uri")) {
                    _uri = entry.getValue();
                } else if (entry.getKey().equals("sensorType")) {
                    sensorType = entry.getValue();
                }
            }


            Calendar cal = Calendar.getInstance();
            long nonce = cal.getTimeInMillis();
            Map<String, String> headers = null;
            try {
                headers = createHeaders(_uri, null, nonce, user.getUsername(), user.getPassword(),
                        user.getApi_key());

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }


            String query = null;
            if (query == null) {
                query = "";
            }
            query += "?nonce=" + nonce;

            HashMap<String, String> params = new HashMap<>();

            try {
                allowSSLCertificates();
                URL url = new URL(stringURL + query);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Accept", "*/*");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    headers.entrySet().forEach(header -> {
                        urlConnection.setRequestProperty(header.getKey(), header.getValue());
                    });
                }
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoOutput(false);
                urlConnection.setReadTimeout(60 * 1000);
                urlConnection.setConnectTimeout(60 * 1000);

                BufferedReader in = null;
                StringBuilder body;
                String inputLine;
                in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                body = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    body.append(inputLine);
                }

                params.put("data", body.toString());
                params.put("wherefrom", wherefrom);
                if (wherefrom.equals("getSensorIDToConfSens")) {
                    params.put("sensorType", sensorType);
                }

                return params;

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return params;
        }

        @Override
        protected void onPostExecute(HashMap<String, String> hashMap) {
            super.onPostExecute(hashMap);

            String data = "";
            String wherefrom = "";

            String sensorType = null;

            for (Map.Entry<String, String> entry : hashMap.entrySet()) {
                if (entry.getKey().equals("data")) {
                    data = entry.getValue();

                } else if (entry.getKey().equals("wherefrom")) {
                    wherefrom = entry.getValue();
                } else if (entry.getKey().equals("sensorType")) {
                    sensorType = entry.getValue();
                }
            }

            Intent intent = null;

            if (wherefrom.equals("getPlacesToConfSens") || wherefrom.equals("getSensorIDToConfSens")) {
                intent = new Intent("ServiceConfigSensors");
                if (wherefrom.equals("getSensorIDToConfSens")) {
                    intent.putExtra("sensorType", sensorType);
                }
            } else if (wherefrom.equals("getPlacesToSensorSwitch") || wherefrom.equals("getActiveSensors")) {
                intent = new Intent("ServiceSensorSwitch");
            }
            else if (wherefrom.equals("getPlacesToPedidoReserva")) {
                intent = new Intent("ServicePedidoReserva");
            }
            else if (wherefrom.equals("getPlacesToMarcAssembleia")) {
                intent = new Intent("ServiceMarcAssembleia");
            }
            else if (wherefrom.equals("getMeetingsToCalendar")) {
                intent = new Intent("ServiceCalendar");
            }

            intent.putExtra("data", data);
            intent.putExtra("wherefrom", wherefrom);
            Bundle b = new Bundle();
            intent.putExtra("Location", b);
            LocalBroadcastManager.getInstance(BackgroundGetServiceAuth.this).sendBroadcast(intent);
        }
    }

    private static void allowSSLCertificates() throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }

        }};
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
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
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
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
        } else {
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
        } else {
            return "";
        }
    }

    private static byte[] hash256(String secret) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return digest.digest(secret.getBytes(StandardCharsets.UTF_8));
        } else {

            return null;
        }
    }

    private static byte[] hash512(String secret) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return digest.digest(secret.getBytes(StandardCharsets.UTF_8));
        } else {
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
