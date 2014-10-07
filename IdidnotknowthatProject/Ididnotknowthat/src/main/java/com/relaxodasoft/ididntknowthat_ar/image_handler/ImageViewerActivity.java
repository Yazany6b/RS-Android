package com.relaxodasoft.ididntknowthat_ar.image_handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.polites.android.GestureImageView;
import com.relaxodasoft.ididntknowthat_ar.R;
import com.relaxodasoft.ididntknowthat_ar.R.id;
import com.relaxodasoft.ididntknowthat_ar.R.layout;
import com.relaxodasoft.ididntknowthat_ar.R.menu;

import android.os.Bundle;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Menu;
import android.view.Window;

public class ImageViewerActivity extends Activity {
	public static final String URL_ID = "url_id";
	public static final String TAG = "Image Viewer Activity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_image_viewer);
		
		
		GestureImageView gs = (GestureImageView) findViewById(R.id.imageToZoom);
		gs.setMinScale(0.1f);
		gs.setMaxScale(10.0f);
		gs.setStrict(false);
		
		String imageName = getIntent().getExtras().getString(URL_ID);
		Log.d(TAG, " Image Name : "  + imageName);
		//String location = Environment.
		File dir = getDir(ImageDownloader.DIR, Activity.MODE_PRIVATE);
		Bitmap bit;
		try {
			bit = new BitmapDrawable(new FileInputStream(new File(dir,imageName))).getBitmap();
			gs.setImageBitmap(bit);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*
		 * 		gesture-image:min-scale="0.1"
        gesture-image:max-scale="10.0"
        gesture-image:strict="false"
		 * 
		 */
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.image_viewer, menu);
		return true;
	}

}
