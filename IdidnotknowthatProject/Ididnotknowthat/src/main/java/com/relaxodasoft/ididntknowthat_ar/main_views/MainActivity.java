package com.relaxodasoft.ididntknowthat_ar.main_views;

import static com.relaxodasoft.ididntknowthat_ar.utility.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.relaxodasoft.ididntknowthat_ar.utility.CommonUtilities.EXTRA_MESSAGE;
import static com.relaxodasoft.ididntknowthat_ar.utility.CommonUtilities.SENDER_ID;
import static com.relaxodasoft.ididntknowthat_ar.utility.CommonUtilities.TAG;

import static com.relaxodasoft.ididntknowthat_ar.utility.CommonUtilities.isConnectingToInternet;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;


import android.view.View;
import android.view.Window;

import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import com.google.android.gcm.GCMRegistrar;

import com.google.android.gms.gcm.GoogleCloudMessaging;

//import com.relaxodasoft.ididntknowthat_ar.ConnectionDetector;
import com.relaxodasoft.ididntknowthat_ar.R;
import com.relaxodasoft.ididntknowthat_ar.WakeLocker;
import com.relaxodasoft.ididntknowthat_ar.R.id;
import com.relaxodasoft.ididntknowthat_ar.R.layout;
import com.relaxodasoft.ididntknowthat_ar.R.string;
import com.relaxodasoft.ididntknowthat_ar.db.DatabaseHandler;
import com.relaxodasoft.ididntknowthat_ar.gcm.GCMMessage;
import com.relaxodasoft.ididntknowthat_ar.gcm.GCMMessageAdapter;
import com.relaxodasoft.ididntknowthat_ar.server_com.ServerUtilities;
import com.relaxodasoft.ididntknowthat_ar.utility.CommonUtilities;

public class MainActivity extends Activity {


	// Alert dialog manager
	AlertDialogManager alert = new AlertDialogManager();

	// Connection detector
	//ConnectionDetector cd;

	//public static String FAIL_UPDATE_REGID = "failUpdateRegId";
	//public static String REG_ID = "regId";
	
	/**
     * Default lifespan (7 days) of a reservation until it is considered expired.
     */
	public static final long REGISTRATION_EXPIRY_TIME_MS = 1000 * 3600 * 24 * 7;

	private EditText searchText;

	private GCMMessageAdapter adapter;

	private String gcmSharedPrefName = "com.google.android.gcm";
	
	private GoogleCloudMessaging gcm;

	String regId;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		// start welcome activity
		startActivityForResult(new Intent(this, WelcomeActivity.class), 1);//change it back to 1 
		
		gcm = GoogleCloudMessaging.getInstance(this);
		
		
		// handle text change in the search text
		searchText = (EditText) findViewById(R.id.searchEditText);
		
		searchText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				DatabaseHandler db = new DatabaseHandler(MainActivity.this);
				ArrayList<GCMMessage> msgs = db.getGCMMessageStartWithTitle(s
						.toString());
				try {// bug here
					if (adapter != null) {
						adapter.clear();

						for (GCMMessage gcmMessage : msgs) {
							adapter.add(gcmMessage);
						}
						adapter.notifyDataSetChanged();
					}
				} catch (NullPointerException exp) {
					exp.printStackTrace();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
		
		ListView lv = (ListView) findViewById(R.id.listViewNews);

		adapter = new GCMMessageAdapter(this, R.layout.listview_item_row,
				new ArrayList<GCMMessage>());
		
		View header = (View) getLayoutInflater().inflate(
				R.layout.listview_header_row, null);
		lv.addHeaderView(header);
		lv.setAdapter(adapter);

	}

	/**
	 * Receiving push messages from gcm 
	 * */
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int id = intent.getIntExtra(EXTRA_MESSAGE, -1);

			DatabaseHandler db = new DatabaseHandler(MainActivity.this);
			GCMMessage newMessage = db.getGCMMessageById(id);

			// Waking up mobile if it is sleeping
			WakeLocker.acquire(getApplicationContext());

			/**
			 * Take appropriate action on this message depending upon your app
			 * requirement For now i am just displaying it on the screen
			 * */

			// Showing received message
			if(adapter != null){
				adapter.add(newMessage);
				adapter.notifyDataSetChanged();
			}else{
				Log.d(TAG, "Null adapter");
			}
			// fillListView();
			// Releasing wake lock
			WakeLocker.release();
		}
	};

	//handle activity destroy 
	@Override
	protected void onDestroy() {
		try {
			unregisterReceiver(mHandleMessageReceiver);
			GCMRegistrar.onDestroy(this);
		} catch (Exception e) {
			Log.e("UnRegister Receiver Error", "> " + e.getMessage());
		}
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1) {// prepare GCM

			//cd = new ConnectionDetector(this);
			// Check if Internet present
			if (!GCMRegistrar.isRegisteredOnServer(this)
					&& !isConnectingToInternet(this)) {
				// Internet Connection is not present
				alert.showAlertDialog(MainActivity.this,
						getString(R.string.alert_title_network_error_ar),
						getString(R.string.alert_message_network_error_ar),
						false);
				// stop executing code by return
				return;
			}

			prepareGCMService();

		} else if (requestCode == 2) {// not register in both our server and GCM

			if (resultCode == RESULT_OK) {
				
				registerBackground();
				
			} else if (resultCode == RESULT_CANCELED) {
				finish();
			}

		}

	}

	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private void prepareGCMService() {

/*
		//case on update from version 1.1 to higher
		if (GCMRegistrar.isRegisteredOnServer(this)
				&& !CommonUtilities.isSharedPreferencesExist(getPackageName(), RegisterActivity.SHARED_NAME)) {

			Log.d(TAG, "Update from version 1.1 to higher");
			
			SharedPreferences sharedGCM = getSharedPreferences(gcmSharedPrefName,
					MODE_PRIVATE);
			SharedPreferences sharedREG = getSharedPreferences(RegisterActivity.SHARED_NAME,
					MODE_PRIVATE);
			Editor editor = sharedREG.edit();
			
			editor.putBoolean(RegisterActivity.SHARED_KEY_REGISTERED_ON_SERVER , sharedGCM.getBoolean("onServer", false));
			editor.putString(RegisterActivity.SHARED_KEY_OLD_REG_ID, sharedGCM.getString("regId", ""));
			editor.putLong(RegisterActivity.SHARED_KEY_ON_SERVER_EXPIRATION_TIME, sharedGCM.getLong("onServerExpirationTime",0));
			editor.putInt(RegisterActivity.SHARED_KEY_APP_VERSION, sharedGCM.getInt("appVersion", 0));
			
			editor.commit();
		}
		*/

		Log.d(TAG, "GCM Check Device");
		// Make sure the device has the proper dependencies.
		GCMRegistrar.checkDevice(this);
		
		Log.d(TAG, "Register broadcast reciver");
		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				DISPLAY_MESSAGE_ACTION));

		Log.d(TAG, "Get GCM registration id");
		// Get GCM registration id
		regId = getRegistrationId(this);

		Log.d(TAG, "GCM = return " + regId);

		// Check if regid already presents
		if (regId.isEmpty()) {
			// This device is not registered in the GCM
			// so i will ask for the registration information
			if (GCMRegistrar.isRegisteredOnServer(this)) {
				updateRegisterationBackground();
			} else {
				Intent registrationIntent = new Intent(this,
						RegisterActivity.class);

				startActivityForResult(registrationIntent, 2);
			}

		} else {
			// Device is already registered on GCM
			if (GCMRegistrar.isRegisteredOnServer(this)) {
				Log.d(TAG, "Already registered with GCM");
					fillListView();	
					loadAds();

			} else {
				registerBackground();
			}
		}

		// if every this is ok, get all the news
		
		//return regId;
	}

	/**
	 * Gets the current registration id for application on GCM service.
	 * <p>
	 * If result is empty, the registration has failed.
	 *
	 * @return registration id, or empty string if the registration is not
	 *         complete.
	 */
	private String getRegistrationId(Context context) {
	    final SharedPreferences prefs = getSharedPreferences(RegisterActivity.SHARED_NAME,
				MODE_PRIVATE);
	    
	    String registrationId = prefs.getString(RegisterActivity.SHARED_KEY_OLD_REG_ID, "");
	    Log.v(TAG, registrationId);
	    
	    if (registrationId.length() == 0) {
	        Log.v(TAG, "Registration not found.");
	        return registrationId;
	    }
	    
	    // check if app was updated; if so, it must clear registration id to
	    // avoid a race condition if GCM sends a message
	    int registeredVersion = prefs.getInt(RegisterActivity.SHARED_KEY_APP_VERSION, Integer.MIN_VALUE);
	    int currentVersion = CommonUtilities.getAppVersion(context);
	    
	    if (registeredVersion != currentVersion || isRegistrationExpired()) {
	        Log.v(TAG, "App version changed or registration expired.");
	        return "";
	    }
	    
	    return registrationId;
	}



	private boolean isRegistrationExpired() {
	    final SharedPreferences prefs = getSharedPreferences(RegisterActivity.SHARED_NAME,
				MODE_PRIVATE);
	    // checks if the information is not stale
	    long expirationTime =
	            prefs.getLong(RegisterActivity.SHARED_KEY_ON_SERVER_EXPIRATION_TIME, -1);
	    return System.currentTimeMillis() > expirationTime;
	}
	
	
	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration id, app versionCode, and expiration time in the 
	 * application's shared preferences.
	 */
	@SuppressWarnings("unchecked")
	private void registerBackground() {
	    new AsyncTask() {
	    	ProgressDialog pd;
			@Override
			protected Object doInBackground(Object... arg0) {

	            try {
	            	
	            	MainActivity.this.runOnUiThread(new Runnable() {
	        			
	        			@Override
	        			public void run() {
	        				// TODO Auto-generated method stub
	        				pd = new ProgressDialog(MainActivity.this);
	        		        pd.setTitle(MainActivity.this.getString(R.string.registration_waiting_title_ar));
	        		        pd.setMessage(MainActivity.this.getString(R.string.registration_waiting_message_ar));
	        		        pd.setCancelable(false);
	        		        pd.setIndeterminate(true);
	        		        
	        		        pd.show();
	        			}
	        		});
	            	
	            	SharedPreferences prefs = getSharedPreferences(RegisterActivity.SHARED_NAME,
	        				MODE_PRIVATE);
	            	
	            	String name = prefs.getString(RegisterActivity.SHARED_KEY_NAME, "");
	            	String email = prefs.getString(RegisterActivity.SHARED_KEY_EMAIL, "");
	            	String region = prefs.getString(RegisterActivity.SHARED_KEY_REGION, "");
	            	String lang = prefs.getString(RegisterActivity.SHARED_KEY_LANG, "");
	            	
	            	name = CommonUtilities.getUTF(name);
	            	
	            	if (gcm == null) {
	                    gcm = GoogleCloudMessaging.getInstance(MainActivity.this);
	                }

	                regId = gcm.register(SENDER_ID);
	                
	                if( ServerUtilities.register(name, email, region, lang, regId)){
	                	GCMRegistrar.setRegisteredOnServer(MainActivity.this, true);
	                	setRegistrationId(MainActivity.this, regId);
	                	return true;
	                }
	                
	            } catch (IOException ex) {
	                ex.printStackTrace();
	            }
	            
	            return false;
			}
			
			protected void onPostExecute(Object result) {
				boolean done = (Boolean) result;
				
				if(done){
					MainActivity.this.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							pd.dismiss();

			                new AlertDialogManager().showAlertDialogWithoutKillActivity(MainActivity.this,
			                		null, MainActivity.this.getString(R.string.registration_success_title_ar), null);
			                //fillListView();
							loadAds();
						}
					});
					
					
				}else{
					
					
					MainActivity.this.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							pd.dismiss();

							new AlertDialogManager().showAlertDialog(MainActivity.this,
			        			null, MainActivity.this.getString(R.string.registration_fail_title_ar), null);
						}
					});
					
				}
			};
	    }.execute(null, null, null);
	}
	
	@SuppressWarnings("unchecked")
	private void updateRegisterationBackground() {
	    new AsyncTask() {
	    	ProgressDialog pd;
	    	
			@Override
			protected Object doInBackground(Object... arg0) {
				
	            try {
	            	
	            	MainActivity.this.runOnUiThread(new Runnable() {
	        			
	        			@Override
	        			public void run() {
	        				// TODO Auto-generated method stub
	        				pd = new ProgressDialog(MainActivity.this);
	        		        pd.setTitle(MainActivity.this.getString(R.string.registration_update_waiting_title_ar));
	        		        pd.setMessage(MainActivity.this.getString(R.string.registration_waiting_message_ar));
	        		        pd.setCancelable(false);
	        		        pd.setIndeterminate(true);
	        		        
	        		        pd.show();
	        			}
	        		});
	            	
	            	
	                if (gcm == null) {
	                    gcm = GoogleCloudMessaging.getInstance(MainActivity.this);
	                }
	                regId = gcm.register(SENDER_ID);
	                
	                String oldRegId = getOldRegId();
	                
	                if(!regId.equals(oldRegId)){
	                	if(ServerUtilities.updateRegister(regId, oldRegId)){
	                		setRegistrationId(MainActivity.this, regId);
	                		return true;
	                	}
	                }else{
	                	setRegistrationId(MainActivity.this, regId);
	                	return true;
	                }

	                
	            } catch (IOException ex) {
	                ex.printStackTrace();
	            }
	            return false;
			}
			
			protected void onPostExecute(Object result) {
				boolean done = (Boolean)result;
				if(done){
					MainActivity.this.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							pd.dismiss();

			                new AlertDialogManager().showAlertDialogWithoutKillActivity(MainActivity.this,
			                		null, MainActivity.this.getString(R.string.registration_update_success_title_ar), null);
						}
					});
					fillListView();
					loadAds();
				}else{
					
					
					MainActivity.this.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							pd.dismiss();

							new AlertDialogManager().showAlertDialog(MainActivity.this,
			        			null, MainActivity.this.getString(R.string.registration_update_fail_title_ar), null);
						}
					});
					
				}
			};
	    }.execute(null, null, null);
	}
	
	/**
	 * Stores the registration id, app versionCode, and expiration time in the
	 * application's {@code SharedPreferences}.
	 *
	 * @param context application's context.
	 * @param regId registration id
	 */
	private void setRegistrationId(Context context, String regId) {
	    final SharedPreferences prefs = getSharedPreferences(RegisterActivity.SHARED_NAME,
				MODE_PRIVATE);
	    int appVersion = CommonUtilities.getAppVersion(context);
	    
	    Log.v(TAG, "Saving regId on app version " + appVersion);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString(RegisterActivity.SHARED_KEY_OLD_REG_ID, regId);
	    editor.putInt(RegisterActivity.SHARED_KEY_APP_VERSION, appVersion);
	    long expirationTime = System.currentTimeMillis() + REGISTRATION_EXPIRY_TIME_MS;

	    Log.v(TAG, "Setting registration expiry time to " +
	            new Timestamp(expirationTime));
	    
	    editor.putLong(RegisterActivity.SHARED_KEY_ON_SERVER_EXPIRATION_TIME, expirationTime);
	    editor.commit();
	}
	
	public String getOldRegId(){
		SharedPreferences prefs = getSharedPreferences(RegisterActivity.SHARED_NAME,
				MODE_PRIVATE);
		
		return prefs.getString(RegisterActivity.SHARED_KEY_OLD_REG_ID, "");
	}
	
	private void fillListView() {
		// TODO Auto-generated method stub
		try {
			if (adapter != null) {

				adapter.clear();
				
				DatabaseHandler db = new DatabaseHandler(this);
				ArrayList<GCMMessage> msgs = db.getAllGCMMessages();
				
				for (GCMMessage gcmMessage : msgs) {
					adapter.add(gcmMessage);
				}
				adapter.notifyDataSetChanged();
			}

			
		} catch (OutOfMemoryError exp) {
			new AlertDialogManager().showAlertDialog(this, null,
					getString(R.string.out_of_memory_exception_ar), null);
		}

	}

	public void loadAds() {

		AdView adView = new AdView(this, AdSize.BANNER,
				getString(R.string.admob_id));
		LinearLayout layout = (LinearLayout) findViewById(R.id.imgBanner);
		layout.addView(adView);

		AdRequest adRequest = new AdRequest();
		// adRequest.setTesting(true);
		// Start loading the ad in the background.
		adView.loadAd(adRequest);
	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) { // TODO
	 * Auto-generated method stub MenuInflater menuInflater = getMenuInflater();
	 * menuInflater.inflate(R.layout.menu, menu); return true; }
	 */
	/*
	 * @Override public boolean onOptionsItemSelected(MenuItem item) { // TODO
	 * Auto-generated method stub
	 * 
	 * switch(item.getItemId()){ case R.id.menu_remove_btn :{ new
	 * AlertDialog.Builder(this) .setTitle("Delete all items")
	 * .setMessage("Are you sure ??") .setPositiveButton("Yes", new
	 * OnClickListener() {
	 * 
	 * @Override public void onClick(DialogInterface arg0, int arg1) { // TODO
	 * Auto-generated method stub
	 * 
	 * } }).setNegativeButton("No", new OnClickListener() {
	 * 
	 * @Override public void onClick(DialogInterface dialog, int which) { //
	 * TODO Auto-generated method stub
	 * 
	 * } }).create().show();
	 * 
	 * 
	 * 
	 * return true;
	 * 
	 * } default : return super.onOptionsItemSelected(item); }
	 * 
	 * 
	 * }
	 */

}