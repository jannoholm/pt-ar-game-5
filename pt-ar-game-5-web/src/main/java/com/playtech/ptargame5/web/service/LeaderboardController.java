package com.playtech.ptargame5.web.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.playtech.ptargame5.web.model.LeaderboardEntry;
import com.playtech.ptargame5.web.model.LeaderboardTable;

@RestController
public class LeaderboardController {

	@RequestMapping("/api/leaderboard")
	public LeaderboardTable getLeadboard() {

		// TODO Replace mock data with logic

		List<LeaderboardEntry> data = new ArrayList<LeaderboardEntry>();

		LeaderboardEntry entry = new LeaderboardEntry();
		entry.setBestScore(240);
		entry.setCorrectAnswers(10);
		entry.setGamesPlayed(2);
		entry.setNickname("AAA");
		entry.setPosition(1);
		entry.setTotalQuestions(20);

		data.add(entry);

		entry = new LeaderboardEntry();
		entry.setBestScore(80);
		entry.setCorrectAnswers(20);
		entry.setGamesPlayed(4);
		entry.setNickname("BBB");
		entry.setPosition(2);
		entry.setTotalQuestions(60);

		data.add(entry);

		LeaderboardTable table = new LeaderboardTable();
		table.setData(data);

		return table;
	}
}