package cn.edu.nuc.weibo.ui;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import cn.edu.nuc.weibo.R;
import cn.edu.nuc.weibo.app.WeiboApplication;
import cn.edu.nuc.weibo.bean.User;
import cn.edu.nuc.weibo.bean.UserInfo;
import cn.edu.nuc.weibo.db.DBInfo;
import cn.edu.nuc.weibo.db.UserInfoService;
import cn.edu.nuc.weibo.db.WeiboHomeService;
import cn.edu.nuc.weibo.util.JsonUtils;
import cn.edu.nuc.weibo.util.WeiboUtils;

import com.weibo.net.AccessToken;
import com.weibo.net.DialogError;
import com.weibo.net.Oauth2AccessTokenHeader;
import com.weibo.net.Utility;
import com.weibo.net.Weibo;
import com.weibo.net.WeiboDialogListener;
import com.weibo.net.WeiboException;
import com.weibo.net.WeiboParameters;

public class WebViewActivity extends Activity implements WeiboDialogListener {
	private static final String TAG = "WebViewActivity";
	private WebView webView = null;
	private ProgressDialog pd = null;
	private WeiboDialogListener dialogListener = null;
	// app_key
	private static final String CONSUMER_KEY = "2905041652";
	// app_secret
	private static final String CONSUMER_SECRET = "7d2f02b441c028a40cd12f06fa0bdf87";
	private static final String MREDIRECTURL = "http://www.sina.com";
	private Weibo weibo = null;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);
		dialogListener = this;
		weibo = Weibo.getInstance();
		weibo.setupConsumerConfig(CONSUMER_KEY, CONSUMER_SECRET);
		weibo.setRedirectUrl(MREDIRECTURL);
		Utility.setAuthorization(new Oauth2AccessTokenHeader());
		webView = (WebView) this.findViewById(R.id.wv_id);
		webView.setHorizontalScrollBarEnabled(false);
		webView.setVerticalScrollBarEnabled(false);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new MyWebViewClient());
		webView.loadUrl(getOauthUrl());
		pd = new ProgressDialog(this);
		pd.requestWindowFeature(Window.FEATURE_NO_TITLE);
		pd.setMessage("正在载入...");

	}

	/**
	 * 获取OauthUrl
	 * 
	 * @return
	 */
	@SuppressWarnings("static-access")
	private String getOauthUrl() {
		CookieSyncManager.createInstance(this);
		WeiboParameters parameters = new WeiboParameters();
		parameters.add("client_id", Weibo.getInstance().getAppKey());
		parameters.add("response_type", "token");
		parameters.add("redirect_uri", Weibo.getInstance().getRedirectUrl());
		parameters.add("display", "mobile");
		String url = Weibo.URL_OAUTH2_ACCESS_AUTHORIZE + "?"
				+ Utility.encodeUrl(parameters);

		return url;
	}

	private void handleRedirectUrl(WebView view, String url) {
		Bundle values = Utility.parseUrl(url);

		String error = values.getString("error");
		String error_code = values.getString("error_code");

		if (error == null && error_code == null) {
			dialogListener.onComplete(values);
		} else if (error.equals("access_denied")) {
			// 用户或授权服务器拒绝授予数据访问权限
			dialogListener.onCancel();
		} else {
			dialogListener.onWeiboException(new WeiboException(error, Integer
					.parseInt(error_code)));
		}
	}

	class MyWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.d(TAG, "shouldOverrideUrlLoading");
			// 待后台增加对默认重定向地址的支持后修改下面的逻辑
			String s = Weibo.getInstance().getRedirectUrl();
			if (url.startsWith(Weibo.getInstance().getRedirectUrl())) {
				handleRedirectUrl(view, url);
				return true;
			}
			// launch non-dialog URLs in a full browser
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
			return true;
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			Log.d(TAG, "onReceivedError");
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			Log.d(TAG, "onPageStarted");
			/**
			 * 点击授权，url正确
			 */
			if (url.startsWith(Weibo.getInstance().getRedirectUrl())) {
				handleRedirectUrl(view, url);
				view.stopLoading();
				return;
			}
			pd.show();
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			Log.d(TAG, "onPageFinished");
			pd.dismiss();
		}
	}

	@Override
	public void onComplete(Bundle values) {
		/**
		 * 在这里要save the access_token
		 */
		final String token = values.getString("access_token");
		final String expires_in = values.getString("expires_in");
		final String uid = values.getString("uid");
		final long start_time = System.currentTimeMillis();
		Log.d(TAG, "uid:" + uid);

		// 获取用户信息
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					String msgStr = WeiboUtils.getUserInfo(Weibo.getInstance(),
							Weibo.getAppKey(), token, uid);
					User mUser = JsonUtils.parseJsonFromUserInfo(msgStr);
					Log.d(TAG, mUser.getScreen_name());
					UserInfo mUserInfo = new UserInfo(uid, token, expires_in,
							String.valueOf(start_time), mUser.getScreen_name(),
							mUser.getStatuses_count(), mUser
									.getFavourites_count(), mUser
									.getFollowers_count(), mUser
									.getProfile_image_url());
					WeiboApplication.mCurrentUserInfo = mUserInfo;
					Log.d(TAG,
							"UserName:"
									+ WeiboApplication.mCurrentUserInfo
											.getScreen_name());
					WeiboApplication.mUserInfoService = new UserInfoService(
							WebViewActivity.this);
					WeiboApplication.mWeiboHomeService = new WeiboHomeService(
							WebViewActivity.this);
					SQLiteDatabase db = openOrCreateDatabase("sina_weibo.db",
							Context.MODE_PRIVATE, null);
					
					String HOME_TABLE = WeiboApplication.mCurrentUserInfo
							.getScreen_name() + "_home_table";
					String HOME_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
							+ HOME_TABLE
							+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT,id LONG,mid TEXT,portrait TEXT,username TEXT,wb_time TEXT,wb_content TEXT,wb_content_pic TEXT,wb_middle_pic TEXT,wb_subcontent TEXT,wb_subcontent_subpic TEXT,wb_submiddle_subpic TEXT,wb_subfrom TEXT,wb_subredirect INTEGER,wb_subcomment INTEGER,wb_subattitude INTEGER,wb_from TEXT,wb_redirect INTEGER,wb_comment INTEGER,wb_attitude INTEGER,verified INTEGER,verified_type INTEGER)";
					db.execSQL("DROP TABLE IF EXISTS "
							+ HOME_TABLE);
					db.execSQL(HOME_TABLE_CREATE);
					db.close();

					if (WeiboApplication.mUserInfoService.isHasUser(uid)) {
						WeiboApplication.mUserInfoService.updateUserInfo(uid,
								mUserInfo);
					} else {
						WeiboApplication.mUserInfoService
								.saveUserInfo(mUserInfo);
					}
					if (WeiboApplication.mActivities.size() > 0) {
						for (Activity activity : WeiboApplication.mActivities) {
							activity.finish();
						}
					}
					
					WebViewActivity.this.startActivity(new Intent(
							WebViewActivity.this, MainTabActivity.class));
					WebViewActivity.this.finish();

				} catch (WeiboException e) {
					Log.d(TAG, "WeiboException");
					e.printStackTrace();
				} catch (JSONException e) {
					Log.d(TAG, "JsonException");
					e.printStackTrace();
				}
			}
		}).start();

		// 保存登录用户的相关信息
		SharedPreferences preferences = getSharedPreferences(
				"token_expires_in", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString("token", token);
		editor.putString("expires_in", expires_in);
		editor.putString("uid", uid);
		editor.putLong("start_time", start_time);
		editor.commit();

		AccessToken accessToken = new AccessToken(token, Weibo.getAppSecret());
		accessToken.setExpiresIn(expires_in);
		Weibo.getInstance().setAccessToken(accessToken);

	}

	@Override
	public void onWeiboException(WeiboException e) {

	}

	@Override
	public void onError(DialogError e) {

	}

	@Override
	public void onCancel() {

	}

}
