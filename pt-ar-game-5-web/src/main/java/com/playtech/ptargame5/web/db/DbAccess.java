package com.playtech.ptargame5.web.db;

import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.playtech.ptargame5.web.model.GameResult;
import com.playtech.ptargame5.web.model.Player;
import com.playtech.ptargame5.web.model.TriviaQuestion;

import io.jsondb.JsonDBTemplate;

@Component
public class DbAccess {

	private static final Logger log = LoggerFactory.getLogger(DbAccess.class);
	private static JsonDBTemplate db = new JsonDBTemplate("triviaspree.db", "com.playtech.ptargame5.web.model");

	@PostConstruct
	private void init() {
		if (!db.collectionExists(Player.class)) {
			db.createCollection(Player.class);
			log.info("Created Player collection db");
		}
		if (!db.collectionExists(TriviaQuestion.class)) {
			db.createCollection(TriviaQuestion.class);
			log.info("Created TriviaQuestion collection db");
		}
		if (!db.collectionExists(GameResult.class)) {
			db.createCollection(GameResult.class);
			log.info("Created GameResult collection db");
		}
	}

	public void addPlayer(Player player) {
		if (db.findById(player.getNickname(), Player.class) == null) {
			db.insert(player);
		} else {
			// TODO Add proper error response
			throw new IllegalStateException("User exists");
		}
	}

	public boolean isInternalPlayer(String nickname) {
		Player player = db.findById(nickname, Player.class);
		if (player != null) {
			return player.getInternal();
		}
		log.warn("Player does not exist in the DB, fail safe to internal");
		return true;
	}

	public void convertToInternal(String nickname) {
		Player player = db.findById(nickname, Player.class);
		if (player != null) {
			player.setInternal(true);
			db.upsert(player);
		} else {
			throw new IllegalStateException("User exists");
		}
	}

	public Player findPlayer(String userId) {
		return db.findById(userId, Player.class);
	}

	public List<Player> getPlayers() {
		return db.findAll(Player.class);
	}

	public List<TriviaQuestion> getQuestions() {
		return db.findAll(TriviaQuestion.class);
	}

	public void addGameResult(GameResult gameResult) {
		db.insert(gameResult);
	}

	public List<GameResult> getGameResults() {
		return db.findAll(GameResult.class);
	}

}
