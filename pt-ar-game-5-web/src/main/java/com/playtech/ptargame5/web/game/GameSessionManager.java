package com.playtech.ptargame5.web.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.ListIterator;

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

		// Extra time for each question, in-memory only, not saved to dB
		for (TriviaQuestion question : questions) {
			log.info("Calculating question extra time");
			int totalLength = question.getQuestion().length();
			for (String answer : question.getAnswers()) {
				totalLength += answer.length();
			}

			if (totalLength > 400) {
				question.setExtraTime(4000);
			} else if (totalLength > 200) {
				question.setExtraTime(2000);
			} else {
				question.setExtraTime(0);
			}
		}

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

		int extraTimeAdded = 0;
		for (TriviaQuestion question : session.getQuestionsAsked()) {
			extraTimeAdded += question.getExtraTime();
		}

		if (System.currentTimeMillis() - session.getSessionStartTime() > 65 * 1000 + extraTimeAdded) {
			// 60 sec is the base time, 5 sec is buffer and extra time is from all questions extra total
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

		TriviaQuestion question = getNextQuestion(wasLastAnswerCorrect, session);

		List<String> answers = new ArrayList<>(question.getAnswers());
		Collections.shuffle(answers);

		NextQuestionResponse response = new NextQuestionResponse();
		response.setAnswers(answers);
		response.setCategory(question.getCategory());
		response.setExtraTimeAdded(question.getExtraTime());
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
			answer.setAnsweredCorrectly(result.isCorrect());
			answer.setCategory(result.getQuestion().getCategory());
			// There is always at least one correct answer...
			answer.setCorrectAnswer(result.getQuestion().getCorrect().get(0));
			answer.setLevel(result.getQuestion().getLevel());
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

	private TriviaQuestion getNextQuestion(boolean wasLastAnswerCorrect, GameSessionData session) {

		int nextQuestionLevel = 1;

		if (session.getQuestionsAnswered().size() > 0) {
			// Player has answered at least one question
			TriviaQuestionResult lastAnswer = session.getQuestionsAnswered().get(session.getQuestionsAnswered().size() - 1);
			int lastQuestionLevel = lastAnswer.getQuestion().getLevel();

			if (wasLastAnswerCorrect) {
				// Increase level by one each time, but we can't go beyond level 10
				nextQuestionLevel = Math.min(10, lastQuestionLevel + 1);
			} else {
				// Decrease level by one, but we can't below level 1
				nextQuestionLevel = Math.max(1, lastQuestionLevel - 1);
			}
		}
		
		List<TriviaQuestion> poolOfQuestions = new ArrayList<>();
		
		for (int i = 0; i < 12; i++) {
			// An easy naive way to get rid of duplicate questions: create a clone list and remove them
			poolOfQuestions = new ArrayList<>(questionsPerLevel.get(nextQuestionLevel));
			poolOfQuestions.removeAll(session.getQuestionsAsked());
			
			if (poolOfQuestions.size() == 0) {
				//in case we overflow the question level return to the beginning
				if (nextQuestionLevel > 10)
					nextQuestionLevel = 0;
				else
					nextQuestionLevel ++;
				continue;
			}
				
		}
		

		// If player is not interested in development, skip questions related to development
		if (session.getPlayer().getInterests() == null || !session.getPlayer().getInterests().getDevelopment()) {
			poolOfQuestions.removeIf(question -> question.getCategory().equals("Software Development"));
		}

		/*
		if (poolOfQuestions.size() < 1) {
			// If for some obscure reason we run out of questions, fallback to default list
			poolOfQuestions = questionsPerLevel.get(nextQuestionLevel > 1 ? nextQuestionLevel - 1: nextQuestionLevel);
		}
		*/

		return poolOfQuestions.get(r.nextInt(poolOfQuestions.size()));
	}

	private int calculateTotalScore(List<TriviaQuestionResult> answers) {

		int totalScore = 0;

		/*
		// This could be optimized by storing latest score and just adding last question score on top
		for (TriviaQuestionResult result : answers) {
			if (result.isCorrect()) {
				totalScore += result.getQuestion().getLevel() * result.getQuestion().getLevel() * 100;
			}
		}
		*/
		
		// reverse iterate to find combo
		ListIterator<TriviaQuestionResult> itr = answers.listIterator(answers.size());
		int multiplier = 0;
		while (itr.hasPrevious()) {
            TriviaQuestionResult result = itr.previous();
                        
			if (result.isCorrect()) {
				multiplier ++;
				totalScore += multiplier * 100;
			} else {
				multiplier = multiplier - 2 < 0 ? 0 : multiplier - 2;
			}
        }

		return totalScore;
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
