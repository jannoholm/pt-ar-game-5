package com.playtech.ptargame5.web.model;

import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;

@Document(collection = "players", schemaVersion = "1.0")
public class Player {

	@Id
	private String nickname;
	private String name;
	private String email;
	private boolean internal;
	private PlayerInterests interests;

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean getInternal() {
		return internal;
	}

	public void setInternal(boolean internal) {
		this.internal = internal;
	}

	public PlayerInterests getInterests() {
		return interests;
	}

	public void setInterests(PlayerInterests interests) {
		this.interests = interests;
	}

	@Override
	public String toString() {
		return "Player [nickname=" + nickname + ", name=" + name + ", email=" + email + ", internal=" + internal
				+ ", interests=" + interests + "]";
	}
}
