package com.playtech.ptargame5.web.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.playtech.ptargame5.web.leaderboard.LeaderboardManager;
import com.playtech.ptargame5.web.model.LeaderboardTable;

@CrossOrigin
@RestController
public class LeaderboardController {

	private static final Logger log = LoggerFactory.getLogger(LeaderboardController.class);

	@Autowired
	private LeaderboardManager leaderboard;

	@GetMapping("/api/leaderboard")
	public LeaderboardTable getLeadboard() {

		// TODO Conduct perftest and see if caching is needed
		LeaderboardTable table =  leaderboard.calculateLeadboard();
		
		log.info("Returning leaderboard table with nrOfPlayers=" + table.getData().size());
		
		return table;
	}
}