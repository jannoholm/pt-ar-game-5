package com.playtech.ptargame5.web.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.playtech.ptargame5.web.db.DbAccess;
import com.playtech.ptargame5.web.leaderboard.LeaderboardManager;
import com.playtech.ptargame5.web.model.GameResult;
import com.playtech.ptargame5.web.model.GameResultAnswer;
import com.playtech.ptargame5.web.model.Player;
import com.playtech.ptargame5.web.model.TriviaQuestion;
import com.playtech.ptargame5.web.model.gamesession.EndGameSessionResponse;
import com.playtech.ptargame5.web.model.gamesession.GameSessionData;
import com.playtech.ptargame5.web.model.gamesession.NextQuestionRequest;
import com.playtech.ptargame5.web.model.gamesession.NextQuestionResponse;
import com.playtech.ptargame5.web.model.gamesession.TriviaQuestionResult;

@Component
public class GameSessionManager {

	private static final Logger log = LoggerFactory.getLogger(GameSessionManager.class);

	@Autowired
	private DbAccess db;
	
	@Autowired
	private LeaderboardManager leaderboard;

	private Random r = new Random();

	private List<TriviaQuestion> questions;
	private Map<Integer, List<TriviaQuestion>> questionsPerLevel = new HashMap<>();

	private Map<String, GameSessionData> sessions = new HashMap<>();

	@PostConstruct
	public void init() {
		questions = db.getQuestions();

		for (int i = 0; i < 12; i++) {
			questionsPerLevel.put(i, new ArrayList<TriviaQuestion>());
		}

		for (TriviaQuestion question : questions) {
			questionsPerLevel.get(question.getLevel()).add(question);
		}

		for (int i = 0; i < 12; i++) {
			log.info("Questions per level: " + i + "=" + questionsPerLevel.get(i).size());
		}

	}

	public String statNewSession(Player player) {
		GameSessionData session = new GameSessionData();
		session.setGameId(UUID.randomUUID().toString());
		session.setPlayer(player);
		session.setSessionStartTime(System.currentTimeMillis());

		sessions.put(session.getGameId(), session);

		return session.getGameId();
	}

	public NextQuestionResponse nextQuestion(NextQuestionRequest request) {

		if (!sessions.containsKey(request.getGameId())) {
			throw new IllegalStateException("Game session not found: " + request.getGameId());
		}

		GameSessionData session = sessions.get(request.getGameId());

		if (System.currentTimeMillis() - session.getSessionStartTime() > 65 * 1000) {
			throw new IllegalStateException("Session time is over");
		}

		boolean wasLastAnswerCorrect = false;
		if (session.getQuestionsAsked().size() != 0) { // Player has been asked at least one question
			TriviaQuestion lastQuestion = session.getQuestionsAsked().get(session.getQuestionsAsked().size() - 1);

			if (lastQuestion.getCorrect().lastIndexOf(request.getLastQuestionAnswer()) >= 0) {
				wasLastAnswerCorrect = true;
			}

			TriviaQuestionResult result = new TriviaQuestionResult();
			result.setCorrect(wasLastAnswerCorrect);
			result.setPlayerInput(request.getLastQuestionAnswer());
			result.setQuestion(lastQuestion);
			result.setTimeTaken(System.currentTimeMillis() - session.getLastQuestionTime());

			session.getQuestionsAnswered().add(result);
		}

		TriviaQuestion question = getNextQuestion(wasLastAnswerCorrect, session.getQuestionsAnswered());

		List<String> answers = new ArrayList<>(question.getAnswers());
		Collections.shuffle(answers);

		int currentSpree = calculateCurrentSpree(session.getQuestionsAnswered());
		int currentMultiplier = calculateCurrentMultiplier(currentSpree);

		NextQuestionResponse response = new NextQuestionResponse();
		response.setAnswers(answers);
		response.setCategory(question.getCategory());
		response.setCurrentMultiplier(currentMultiplier);
		response.setCurrentSpree(currentSpree);
		response.setLastQuestionCorrect(wasLastAnswerCorrect);
		response.setLevel(question.getLevel());
		response.setNewTotalScore(calculateTotalScore(session.getQuestionsAnswered()));
		response.setQuestion(question.getQuestion());

		session.getQuestionsAsked().add(question);
		session.setLastQuestionTime(System.currentTimeMillis());

		log.info("New game session state=" + session);

		return response;
	}

	public EndGameSessionResponse endGameSession(String gameId) {

		if (!sessions.containsKey(gameId)) {
			throw new IllegalStateException("Game session not found: " + gameId);
		}

		GameSessionData session = sessions.get(gameId);

		List<GameResultAnswer> answers = new ArrayList<>();
		for (TriviaQuestionResult result : session.getQuestionsAnswered()) {
			// GameResultAnswer is more compact data holder for statistical purposes, no need to save everything
			GameResultAnswer answer = new GameResultAnswer();
			answer.setPlayerInput(result.getPlayerInput());
			answer.setQuestion(result.getQuestion().getQuestion());
			answer.setTimeTaken(result.getTimeTaken());
			
			answers.add(answer);
		}
		
		GameResult gameResult = new GameResult();
		gameResult.setAnswers(answers);
		gameResult.setCorrectAnswers(calculateCorrectAnswers(session.getQuestionsAnswered()));
		gameResult.setGameId(gameId);
		gameResult.setNickname(session.getPlayer().getNickname());
		gameResult.setQuestionsAttempted(session.getQuestionsAnswered().size());
		gameResult.setTotalScore(calculateTotalScore(session.getQuestionsAnswered()));
		
		log.info("Storing new game result: " + gameResult);
		db.addGameResult(gameResult);

		EndGameSessionResponse response = new EndGameSessionResponse();
		response.setCorrectAnswers(gameResult.getCorrectAnswers());
		response.setQuestionsAttempted(gameResult.getQuestionsAttempted());
		response.setTotalScore(gameResult.getTotalScore());
		response.setNewLeaderboardPosition(leaderboard.getPlayerPosition(session.getPlayer()));

		log.info("Session ended: " + session);
		
		sessions.remove(gameId);
		
		return response;
	}

	private TriviaQuestion getNextQuestion(boolean wasLastAnswerCorrect, List<TriviaQuestionResult> questionsAnswered) {

		// TODO Don't give repeating questions
		if (wasLastAnswerCorrect) {
			TriviaQuestionResult lastAnswer = questionsAnswered.get(questionsAnswered.size() - 1);
			int lastQuestionLevel = lastAnswer.getQuestion().getLevel();

			if (lastQuestionLevel <= 10) {
				// Increase level by one each time
				List<TriviaQuestion> questions = questionsPerLevel.get(lastQuestionLevel + 1);
				return questions.get(r.nextInt(questions.size()));
			} else {
				// We can't go beyond level 10
				List<TriviaQuestion> questions = questionsPerLevel.get(lastQuestionLevel);
				return questions.get(r.nextInt(questions.size()));
			}
		} else {
			// Start from beginning if last answer was incorrect or it's a first question
			List<TriviaQuestion> questions = questionsPerLevel.get(1);
			return questions.get(r.nextInt(questions.size()));
		}
	}

	private int calculateTotalScore(List<TriviaQuestionResult> answers) {

		int totalScore = 0;
		int currentSpree = 1;

		for (TriviaQuestionResult result : answers) {
			if (result.isCorrect()) {

				totalScore += calculateCurrentMultiplier(currentSpree) * result.getQuestion().getLevel() * 100;

				currentSpree++;
			} else {
				currentSpree = 1;
			}
		}

		return totalScore;
	}

	private int calculateCurrentSpree(List<TriviaQuestionResult> questionsAnswered) {
		int currentSpree = 1;

		for (TriviaQuestionResult result : questionsAnswered) {
			if (result.isCorrect()) {
				currentSpree++;
			} else {
				currentSpree = 1;
			}
		}

		return currentSpree;
	}

	private int calculateCurrentMultiplier(int currentSpree) {
		// TODO More complex logic in defined starting from 10 questions
		return currentSpree;
	}

	private int calculateCorrectAnswers(List<TriviaQuestionResult> questionsAnswered) {
		int totalCorrect = 0;
		for (TriviaQuestionResult result : questionsAnswered) {
			if (result.isCorrect()) {
				totalCorrect++;
			}
		}
		return totalCorrect;
	}
}
