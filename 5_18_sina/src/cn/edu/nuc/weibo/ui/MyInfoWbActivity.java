package cn.edu.nuc.weibo.ui;

import java.util.ArrayList;
import java.util.HashMap;

import cn.edu.nuc.weibo.R;
import cn.edu.nuc.weibo.adapter.WeiboAdapter;
import cn.edu.nuc.weibo.bean.Status;
import cn.edu.nuc.weibo.bean.Task;
import cn.edu.nuc.weibo.logic.IWeiboActivity;
import cn.edu.nuc.weibo.logic.MainService;
import cn.edu.nuc.weibo.widget.PullToRefreshListView;
import cn.edu.nuc.weibo.widget.PullToRefreshListView.OnRefreshListener;
import cn.edu.nuc.weibo.widget.PullToRefreshListView.onLoadOldListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MyInfoWbActivity extends Activity implements IWeiboActivity,
		OnClickListener {
	private Button mBackBtn;
	private Button mHomeBtn;
	private PullToRefreshListView mListView;
	private WeiboAdapter mAdapter;
	private LinearLayout mLoadingLayout;

	private ArrayList<Status> mStatus;
	// 加载数据时用的maxid
	private long max_id = 0;

	// 加载数据状态
	public static final int INITIATE = 1;
	public static final int MORE_NEW = 2;
	public static final int MORE_OLD = 3;
	private int current_state = INITIATE;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myinfo_wb);
		init();
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		mBackBtn = (Button) this.findViewById(R.id.btn_myinfo_wb_title_back);
		mHomeBtn = (Button) this.findViewById(R.id.btn_myinfo_wb_title_home);
		mBackBtn.setOnClickListener(this);
		mHomeBtn.setOnClickListener(this);
		mLoadingLayout = (LinearLayout) this.findViewById(R.id.ll_loading);
		mListView = (PullToRefreshListView) this
				.findViewById(R.id.myinfo_lv_wb);
		mListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				current_state = MORE_NEW;
				createTask(current_state, max_id);
			}
		});
		mListView.setOnLoadOldListener(new onLoadOldListener() {

			@Override
			public void onLoadOld() {
				// TODO Auto-generated method stub
				current_state = MORE_OLD;
				createTask(current_state, max_id);
			}
		});
		createTask(INITIATE, max_id);
	}

	@Override
	public void refresh(Object... params) {
		// TODO Auto-generated method stub
		mStatus = (ArrayList<Status>) params[0];
		if (mStatus != null && mStatus.size() != 0) {
			max_id = Long.parseLong(mStatus.get(mStatus.size() - 1).getMid()) - 1;
			switch (current_state) {
			case INITIATE:
				mLoadingLayout.setVisibility(View.GONE);
				mAdapter = new WeiboAdapter(mStatus, this);
				mListView.setAdapter(mAdapter);
				break;
			case MORE_NEW:
				mAdapter = new WeiboAdapter(mStatus, this);
				mListView.setAdapter(mAdapter);
				mListView.onRefreshComplete();
				break;
			case MORE_OLD:
				mAdapter.refresh(mStatus);
				mListView.setSelection(mAdapter.getCount() - 20);
				mListView.resetFooter();
				break;

			default:
				break;
			}
		} else {
			mListView.onRefreshComplete();
			Toast.makeText(this, R.string.has_get_all, Toast.LENGTH_SHORT)
					.show();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_myinfo_wb_title_back:
			this.finish();
			break;
		case R.id.btn_myinfo_wb_title_home:
			this.finish();
			break;

		default:
			break;
		}

	}

	private void createTask(int mCurrentState, long max_id) {
		Task mTask = null;
		HashMap<String, Object> mTaskParams = new HashMap<String, Object>();
		switch (mCurrentState) {
		case INITIATE:
			mTaskParams.put("state", INITIATE);
			this.startService(new Intent(this, MainService.class));
			break;
		case MORE_NEW:
			mTaskParams.put("state", MORE_NEW);
			break;
		case MORE_OLD:
			mTaskParams.put("state", MORE_OLD);
			mTaskParams.put("max_id", String.valueOf(max_id));
			break;

		default:
			break;
		}
		mTask = new Task(Task.WEIBO_MYINFO_STATUSES, mTaskParams);
		MainService.addTask(mTask);
		MainService.addActivity(this);
	}

}
