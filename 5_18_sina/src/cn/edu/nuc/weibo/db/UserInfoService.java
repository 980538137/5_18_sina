package cn.edu.nuc.weibo.db;

import cn.edu.nuc.weibo.bean.UserInfo;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class UserInfoService {

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
	
	
	
	

}
