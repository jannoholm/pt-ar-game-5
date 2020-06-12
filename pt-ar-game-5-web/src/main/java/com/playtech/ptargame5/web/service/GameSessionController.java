package com.playtech.ptargame5.web.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.playtech.ptargame5.web.db.DbAccess;
import com.playtech.ptargame5.web.game.GameSessionManager;
import com.playtech.ptargame5.web.model.Player;
import com.playtech.ptargame5.web.model.gamesession.EndGameSessionRequest;
import com.playtech.ptargame5.web.model.gamesession.EndGameSessionResponse;
import com.playtech.ptargame5.web.model.gamesession.NextQuestionRequest;
import com.playtech.ptargame5.web.model.gamesession.NextQuestionResponse;
import com.playtech.ptargame5.web.model.gamesession.StartGameSessionRequest;
import com.playtech.ptargame5.web.model.gamesession.StartGameSessionResponse;

@CrossOrigin
@RestController
public class GameSessionController {

	private static final Logger log = LoggerFactory.getLogger(GameController.class);

	@Autowired
	private DbAccess db;

	@Autowired
	private GameSessionManager manager;

	@PostMapping("/api/gamesession/start")
	public StartGameSessionResponse startNewSession(@RequestBody StartGameSessionRequest request) {


		Assert.hasText(request.getNickname(), "Nickname is required");
		Assert.isTrue(request.getNickname().length() < 400, "Nickname is too long");
		
		Player player = db.findPlayer(request.getNickname());
		
		if(player == null) {
			throw new IllegalStateException("Nickname not found!");
		}		
		
		String gameId = manager.statNewSession(player);

		log.info("Started new session for nickname=" + request.getNickname() + ", gameId=" + gameId);

		return new StartGameSessionResponse(gameId);
	}

	@PostMapping("/api/gamesession/next")
	public NextQuestionResponse nextQuestion(@RequestBody NextQuestionRequest request) {
		Assert.hasText(request.getGameId(), "Game ID is required");
		Assert.isTrue(request.getGameId().length() < 40, "Game ID is too long");
		if(request.getLastQuestionAnswer()!= null) {
			Assert.hasText(request.getLastQuestionAnswer(), "Last answer has to have content");
			Assert.isTrue(request.getLastQuestionAnswer().length() < 400, "Last answer is too long");	
		}		
		
		return manager.nextQuestion(request);
	}

	@PostMapping("/api/gamesession/end")
	public EndGameSessionResponse endGameSession(@RequestBody EndGameSessionRequest request) {
		Assert.hasText(request.getGameId(), "Game ID is required");
		Assert.isTrue(request.getGameId().length() < 40, "Game ID is too long");
		return manager.endGameSession(request.getGameId());
	}
}
