package com.playtech.ptargame5.web.model;

import com.playtech.ptargame5.web.LogUtils;

public class GameResultAnswer {

	private String question;
	private int level;
	private String category;
	private Boolean answeredCorrectly;
	private String correctAnswer;
	private String playerInput;
	private Long timeTaken;

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

	public Boolean getAnsweredCorrectly() {
		return answeredCorrectly;
	}

	public void setAnsweredCorrectly(Boolean answeredCorrectly) {
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

	public Long getTimeTaken() {
		return timeTaken;
	}

	public void setTimeTaken(Long timeTaken) {
		this.timeTaken = timeTaken;
	}

	@Override
	public String toString() {
		return LogUtils.toString(this);
	}
}
