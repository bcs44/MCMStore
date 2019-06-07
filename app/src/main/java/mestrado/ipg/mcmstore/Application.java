package mestrado.ipg.mcmstore;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.*;

import java.security.cert.X509Certificate;

public class Application {

    private static final String HMAC_SHA_ALGORITHM = "HmacSHA512";

    public static final void main(String[] args) {
        Calendar cal = Calendar.getInstance();
        long nonce = cal.getTimeInMillis();
        try {
            Map<String, String> headers = createHeaders("/user/121", null, nonce, "jesus", "secret",
                    "M0QyOEZCOEIxNURFNTJFRTk3NTEzMTYzQ0UzMUMzQjMzMDA2OTI4NQ==");
            String response = call("GET", "https://bd.ipg.pt:5500/ords/bda_1701887/user/121", null, nonce, headers, "");
            System.out.println(response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    public static String call(String method, String path,
                              String query, long nonce, Map<String, String> headers, String body) throws IOException, KeyManagementException, NoSuchAlgorithmException {
        if (query == null) {
            query = "";
        }
        query += "?nonce=" + nonce;
        allowSSLCertificates();
        URL url = new URL(path + query);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod(method);
        urlConnection.setReadTimeout(60 * 1000);
        urlConnection.setConnectTimeout(60 * 1000);
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setRequestProperty("Content-length", "" + body.getBytes().length);
        headers.entrySet().forEach(header ->{
            urlConnection.setRequestProperty(header.getKey(), header.getValue());
        });
        urlConnection.setDoOutput(true);
        InputStream is = urlConnection.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String output;
        String response = "";
        while ((output = br.readLine()) != null) {
            response += output;
        }
        return response;
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

    public static Map<String, String> createHeaders(String URI, String params, long nonce, String username, String password, String apiKey)
            throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        String sParamsNonce = "";
        if (params != null && !params.isEmpty()) {
            sParamsNonce = params + "&nonce=" + nonce;
        } else {
            sParamsNonce = "nonce=" + nonce;
        }

        // prepare path
        byte[] urlBytes = URI.getBytes(StandardCharsets.US_ASCII);
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
        return Base64.getEncoder().encodeToString(originalInput.toUpperCase().getBytes());
    }

    private static String calculateHMAC(byte[] data, String secret)
            throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] decoded_key = Base64.getDecoder().decode(secret.getBytes());
        SecretKeySpec signingKey = new SecretKeySpec(decoded_key, HMAC_SHA_ALGORITHM);
        Mac mac = Mac.getInstance(HMAC_SHA_ALGORITHM);
        mac.init(signingKey);
        return Base64.getEncoder().encodeToString(mac.doFinal(data));
    }

    private static byte[] hash256(String secret) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(secret.getBytes(StandardCharsets.UTF_8));
    }

    private static byte[] hash512(String secret) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        return digest.digest(secret.getBytes(StandardCharsets.UTF_8));
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