package cn.edu.nuc.weibo.ui;

import org.json.JSONException;

import com.weibo.net.AccessToken;
import com.weibo.net.Oauth2AccessTokenHeader;
import com.weibo.net.Utility;
import com.weibo.net.Weibo;
import com.weibo.net.WeiboException;

import cn.edu.nuc.weibo.R;
import cn.edu.nuc.weibo.app.WeiboApplication;
import cn.edu.nuc.weibo.db.UserInfoService;
import cn.edu.nuc.weibo.logic.MainService;
import cn.edu.nuc.weibo.util.JsonUtils;
import cn.edu.nuc.weibo.util.WeiboUtils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class LoginActivity extends Activity {
	private static final String TAG = "LoginActivity";
	private SharedPreferences sp = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		sp = this
				.getSharedPreferences("token_expires_in", Context.MODE_PRIVATE);
		final String token = sp.getString("token", "");
		String expires_in = sp.getString("expires_in", "");
		long start_time = sp.getLong("start_time", 0);
		final String uid = sp.getString("uid", "");

		if (token != null && token != "") {
			if ((System.currentTimeMillis() - start_time) > Long
					.parseLong(expires_in) * 1000) {
				startActivity(new Intent(LoginActivity.this,
						WebViewActivity.class));
				this.finish();
			} else {
				Intent intent = new Intent(LoginActivity.this,
						MainTabActivity.class);
				AccessToken accessToken = new AccessToken(token,
						Weibo.getAppSecret());
				accessToken.setExpiresIn(expires_in);
				Weibo.getInstance().setAccessToken(accessToken);
				startActivity(intent);
				this.finish();
			}

		}
		this.findViewById(R.id.btn_login).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						startActivity(new Intent(LoginActivity.this,
								WebViewActivity.class));
						LoginActivity.this.finish();

					}
				});
	}

}
