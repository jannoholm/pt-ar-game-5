package com.playtech.ptargame5.web.model;

public class GameResultAnswer {

	private String question;
	private String playerInput;
	private Long timeTaken;

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
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
		return "GameResultAnswer [question=" + question + ", playerInput=" + playerInput + ", timeTaken=" + timeTaken
				+ "]";
	}
}
