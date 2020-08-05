package com.playtech.ptargame5.web.model;

import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;

@Document(collection = "feedback", schemaVersion = "1.0")
public class PlayerFeedback {

	@Id
	private String id;
	private String nickname;
	private String feedback;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getFeedback() {
		return feedback;
	}

	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}

	@Override
	public String toString() {
		return "PlayerFeedback [id=" + id + ", nickname=" + nickname + ", feedback=" + feedback + "]";
	}
}
