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
		db.insert(DBInfo.Table.USER_TABLE, null, values);
		db.close();
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
				UserInfo mUserInfo = new UserInfo(uid, access_token,
						expires_in, start_time);
				Log.d(TAG, uid + "   " + access_token + "   "
						+ expires_in + "   " + start_time);
				mUserInfos.add(mUserInfo);
			}
		}
		return mUserInfos;

	}

}
