package com.playtech.ptargame5.web.model.gamesession;

import java.util.List;

import com.playtech.ptargame5.web.LogUtils;

public class NextQuestionResponse {

	private Boolean lastQuestionCorrect;
	private Integer newTotalScore;
	private Integer extraTimeAdded;
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

	public Integer getNewTotalScore() {
		return newTotalScore;
	}

	public void setNewTotalScore(Integer newTotalScore) {
		this.newTotalScore = newTotalScore;
	}

	public Integer getExtraTimeAdded() {
		return extraTimeAdded;
	}

	public void setExtraTimeAdded(Integer extraTimeAdded) {
		this.extraTimeAdded = extraTimeAdded;
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
