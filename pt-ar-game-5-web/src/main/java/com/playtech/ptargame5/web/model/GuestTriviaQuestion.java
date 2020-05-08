package com.playtech.ptargame5.web.model;

import java.util.List;

import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;

@Document(collection = "guestquestions", schemaVersion = "1.0")
public class GuestTriviaQuestion {

	@Id
	private String questionId;
	private String nickname;
	private String question;
	private List<String> correct;
	private List<String> answers;

	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
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
		return "GuestTriviaQuestion [questionId=" + questionId + ", nickname=" + nickname + ", question=" + question
				+ ", correct=" + correct + ", answers=" + answers + "]";
	}
}