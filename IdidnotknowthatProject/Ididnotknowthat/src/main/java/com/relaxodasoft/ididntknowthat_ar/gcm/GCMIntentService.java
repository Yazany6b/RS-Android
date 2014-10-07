package com.relaxodasoft.ididntknowthat_ar.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.relaxodasoft.ididntknowthat_ar.R;
import com.relaxodasoft.ididntknowthat_ar.db.DatabaseHandler;
import com.relaxodasoft.ididntknowthat_ar.main_views.NotificationViewerActivity;
import com.relaxodasoft.ididntknowthat_ar.server_com.ServerUtilities;

import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import static com.relaxodasoft.ididntknowthat_ar.utility.CommonUtilities.SENDER_ID;
import static com.relaxodasoft.ididntknowthat_ar.utility.CommonUtilities.displayMessage;

public class GCMIntentService extends GCMBaseIntentService {
 
    private static final String TAG = "GCMIntentService";
    public static final String MESSAGE = "message";
    public GCMIntentService() {
        super(SENDER_ID);
    }
 
    /**
     * Method called on device registered
     **/
    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "Device registered: regId = " + registrationId);

        //ServerUtilities.register(context, registrationId);
    }
 
    /**
     * Method called on device un registred
     * */
    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
        //displayMessage(context, getString(R.string.gcm_unregistered));
        ServerUtilities.unregister(context, registrationId);
    }
 
    /**
     * Method called on Receiving a new message
     * */
    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.i(TAG, "Received message");
        String message = intent.getExtras().getString("price");
        
        GCMMessage gcmMsg = null;
        
		try {
			gcmMsg = GCMMessage.ToGCMMessage(message);
			
			//store message and set some info into msg
	        //storeMessage(gcmMsg);
	        DatabaseHandler db = new DatabaseHandler(context);
	        db.addGCMMessage(gcmMsg);
	        
	        //
	        displayMessage(context, gcmMsg.getId());
	        
	        // notifies user
	        generateNotification(context, gcmMsg);
	        
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(NullPointerException e){
			e.printStackTrace();
		}
        
        
    }
 
    /**
     * Method called on receiving a deleted message
     * */
    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        
        //displayMessage(context, message);
        // notifies user
        //generateNotification(context, message);
    }
 
    /**
     * Method called on Error
     * */
    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "Received error: " + errorId);
        //displayMessage(context, getString(R.string.gcm_error, errorId));
    }
 
    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        //displayMessage(context, getString(R.string.gcm_recoverable_error,
                //errorId));
        return super.onRecoverableError(context, errorId);
    }
 
    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, GCMMessage message) {
        int icon = R.drawable.ic_launcher;
        
        long when = System.currentTimeMillis();
        
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        
        Notification notification = new Notification(icon, message.getTitle(), when);
         
        String title = context.getString(R.string.app_name);
        
        Intent notificationIntent = new Intent(context, NotificationViewerActivity.class);
        notificationIntent.putExtra(MESSAGE, message.getId());
        
        
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP/* |
                Intent.FLAG_ACTIVITY_SINGLE_TOP*/);
        
        PendingIntent intent =
                PendingIntent.getActivity(context, message.getId(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        
        notification.setLatestEventInfo(context, title, message.getTitle(), intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
         
        // Play default notification sound
        //notification.defaults |= Notification.DEFAULT_SOUND; //defualt
         notification.sound =  Uri.parse("android.resource://" + context.getPackageName() +"/"+ R.raw.oringz07); //custom
         
         
        // Vibrate if vibrate is enabled
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(message.getId(), notification);      
 
    }
 
}