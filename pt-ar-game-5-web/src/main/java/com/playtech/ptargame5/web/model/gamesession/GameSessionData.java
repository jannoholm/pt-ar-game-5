package com.playtech.ptargame5.web.model.gamesession;

import java.util.ArrayList;
import java.util.List;

import com.playtech.ptargame5.web.LogUtils;
import com.playtech.ptargame5.web.model.Player;
import com.playtech.ptargame5.web.model.TriviaQuestion;

import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;

@Document(collection = "gamessessions", schemaVersion = "1.0")
public class GameSessionData {

	@Id
	private String gameId;
	private long sessionStartTime;
	private long lastQuestionTime;
	private long extraQuestionsTime;
	private Player player;
	private List<TriviaQuestion> questionsAsked = new ArrayList<>();
	private List<TriviaQuestionResult> questionsAnswered = new ArrayList<>();

	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public long getSessionStartTime() {
		return sessionStartTime;
	}

	public void setSessionStartTime(long sessionStartTime) {
		this.sessionStartTime = sessionStartTime;
	}

	public long getLastQuestionTime() {
		return lastQuestionTime;
	}

	public void setLastQuestionTime(long lastQuestionTime) {
		this.lastQuestionTime = lastQuestionTime;
	}

	public long getExtraQuestionsTime() {
		return extraQuestionsTime;
	}

	public void setExtraQuestionsTime(long extraQuestionsTime) {
		this.extraQuestionsTime = extraQuestionsTime;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public List<TriviaQuestion> getQuestionsAsked() {
		return questionsAsked;
	}

	public void setQuestionsAsked(List<TriviaQuestion> questionsAsked) {
		this.questionsAsked = questionsAsked;
	}

	public List<TriviaQuestionResult> getQuestionsAnswered() {
		return questionsAnswered;
	}

	public void setQuestionsAnswered(List<TriviaQuestionResult> questionsAnswered) {
		this.questionsAnswered = questionsAnswered;
	}

	@Override
	public String toString() {
		return LogUtils.toString(this);
	}
}
