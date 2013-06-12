package cn.edu.nuc.weibo.app;

import org.json.JSONException;

import com.weibo.net.Weibo;
import com.weibo.net.WeiboException;

import cn.edu.nuc.weibo.bean.User;
import cn.edu.nuc.weibo.bean.UserInfo;
import cn.edu.nuc.weibo.db.UserInfoService;
import cn.edu.nuc.weibo.db.WeiboHomeService;
import cn.edu.nuc.weibo.loadimg.AsyncImageLoader;
import cn.edu.nuc.weibo.parsewb.ParseTimeManager;
import cn.edu.nuc.weibo.parsewb.WeiboParseManager;
import cn.edu.nuc.weibo.util.JsonUtils;
import cn.edu.nuc.weibo.util.WeiboUtils;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class WeiboApplication extends Application {
	public static AsyncImageLoader asyncImageLoader = null;
	public static Context mContext = null;
	public static WeiboParseManager weiboParseManager = null;
	public static ParseTimeManager parseTimeManager = null;
	public static Handler handler = null;
	public static final int TIME_OUT = 1;
	public static UserInfoService mUserInfoService = null;
	public static WeiboHomeService mWeiboHomeService = null;
	// 当前登录用户信息
	public static UserInfo mCurrentUserInfo;

	class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			int message = (Integer) msg.obj;
			if (message == TIME_OUT) {
				Toast.makeText(getApplicationContext(), "网络异常，请检查网络配置",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mContext = this.getApplicationContext();
		initWeiboParse();
		handler = new MyHandler();
		initDatabase();
		
		
		SharedPreferences sp = this.getSharedPreferences("token_expires_in",
				Context.MODE_PRIVATE);
		final String token = sp.getString("token", "");
		final String uid = sp.getString("uid", "");
		if (token != null && token != "") {
			mCurrentUserInfo = WeiboApplication.mUserInfoService
					.getUserInfo(uid);
		}

	}

	private void initWeiboParse() {
		asyncImageLoader = new AsyncImageLoader(mContext);
		weiboParseManager = new WeiboParseManager();
		parseTimeManager = new ParseTimeManager();
	}

	private void initDatabase() {
		mUserInfoService = new UserInfoService(this);
		mWeiboHomeService = new WeiboHomeService(this);
	}

}
