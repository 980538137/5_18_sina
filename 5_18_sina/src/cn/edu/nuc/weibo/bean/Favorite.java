package cn.edu.nuc.weibo.bean;

public class Favorite {
	private Status mStatus;
	private String favorited_time;

	public Favorite(Status mStatus, String favorited_time) {
		this.mStatus = mStatus;
		this.favorited_time = favorited_time;
	}

	public Status getmStatus() {
		return mStatus;
	}

	public void setmStatus(Status mStatus) {
		this.mStatus = mStatus;
	}

	public String getFavorited_time() {
		return favorited_time;
	}

	public void setFavorited_time(String favorited_time) {
		this.favorited_time = favorited_time;
	}

}
