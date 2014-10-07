package com.relaxodasoft.ididntknowthat_ar.gcm;

import java.io.File;
import java.util.ArrayList;

import com.relaxodasoft.ididntknowthat_ar.R;
import com.relaxodasoft.ididntknowthat_ar.R.anim;
import com.relaxodasoft.ididntknowthat_ar.R.drawable;
import com.relaxodasoft.ididntknowthat_ar.R.id;
import com.relaxodasoft.ididntknowthat_ar.R.string;
import com.relaxodasoft.ididntknowthat_ar.db.DatabaseHandler;
import com.relaxodasoft.ididntknowthat_ar.image_handler.ImageDownloader;
import com.relaxodasoft.ididntknowthat_ar.image_handler.ImageUrl;
import com.relaxodasoft.ididntknowthat_ar.main_views.NotificationViewerActivity;

import static com.relaxodasoft.ididntknowthat_ar.gcm.GCMIntentService.MESSAGE;
import static com.relaxodasoft.ididntknowthat_ar.image_handler.ImageDownloader.DIR;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

import android.widget.ArrayAdapter;

import android.widget.ImageView;
import android.widget.TextView;
import android.view.animation.Transformation;


public class GCMMessageAdapter extends ArrayAdapter<GCMMessage> {
	ArrayList<GCMMessage> news;
	Context context;
	int resourceId;

	public GCMMessageAdapter(Context context, int textViewResourceId,
			ArrayList<GCMMessage> objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub

		this.news = objects;
		this.context = context;
		this.resourceId = textViewResourceId;

	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		View row = convertView;

		GCMMessageHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(resourceId, parent, false);

			holder = new GCMMessageHolder();
			holder.imgIcon = (ImageView) row.findViewById(R.id.imgIcon);
			holder.txtTitle = (TextView) row.findViewById(R.id.txtTitle);
			row.setBackgroundColor(Color.WHITE);
			row.setTag(holder);

		} else {
			holder = (GCMMessageHolder) row.getTag();
		}

		GCMMessage msg = news.get(position);

		holder.txtTitle.setText(msg.getTitle());

		if (msg.getImageLinks().isEmpty()) {
			holder.imgIcon.setImageResource(R.drawable.noimage);
		} else {

			new ImageDownloader((Activity) context, holder.imgIcon,
					R.drawable.loading, R.drawable.noimage).execute(msg
					.getImageLinks().get(0));

		}

		row.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				GCMMessageHolder holder = (GCMMessageHolder) arg0.getTag();
				Log.d("GCM Adapter", "click on item in postions : " + position);

				Intent in = new Intent(context,
						NotificationViewerActivity.class);
				in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				in.putExtra(MESSAGE, news.get(position).getId());

				context.startActivity(in);
				((Activity) context).overridePendingTransition(
						R.anim.slide_left_to_right,R.anim.slide_right_to_left);
			}
		});

		row.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(final View arg0) {
				// TODO Auto-generated method stub

				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setMessage(context.getString(R.string.delete_news_message_ar))
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setTitle(context.getString(R.string.delete_news_title_ar))
						.setPositiveButton(context.getString(R.string.delete_news_button_label_ar),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {

										removeRow(arg0,position);
									}
								})
						.setNegativeButton(context.getString(R.string.cancel_deleting_news_button_label_ar),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										// User cancelled the dialog
									}
								});
				// Create the AlertDialog object and return it
				builder.show();

				return false;
			}
		});

		return row;
	}

	private void removeRow(final View row, final int position) {
	    final int initialHeight = row.getHeight();
	    Animation animation = new Animation() {
	        @Override
	        protected void applyTransformation(float interpolatedTime,
	                Transformation t) {
	            super.applyTransformation(interpolatedTime, t);
	            int newHeight = (int) (initialHeight * (1 - interpolatedTime));
	            if (newHeight > 0) {
	                row.getLayoutParams().height = newHeight;
	                row.requestLayout();
	            }
	        }
	    };
	    animation.setAnimationListener(new AnimationListener() {
	        @Override
	        public void onAnimationStart(Animation animation) {
	        }
	        @Override
	        public void onAnimationRepeat(Animation animation) {
	        }
	        @Override
	        public void onAnimationEnd(Animation animation) {
	            row.getLayoutParams().height = initialHeight;
	            row.requestLayout();
	            cleanNews(position);
	            news.remove(position);
	            notifyDataSetChanged();
	        }
	    });
	    animation.setDuration(300);
	    row.startAnimation(animation);
	}
	
	private void cleanNews(int position) {
		GCMMessage msg = news.get(position);

		DatabaseHandler db = new DatabaseHandler(context);
		msg = db.getGCMMessageById(msg.getId());

		// start by cleaning the images from hard disk
		ArrayList<ImageUrl> images = msg.getImageLinks();

		File mydir = context.getDir(DIR, Activity.MODE_PRIVATE);
		File fileWithinMyDir = null;

		for (ImageUrl imageUrl : images) {

			if (imageUrl.isImageExistOnDisk()) {
				try {
					Log.d("Deleting news",
							"Try to delete " + imageUrl.getUrl_id() + ".jpg");
					fileWithinMyDir = new File(mydir, imageUrl.getUrl_id()
							+ ".jpg");

					fileWithinMyDir.delete();
				} catch (Exception exp) {
					exp.printStackTrace();
				}
			}

			if (imageUrl.isThumbExistOnDisk()) {
				try {
					Log.d("Deleting news",
							"Try to delete " + imageUrl.getUrl_id() + "t.jpg");
					fileWithinMyDir = new File(mydir, imageUrl.getUrl_id()
							+ "t.jpg");

					fileWithinMyDir.delete();
				} catch (Exception exp) {
					exp.printStackTrace();
				}
			}
		}

		// remove news form the database
		db.deleteNews(msg.getId());
		
		NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		//delete the notification form notification bar if exist
		notificationManager.cancel(msg.getId());

	}

	static class GCMMessageHolder {
		ImageView imgIcon;
		TextView txtTitle;
	}

}
