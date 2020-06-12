package com.playtech.ptargame5.web.model;

import java.util.List;

import com.playtech.ptargame5.web.LogUtils;

public class LeaderboardTable {

	private List<LeaderboardEntry> data;

	public List<LeaderboardEntry> getData() {
		return data;
	}

	public void setData(List<LeaderboardEntry> data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return LogUtils.toString(this);
	}
}
