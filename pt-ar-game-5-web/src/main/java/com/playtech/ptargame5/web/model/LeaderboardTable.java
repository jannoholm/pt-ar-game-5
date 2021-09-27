package com.playtech.ptargame5.web.model;

import java.util.List;

import com.playtech.ptargame5.web.LogUtils;

public class LeaderboardTable {

	private final List<LeaderboardEntry> data;

	public LeaderboardTable(List<LeaderboardEntry> data) {
    this.data = data;
  }

	public List<LeaderboardEntry> getData() {
		return data;
	}

  @Override
	public String toString() {
		return LogUtils.toString(this);
	}
}
