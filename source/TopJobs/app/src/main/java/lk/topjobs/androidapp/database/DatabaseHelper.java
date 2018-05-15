package lk.topjobs.androidapp.database;

import java.util.ArrayList;
import java.util.Date;

import lk.topjobs.androidapp.data.JobPostData;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author Harsha Kodagoda
 * 
 */
public class DatabaseHelper extends SQLiteOpenHelper {
	public static final String DATABASE_NAME = "topjobs.db";
	public static final int DATABASE_VERSION = 1;
	public static final String TABLE_FAVORITES = "favorites";

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sqlQuery = "CREATE TABLE "
				+ TABLE_FAVORITES
				+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ " js TEXT, ac TEXT, ec TEXT, position TEXT, employer TEXT,"
				+ " description TEXT, link TEXT, pub_date NUMERIC, closing_date NUMERIC)";
		db.execSQL(sqlQuery);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
		onCreate(db);
	}

	public int toggleFavorite(JobPostData jobPostData) {
		if (jobPostData != null) {
			if (isFavorite(jobPostData.getRefNo())) {
				if (removeFromFavorite(jobPostData.getRefNo())) {
					return 0;
				} else {
					return -1;
				}
			} else {
				if (addToFavorite(jobPostData)) {
					return 1;
				} else {
					return -1;
				}
			}
		} else {
			return -1;
		}
	}

	public boolean addToFavorite(JobPostData jobPostData) {
		SQLiteDatabase db = getWritableDatabase();
		try {
			ContentValues values = new ContentValues();
			values.put("js", jobPostData.getJs());
			values.put("ac", jobPostData.getAc());
			values.put("ec", jobPostData.getEc());
			values.put("position", jobPostData.getPosition());
			values.put("employer", jobPostData.getEmployer());
			values.put("description", jobPostData.getDescription());
			values.put("link", jobPostData.getLink());
			values.put("pub_date", jobPostData.getPubDate().getTime());
			values.put("closing_date", jobPostData.getClosingDate().getTime());
			db.insertOrThrow(TABLE_FAVORITES, null, values);
			return true;
		} catch (Exception ex) {
			return false;
		} finally {
			try {
				db.close();
			} catch (Exception ex) {
			}
		}
	}

	public boolean removeFromFavorite(String jobRef) {
		SQLiteDatabase db = getWritableDatabase();
		try {
			db.delete(TABLE_FAVORITES, "js = '" + jobRef + "'", null);
			return true;
		} catch (Exception ex) {
			return false;
		} finally {
			try {
				db.close();
			} catch (Exception e) {
			}
		}
	}

	public boolean removeAllFavorite() {
		SQLiteDatabase db = getWritableDatabase();
		try {
			db.delete(TABLE_FAVORITES, null, null);
			return true;
		} catch (Exception ex) {
			return false;
		} finally {
			try {
				db.close();
			} catch (Exception e) {
			}
		}
	}

	public boolean isFavorite(String jobRef) {
		SQLiteDatabase db = getReadableDatabase();
		try {
			Cursor cursor = db.query(TABLE_FAVORITES, null, "js = '" + jobRef
					+ "'", null, null, null, null);
			if (cursor.moveToNext()) {
				String job_ref = cursor.getString(cursor.getColumnIndex("js"));
				if (job_ref.length() > 0) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} catch (Exception ex) {
			return false;
		} finally {
			try {
				db.close();
			} catch (Exception e) {
			}
		}
	}

	public ArrayList<JobPostData> getAllFavorites() {
		SQLiteDatabase db = getReadableDatabase();
		try {
			ArrayList<JobPostData> favoriteList = new ArrayList<JobPostData>();
			Cursor cursor = db.query(TABLE_FAVORITES, null, null, null, null,
					null, null);
			int id = 0;
			while (cursor.moveToNext()) {
				JobPostData jobPostData = new JobPostData();
				jobPostData.setId(id);
				jobPostData
						.setJs(cursor.getString(cursor.getColumnIndex("js")));
				jobPostData
						.setAc(cursor.getString(cursor.getColumnIndex("ac")));
				jobPostData
						.setEc(cursor.getString(cursor.getColumnIndex("ec")));
				jobPostData.setPosition(cursor.getString(cursor
						.getColumnIndex("position")));
				jobPostData.setEmployer(cursor.getString(cursor
						.getColumnIndex("employer")));
				jobPostData.setDescription(cursor.getString(cursor
						.getColumnIndex("description")));
				jobPostData.setLink(cursor.getString(cursor
						.getColumnIndex("link")));
				long pubDateMillis = cursor.getLong(cursor
						.getColumnIndex("pub_date"));
				Date pubDate = new Date(pubDateMillis);
				jobPostData.setPubDate(pubDate);
				long closingDateMillis = cursor.getLong(cursor
						.getColumnIndex("closing_date"));
				Date closingDate = new Date(closingDateMillis);
				jobPostData.setClosingDate(closingDate);
				id = id + 1;
				favoriteList.add(jobPostData);
			}
			return favoriteList;
		} catch (Exception e) {
			return null;
		} finally {
			try {
				db.close();
			} catch (Exception e) {
			}
		}
	}
}
