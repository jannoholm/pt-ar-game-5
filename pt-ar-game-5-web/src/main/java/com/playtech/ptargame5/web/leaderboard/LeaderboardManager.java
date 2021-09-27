package com.playtech.ptargame5.web.leaderboard;

import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.StreamReadFeature;
import com.playtech.ptargame5.web.db.DbAccess;
import com.playtech.ptargame5.web.game.GameSessionManager;
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

		if (player.getInternal()) {
			return -1;
		}

		LeaderboardTable table = calculateLeadboard();
		for (LeaderboardEntry entry : table.getData()) {
			if (entry.getNickname().matches(player.getNickname())) {
				return entry.getPosition();
			}
		}

		throw new IllegalStateException("Nickname not found in the leaderboard!");
	}

	private static final int BEST_OF = 2;

	LeaderboardEntry reduce(Collection<LeaderboardEntry> entries) {// sorted already, at least one entry, e.g. non empty
																	// colleciton
		Iterator<LeaderboardEntry> i = entries.iterator();
		LeaderboardEntry result = i.next();

		// best score out of the N
		for (int bestOf = BEST_OF; --bestOf > 0;) {// the 1st is already loaded
			if (!i.hasNext()) {
				return result;
			}
			LeaderboardEntry next = i.next();
			result.combine(next);
		}

		if (!i.hasNext())
			return result;

		// allow adding to the baseline, 11% out all the next questions via weighted
		// average
		int baseline = result.getBestScore();
		double extra = 0;
		long weight = 0;
		for (long p = entries.size() - BEST_OF; i.hasNext(); p--) {// earlier - more weight
			LeaderboardEntry e = i.next();
			result.combine(e);
			extra += e.getBestScore() * p;
			weight += p;
		}
		extra /= weight;
		extra *= .11;

		baseline += Math.floor(extra);
		result.setBestScore(baseline);
		return result;
	}

	public LeaderboardTable calculateLeadboard() {
		List<GameResult> games = db.getGameResults();
		Set<String> internals = db.getPlayers().stream().filter(Player::getInternal).map(Player::getNickname)
				.collect(Collectors.toCollection(LinkedHashSet::new));
		internals.add(null);// handle nulls, just in case

		log.info("Creating leaderboard, nrOfGames=" + games.size());
		Collections.sort(games);
		Map<String, List<LeaderboardEntry>> leaderboard = new LinkedHashMap<>();

		// Collect total stats over all games
		for (GameResult game : games) {
			// Filter out internal players
			if (internals.contains(game.getNickname())) {// db.isInternalPlayer(game.getNickname())) {//this is quite an
															// expensive way to do it - i.e. lookup
				continue;
			}
			LeaderboardEntry entry = new LeaderboardEntry(game.getNickname());
			entry.setCorrectAnswers(nonNull(game.getCorrectAnswers()));
			entry.setTotalQuestions(nonNull(game.getQuestionsAttempted()));
			entry.setBestScore(nonNull(game.getTotalScore()));
			entry.setLastModification(game.getTime());
			leaderboard.computeIfAbsent(game.getNickname(), x -> new ArrayList<>()).add(entry);
		}

		LeaderboardEntry[] reduced = leaderboard.values().stream()
				.filter(e -> e.stream().filter(s -> s.getTotalQuestions() > GameSessionManager.MAX_QUESTIONS_PER_GAME).count() == 0)
				.map(this::reduce)
				.toArray(LeaderboardEntry[]::new);
		Arrays.sort(reduced);
		for (int i = 0; i < reduced.length;)
			reduced[i].setPosition(++i);

		LeaderboardTable table = new LeaderboardTable(Arrays.asList(reduced));
		return table;

		/*
		 * if (entry != null) { // Player has played more than one game entry =
		 * entry.clone(); if (entry.getBestScore() < game.getTotalScore()) {
		 * entry.setBestScore(game.getTotalScore()); }
		 * entry.setCorrectAnswers(entry.getCorrectAnswers() +
		 * game.getCorrectAnswers());//can't use java.lang.Integer and derefence w/o
		 * checks entry.setGamesPlayed(entry.getGamesPlayed() + 1);
		 * entry.setNickname(game.getNickname()); entry.setPosition(999);
		 * entry.setTotalQuestions(entry.getTotalQuestions() +
		 * game.getQuestionsAttempted()); } else { // First game of the player is
		 * discovered, create new entry LeaderboardEntry entry = new LeaderboardEntry();
		 * entry.setBestScore(game.getTotalScore());
		 * entry.setCorrectAnswers(game.getCorrectAnswers()); entry.setGamesPlayed(1);
		 * entry.setNickname(game.getNickname()); entry.setPosition(999);
		 * entry.setTotalQuestions(game.getQuestionsAttempted());
		 * 
		 * leaderboard.put(game.getNickname(), entry); } // Sort the leaderboard
		 * correctly List<LeaderboardEntry> result = new
		 * ArrayList<>(leaderboard.values()); Collections.sort(result);
		 * 
		 * // Set correct position; should clone them here int position = 1; for
		 * (LeaderboardEntry entry : result) { entry.setPosition(position++); }
		 * 
		 * LeaderboardTable table = new LeaderboardTable(); table.setData(new
		 * ArrayList<>(leaderboard.values()));
		 * 
		 * return table;
		 */
	}

	private static int nonNull(Integer n) {
		return n == null ? 0 : n.intValue();
	}
}
