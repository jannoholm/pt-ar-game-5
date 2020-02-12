package com.playtech.ptargame5.web.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.playtech.ptargame5.web.db.DbAccess;
import com.playtech.ptargame5.web.model.Nicknames;
import com.playtech.ptargame5.web.model.Player;

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

		Assert.hasText(player.getEmail(), "Email is required");
		Assert.isTrue(player.getEmail().length() < 400, "Email is too long");

		if (player.getNickname().equalsIgnoreCase("DMO")) {
			player.setInternal(true);
		} else if (player.getInternal()) {
			player.setInternal(false);
		}

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
}
