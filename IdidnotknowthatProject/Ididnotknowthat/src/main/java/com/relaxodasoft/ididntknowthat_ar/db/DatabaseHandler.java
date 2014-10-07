package com.relaxodasoft.ididntknowthat_ar.db;


import java.util.ArrayList;

import com.relaxodasoft.ididntknowthat_ar.gcm.GCMMessage;
import com.relaxodasoft.ididntknowthat_ar.image_handler.ImageUrl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {

	// database version
	public final static int DATABASE_VERSION = 1;

	// database name
	public final static String DATABASE_NAME = "newsdb";

	// database tables
	public final static String TABLE_NEWS = "news";
	public final static String TABLE_IMAGES = "images";

	// news columns
	public final static String COLUMN_ID = "id";
	public final static String COLUMN_TITLE = "title";
	public final static String COLUMN_DESCRIPTION = "description";
	public final static String COLUMN_DATE = "date";

	// images columns
	public final static String COLUMN_URL_ID = "url_id";
	public final static String COLUMN_IMAGE_URL = "image_url";
	public final static String COLUMN_THUMB_URL = "thumb_url";
	public final static String COLUMN_IMAGE_DISK = "image_disk";
	public final static String COLUMN_THUMB_DISK = "thumb_disk";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub

		String CREATE_NEWS_TABLE = "CREATE TABLE " + TABLE_NEWS + "("
				+ COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_TITLE + " TEXT,"
				+ COLUMN_DESCRIPTION + " TEXT," + COLUMN_DATE + " TEXT" + ")";

		String CREATE_IMAGES_TABLE = "CREATE TABLE " + TABLE_IMAGES + "("
				+ COLUMN_URL_ID + " INTEGER PRIMARY KEY," + COLUMN_ID
				+ " INTEGER," + COLUMN_IMAGE_URL + " TEXT," + COLUMN_THUMB_URL
				+ " TEXT," + COLUMN_IMAGE_DISK + " TEXT DEFAULT NULL,"
				+ COLUMN_THUMB_DISK + " TEXT DEFAULT NULL," + "FOREIGN KEY("
				+ COLUMN_ID + ") REFERENCES " + TABLE_NEWS + "(" + COLUMN_ID
				+ "))";

		arg0.execSQL(CREATE_NEWS_TABLE);
		arg0.execSQL(CREATE_IMAGES_TABLE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	public ArrayList<GCMMessage> getAllGCMMessages() {
		ArrayList<GCMMessage> newsList = new ArrayList<GCMMessage>();

		// Select All Query
		String selectQuery1 = "SELECT  * FROM " + TABLE_NEWS;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor1 = db.rawQuery(selectQuery1, null);

		// looping through all rows and adding to list
		if (cursor1.moveToFirst()) {
			do {
				GCMMessage msg = new GCMMessage();

				msg.setId(Integer.parseInt(cursor1.getString(0)));

				msg.setTitle(cursor1.getString(1));

				msg.setDescription(cursor1.getString(2));

				String selectQuery2 = "SELECT  * FROM " + TABLE_IMAGES
						+ " Where " + COLUMN_ID + " = " + msg.getId();

				Cursor cursor2 = db.rawQuery(selectQuery2, null);
				ArrayList<ImageUrl> images = new ArrayList<ImageUrl>();

				if (cursor2.moveToFirst()) {
					do {
						ImageUrl image = new ImageUrl();
						image.setUrl_id(cursor2.getInt(0));
						image.setId(cursor2.getInt(1));
						image.setImage_url(cursor2.getString(2));
						image.setThumb_url(cursor2.getString(3));
						image.setImage_disk(cursor2.getString(4));
						image.setThumb_disk(cursor2.getString(5));

						images.add(image);

					} while (cursor2.moveToNext());
				}

				cursor2.close();
				msg.setImageLinks(images);

				newsList.add(msg);
			} while (cursor1.moveToNext());
		}
		Log.i("DATA BASE", "# of selected rows" + newsList.size());

		cursor1.close();
		db.close();
		// return contact list
		return newsList;
	}

	// Getting single contact
	public ArrayList<GCMMessage> getGCMMessageStartWithTitle(String tit) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_NEWS, null, COLUMN_TITLE + " like "
				+ "\"" + tit + "%\"", null, null, null, null, null);

		ArrayList<GCMMessage> newsList = new ArrayList<GCMMessage>();

		if (cursor.moveToFirst()) {
			do {
				GCMMessage msg = new GCMMessage();

				msg.setId(Integer.parseInt(cursor.getString(0)));

				msg.setTitle(cursor.getString(1));

				msg.setDescription(cursor.getString(2));

				String selectQuery2 = "SELECT  * FROM " + TABLE_IMAGES
						+ " Where " + COLUMN_ID + " = " + msg.getId();

				Cursor cursor2 = db.rawQuery(selectQuery2, null);
				ArrayList<ImageUrl> images = new ArrayList<ImageUrl>();

				if (cursor2.moveToFirst()) {
					do {
						ImageUrl image = new ImageUrl();
						image.setUrl_id(cursor2.getInt(0));
						image.setId(cursor2.getInt(1));
						image.setImage_url(cursor2.getString(2));
						image.setThumb_url(cursor2.getString(3));
						image.setImage_disk(cursor2.getString(4));
						image.setThumb_disk(cursor2.getString(5));

						images.add(image);

					} while (cursor2.moveToNext());
				}

				cursor2.close();
				msg.setImageLinks(images);

				newsList.add(msg);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();

		return newsList;
	}

	// Getting single contact
	public GCMMessage getGCMMessageById(int id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_NEWS, null, COLUMN_ID + " = " + id,
				null, null, null, null, null);

		GCMMessage msg = null;

		if (cursor.moveToFirst()) {
			msg = new GCMMessage();

			msg.setId(Integer.parseInt(cursor.getString(0)));

			msg.setTitle(cursor.getString(1));

			msg.setDescription(cursor.getString(2));

			String selectQuery2 = "SELECT  * FROM " + TABLE_IMAGES + " Where "
					+ COLUMN_ID + " = " + msg.getId();

			Cursor cursor2 = db.rawQuery(selectQuery2, null);
			ArrayList<ImageUrl> images = new ArrayList<ImageUrl>();

			if (cursor2.moveToFirst()) {
				do {
					ImageUrl image = new ImageUrl();
					image.setUrl_id(cursor2.getInt(0));
					image.setId(cursor2.getInt(1));
					image.setImage_url(cursor2.getString(2));
					image.setThumb_url(cursor2.getString(3));
					image.setImage_disk(cursor2.getString(4));
					image.setThumb_disk(cursor2.getString(5));

					images.add(image);

				} while (cursor2.moveToNext());
			}

			cursor2.close();
			msg.setImageLinks(images);
		}
		cursor.close();
		db.close();

		return msg;
	}

	public GCMMessage addGCMMessage(GCMMessage msg) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values1 = new ContentValues();

		values1.put(COLUMN_TITLE, msg.getTitle());
		values1.put(COLUMN_DESCRIPTION, msg.getDescription());

		// Inserting Row
		int id = (int) db.insert(TABLE_NEWS, null, values1);
		msg.setId(id);

		ArrayList<ImageUrl> images = msg.getImageLinks();
		if (!images.isEmpty()) {

			ContentValues values2;
			int id2;
			for (ImageUrl imageUrl : images) {

				values2 = new ContentValues();

				values2.put(COLUMN_ID, id);
				values2.put(COLUMN_IMAGE_URL, imageUrl.getImage_url());
				values2.put(COLUMN_THUMB_URL, imageUrl.getThumb_url());
				id2 = (int) db.insert(TABLE_IMAGES, null, values2);

				imageUrl.setId(id);
				imageUrl.setUrl_id(id2);
			}

		}

		db.close(); // Closing database connection

		return msg;
	}

	public void updateImageDisk(int imageId, String imageDisk) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_IMAGE_DISK, imageDisk);

		db.update(TABLE_IMAGES, values, COLUMN_URL_ID + " = " + imageId, null);
		db.close();
	}

	public void updateThumbDisk(int imageId, String thumbDisk) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_THUMB_DISK, thumbDisk);

		db.update(TABLE_IMAGES, values, COLUMN_URL_ID + " = " + imageId, null);
		db.close();
	}

	public void updateImageAndThumbDisk(int imageId, String imageDisk,
			String thumbDisk) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_IMAGE_DISK, imageDisk);
		values.put(COLUMN_THUMB_DISK, thumbDisk);

		db.update(TABLE_IMAGES, values, COLUMN_URL_ID + " = " + imageId, null);
		db.close();
	}
	
	// Deleting single News
    public void deleteNews(int newsId) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        db.delete(TABLE_IMAGES, COLUMN_ID + " = " + newsId,null);
        db.delete(TABLE_NEWS, COLUMN_ID + " = " + newsId,null);
        db.close();
    }
}
