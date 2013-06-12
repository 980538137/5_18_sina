package cn.edu.nuc.weibo.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import cn.edu.nuc.weibo.R;
import cn.edu.nuc.weibo.util.CheckNetState;

import com.weibo.net.AccessToken;
import com.weibo.net.Weibo;

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
						if (!CheckNetState.checkNetworkState(LoginActivity.this)) {
							Toast.makeText(getApplicationContext(), "网络异常，请检查网络配置",
									Toast.LENGTH_SHORT).show();
						}else {
							startActivity(new Intent(LoginActivity.this,
									WebViewActivity.class));
							LoginActivity.this.finish();
						}
						

					}
				});
	}

}
