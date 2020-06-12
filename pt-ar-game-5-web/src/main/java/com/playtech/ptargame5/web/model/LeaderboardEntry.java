package com.playtech.ptargame5.web.model;

import com.playtech.ptargame5.web.LogUtils;

import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;

@Document(collection = "leaderboard", schemaVersion = "1.0")
public class LeaderboardEntry {

	@Id
	private String nickname;
	private Integer position;
	private Integer bestScore;
	private Integer correctAnswers;
	private Integer totalQuestions;
	private Integer gamesPlayed;

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public Integer getBestScore() {
		return bestScore;
	}

	public void setBestScore(Integer bestScore) {
		this.bestScore = bestScore;
	}

	public Integer getCorrectAnswers() {
		return correctAnswers;
	}

	public void setCorrectAnswers(Integer correctAnswers) {
		this.correctAnswers = correctAnswers;
	}

	public Integer getTotalQuestions() {
		return totalQuestions;
	}

	public void setTotalQuestions(Integer totalQuestions) {
		this.totalQuestions = totalQuestions;
	}

	public Integer getGamesPlayed() {
		return gamesPlayed;
	}

	public void setGamesPlayed(Integer gamesPlayed) {
		this.gamesPlayed = gamesPlayed;
	}

	@Override
	public String toString() {
		return LogUtils.toString(this);
	}
}
