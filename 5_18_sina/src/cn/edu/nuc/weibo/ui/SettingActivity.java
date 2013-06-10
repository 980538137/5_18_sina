package cn.edu.nuc.weibo.ui;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import cn.edu.nuc.weibo.R;

public class SettingActivity extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.setting_preference);
	}

}
