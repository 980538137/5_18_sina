package cn.edu.nuc.weibo.bean;

import java.util.List;

public class Followers {
	private List<User> users;
	private int nextCursor;
	private int previousCursor;
	private int totalNumber;
	public List<User> getUsers() {
		return users;
	}
	public void setUsers(List<User> users) {
		this.users = users;
	}
	public int getNextCursor() {
		return nextCursor;
	}
	public void setNextCursor(int nextCursor) {
		this.nextCursor = nextCursor;
	}
	public int getPreviousCursor() {
		return previousCursor;
	}
	public void setPreviousCursor(int previousCursor) {
		this.previousCursor = previousCursor;
	}
	public int getTotalNumber() {
		return totalNumber;
	}
	public void setTotalNumber(int totalNumber) {
		this.totalNumber = totalNumber;
	}

}
