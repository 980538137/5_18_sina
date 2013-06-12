package cn.edu.nuc.weibo.db;

import java.util.ArrayList;

import cn.edu.nuc.weibo.bean.UserInfo;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class UserInfoService {
	private static final String TAG = "UserInfoService";
	private DBHelper mDbHelper = null;

	public UserInfoService(Context mContext) {
		mDbHelper = new DBHelper(mContext);

	}

	public boolean isHasUser(String uid) {
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		Cursor mCursor = db.query(DBInfo.Table.USER_TABLE, null, "uid = ?",
				new String[] { uid }, null, null, null);
		if (mCursor.getCount() > 0) {
			return true;
		}
		return false;
	}

	public void updateUserInfo(String uid, UserInfo mUserInfo) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("uid", mUserInfo.getUid());
		values.put("access_token", mUserInfo.getAccess_token());
		values.put("expires_in", mUserInfo.getExpires_in());
		values.put("start_time", mUserInfo.getStart_time());
		values.put("screen_name", mUserInfo.getScreen_name());
		values.put("statuses_count", mUserInfo.getStatuses_count());
		values.put("favourites_count", mUserInfo.getFavourites_count());
		values.put("followers_count", mUserInfo.getFollowers_count());
		values.put("profile_image_url", mUserInfo.getProfile_image_url());
		db.update(DBInfo.Table.USER_TABLE, values, "uid = ?",
				new String[] { uid });
		db.close();
	}

	/**
	 * 保存登录用户的信息
	 * 
	 * @param mUserInfo
	 */
	public void saveUserInfo(UserInfo mUserInfo) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("uid", mUserInfo.getUid());
		values.put("access_token", mUserInfo.getAccess_token());
		values.put("expires_in", mUserInfo.getExpires_in());
		values.put("start_time", mUserInfo.getStart_time());
		values.put("screen_name", mUserInfo.getScreen_name());
		values.put("statuses_count", mUserInfo.getStatuses_count());
		values.put("favourites_count", mUserInfo.getFavourites_count());
		values.put("followers_count", mUserInfo.getFollowers_count());
		values.put("profile_image_url", mUserInfo.getProfile_image_url());
		db.insert(DBInfo.Table.USER_TABLE, null, values);
		db.close();
	}

	public UserInfo getUserInfo(String uidStr) {
		UserInfo mUserInfo = null;
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		Cursor mCursor = db.query(DBInfo.Table.USER_TABLE, null, "uid = ?",
				new String[] { uidStr }, null, null, null);
		Log.d(TAG, "Count:" + mCursor.getCount());
		if (mCursor.getCount() > 0) {
			mCursor.moveToFirst();
			String uid = mCursor.getString(mCursor.getColumnIndex("uid"));
			String access_token = mCursor.getString(mCursor
					.getColumnIndex("access_token"));
			String expires_in = mCursor.getString(mCursor
					.getColumnIndex("expires_in"));
			String start_time = mCursor.getString(mCursor
					.getColumnIndex("start_time"));
			String screen_name = mCursor.getString(mCursor
					.getColumnIndex("screen_name"));
			int statuses_count = mCursor.getInt(mCursor
					.getColumnIndex("statuses_count"));
			int favourites_count = mCursor.getInt(mCursor
					.getColumnIndex("favourites_count"));
			int followers_count = mCursor.getInt(mCursor
					.getColumnIndex("followers_count"));
			String profile_image_url = mCursor.getString(mCursor
					.getColumnIndex("profile_image_url"));
			mUserInfo = new UserInfo(uid, access_token, expires_in, start_time,
					screen_name, statuses_count, favourites_count,
					followers_count, profile_image_url);
			Log.d(TAG, uid + "   " + access_token + "   " + expires_in + "   "
					+ start_time + "   " + screen_name);
		}
		return mUserInfo;
	}

	public ArrayList<UserInfo> getAllUserInfo() {
		ArrayList<UserInfo> mUserInfos = new ArrayList<UserInfo>();
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		Cursor mCursor = db.query(DBInfo.Table.USER_TABLE, null, null, null,
				null, null, null);
		Log.d(TAG, "Count:" + mCursor.getCount());
		if (mCursor.getCount() > 0) {
			while (mCursor.moveToNext()) {
				String uid = mCursor.getString(mCursor.getColumnIndex("uid"));
				String access_token = mCursor.getString(mCursor
						.getColumnIndex("access_token"));
				String expires_in = mCursor.getString(mCursor
						.getColumnIndex("expires_in"));
				String start_time = mCursor.getString(mCursor
						.getColumnIndex("start_time"));
				String screen_name = mCursor.getString(mCursor
						.getColumnIndex("screen_name"));
				int statuses_count = mCursor.getInt(mCursor
						.getColumnIndex("statuses_count"));
				int favourites_count = mCursor.getInt(mCursor
						.getColumnIndex("favourites_count"));
				int followers_count = mCursor.getInt(mCursor
						.getColumnIndex("followers_count"));
				String profile_image_url = mCursor.getString(mCursor
						.getColumnIndex("profile_image_url"));
				UserInfo mUserInfo = new UserInfo(uid, access_token,
						expires_in, start_time, screen_name, statuses_count,
						favourites_count, followers_count, profile_image_url);
				Log.d(TAG, uid + "   " + access_token + "   " + expires_in
						+ "   " + start_time + "   " + screen_name);
				mUserInfos.add(mUserInfo);
			}
		}
		db.close();
		return mUserInfos;

	}

}
