package cn.edu.nuc.weibo.bean;

public class UserInfo {

	

	public UserInfo(String uid, String access_token, String expires_in,
			String start_time, String screen_name, int statuses_count,
			int favourites_count, int followers_count,String profile_image_url) {
		super();
		this.uid = uid;
		this.access_token = access_token;
		this.expires_in = expires_in;
		this.start_time = start_time;
		this.screen_name = screen_name;
		this.statuses_count = statuses_count;
		this.favourites_count = favourites_count;
		this.followers_count = followers_count;
		this.profile_image_url = profile_image_url;
	}

	public String getScreen_name() {
		return screen_name;
	}

	public void setScreen_name(String screen_name) {
		this.screen_name = screen_name;
	}

	private String uid;
	private String access_token;
	private String expires_in;
	private String start_time;
	private String screen_name;
	private int statuses_count;
	private int favourites_count;
	private int followers_count;
	private String profile_image_url;

	public String getProfile_image_url() {
		return profile_image_url;
	}

	public void setProfile_image_url(String profile_image_url) {
		this.profile_image_url = profile_image_url;
	}

	public int getStatuses_count() {
		return statuses_count;
	}

	public void setStatuses_count(int statuses_count) {
		this.statuses_count = statuses_count;
	}

	public int getFavourites_count() {
		return favourites_count;
	}

	public void setFavourites_count(int favourites_count) {
		this.favourites_count = favourites_count;
	}

	public int getFollowers_count() {
		return followers_count;
	}

	public void setFollowers_count(int followers_count) {
		this.followers_count = followers_count;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public String getExpires_in() {
		return expires_in;
	}

	public void setExpires_in(String expires_in) {
		this.expires_in = expires_in;
	}

	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

}
