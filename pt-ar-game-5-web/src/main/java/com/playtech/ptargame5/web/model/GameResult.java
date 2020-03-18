package com.playtech.ptargame5.web.model;

import java.util.List;

import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;

@Document(collection = "games", schemaVersion = "1.0")
public class GameResult {

	@Id
	private String gameId;
	private String nickname;
	private Integer questionsAttempted;
	private Integer correctAnswers;
	private Integer totalScore;
	private List<GameResultAnswer> answers;

	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public Integer getQuestionsAttempted() {
		return questionsAttempted;
	}

	public void setQuestionsAttempted(Integer questionsAttempted) {
		this.questionsAttempted = questionsAttempted;
	}

	public Integer getCorrectAnswers() {
		return correctAnswers;
	}

	public void setCorrectAnswers(Integer correctAnswers) {
		this.correctAnswers = correctAnswers;
	}

	public Integer getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(Integer totalScore) {
		this.totalScore = totalScore;
	}

	public List<GameResultAnswer> getAnswers() {
		return answers;
	}

	public void setAnswers(List<GameResultAnswer> answers) {
		this.answers = answers;
	}

	@Override
	public String toString() {
		return "GameResult [gameId=" + gameId + ", nickname=" + nickname + ", questionsAttempted=" + questionsAttempted
				+ ", correctAnswers=" + correctAnswers + ", totalScore=" + totalScore + ", answers=" + answers + "]";
	}
}
