package com.playtech.ptargame5.web.model;

import java.util.List;

import com.playtech.ptargame5.web.LogUtils;

public class Nicknames {

	private List<String> nicknames;

	public List<String> getNicknames() {
		return nicknames;
	}

	public void setNicknames(List<String> nicknames) {
		this.nicknames = nicknames;
	}

	@Override
	public String toString() {
		return LogUtils.toString(this);
	}
}
