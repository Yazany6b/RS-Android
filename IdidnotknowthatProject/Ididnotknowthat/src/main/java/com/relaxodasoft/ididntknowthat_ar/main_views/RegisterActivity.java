package com.relaxodasoft.ididntknowthat_ar.main_views;

import static com.relaxodasoft.ididntknowthat_ar.utility.CommonUtilities.isConnectingToInternet;
import com.relaxodasoft.ididntknowthat_ar.R;
import com.relaxodasoft.ididntknowthat_ar.R.id;
import com.relaxodasoft.ididntknowthat_ar.R.layout;
import com.relaxodasoft.ididntknowthat_ar.R.string;
import com.relaxodasoft.ididntknowthat_ar.utility.CommonUtilities;

import android.app.Activity;
import android.app.AlertDialog;

import android.content.DialogInterface;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends Activity {
	// alert dialog manager
	AlertDialogManager alert = new AlertDialogManager();
	public final static String SHARED_NAME = "Registration_info";
	public final static String SHARED_KEY_NAME = "name";
	public final static String SHARED_KEY_EMAIL = "email";
	public final static String SHARED_KEY_LANG = "lang";
	public final static String SHARED_KEY_REGION = "region";
	public final static String SHARED_KEY_OLD_REG_ID = "oldRegId";
	public final static String SHARED_KEY_REGISTERED_ON_SERVER = "onServer";
	public final static String SHARED_KEY_APP_VERSION = "appVersion";
	public final static String SHARED_KEY_ON_SERVER_EXPIRATION_TIME = "expiration";
	
	public final static String versionLang = "ar";

	// Internet detector
	//ConnectionDetector cd;

	// UI elements
	EditText txtName;
	EditText txtEmail;

	// Register button
	Button btnRegister;
	Button btnShowEmails;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register);

		//cd = new ConnectionDetector(getApplicationContext());
		txtName = (EditText) findViewById(R.id.txtName);
		txtEmail = (EditText) findViewById(R.id.txtEmail);
		btnRegister = (Button) findViewById(R.id.btnRegister);

		btnShowEmails = (Button) findViewById(R.id.btn_registration_myEmails);
		
		// Check if Internet present
		if (!isConnectingToInternet(this)) {
			// Internet Connection is not present
			alert.showAlertDialog(RegisterActivity.this,
					"Internet Connection Error",
					"Please connect to working Internet connection", false);

			setResult(RESULT_CANCELED);
			// stop executing code by return
			return;
		}


		
		final String[] emails = CommonUtilities.getEmails(this);
		
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.registration_myEmails_Dialog_title_ar)
				.setItems(CommonUtilities.getEmails(this),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									final int which) {

								final EditText email = (EditText) findViewById(R.id.txtEmail);
								runOnUiThread(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										email.setText(emails[which]);
									}
								});
							}
						});


		btnShowEmails.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				builder.create().show();
			}
		});

		SharedPreferences shared = getSharedPreferences(SHARED_NAME,
				MODE_PRIVATE);
		final Editor editor = shared.edit();

		if (shared.contains(SHARED_KEY_NAME)) {
			txtName.setText(shared.getString(SHARED_KEY_NAME, ""));// bug
		}

		if (shared.contains(SHARED_KEY_EMAIL)) {
			txtEmail.setText(shared.getString(SHARED_KEY_EMAIL, ""));
		}

		if (shared.contains(SHARED_KEY_NAME)
				&& shared.contains(SHARED_KEY_EMAIL)) {
			btnRegister.setText(getString(R.string.register_confirm_button_ar));
		}

		btnRegister.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Read EditText data
				String name = txtName.getText().toString();
				String email = txtEmail.getText().toString();

				// Check if user filled the form
				if (name.trim().length() > 0 && email.trim().length() > 0) {

					// get current country
					String locale = CommonUtilities
							.getRegion(RegisterActivity.this);

					editor.putString(SHARED_KEY_NAME, name);
					editor.putString(SHARED_KEY_EMAIL, email);
					editor.putString(SHARED_KEY_LANG, versionLang);
					editor.putString(SHARED_KEY_REGION, locale);
					editor.commit();

					setResult(RESULT_OK);
					finish();// close the current activity
				} else {
					// user doen't filled that data
					// ask him to fill the form
					alert.showAlertDialogWithoutKillActivity(
							RegisterActivity.this,
							getString(R.string.alert_title_registration_error_ar),
							getString(R.string.alert_message_registration_error_ar),
							false);
				}
			}
		});

	}

	private void doFadeoutAnimation() {

		final View l = findViewById(R.id.registerView);

		final Animation a = AnimationUtils.loadAnimation(RegisterActivity.this,
				android.R.anim.fade_in);

		a.setDuration(3000);
		RegisterActivity.this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				l.startAnimation(a);
			}
		});
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		setResult(RESULT_CANCELED);
	}

}
