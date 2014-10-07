package com.relaxodasoft.ididntknowthat_ar.main_views;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

import java.util.Timer;
import java.util.TimerTask;

import com.relaxodasoft.ididntknowthat_ar.R;
import com.relaxodasoft.ididntknowthat_ar.R.id;
import com.relaxodasoft.ididntknowthat_ar.R.layout;
import com.relaxodasoft.ididntknowthat_ar.R.menu;

public class WelcomeActivity extends Activity {

	Timer liveTimber;
	TimerTask taskForTimber;
	
	final int delay = 3000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_welcome);
		liveTimber = new Timer();
		
		taskForTimber = new TimerTask() {
			
			@Override
			public void run() {
				doFadeoutAnimation();
			}
		};
		
		liveTimber.schedule(taskForTimber, delay);
		
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		doFadeoutAnimation();
	}
	
	private void doFadeoutAnimation(){
		
		final View l = findViewById(R.id.welcomeView);

        final Animation a = AnimationUtils.loadAnimation(
        		WelcomeActivity.this, android.R.anim.fade_out);
        a.setDuration(1000);
        a.setAnimationListener(new AnimationListener() {

            public void onAnimationEnd(Animation animation) {
                    // Do what ever you need, if not remove it.  
            }

            public void onAnimationRepeat(Animation animation) {
                    // Do what ever you need, if not remove it.  
            }

            public void onAnimationStart(Animation animation) {
                    // Do what ever you need, if not remove it.  
            }

        });
        WelcomeActivity.this.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				//l.startAnimation(a);
				WelcomeActivity.this.finish();
				WelcomeActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.welcome, menu);
		return true;
	}

}
