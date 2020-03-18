package com.playtech.ptargame5.web.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.playtech.ptargame5.web.db.DbAccess;
import com.playtech.ptargame5.web.model.GameResult;
import com.playtech.ptargame5.web.model.GameResultAnswer;
import com.playtech.ptargame5.web.model.TriviaQuestion;

@RestController
public class TriviaController {

	private static final Logger log = LoggerFactory.getLogger(TriviaController.class);

	@Autowired
	private DbAccess db;

	@GetMapping("/api/private/questions")
	public List<TriviaQuestion> getQuestions() {

		List<TriviaQuestion> result = db.getQuestions();
		log.info("Returning trivia questions: " + result.size());

		return result;
	}

	@GetMapping("/api/private/questions/stats")
	public Map<String, Map<String, Integer>> getQuestionStats() {

		List<GameResult> games = db.getGameResults();

		Map<String, Map<String, Integer>> stats = new HashMap<>();

		for (GameResult game : games) {
			if(game.getAnswers() == null) {
				continue;
			}
			for (GameResultAnswer answer : game.getAnswers()) {
				stats.putIfAbsent(answer.getQuestion(), new HashMap<>());
				int count = stats.get(answer.getQuestion()).getOrDefault(answer.getPlayerInput(), 0);
				stats.get(answer.getQuestion()).put(answer.getPlayerInput(), ++count);
			}
		}

		log.info("Returing question stats: " + stats);

		return stats;
	}
}
