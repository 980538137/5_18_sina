package cn.edu.nuc.weibo.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.edu.nuc.weibo.R;
import cn.edu.nuc.weibo.adapter.WeiboAdapter.PicOnClickListener;
import cn.edu.nuc.weibo.bean.Retweeted_Status;
import cn.edu.nuc.weibo.bean.Status;
import cn.edu.nuc.weibo.bean.User;
import cn.edu.nuc.weibo.loadimg.SimpleImageLoader;
import cn.edu.nuc.weibo.parsewb.SimpleParseManager;

public class FollowAdapter extends BaseAdapter {
	private ArrayList<User> mUsers;
	private Context mContext;
	private LayoutInflater mInflater;
	public static final int PORTRAIT_IMG = 1;
	public static final int CONTENT_IMG = 2;

	public FollowAdapter(ArrayList<User> mUsers, Context mContext) {
		this.mUsers = mUsers;
		this.mContext = mContext;
		mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mUsers.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mUsers.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return mUsers == null ? null : mUsers.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return createViewFromResource(position, convertView);
	}

	private View createViewFromResource(int position, View convertView) {
		FollowHolder mFollowHolder = null;
		User mUser = mUsers.get(position);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.follow_item, null);
			mFollowHolder = new FollowHolder();
			findViews(mFollowHolder, convertView);
			convertView.setTag(mFollowHolder);
		} else {
			mFollowHolder = (FollowHolder) convertView.getTag();
		}
		setValues(mUser, mFollowHolder, convertView);
		return convertView;
	}

	private void setValues(User mUser, FollowHolder mFollowHolder,
			View convertView) {
		final String portraitURl = mUser.getProfile_image_url();
		SimpleImageLoader.showImg(mFollowHolder.iv_portrait, portraitURl,
				mContext, PORTRAIT_IMG);

		if (mUser.isVerified() && mUser.getVerified_type() == 0) {
			mFollowHolder.iv_portrait_v_red.setVisibility(View.GONE);
			mFollowHolder.iv_portrait_v_blue.setVisibility(View.GONE);
			mFollowHolder.iv_portrait_v_yellow.setVisibility(View.VISIBLE);
		} else if (mUser.isVerified() && mUser.getVerified_type() != 0) {
			mFollowHolder.iv_portrait_v_red.setVisibility(View.GONE);
			mFollowHolder.iv_portrait_v_yellow.setVisibility(View.GONE);
			mFollowHolder.iv_portrait_v_blue.setVisibility(View.VISIBLE);
		} else {
			mFollowHolder.iv_portrait_v_red.setVisibility(View.GONE);
			mFollowHolder.iv_portrait_v_yellow.setVisibility(View.GONE);
			mFollowHolder.iv_portrait_v_blue.setVisibility(View.GONE);
		}
		mFollowHolder.mTvScreenName.setText(mUser.getScreen_name());
		mFollowHolder.mTvDescription.setText(mUser.getDomain());
//		if (mUser.isFollowing()) {
//
//			mFollowHolder.mAttendBtn.setText(R.string.cancel_attend);
//		} else {
//			mFollowHolder.mAttendBtn
//					.setBackgroundResource(R.drawable.contacts_mebutton_background);
//			mFollowHolder.mAttendBtn.setText(R.string.attend);
//		}
//		mFollowHolder.mAttendBtn.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Toast.makeText(mContext, "attend", Toast.LENGTH_SHORT).show();
//			}
//		});
	}

	private void findViews(FollowHolder followHolder, View convertView) {
		followHolder.iv_portrait = (ImageView) convertView
				.findViewById(R.id.iv_portrait);
		followHolder.iv_portrait_v_blue = (ImageView) convertView
				.findViewById(R.id.iv_portrait_v_blue);
		followHolder.iv_portrait_v_red = (ImageView) convertView
				.findViewById(R.id.iv_portrait_v_red);
		followHolder.iv_portrait_v_yellow = (ImageView) convertView
				.findViewById(R.id.iv_portrait_v_yellow);
		followHolder.mTvScreenName = (TextView) convertView
				.findViewById(R.id.tv_screen_name);
		followHolder.mTvDescription = (TextView) convertView
				.findViewById(R.id.tv_description);
//		followHolder.mAttendBtn = (Button) convertView
//				.findViewById(R.id.btn_attention);
	}

	private class FollowHolder {
		ImageView iv_portrait;
		ImageView iv_portrait_v_blue;
		ImageView iv_portrait_v_red;
		ImageView iv_portrait_v_yellow;
		TextView mTvScreenName;
		TextView mTvDescription;
		Button mAttendBtn;
	}

	// 在加载更多微博时，调用此方法更新adapter
	public void refresh(List<User> new_user) {
		mUsers.addAll(new_user);
		this.notifyDataSetChanged();

	}

}
