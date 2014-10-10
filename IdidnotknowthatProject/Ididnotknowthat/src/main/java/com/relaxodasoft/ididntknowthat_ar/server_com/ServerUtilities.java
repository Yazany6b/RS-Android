package com.relaxodasoft.ididntknowthat_ar.server_com;



import static com.relaxodasoft.ididntknowthat_ar.utility.CommonUtilities.SERVER_URL_REGISTER;
import static com.relaxodasoft.ididntknowthat_ar.utility.CommonUtilities.SERVER_URL_UNREGISTER;
import static com.relaxodasoft.ididntknowthat_ar.utility.CommonUtilities.TAG;
 
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
 
import android.app.ProgressDialog;
import android.content.Context;

import android.util.Log;
 
import com.google.android.gcm.GCMRegistrar;
import com.relaxodasoft.ididntknowthat_ar.R;
import com.relaxodasoft.ididntknowthat_ar.R.string;
 
 
public final class ServerUtilities {
    private static final int MAX_ATTEMPTS = 3;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();
   
    private static ProgressDialog pd = null;
    /**
     * Register this account/device pair within the server.
     *
     */
   public static boolean register(String name,String email,String region,String lang , String regId) {
        
        Map<String, String> params = new HashMap<String, String>();

        params.put("regId", regId);
        params.put("name", name);
    	params.put("email", email);
    	params.put("region", region);
    	params.put("lang", lang);
    	

        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
        // Once GCM returns a registration id, we need to register on our server
        // As the server might be down, we will retry it a couple
        // times.
        for (int i = 1; i <= MAX_ATTEMPTS; i++) {
            Log.d(TAG, "Attempt #" + i + " to register");
            try {
                displayMessage(context, context.getString(
                        R.string.server_registering, i, MAX_ATTEMPTS));
                post(SERVER_URL_REGISTER, params);
                
                return true;
                
            } catch (IOException e) {
                // Here we are simplifying and retrying on any error; in a real
                // application, it should retry only on unrecoverable errors
                // (like HTTP error code 503).
                Log.e(TAG, "Failed to register on attempt " + i + ":" + e);
                
                if (i == MAX_ATTEMPTS) {
                    break;
                }
                try {
                    Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                    // Activity finished before we complete - exit.
                    Log.d(TAG, "Thread interrupted: abort remaining retries!");
                    Thread.currentThread().interrupt();
                    
                    return false;
                }
                // increase backoff exponentially
                backoff *= 2;
            }
            
            
        }

        return false;
    }
    
    
   public static boolean updateRegister(String regId,String oldRegId) {
        Log.i(TAG, "update register from : " + oldRegId + " $$$ To " + regId);
        
        Map<String, String> params = new HashMap<String, String>();

        params.put("oldregId",oldRegId);
        params.put("regId", regId);
        
        
        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
        // Once GCM returns a registration id, we need to register on our server
        // As the server might be down, we will retry it a couple
        // times.
        for (int i = 1; i <= MAX_ATTEMPTS; i++) {
            Log.d(TAG, "Attempt #" + i + " to register");
            try {

                post(SERVER_URL_REGISTER, params);
                
                return true;
                
            } catch (IOException e) {
                // Here we are simplifying and retrying on any error; in a real
                // application, it should retry only on unrecoverable errors
                // (like HTTP error code 503).
                Log.e(TAG, "Failed to register on attempt " + i + ":" + e);
                
                if (i == MAX_ATTEMPTS) {
                    break;
                }
                try {
                    Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                    // Activity finished before we complete - exit.
                    Log.d(TAG, "Thread interrupted: abort remaining retries!");
                    Thread.currentThread().interrupt();
                    
                    return false;
                }
                // increase backoff exponentially
                backoff *= 2;
            }
            
        }
        
        return false;
        
    }
 
    /**
     * Unregister this account/device pair within the server.
     */
    public static void unregister(final Context context, final String regId) {
        Log.i(TAG, "unregistering device (regId = " + regId + ")");
        String serverUrl = SERVER_URL_UNREGISTER;
        Map<String, String> params = new HashMap<String, String>();
        params.put("regId", regId);
        try {
            post(serverUrl, params);
            GCMRegistrar.setRegisteredOnServer(context, false);
            String message = context.getString(R.string.server_unregistered);
            Log.i(TAG, message);
            //CommonUtilities.displayMessage(context, message);
        } catch (IOException e) {
            // At this point the device is unregistered from GCM, but still
            // registered in the server.
            // We could try to unregister again, but it is not necessary:
            // if the server tries to send a message to the device, it will get
            // a "NotRegistered" error message and should unregister the device.
            String message = context.getString(R.string.server_unregister_error,
                    e.getMessage());
            Log.d(TAG, message);
            //CommonUtilities.displayMessage(context, message);
        }
    }
 
    /**
     * Issue a POST request to the server.
     *
     * @param endpoint POST address.
     * @param params request parameters.
     *
     * @throws IOException propagated from POST.
     */
    private static void post(String endpoint, Map<String, String> params)
            throws IOException {    
         
        URL url;
        try {
            url = new URL(endpoint);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + endpoint);
        }
        
        Log.d(TAG, "Enter Post Method");
        
        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
        // constructs the POST body using the parameters
        while (iterator.hasNext()) {
            Entry<String, String> param = iterator.next();
            bodyBuilder.append(param.getKey()).append('=')
                    .append(param.getValue());
            if (iterator.hasNext()) {
                bodyBuilder.append('&');
            }
        }
        
        String body = bodyBuilder.toString();
        Log.v(TAG, "Posting '" + body + "' to " + url);
        byte[] bytes = body.getBytes();
        HttpURLConnection conn = null;
        try {
            Log.e("URL", "> " + url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=UTF-8");
            // post the request
            OutputStream out = conn.getOutputStream();
            out.write(bytes);
            out.flush();
            out.close();
            // handle the response
            int status = conn.getResponseCode();
            Log.v(TAG, "Respones Message : " + conn.getResponseMessage());
            if (status != 200) {
              throw new IOException("Post failed with error code " + status);
            }
            
        }
        finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
      }
}
