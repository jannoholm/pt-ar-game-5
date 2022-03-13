package com.playtech.ptargame5.web.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.playtech.ptargame5.web.db.DbAccess;
import com.playtech.ptargame5.web.model.Nicknames;
import com.playtech.ptargame5.web.model.Player;
import com.playtech.ptargame5.web.model.PlayerFeedback;

@CrossOrigin
@RestController
public class PlayerController {

	private static final Logger log = LoggerFactory.getLogger(PlayerController.class);

	@Autowired
	private DbAccess db;

	@PostMapping("/api/player")
	public Player addPlayer(@RequestBody Player player) {

		log.info("Received player registration: " + player);

		Assert.hasText(player.getName(), "Name is required");
		Assert.isTrue(player.getName().length() < 400, "Name is too long");

		Assert.hasText(player.getNickname(), "Nickname is required");
		Assert.isTrue(player.getNickname().matches("[A-Z]{3}"), "Nickname has to be 3 capital letters");
		if ("aaa".equalsIgnoreCase(player.getNickname())){
		  throw new IllegalArgumentException("AAA is banned!");
		}
		
		Assert.hasText(player.getEmail(), "Email is required");
		Assert.isTrue(player.getEmail().length() < 400, "Email is too long");

		player.setInternal("DMO".equalsIgnoreCase(player.getNickname()));

		db.addPlayer(player);

		log.info("Player registered successfully: " + player);

		return player;
	}

	@GetMapping("/api/player/internal")
	public void convertToInternal(@RequestParam String nickname) {

		log.info("Converting user to internal: " + nickname);

		Assert.hasText(nickname, "Name is required");
		Assert.isTrue(nickname.matches("[A-Z]{3}"), "Nickname has to be 3 capital letters");

		db.convertToInternal(nickname);

		log.info("Converting user to internal successful: " + nickname);
	}

	@GetMapping("/api/private/players")
	public List<Player> getPlayers() {
		log.info("Returning all players");
		return db.getPlayers();
	}

	@GetMapping("/api/nicknames")
	public Nicknames getNicknames() {

		List<String> nicknames = new ArrayList<String>();

		for (Player player : db.getPlayers()) {
			nicknames.add(player.getNickname());
		}

		Nicknames nicknamesContainer = new Nicknames();
		nicknamesContainer.setNicknames(nicknames);

		log.info("Returning all nicknames: " + nicknames.size());
		return nicknamesContainer;
	}
	
	@PostMapping("/api/player/feedback")
	public PlayerFeedback submitFeedback(@RequestBody PlayerFeedback feedback) {

		log.info("Received player feedback: " + feedback);

		Assert.hasText(feedback.getNickname(), "Nickname is required");
		Assert.isTrue(feedback.getNickname().length() < 5, "Nickname is too long"); // Note: 'anon' is also correct

		Assert.hasText(feedback.getFeedback(), "Feedback is required");
		Assert.isTrue(feedback.getFeedback().length() < 400, "Feedback is too long");

		feedback.setId(UUID.randomUUID().toString());

		db.addFeedback(feedback);

		log.info("Player feedback submitted successfully: " + feedback);

		return feedback;
	}

}
