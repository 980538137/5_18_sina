package cn.edu.nuc.weibo.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.edu.nuc.weibo.R;
import cn.edu.nuc.weibo.adapter.MsgAtAdapter;
import cn.edu.nuc.weibo.adapter.MsgCommentAdapter;
import cn.edu.nuc.weibo.app.WeiboApplication;
import cn.edu.nuc.weibo.bean.Comment;
import cn.edu.nuc.weibo.bean.Status;
import cn.edu.nuc.weibo.bean.Task;
import cn.edu.nuc.weibo.logic.IWeiboActivity;
import cn.edu.nuc.weibo.logic.MainService;
import cn.edu.nuc.weibo.widget.PullToRefreshListView;
import cn.edu.nuc.weibo.widget.PullToRefreshListView.OnRefreshListener;
import cn.edu.nuc.weibo.widget.PullToRefreshListView.onLoadOldListener;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MsgActivity extends BaseActivity implements IWeiboActivity,
		OnClickListener {
	private static final String TAG = "MsgActivity";
	// ViewPager
	private ViewPager mViewPager = null;
	private List<View> mViews = null;

	// 四个界面的图片标识
	private ImageView iv_at = null;
	private ImageView iv_comment = null;
	private ImageView iv_message = null;
	private ImageView iv_notification = null;
	private ImageView iv_cursor = null;
	private ImageView iv_cursor_monitor = null;

	// 四个界面中的ProgressBar
	private ProgressBar pb_at = null;
	private ProgressBar pb_comment = null;

	// 四个界面的标识
	public static final int AT = 0;
	public static final int COMMENT = 1;
	public static final int MESSAGE = 2;
	public static final int NOTIFICATION = 3;
	private int currentIndex;

	// MsgActivity中四个界面的ListView
	private PullToRefreshListView lv_msg_at = null;
	private PullToRefreshListView lv_msg_comment = null;
	private PullToRefreshListView lv_msg_message = null;
	private PullToRefreshListView lv_msg_notification = null;

	// @界面PopupWindow中的按钮
	private Button btn_at_wb_all = null;
	private Button btn_at_wb_friends = null;
	private Button btn_at_wb_original = null;
	private Button btn_at_comment_all = null;
	private Button btn_at_comment_friends = null;

	// comment界面PopupWindow中的界面
	private Button btn_comment_to_me_all = null;
	private Button btn_comment_to_me_friends = null;
	private Button btn_comment_by_me_all = null;

	// 定义界面中的PopupWindow
	private PopupWindow popupWindow_at;
	private PopupWindow popupWindow_comment;

	// 加载数据时用到的max_id
	private long at_max_id;
	private long comment_max_id;

	// @界面中加载数据时的状态
	public static final int AT_INITIATE = 1;
	public static final int AT_MORE_NEW = 2;
	public static final int AT_MORE_OLD = 3;
	private int at_current_state = AT_INITIATE;
	private TextView mAtTitleText;
	// 标识AT界面加载微博还是加载评论
	private static final int AT_WEIBO = 1;
	private static final int AT_COMMENT = 2;
	private int mAtMode = AT_WEIBO;

	// comment界面加载数据时的状态
	public static final int COMMENT_INITIATE = 1;
	public static final int COMMENT_MORE_NEW = 2;
	public static final int COMMENT_MORE_OLD = 3;
	private int comment_current_state = COMMENT_INITIATE;
	private TextView mCommentTitleText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WeiboApplication.mActivities.add(this);
		setContentView(R.layout.msg);
		init();
		createAtTask(at_current_state, at_max_id, true);
	}

	/**
	 * 初始化方法
	 */
	@Override
	public void init() {
		initImageView();
		initViewPager();
	}

	/**
	 * 初始化ImageView
	 */
	private void initImageView() {
		iv_at = (ImageView) this.findViewById(R.id.iv_at);
		iv_comment = (ImageView) this.findViewById(R.id.iv_comment);
		iv_message = (ImageView) this.findViewById(R.id.iv_message);
		iv_notification = (ImageView) this.findViewById(R.id.iv_notification);

		iv_at.setOnClickListener(new TitleOnClickListener(AT));
		iv_comment.setOnClickListener(new TitleOnClickListener(COMMENT));
		iv_message.setOnClickListener(new TitleOnClickListener(MESSAGE));
		iv_notification.setOnClickListener(new TitleOnClickListener(
				NOTIFICATION));
		iv_cursor_monitor = (ImageView) this
				.findViewById(R.id.iv_cursor_monitor);
		iv_cursor = (ImageView) this.findViewById(R.id.iv_cursor);
	}

	/**
	 * 初始化ViewPager
	 */
	private void initViewPager() {
		mViewPager = (ViewPager) this.findViewById(R.id.vp);
		mViews = new ArrayList<View>();
		// 获取LayoutInflater对象
		LayoutInflater inflater = this.getLayoutInflater();
		// 初始化ViewPager的每个页面
		initAtView(inflater, mViews);
		initCommentView(inflater, mViews);
		initMessageView(inflater, mViews);
		initNotificationView(inflater, mViews);
		// 对ViewPager进行配置
		mViewPager.setAdapter(new MyPagerAdapter(mViews));
		mViewPager.setCurrentItem(AT);
		mViewPager.setOnPageChangeListener(new MyPageChangeListener());
	}

	/**
	 * 新建@界面任务
	 * 
	 * @param current_state
	 * @param max_id
	 * @param isStatus
	 */
	private void createAtTask(int current_state, long max_id, boolean isStatus) {
		Task task = null;
		HashMap<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put("access_token", WeiboApplication.mCurrentUserInfo.getAccess_token());
		switch (current_state) {
		case AT_INITIATE:
			taskParams.put("state", AT_INITIATE);
			this.startService(new Intent(this, MainService.class));
			break;
		case AT_MORE_NEW:
			taskParams.put("state", AT_MORE_NEW);
			break;
		case AT_MORE_OLD:
			taskParams.put("state", AT_MORE_OLD);
			taskParams.put("max_id", String.valueOf(max_id));
			break;
		}
		if (isStatus) {
			task = new Task(Task.WEIBO_MSG_AT_WB, taskParams);
		} else {
			task = new Task(Task.WEIBO_MSG_AT_COMMENT, taskParams);
		}
		MainService.addTask(task);
		MainService.addActivity(this);
	}

	/**
	 * 创建comment界面任务
	 * 
	 * @param mCurrentState
	 * @param mMaxId
	 * @param isToMe
	 */
	private void createCommentTask(int mCurrentState, long mMaxId,
			boolean isToMe) {
		Task mTask = null;
		HashMap<String, Object> mTaskParams = new HashMap<String, Object>();
		mTaskParams.put("access_token", WeiboApplication.mCurrentUserInfo.getAccess_token());
		switch (mCurrentState) {
		case COMMENT_INITIATE:
			mTaskParams.put("state", COMMENT_INITIATE);
			break;
		case COMMENT_MORE_NEW:
			mTaskParams.put("state", COMMENT_MORE_NEW);
			break;
		case COMMENT_MORE_OLD:
			mTaskParams.put("state", COMMENT_MORE_OLD);
			mTaskParams.put("max_id", String.valueOf(mMaxId));
			break;
		default:
			break;
		}
		if (isToMe) {
			mTask = new Task(Task.WEIBO_MSG_COMMENT_TO_ME, mTaskParams);
		} else {
			mTask = new Task(Task.WEIBO_MSG_COMMENT_BY_ME, mTaskParams);
		}
		MainService.addTask(mTask);
		MainService.addActivity(this);

	}

	/**
	 * 初始化@界面
	 * 
	 * @param inflater
	 * @param listViews
	 */
	private void initAtView(LayoutInflater inflater, List<View> listViews) {
		View at_view = inflater.inflate(R.layout.msg_at, null);
		listViews.add(at_view);
		mAtTitleText = (TextView) at_view.findViewById(R.id.tv_msg_at_top);
		lv_msg_at = (PullToRefreshListView) at_view
				.findViewById(R.id.lv_msg_at);
		// 下拉刷新
		lv_msg_at.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				at_current_state = AT_MORE_NEW;
				switch (mAtMode) {
				case AT_WEIBO:
					createAtTask(at_current_state, at_max_id, true);
					break;
				case AT_COMMENT:
					createAtTask(at_current_state, at_max_id, false);
					break;

				default:
					break;
				}

			}
		});
		lv_msg_at.setOnLoadOldListener(new onLoadOldListener() {

			@Override
			public void onLoadOld() {
				at_current_state = AT_MORE_OLD;
				switch (mAtMode) {
				case AT_WEIBO:
					createAtTask(at_current_state, at_max_id, true);
					break;
				case AT_COMMENT:
					createAtTask(at_current_state, at_max_id, false);
					break;

				default:
					break;
				}

			}
		});

		pb_at = (ProgressBar) at_view.findViewById(R.id.pb_at);

		View popup_window_at = LayoutInflater.from(this).inflate(
				R.layout.popup_window_at, null);
		popupWindow_at = createPopupWindow(popup_window_at, 230,
				LayoutParams.WRAP_CONTENT);
		final LinearLayout ll_msg_at_wb_comment = (LinearLayout) at_view
				.findViewById(R.id.ll_msg_at_wb_comment);
		ll_msg_at_wb_comment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				popupWindow_at.showAsDropDown(ll_msg_at_wb_comment, 10, -5);
			}
		});
		btn_at_wb_all = (Button) popup_window_at
				.findViewById(R.id.btn_at_wb_all);
		btn_at_wb_friends = (Button) popup_window_at
				.findViewById(R.id.btn_at_wb_friends);
		btn_at_wb_original = (Button) popup_window_at
				.findViewById(R.id.btn_at_wb_original);
		btn_at_comment_all = (Button) popup_window_at
				.findViewById(R.id.btn_at_comment_all);
		btn_at_comment_friends = (Button) popup_window_at
				.findViewById(R.id.btn_at_comment_friends);
		btn_at_wb_all.setOnClickListener(this);
		btn_at_wb_friends.setOnClickListener(this);
		btn_at_wb_original.setOnClickListener(this);
		btn_at_comment_all.setOnClickListener(this);
		btn_at_comment_friends.setOnClickListener(this);
	}

	/**
	 * 初始化Comment界面
	 * 
	 * @param inflater
	 * @param listViews
	 */
	private void initCommentView(LayoutInflater inflater, List<View> listViews) {
		// Comment View
		View comment_view = inflater.inflate(R.layout.msg_comment, null);
		listViews.add(comment_view);
		mCommentTitleText = (TextView) comment_view
				.findViewById(R.id.tv_msg_comment_top);
		lv_msg_comment = (PullToRefreshListView) comment_view
				.findViewById(R.id.lv_msg_comment);
		lv_msg_comment.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				comment_current_state = COMMENT_MORE_NEW;
			}
		});
		lv_msg_comment.setOnLoadOldListener(new onLoadOldListener() {

			@Override
			public void onLoadOld() {
				comment_current_state = COMMENT_MORE_OLD;
			}
		});
		pb_comment = (ProgressBar) comment_view.findViewById(R.id.pb_comment);
		// PopupWindow View
		View popup_window_comment = LayoutInflater.from(this).inflate(
				R.layout.popup_window_comment, null);
		popupWindow_comment = createPopupWindow(popup_window_comment, 230,
				LayoutParams.WRAP_CONTENT);
		final LinearLayout ll_msg_comment_to_by = (LinearLayout) comment_view
				.findViewById(R.id.ll_msg_comment_to_by);
		ll_msg_comment_to_by.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popupWindow_comment
						.showAsDropDown(ll_msg_comment_to_by, 10, -5);
			}
		});

		btn_comment_to_me_all = (Button) popup_window_comment
				.findViewById(R.id.btn_comment_to_me_all);
		btn_comment_to_me_friends = (Button) popup_window_comment
				.findViewById(R.id.btn_comment_to_me_friends);
		btn_comment_by_me_all = (Button) popup_window_comment
				.findViewById(R.id.btn_comment_by_me_all);

		btn_comment_to_me_all.setOnClickListener(this);
		btn_comment_to_me_friends.setOnClickListener(this);
		btn_comment_by_me_all.setOnClickListener(this);
	}

	/**
	 * 初始化Message界面
	 * 
	 * @param inflater
	 * @param listViews
	 */
	private void initMessageView(LayoutInflater inflater, List<View> listViews) {
		View message_view = inflater.inflate(R.layout.msg_message, null);
		listViews.add(message_view);
		lv_msg_message = (PullToRefreshListView) message_view
				.findViewById(R.id.lv_msg_message);
	}

	/**
	 * 初始化Notification界面
	 * 
	 * @param inflater
	 * @param listViews
	 */
	private void initNotificationView(LayoutInflater inflater,
			List<View> listViews) {
		View notification_view = inflater.inflate(R.layout.msg_notification,
				null);
		listViews.add(notification_view);
		lv_msg_notification = (PullToRefreshListView) notification_view
				.findViewById(R.id.lv_msg_notification);
	}

	/**
	 * 回调刷新
	 */
	@Override
	public void refresh(Object... params) {
		int msg_witch = (Integer) params[1];
		switch (msg_witch) {
		case AT:// AT界面的毁掉刷新
			MsgAtAdapter aTAdapter = null;
			MsgCommentAdapter commentAdapter = null;
			if (params[2].equals("status")) {
				Log.d(TAG, "status");
				@SuppressWarnings("unchecked")
				List<Status> statuses = (List<Status>) params[0];
				aTAdapter = new MsgAtAdapter(statuses, this);
				if (statuses.size() != 0) {
					at_max_id = Long.parseLong(statuses
							.get(statuses.size() - 1).getMid()) - 1;
					switch (at_current_state) {
					case AT_INITIATE:
						Log.d(TAG, "init");
						lv_msg_at.setAdapter(aTAdapter);
						break;
					case AT_MORE_NEW:
						Log.d(TAG, "more_new");
						lv_msg_at.setAdapter(aTAdapter);
						lv_msg_at.onRefreshComplete();
						break;
					case AT_MORE_OLD:
						Log.d(TAG, "more_old");
						aTAdapter.refresh(statuses);
						if (aTAdapter.getCount() >= 20) {
							lv_msg_at.setSelection(aTAdapter.getCount() - 20);
						} else {
							lv_msg_at.setSelection(aTAdapter.getCount() - 1);
						}
						lv_msg_at.resetFooter();
						break;
					}
				} else {
					aTAdapter.notifyDataSetChanged();
					Toast.makeText(this, "暂无数据", Toast.LENGTH_SHORT).show();
					lv_msg_at.resetFooter();
				}
			} else if (params[2].equals("comment")) {
				@SuppressWarnings("unchecked")
				List<Comment> comments = (List<Comment>) params[0];
				commentAdapter = new MsgCommentAdapter(comments, this);
				if (comments.size() != 0) {
					at_max_id = Long.parseLong(comments
							.get(comments.size() - 1).getMid()) - 1;
					switch (at_current_state) {
					case AT_INITIATE:
						lv_msg_at.setAdapter(commentAdapter);
						break;
					case AT_MORE_NEW:
						lv_msg_at.setAdapter(commentAdapter);
						lv_msg_at.onRefreshComplete();
						break;
					case AT_MORE_OLD:
						commentAdapter.refresh(comments);
						if (commentAdapter.getCount() >= 20) {
							lv_msg_at
									.setSelection(commentAdapter.getCount() - 20);
						} else {
							lv_msg_at
									.setSelection(commentAdapter.getCount() - 1);
						}
						lv_msg_at.resetFooter();
						break;

					default:
						break;
					}
				} else {
					commentAdapter.notifyDataSetChanged();
					Toast.makeText(this, "暂无数据", Toast.LENGTH_SHORT).show();
					lv_msg_at.resetFooter();
				}
			}
			pb_at.setVisibility(View.GONE);
			break;
		case COMMENT:// 评论界面的回掉界面刷新
			@SuppressWarnings("unchecked")
			List<Comment> comments = (List<Comment>) params[0];
			MsgCommentAdapter mCommentAdapter = new MsgCommentAdapter(comments,
					this);
			if (comments.size() != 0) {
				comment_max_id = Long.parseLong(comments.get(
						comments.size() - 1).getMid()) - 1;
				switch (comment_current_state) {
				case COMMENT_INITIATE:
					lv_msg_comment.setAdapter(mCommentAdapter);
					break;
				case COMMENT_MORE_NEW:
					lv_msg_comment.setAdapter(mCommentAdapter);
					lv_msg_comment.onRefreshComplete();
					break;
				case COMMENT_MORE_OLD:
					mCommentAdapter.refresh(comments);
					if (mCommentAdapter.getCount() >= 20) {
						lv_msg_at.setSelection(mCommentAdapter.getCount() - 20);
					} else {
						lv_msg_at.setSelection(mCommentAdapter.getCount() - 1);
					}
					lv_msg_at.resetFooter();
					break;

				default:
					break;
				}
			} else {
				mCommentAdapter.notifyDataSetChanged();
				lv_msg_comment.setAdapter(mCommentAdapter);
				lv_msg_comment.resetFooter();
				Toast.makeText(this, "暂无数据", Toast.LENGTH_SHORT).show();
			}
			pb_comment.setVisibility(View.GONE);
			break;
		case MESSAGE:

			break;
		case NOTIFICATION:

			break;
		}
	}

	// =======================
	// 点击事件监听器
	// =======================
	@Override
	public void onClick(View v) {
		Task task = null;
		HashMap<String, Object> taskParams = new HashMap<String, Object>();
		popupWindow_at.dismiss();
		popupWindow_comment.dismiss();
		switch (v.getId()) {
		case R.id.btn_at_wb_all:
			mAtMode = AT_WEIBO;
			mAtTitleText.setText(R.string.msg_at_weibo);
			task = new Task(Task.WEIBO_MSG_AT_WB, taskParams);
			pb_at.setVisibility(View.VISIBLE);
			break;
		case R.id.btn_at_wb_friends:
			mAtMode = AT_WEIBO;
			mAtTitleText.setText(R.string.msg_at_weibo);
			lv_msg_at.refreshDrawableState();
			taskParams.put("filter_by_author", 1);
			task = new Task(Task.WEIBO_MSG_AT_WB, taskParams);
			pb_at.setVisibility(View.VISIBLE);
			break;
		case R.id.btn_at_wb_original:
			mAtMode = AT_WEIBO;
			mAtTitleText.setText(R.string.msg_at_weibo);
			taskParams.put("filter_by_type", 1);
			task = new Task(Task.WEIBO_MSG_AT_WB, taskParams);
			pb_at.setVisibility(View.VISIBLE);
			break;
		case R.id.btn_at_comment_all:
			mAtMode = AT_COMMENT;
			mAtTitleText.setText(R.string.msg_at_comment);
			task = new Task(Task.WEIBO_MSG_AT_COMMENT, taskParams);
			pb_at.setVisibility(View.VISIBLE);
			break;
		case R.id.btn_at_comment_friends:
			mAtMode = AT_COMMENT;
			mAtTitleText.setText(R.string.msg_at_comment);
			taskParams.put("filter_by_author", 1);
			task = new Task(Task.WEIBO_MSG_AT_COMMENT, taskParams);
			pb_at.setVisibility(View.VISIBLE);
			break;
		case R.id.btn_comment_to_me_all:
			mCommentTitleText.setText(R.string.msg_comment_to_me);
			task = new Task(Task.WEIBO_MSG_COMMENT_TO_ME, taskParams);
			pb_comment.setVisibility(View.VISIBLE);
			break;
		case R.id.btn_comment_to_me_friends:
			mCommentTitleText.setText(R.string.msg_comment_to_me);
			taskParams.put("filter_by_author", 1);
			task = new Task(Task.WEIBO_MSG_COMMENT_TO_ME, taskParams);
			pb_comment.setVisibility(View.VISIBLE);
			break;
		case R.id.btn_comment_by_me_all:
			mCommentTitleText.setText(R.string.msg_comment_by_me);
			task = new Task(Task.WEIBO_MSG_COMMENT_BY_ME, taskParams);
			pb_comment.setVisibility(View.VISIBLE);
			break;
		}
		MainService.addTask(task);
		MainService.addActivity(this);

	}

	// =========================
	// 标题点击事件监听器
	// =========================
	class TitleOnClickListener implements OnClickListener {
		private int index = 0;

		public TitleOnClickListener(int index) {
			this.index = index;
		}

		@Override
		public void onClick(View v) {
			mViewPager.setCurrentItem(index);
		}
	}

	// ========================
	// ViewPager适配器
	// ========================
	class MyPagerAdapter extends PagerAdapter {
		private List<View> list = null;

		public MyPagerAdapter(List<View> list) {
			this.list = list;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(list.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(list.get(arg1), 0);
			return list.get(arg1);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}
	}

	// ==================================
	// ViewPage中Page改变时的监听类
	// ===================================
	class MyPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageSelected(int arg0) {
			// System.out.println("onPageSelected " +arg0);
			cursorMove(arg0);
			loadData(arg0);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// System.out.println("onPageScrolled " + arg0 +" "+arg1 +" "+arg2);
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// System.out.println("onPageScrollStateChanged "+arg0);
		}
	}

	/**
	 * 处理游标移动
	 * 
	 * @param index
	 */
	private void cursorMove(int index) {
		iv_cursor.setVisibility(View.VISIBLE);
		iv_cursor_monitor.setVisibility(View.GONE);

		int[] at_location = new int[2];
		// 得到控件在屏幕中的位置
		iv_at.getLocationOnScreen(at_location);

		int[] comment_location = new int[2];
		iv_comment.getLocationOnScreen(comment_location);

		int[] message_location = new int[2];
		iv_message.getLocationOnScreen(message_location);

		int[] notification_location = new int[2];
		iv_notification.getLocationOnScreen(notification_location);

		Animation animation = null;
		switch (index) {
		case AT:
			if (currentIndex == COMMENT) {
				animation = new TranslateAnimation(comment_location[0],
						at_location[0], 0, 0);
			} else if (currentIndex == MESSAGE) {
				animation = new TranslateAnimation(message_location[0],
						at_location[0], 0, 0);
			} else if (currentIndex == NOTIFICATION) {
				animation = new TranslateAnimation(notification_location[0],
						at_location[0], 0, 0);
			}
			break;
		case COMMENT:
			if (currentIndex == AT) {
				animation = new TranslateAnimation(at_location[0],
						comment_location[0], 0, 0);
			} else if (currentIndex == MESSAGE) {
				animation = new TranslateAnimation(message_location[0],
						comment_location[0], 0, 0);
			} else if (currentIndex == NOTIFICATION) {
				animation = new TranslateAnimation(notification_location[0],
						comment_location[0], 0, 0);
			}
			break;
		case MESSAGE:
			if (currentIndex == AT) {
				animation = new TranslateAnimation(at_location[0],
						message_location[0], 0, 0);
			} else if (currentIndex == COMMENT) {
				animation = new TranslateAnimation(comment_location[0],
						message_location[0], 0, 0);
			} else if (currentIndex == NOTIFICATION) {
				animation = new TranslateAnimation(notification_location[0],
						message_location[0], 0, 0);
			}
			break;
		case NOTIFICATION:
			if (currentIndex == AT) {
				animation = new TranslateAnimation(at_location[0],
						notification_location[0], 0, 0);
			} else if (currentIndex == COMMENT) {
				animation = new TranslateAnimation(comment_location[0],
						notification_location[0], 0, 0);
			} else if (currentIndex == MESSAGE) {
				animation = new TranslateAnimation(message_location[0],
						notification_location[0], 0, 0);
			}

			break;
		}

		currentIndex = index;
		animation.setFillAfter(true);// True:图片停在动画结束位置
		animation.setDuration(300);
		iv_cursor.startAnimation(animation);
	}

	/**
	 * 在ViewPage中的View移动时，加载数据;
	 * 
	 * @param index
	 */
	private void loadData(int index) {
		switch (index) {
		case AT:
			break;
		case COMMENT:
			if (lv_msg_comment.getCount() == 0) {
				// Task task = new Task(Task.WEIBO_MSG_COMMENT_TO_ME, null);
				// MainService.addTask(task);
				// MainService.addActivity(this);
				createCommentTask(comment_current_state, comment_max_id, true);
			}
			break;
		case MESSAGE:

			break;
		case NOTIFICATION:

			break;
		}
	}

	/**
	 * 创建PopupWindow
	 * 
	 * @param view
	 * @param width
	 * @param height
	 * @return
	 */
	private PopupWindow createPopupWindow(View view, int width, int height) {
		PopupWindow popupWindow = new PopupWindow(view, width, height);
		// 设置能获取焦点
		popupWindow.setFocusable(true);
		// 设置可触摸
		popupWindow.setTouchable(true);
		// popupWindow.setAnimationStyle(R.style.animation_fade);
		// 设置允许在外点击消失
		popupWindow.setOutsideTouchable(true);
		// 刷新状态
		popupWindow.update();
		// 按返回按钮能够关闭popuwindow
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		return popupWindow;
	}

}
