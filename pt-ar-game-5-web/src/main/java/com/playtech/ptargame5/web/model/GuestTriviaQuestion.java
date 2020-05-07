package com.playtech.ptargame5.web.model;

import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;

@Document(collection = "guestquestions", schemaVersion = "1.0")
public class GuestTriviaQuestion extends TriviaQuestion {

	@Id
	private String questionId;
	private String nickname;

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

	@Override
	public String toString() {
		return "GuestTriviaQuestion [questionId=" + questionId + ", nickname=" + nickname + "]";
	}
}
