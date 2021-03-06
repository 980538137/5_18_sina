package cn.edu.nuc.weibo.ui;

import java.util.List;

import cn.edu.nuc.weibo.R;
import cn.edu.nuc.weibo.adapter.AccountAdapter;
import cn.edu.nuc.weibo.app.WeiboApplication;
import cn.edu.nuc.weibo.bean.UserInfo;
import cn.edu.nuc.weibo.logic.MainService;
import cn.edu.nuc.weibo.util.CheckNetState;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class AccountActivity extends Activity {
	private static final String TAG = "AccountActivity";
	private Button mBackBtn;
	private Button mAddAcountBtn;
	private ListView mAccountListView;
	private List<UserInfo> mUserInfos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		WeiboApplication.mActivities.add(this);
		setContentView(R.layout.account);
		initView();
	}

	private void initView() {
		mBackBtn = (Button) this.findViewById(R.id.btn_title_back);
		mAddAcountBtn = (Button) this.findViewById(R.id.btn_title_add_acount);
		mBackBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AccountActivity.this.finish();
			}
		});
		mAddAcountBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!CheckNetState.checkNetworkState(AccountActivity.this)) {
					Toast.makeText(getApplicationContext(), "网络异常，请检查网络配置",
							Toast.LENGTH_SHORT).show();
				} else {
					Intent mIntent = new Intent(AccountActivity.this,
							WebViewActivity.class);
					startActivity(mIntent);
				}

			}
		});
		mUserInfos = WeiboApplication.mUserInfoService.getAllUserInfo();
		Log.d(TAG, "UserInfoSize:" + mUserInfos.size());
		mAccountListView = (ListView) this.findViewById(R.id.lv_account_list);
		mAccountListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (CheckNetState.checkNetworkState(AccountActivity.this)) {
					UserInfo mUserInfo = mUserInfos.get(position);
					WeiboApplication.mCurrentUserInfo = mUserInfo;
					SharedPreferences preferences = getSharedPreferences(
							"token_expires_in", Context.MODE_PRIVATE);
					Editor editor = preferences.edit();
					editor.putString("token", mUserInfo.getAccess_token());
					editor.putString("expires_in", mUserInfo.getExpires_in());
					editor.putString("uid", mUserInfo.getUid());
					editor.putLong("start_time",
							Long.valueOf(mUserInfo.getStart_time()));
					editor.commit();
				
					if (WeiboApplication.mActivities.size() > 0) {
						for (Activity activity : WeiboApplication.mActivities) {
							activity.finish();
						}
					}
					
					AccountActivity.this.startActivity(new Intent(
							AccountActivity.this, MainTabActivity.class));
				} else {
					Toast.makeText(getApplicationContext(), "网络异常，请检查网络配置",
							Toast.LENGTH_SHORT).show();
				}

			}
		});

		mAccountListView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				return false;
			}
		});

		AccountAdapter mAdapter = new AccountAdapter(this, mUserInfos);
		mAccountListView.setAdapter(mAdapter);
	}
}
