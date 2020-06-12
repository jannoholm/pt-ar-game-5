package com.playtech.ptargame5.web.model.gamesession;

import com.playtech.ptargame5.web.LogUtils;

public class EndGameSessionResponse {

	private int totalScore;
	private int questionsAttempted;
	private int correctAnswers;
	private int newLeaderboardPosition;

	public int getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(int totalScore) {
		this.totalScore = totalScore;
	}

	public int getQuestionsAttempted() {
		return questionsAttempted;
	}

	public void setQuestionsAttempted(int questionsAttempted) {
		this.questionsAttempted = questionsAttempted;
	}

	public int getCorrectAnswers() {
		return correctAnswers;
	}

	public void setCorrectAnswers(int correctAnswers) {
		this.correctAnswers = correctAnswers;
	}

	public int getNewLeaderboardPosition() {
		return newLeaderboardPosition;
	}

	public void setNewLeaderboardPosition(int newLeaderboardPosition) {
		this.newLeaderboardPosition = newLeaderboardPosition;
	}

	@Override
	public String toString() {
		return LogUtils.toString(this);
	}
}
