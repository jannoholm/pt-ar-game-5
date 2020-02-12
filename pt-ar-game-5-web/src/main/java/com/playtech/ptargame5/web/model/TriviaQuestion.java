package com.playtech.ptargame5.web.model;

import java.util.List;

import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;

@Document(collection = "questions", schemaVersion = "1.0")
public class TriviaQuestion {

	@Id
	private String question;
	private Integer level;
	private String correct;
	private List<String> answers;

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getCorrect() {
		return correct;
	}

	public void setCorrect(String correct) {
		this.correct = correct;
	}

	public List<String> getAnswers() {
		return answers;
	}

	public void setAnswers(List<String> answers) {
		this.answers = answers;
	}

	@Override
	public String toString() {
		return "TriviaQuestion [question=" + question + ", level=" + level + ", correct=" + correct + ", answers="
				+ answers + "]";
	}
}
