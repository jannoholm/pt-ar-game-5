package com.playtech.ptargame5.web.model;

import java.util.List;

import com.playtech.ptargame5.web.LogUtils;

import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;

@Document(collection = "guestquestions", schemaVersion = "1.0")
public class GuestTriviaQuestion {

	@Id
	private String questionId;
	private String author;
	private String question;
	private List<String> correct;
	private List<String> answers;

	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public List<String> getCorrect() {
		return correct;
	}

	public void setCorrect(List<String> correct) {
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
		return LogUtils.toString(this);
	}
}
