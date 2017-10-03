package com.example.show_smartmms.bean;

import java.io.Serializable;

public class GroupBean implements Serializable{

	private String groupName;
	private int groupId;
	private int threadid;
	
	public GroupBean() {
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public int getThreadid() {
		return threadid;
	}

	public void setThreadid(int threadid) {
		this.threadid = threadid;
	}

	@Override
	public String toString() {
		return "GroupBean [groupName=" + groupName + ", groupId=" + groupId
				+ ", threadid=" + threadid + "]";
	}
	
}
