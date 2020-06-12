package com.playtech.ptargame5.web.model.gamesession;

import com.playtech.ptargame5.web.LogUtils;

public class EndGameSessionRequest {

	private String gameId;

	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	@Override
	public String toString() {
		return LogUtils.toString(this);
	}
}
