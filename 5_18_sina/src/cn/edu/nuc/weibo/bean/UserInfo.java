package cn.edu.nuc.weibo.bean;

public class UserInfo {

	public UserInfo(String uid, String access_token, String expires_in,
			String start_time, String screen_name) {
		super();
		this.uid = uid;
		this.access_token = access_token;
		this.expires_in = expires_in;
		this.start_time = start_time;
		this.screen_name = screen_name;
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
