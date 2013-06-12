package cn.edu.nuc.weibo.adapter;

import java.util.List;

import cn.edu.nuc.weibo.R;
import cn.edu.nuc.weibo.bean.UserInfo;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AccountAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<UserInfo> mUserInfos;

	public AccountAdapter(Context mContext, List<UserInfo> mUserInfos) {
		mInflater = LayoutInflater.from(mContext);
		this.mUserInfos = mUserInfos;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mUserInfos.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mUserInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return createViewFromResource(position, convertView);
	}

	private View createViewFromResource(int position, View convertView) {
		AccountHolder mAccountHolder = null;
		UserInfo mUserInfo = mUserInfos.get(position);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.account_item, null);
			mAccountHolder = new AccountHolder();
			findViews(mAccountHolder, convertView);
			convertView.setTag(mAccountHolder);
		} else {
			mAccountHolder = (AccountHolder) convertView.getTag();
		}
		setValues(mUserInfo, mAccountHolder);
		return convertView;

	}

	private void setValues(UserInfo mInfo, AccountHolder mAccountHolder) {
		mAccountHolder.mAccountName.setText(mInfo.getScreen_name());
	}

	private void findViews(AccountHolder mAccountHolder, View convertView) {
		mAccountHolder.mAccountName = (TextView) convertView
				.findViewById(R.id.tv_account_name);
	}

	private class AccountHolder {
		TextView mAccountName;
	}

}
