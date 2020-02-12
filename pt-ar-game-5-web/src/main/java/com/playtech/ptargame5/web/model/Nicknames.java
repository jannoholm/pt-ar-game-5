package com.playtech.ptargame5.web.model;

import java.util.List;

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
		return "Nicknames [nicknames=" + nicknames + "]";
	}
}
