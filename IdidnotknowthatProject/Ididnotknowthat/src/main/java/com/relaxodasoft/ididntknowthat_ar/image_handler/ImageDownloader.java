package com.relaxodasoft.ididntknowthat_ar.image_handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import com.relaxodasoft.ididntknowthat_ar.db.DatabaseHandler;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageSwitcher;
import android.widget.ImageView;

public class ImageDownloader extends AsyncTask<ImageUrl, Void, Bitmap>{
	public static final String DIR = "dbimages";
	private static final String TAG = "Image Downloader Class ";
	
	private Activity activity;
	private ImageView image;
	private ImageSwitcher imageSwitcher;
	
	private int failImage;
	private int waitImage;
	private OnImageDonwloaderCompleteListener downloadComplete;
	
	private int target;//0:image, 1:thumb
	public ImageDownloader(Activity actvity,ImageView image,int waitImageRresourceId,int failImageResourceId){
		this.activity = actvity;
		this.image = image;
		this.imageSwitcher = null;
		this.waitImage = waitImageRresourceId;
		this.failImage = failImageResourceId;
		target = 1;
		
	}
	public ImageDownloader(Activity actvity,ImageSwitcher image,int waitImageRresourceId,int failImageResourceId){
		this.activity = actvity;
		this.image = null;
		this.imageSwitcher = image;
		
		this.waitImage = waitImageRresourceId;
		this.failImage = failImageResourceId;
		target = 0;
		
	}
	
	@Override
	protected Bitmap doInBackground(ImageUrl... params) {
		// TODO Auto-generated method stub
		
		//try to avoid run more than one download in the same time
		try {
			Thread.sleep(new Random().nextInt(300));
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		ImageUrl url = params[0];
		Bitmap bitmap = null;
		
		activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(image != null)
					image.setImageResource(waitImage);
				else
					imageSwitcher.setImageResource(waitImage);
			}
		});
		
		try {
			
			if(target == 0){				
				
				if(url.isImageExistOnDisk()){
					
					File mydir = activity.getDir(DIR, Activity.MODE_PRIVATE); //Creating an internal dir;
					File fileWithinMyDir = new File(mydir, url.getImage_disk()); //Getting a file within the dir.
					FileInputStream in = new FileInputStream(fileWithinMyDir); //Use the stream as usual to write into the file.
					bitmap = BitmapFactory.decodeStream(in);
					
				}else{
					try{

						File mydir = activity.getDir(DIR, Activity.MODE_PRIVATE); //Creating an internal dir;
	
						
						File fileWithinMyDir = new File(mydir , url.getUrl_id()+".jpg"); //Getting a file within the dir.
						
						InputStream in = (InputStream)new URL(url.getImage_url()).getContent();
						
						bitmap = BitmapFactory.decodeStream(in);
			
						FileOutputStream out = new FileOutputStream(fileWithinMyDir);
						bitmap.compress(Bitmap.CompressFormat.JPEG, 70, out);
						
						in.close();
						out.flush();
						out.close();
						
						
						
						url.setImage_disk(url.getUrl_id() + ".jpg");
						DatabaseHandler db = new DatabaseHandler(activity);
						db.updateImageDisk(url.getUrl_id(), url.getUrl_id() + ".jpg");
					
					}catch(NullPointerException exp){
						Log.d(TAG, " null bitmap, bad image url");
						exp.printStackTrace();
					}
				}
			
			}else if(target == 1){
				if(url.isThumbExistOnDisk()){
					
					File mydir = activity.getDir(DIR, Activity.MODE_PRIVATE); //Creating an internal dir;
					File fileWithinMyDir = new File(mydir, url.getThumb_disk()); //Getting a file within the dir.
					FileInputStream in = new FileInputStream(fileWithinMyDir); //Use the stream as usual to write into the file.
					bitmap = BitmapFactory.decodeStream(in);
					
				}else{
				
					try{
						
						File mydir = activity.getDir(DIR, Activity.MODE_PRIVATE); //Creating an internal dir;
						
						File fileWithinMyDir = new File(mydir, url.getUrl_id()+"t.jpg"); //Getting a file within the dir.
						
						
						FileOutputStream out = new FileOutputStream(fileWithinMyDir);
						
						InputStream in = (InputStream)new URL(url.getThumb_url()).getContent();
						
						bitmap = BitmapFactory.decodeStream(in);
	
						bitmap.compress(Bitmap.CompressFormat.JPEG, 70, out);
	
						
						in.close();
						out.flush();
						out.close();
						
						
						 //Use the stream as usual to write into the file.
						
						url.setThumb_disk(url.getUrl_id() + "t.jpg");
						DatabaseHandler db = new DatabaseHandler(activity);
						db.updateThumbDisk(url.getUrl_id(), url.getUrl_id() + "t.jpg");
					
					}catch(NullPointerException exp){
						Log.d(TAG, " null bitmap, bad Thumb url");
						exp.printStackTrace();
					}
				}
			}
		
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bitmap;
		
	}
	
	public ImageDownloader setOnImageDonwloaderCompleteListener(OnImageDonwloaderCompleteListener lsn){
		downloadComplete = lsn;
		return this;
	}
	
	@Override
	protected void onPostExecute(Bitmap result) {
		// TODO Auto-generated method stub
		final Bitmap bitmap = result;
		
		
		activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(bitmap != null)
					if(image != null){
						image.setImageBitmap(bitmap);
						
					}
					else{
						Drawable d = new BitmapDrawable(activity.getResources(), bitmap);
						imageSwitcher.setImageDrawable(d);
						
					}
				else
					if(image != null)
						image.setImageResource(failImage);
					else
						imageSwitcher.setImageResource(failImage);
			}	
		});
		
		if(downloadComplete != null)
			downloadComplete.finish();
		
	}
	
	public interface OnImageDonwloaderCompleteListener{
		void finish();
	}

}
