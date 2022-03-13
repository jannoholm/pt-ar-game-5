package com.playtech.ptargame5.web.model;

import com.playtech.ptargame5.web.LogUtils;

public class GameResultAnswer {

	private String question;
	private int level  = 1;
	private String category;
	private boolean answeredCorrectly;
	private String correctAnswer;
	private String playerInput;
	private long timeTaken;

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public boolean getAnsweredCorrectly() {
		return answeredCorrectly;
	}

	public void setAnsweredCorrectly(boolean answeredCorrectly) {
		this.answeredCorrectly = answeredCorrectly;
	}

	public String getCorrectAnswer() {
		return correctAnswer;
	}

	public void setCorrectAnswer(String correctAnswer) {
		this.correctAnswer = correctAnswer;
	}

	public String getPlayerInput() {
		return playerInput;
	}

	public void setPlayerInput(String playerInput) {
		this.playerInput = playerInput;
	}

	public long getTimeTaken() {
		return timeTaken;
	}

	public void setTimeTaken(long timeTaken) {
		this.timeTaken = timeTaken;
	}

	@Override
	public String toString() {
		return LogUtils.toString(this);
	}
}
