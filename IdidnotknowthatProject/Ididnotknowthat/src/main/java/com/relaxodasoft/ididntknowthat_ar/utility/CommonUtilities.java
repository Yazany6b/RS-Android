package com.relaxodasoft.ididntknowthat_ar.utility;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Pattern;

import com.relaxodasoft.ididntknowthat_ar.gcm.GCMMessage;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Patterns;
 
public final class CommonUtilities {
     
    // give your server registration url here"http://relaxodasoft.freeiz.com/OdaiProject2/register.php";//
    public static final String SERVER_URL_REGISTER = "http://ec2-54-68-66-205.us-west-2.compute.amazonaws.com/php-repo/GCM_SERVER/register.php";
    public static final String SERVER_URL_UNREGISTER = "http://ec2-54-68-66-205.us-west-2.compute.amazonaws.com/php-repo/GCM_SERVER/register.php/unregister.php"; 
    
    // Google project id
    public static final String SENDER_ID = "688629683349"; 
 
    /**
     * Tag used on log messages.
     */
    public static final String TAG = "AndroidHive GCM";
 
    public static final String DISPLAY_MESSAGE_ACTION =
            "com.relaxodasoft.ididntknowthat_ar.DISPLAY_MESSAGE";
 
    public static final String EXTRA_MESSAGE = "message";
 
    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    public static void displayMessage(Context context, int id) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE ,id );
        context.sendBroadcast(intent);
    }
    
    public static boolean isSharedPreferencesExist(String defualtPackage,String prefName){
    	File f = new File(
    			"/data/data/"+ defualtPackage +"/shared_prefs/"+ prefName +".xml");
    	
    			return f.exists();
    }
    
    public static String getRegion(Context context){
    	String locale="";
    	
		TelephonyManager teleMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		if(teleMgr != null)
			locale = teleMgr.getNetworkCountryIso();
		else
			locale = context.getResources().getConfiguration().locale.getCountry(); 
		
		if(locale != null && locale.length() < 2){
			
			locale = context.getResources().getConfiguration().locale.getCountry();
			if(locale == null)
				locale = "unknow";
			
		}else if( locale == null){
			locale = "unknow";
		}
		
		return locale;
    }
    
    public static String getUTF(String text){
		try {
			return URLEncoder.encode(text, "UTF-8"); 
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }
    
    public static String[] getEmails(Context context){
    	
    	Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+

		Account[] accounts = AccountManager.get(context).getAccounts();
		final ArrayList<String> emails = new ArrayList<String>();

		for (Account account : accounts) {
			if (emailPattern.matcher(account.name).matches()) {
				if(emails.indexOf(account.name.toLowerCase()) == -1)
					emails.add(account.name.toLowerCase());

			}
		}
		
		String[] arr = new String[emails.size()];
		emails.toArray(arr);
		
		return arr;
    }
    
    public static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Coult not get package name: " + e);
        }
    }

    /**
     * Checking for all possible internet providers
     * **/
    public static boolean isConnectingToInternet(Context _context){
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
          if (connectivity != null)
          {
              NetworkInfo[] info = connectivity.getAllNetworkInfo();
              if (info != null)
                  for (int i = 0; i < info.length; i++)
                      if (info[i].getState() == NetworkInfo.State.CONNECTED)
                      {
                          return true;
                      }
  
          }
          return false;
    }
    
    static void storeMessage(GCMMessage msg){
    	
    }
}
