package com.playtech.ptargame5.web.model.gamesession;

import com.playtech.ptargame5.web.LogUtils;
import com.playtech.ptargame5.web.model.TriviaQuestion;

public class TriviaQuestionResult {

	private boolean correct;
	private TriviaQuestion question;
	private String playerInput;
	private Long timeTaken;

	public boolean isCorrect() {
		return correct;
	}

	public void setCorrect(boolean correct) {
		this.correct = correct;
	}

	public TriviaQuestion getQuestion() {
		return question;
	}

	public void setQuestion(TriviaQuestion question) {
		this.question = question;
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
