package cn.edu.nuc.weibo.ui;

import cn.edu.nuc.weibo.R;
import cn.edu.nuc.weibo.app.WeiboApplication;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class SettingActivity extends BaseActivity {
	private LinearLayout mAccountBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		WeiboApplication.mActivities.add(this);
		mAccountBtn = (LinearLayout) this.findViewById(R.id.btn_account);
		mAccountBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent mIntent = new Intent(SettingActivity.this,
						AccountActivity.class);
				startActivity(mIntent);
			}
		});
	}

}
