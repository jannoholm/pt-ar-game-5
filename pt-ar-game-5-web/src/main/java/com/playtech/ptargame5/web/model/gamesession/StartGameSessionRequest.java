package com.playtech.ptargame5.web.model.gamesession;

import com.playtech.ptargame5.web.LogUtils;

public class StartGameSessionRequest {

	private String nickname;

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	@Override
	public String toString() {
		return LogUtils.toString(this);
	}
}
