package com.playtech.ptargame5.web.model.gamesession;

import java.util.List;

import com.playtech.ptargame5.web.LogUtils;

public class NextQuestionResponse {

	private Boolean lastQuestionCorrect;
	private Integer currentSpree;
	private Integer currentMultiplier;
	private Integer newTotalScore;
	private Integer level;
	private String category;
	private String question;
	private List<String> answers;

	public Boolean getLastQuestionCorrect() {
		return lastQuestionCorrect;
	}

	public void setLastQuestionCorrect(Boolean lastQuestionCorrect) {
		this.lastQuestionCorrect = lastQuestionCorrect;
	}

	public Integer getCurrentSpree() {
		return currentSpree;
	}

	public void setCurrentSpree(Integer currentSpree) {
		this.currentSpree = currentSpree;
	}

	public Integer getCurrentMultiplier() {
		return currentMultiplier;
	}

	public void setCurrentMultiplier(Integer currentMultiplier) {
		this.currentMultiplier = currentMultiplier;
	}

	public Integer getNewTotalScore() {
		return newTotalScore;
	}

	public void setNewTotalScore(Integer newTotalScore) {
		this.newTotalScore = newTotalScore;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public List<String> getAnswers() {
		return answers;
	}

	public void setAnswers(List<String> answers) {
		this.answers = answers;
	}

	@Override
	public String toString() {
		return LogUtils.toString(this);
	}
}
