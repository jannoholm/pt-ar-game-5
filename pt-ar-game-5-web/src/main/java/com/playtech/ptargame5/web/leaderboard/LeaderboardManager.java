package com.playtech.ptargame5.web.leaderboard;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.playtech.ptargame5.web.db.DbAccess;
import com.playtech.ptargame5.web.model.GameResult;
import com.playtech.ptargame5.web.model.LeaderboardEntry;
import com.playtech.ptargame5.web.model.LeaderboardTable;
import com.playtech.ptargame5.web.model.Player;

@Component
public class LeaderboardManager {

	private static final Logger log = LoggerFactory.getLogger(LeaderboardManager.class);

	@Autowired
	private DbAccess db;
	
	public int getPlayerPosition(Player player) {
		
		if(player.getInternal()) {
			return -1;
		}
		
		LeaderboardTable table = calculateLeadboard();
		for(LeaderboardEntry entry : table.getData()) {
			if(entry.getNickname().matches(player.getNickname())) {
				return entry.getPosition();
			}
		}
		
		throw new IllegalStateException("Nickname not found in the leaderboard!");
	}

	public LeaderboardTable calculateLeadboard() {

		List<GameResult> games = db.getGameResults();

		log.info("Creating leaderboard, nrOfGames=" + games.size());

		Map<String, LeaderboardEntry> leaderboard = new HashMap<String, LeaderboardEntry>();

		// Collect total stats over all games
		for (GameResult game : games) {

			if (db.isInternalPlayer(game.getNickname())) {
				// Filter out internal players
				continue;
			}

			if (leaderboard.containsKey(game.getNickname())) {
				// Player has played more than one game
				LeaderboardEntry entry = leaderboard.get(game.getNickname());
				if (entry.getBestScore() < game.getTotalScore()) {
					entry.setBestScore(game.getTotalScore());
				}
				entry.setCorrectAnswers(entry.getCorrectAnswers() + game.getCorrectAnswers());
				entry.setGamesPlayed(entry.getGamesPlayed() + 1);
				entry.setNickname(game.getNickname());
				entry.setPosition(999);
				entry.setTotalQuestions(entry.getTotalQuestions() + game.getQuestionsAttempted());
			} else {
				// First game of the player is discovered, create new entry
				LeaderboardEntry entry = new LeaderboardEntry();
				entry.setBestScore(game.getTotalScore());
				entry.setCorrectAnswers(game.getCorrectAnswers());
				entry.setGamesPlayed(1);
				entry.setNickname(game.getNickname());
				entry.setPosition(999);
				entry.setTotalQuestions(game.getQuestionsAttempted());

				leaderboard.put(game.getNickname(), entry);
			}
		}

		// Sort the leaderboard correctly
		List<LeaderboardEntry> result = new ArrayList<>(leaderboard.values());
		result.sort(new Comparator<LeaderboardEntry>() {
			@Override
			public int compare(LeaderboardEntry first, LeaderboardEntry second) {
				// Reverse sort
				return second.getBestScore().compareTo(first.getBestScore());
			}
		});

		// Set correct position
		int position = 1;
		for (LeaderboardEntry entry : result) {
			entry.setPosition(position++);
		}

		LeaderboardTable table = new LeaderboardTable();
		table.setData(new ArrayList<>(leaderboard.values()));

		return table;
	}
}
