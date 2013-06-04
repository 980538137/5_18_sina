package cn.edu.nuc.weibo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.edu.nuc.weibo.R;
import cn.edu.nuc.weibo.app.WeiboApplication;
import cn.edu.nuc.weibo.bean.User;
import cn.edu.nuc.weibo.loadimg.SimpleImageLoader;
import cn.edu.nuc.weibo.logic.IWeiboActivity;

public class MyInfoActivity extends BaseActivity implements IWeiboActivity {
	private ImageView mPortraitImage;
	private TextView mUserName;
	private TextView mWbNum;
	private TextView mFavNum;
	private TextView mFollowNum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myinfo);
		init();
	}

	@Override
	public void init() {
		mPortraitImage = (ImageView) this.findViewById(R.id.iv_myinfo_portrait);
		mUserName = (TextView) this.findViewById(R.id.tv_myinfo_username);
		mWbNum = (TextView) this.findViewById(R.id.tv_myinfo_wb_num);
		mFavNum = (TextView) this.findViewById(R.id.tv_myinfo_favorite_num);
		mFollowNum = (TextView) this.findViewById(R.id.tv_myinfo_follow_num);
		User mUser = WeiboApplication.mCurrentUser;
		SimpleImageLoader.showImg(mPortraitImage, mUser.getProfile_image_url(),
				this, 1);
		mUserName.setText(mUser.getScreen_name());
		mWbNum.setText(mUser.getStatuses_count() + "");
		mFavNum.setText(mUser.getFavourites_count() + "");
		mFollowNum.setText(mUser.getFollowers_count() + "");
	}

	@Override
	public void refresh(Object... params) {

	}

	public void onMyClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.ll_myinfo_wb:
			Intent mIntent = new Intent(this, MyInfoWbActivity.class);
			startActivity(mIntent);
			break;
		case R.id.ll_myinfo_fav:
			Intent mIntent2 = new Intent(this, MyInfoFavActivity.class);
			startActivity(mIntent2);
			break;
		case R.id.ll_myinfo_follow:
			Intent mIntent3 = new Intent(this, MyInfoFollowActivity.class);
			startActivity(mIntent3);
			break;

		default:
			break;
		}

	}

}
