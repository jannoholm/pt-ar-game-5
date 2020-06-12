package com.playtech.ptargame5.web.model.gamesession;

import com.playtech.ptargame5.web.LogUtils;

public class NextQuestionRequest {

	private String gameId;
	private String lastQuestionAnswer;

	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public String getLastQuestionAnswer() {
		return lastQuestionAnswer;
	}

	public void setLastQuestionAnswer(String lastQuestionAnswer) {
		this.lastQuestionAnswer = lastQuestionAnswer;
	}

	@Override
	public String toString() {
		return LogUtils.toString(this);
	}
}
