package com.relaxodasoft.ididntknowthat_ar.main_views;

import static com.relaxodasoft.ididntknowthat_ar.gcm.GCMIntentService.MESSAGE;


import java.util.ArrayList;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.relaxodasoft.ididntknowthat_ar.R.anim;
import com.relaxodasoft.ididntknowthat_ar.R.drawable;
import com.relaxodasoft.ididntknowthat_ar.R.id;
import com.relaxodasoft.ididntknowthat_ar.R.layout;
import com.relaxodasoft.ididntknowthat_ar.R.menu;
import com.relaxodasoft.ididntknowthat_ar.R.string;
import com.relaxodasoft.ididntknowthat_ar.db.DatabaseHandler;
import com.relaxodasoft.ididntknowthat_ar.gcm.GCMMessage;
import com.relaxodasoft.ididntknowthat_ar.image_handler.ImageDownloader;
import com.relaxodasoft.ididntknowthat_ar.image_handler.ImageUrl;
import com.relaxodasoft.ididntknowthat_ar.image_handler.ImageViewerActivity;
import com.relaxodasoft.ididntknowthat_ar.image_handler.ImageDownloader.OnImageDonwloaderCompleteListener;
import com.relaxodasoft.ididntknowthat_ar.R;

import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;

import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageView.ScaleType;
import android.widget.ViewSwitcher.ViewFactory;
import android.widget.AdapterView.OnItemClickListener;


public class NotificationViewerActivity extends Activity {
	private static final String TAG = "Notification Viewer Activity";
	ImageSwitcher imageSwitcher;
	String selectedImageUrl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_notification_viewer);
		
		
		loadAds();
		
		selectedImageUrl = null;
		
		Intent in = getIntent();
		TextView title = (TextView) findViewById(R.id.textViewTitle_notificationViewer);
		TextView desctiption = (TextView) findViewById(R.id.textViewDescription_notificationViewer);

		imageSwitcher = (ImageSwitcher) findViewById(R.id.imageSwitcher_notificationViewer);

		imageSwitcher.setInAnimation(AnimationUtils.loadAnimation(this,
				android.R.anim.fade_in));
		imageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this,
				android.R.anim.fade_out));

		imageSwitcher.setFactory(new ViewFactory() {

			@Override
			public View makeView() {
				// TODO Auto-generated method stub
				//final ImageView image = new ImageView(NotificationViewerActivity.this);
				
				final ImageView image = new ImageView(NotificationViewerActivity.this);
				
				image.setOnLongClickListener(new OnLongClickListener() {
					@Override
					public boolean onLongClick(View arg0) {
						if(selectedImageUrl != null){
						// TODO Auto-generated method stub
						Log.d(TAG, "Long Click ");
						//.setIcon(R.drawable.alert_dialog_icon)
					    final String saveExten = ".jpg";
					    
					    
						LayoutInflater factory = LayoutInflater.from(NotificationViewerActivity.this);
			            //final View textEntryView = factory.inflate(R.layout.simple_edit_text, null);
			            new AlertDialog.Builder(NotificationViewerActivity.this)
			                .setTitle(getString(R.string.image_save_title_ar))
			                .setPositiveButton(getString(R.string.image_save_button_yes_ar), new DialogInterface.OnClickListener() {
			                    public void onClick(final DialogInterface dialog, int whichButton) {
			                    	
			                    	dialog.dismiss();
			                    	AsyncTask<Void, Void, Void> asy = new AsyncTask<Void,Void,Void>(){
			                    		String sa ;
			                    		ProgressDialog pd;
										@Override
										protected Void doInBackground(
												Void... params) {
											// TODO Auto-generated method stub
											NotificationViewerActivity.this.runOnUiThread(new Runnable() {
												
												@Override
												public void run() {
													// TODO Auto-generated method stub
													pd = new ProgressDialog(NotificationViewerActivity.this);
													pd.setCancelable(false);
													pd.setTitle(NotificationViewerActivity.this.getString(R.string.image_save_wait_title_ar));
													pd.setIndeterminate(true);
													
													pd.show();
												}
											});
											
											
											Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
					                    	sa = MediaStore.Images.Media.insertImage(NotificationViewerActivity.this.getContentResolver(), bitmap, null , null);	
					                    	
											return null;
										}
										
										protected void onPostExecute(Void result) {
											NotificationViewerActivity.this.runOnUiThread(new Runnable() {
												
												@Override
												public void run() {
													// TODO Auto-generated method stub
													
													pd.dismiss();
												}
											});
											
											if(null == sa){
					                    		Toast.makeText(NotificationViewerActivity.this,NotificationViewerActivity.this.getString(R.string.image_fail_in_sdcard_ar), Toast.LENGTH_LONG).show();
					                    	}else{
					                    		Toast.makeText(NotificationViewerActivity.this,NotificationViewerActivity.this.getString(R.string.image_saved_in_sdcard_ar), Toast.LENGTH_LONG).show();
					                    	}
										};
			                    		
			                    	};
			                    	asy.execute(null,null,null);
			                    	
			                    	
			                    }
			                })
			                .setNegativeButton(getString(R.string.image_save_button_no_ar), new DialogInterface.OnClickListener() {
			                    public void onClick(DialogInterface dialog, int whichButton) {

			                        /* User clicked cancel so do some stuff */
			                    }
			                })
			                .show();
						
						//Toast.makeText(NotificationViewerActivity.this, "is equal to null ? " + ((bitmap == null)?"Yes":"No"), Toast.LENGTH_LONG).show();
						
					}
						return false;
					}
					
				});
				
				image.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if(selectedImageUrl != null){
							Intent intent = new Intent(NotificationViewerActivity.this, ImageViewerActivity.class);
							intent.putExtra(ImageViewerActivity.URL_ID, selectedImageUrl);
							startActivity(intent);
						}
					}
				});
				
				image.setBackgroundColor(0x00000000);
				// i.setScaleType(ImageView.ScaleType.FIT_CENTER);
				image.setLayoutParams(new ImageSwitcher.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
				return image;
			}
		});
		

		final int id = in.getIntExtra(MESSAGE, -1);
		DatabaseHandler db = new DatabaseHandler(this);

		final GCMMessage msg = db.getGCMMessageById(id);

		if (msg != null) {
			title.setText(msg.getTitle());

			desctiption.setText(msg.getDescription());

			if (msg.getImageLinks().isEmpty())
				imageSwitcher.setImageResource(R.drawable.noimage);
			else
				new ImageDownloader(this, imageSwitcher, R.drawable.loading,
						R.drawable.noimage)
						.setOnImageDonwloaderCompleteListener(
						new OnImageDonwloaderCompleteListener() {
	
							@Override
							public void finish() {
								// TODO Auto-generated method
								// stub
								selectedImageUrl = getImageNameOnDisk(id, 0);
							}
						})
						.execute(msg.getImageLinks().get(0));
						


			
			Gallery g = (Gallery) findViewById(R.id.gallery_notificationViewer);
			g.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, final View arg1,
						final int arg2, long arg3) {
					// TODO Auto-generated method stub
					Log.d(TAG, "click on gallery image");
					selectedImageUrl = null;
					if (arg1.getTag() == null) {
						arg1.setTag(new CurrentState(true));
						new ImageDownloader(
								((Activity) NotificationViewerActivity.this),
								imageSwitcher, R.drawable.loading,
								R.drawable.noimage)
								.setOnImageDonwloaderCompleteListener(
										new OnImageDonwloaderCompleteListener() {

											@Override
											public void finish() {
												// TODO Auto-generated method
												// stub
												CurrentState cur = (CurrentState) arg1
														.getTag();
												cur.setDownloadingState(false);
												
												selectedImageUrl = getImageNameOnDisk(id, arg2);
											}
										}).execute(msg.getImageLinks().get(arg2));
						
					} else if(!((CurrentState) arg1.getTag()).getDonwloadingState()) {
						new ImageDownloader(
								((Activity) NotificationViewerActivity.this),
								imageSwitcher, R.drawable.loading,
								R.drawable.noimage)
								.setOnImageDonwloaderCompleteListener(
								new OnImageDonwloaderCompleteListener() {

									@Override
									public void finish() {
										// TODO Auto-generated method
										// stub
										
										selectedImageUrl = getImageNameOnDisk(id, arg2);
									}
								}).execute(msg.getImageLinks().get(arg2));
								
					}else{
						Log.d("ImageDownloader",
								"Image Donwload is in downloading mode");
					}
					
				}
			});

			g.setAdapter(new ImageAdapter(this, msg.getImageLinks()));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.notification_viewer, menu);
		return true;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finish();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
	}

	public class ImageAdapter extends BaseAdapter {
		private Context mContext;
		private ArrayList<ImageUrl> links;

		public ImageAdapter(Context c, ArrayList<ImageUrl> links) {
			this.mContext = c;
			this.links = links;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {

			ImageView imageView = new ImageView(mContext);

			new ImageDownloader(((Activity) mContext), imageView,
					R.drawable.loading, R.drawable.noimage).execute(links
					.get(position));

			//convert from density to pixels
			int dps = 80;//80dp
			float scale = mContext.getResources().getDisplayMetrics().density;
			int pixels = (int) (dps * scale + 0.5f);
			
			imageView.setScaleType(ScaleType.FIT_XY);
			
			
			imageView.setLayoutParams(new Gallery.LayoutParams(pixels,pixels));
			
			return imageView;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return links.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return links.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

	}
	
	public String getImageNameOnDisk(int id,int index){
		String name = null;
		try{
			DatabaseHandler db2 = new DatabaseHandler(NotificationViewerActivity.this);
			GCMMessage msg2 = db2.getGCMMessageById(id);
			name = msg2.getImageLinks().get(index).getImage_disk();
		}catch(Exception exp){
			exp.printStackTrace();
		}
		return name;
	}
	
public void loadAds() {
		
		AdView adView = new AdView(this, AdSize.BANNER,
				getString(R.string.admob_id));
		LinearLayout layout = (LinearLayout) findViewById(R.id.imgBanner);
		layout.addView(adView);

		AdRequest adRequest = new AdRequest();
		//adRequest.setTesting(true);
		// Start loading the ad in the background.
		adView.loadAd(adRequest);
	}
	
	public class CurrentState {
		boolean isDownLoading;

		public CurrentState(boolean state) {
			this.isDownLoading = state;
		}

		public boolean getDonwloadingState() {
			return isDownLoading;
		}

		public void setDownloadingState(boolean state) {
			isDownLoading = state;
		}
	}

}
