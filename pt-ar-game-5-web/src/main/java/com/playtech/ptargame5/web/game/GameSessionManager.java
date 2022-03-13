package com.playtech.ptargame5.web.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

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
	private Map<Integer, List<TriviaQuestion>> questionsPerLevel = new LinkedHashMap<>();

	private Map<String, GameSessionData> sessions = new LinkedHashMap<>();
	
	public static final int GAME_LENGTH_SECONDS = 120;
	public static final int MAX_QUESTIONS_PER_GAME = GAME_LENGTH_SECONDS;

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

		if (System.currentTimeMillis() - session.getSessionStartTime() > (GAME_LENGTH_SECONDS + 5) * 1000 + extraTimeAdded) {
			// 60 sec is the base time, 5 sec is buffer and extra time is from all questions extra total
			throw new IllegalStateException("Session time is over");
		}
		
		if (session.getQuestionsAsked().size() > MAX_QUESTIONS_PER_GAME) {
			throw new IllegalStateException("Too many questions per session");
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
		response.setLevel(calculateMultiplier(session.getQuestionsAnswered()));
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
	
	private static final TriviaQuestion[] EMPTY={};
	static final String showCaseCategory = System.getProperty("showCaseCategory");
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
		
		final String showCase = r.nextInt(8) == 0 ? showCaseCategory : null;
			
		
		for (int i = 0; i < 12; i++) {
			// An easy naive way to get rid of duplicate questions: create a clone list and remove them
			Collection<TriviaQuestion> poolOfQuestions = new LinkedHashSet<>(questionsPerLevel.get(nextQuestionLevel));
			
			session.getQuestionsAnswered().stream()
					.filter(e -> e.getQuestion().getCorrect().contains(e.getPlayerInput()))
					.map(TriviaQuestionResult::getQuestion)
					.forEach(poolOfQuestions::remove);

		  if (showCase != null){
		    poolOfQuestions.removeIf(question -> !showCase.equals(question.getCategory()));
		  }		  

		  else if (session.getPlayer().getInterests() == null || !session.getPlayer().getInterests().getDevelopment()) {
	      // If player is not interested in development, skip questions related to development
		    poolOfQuestions.removeIf(question -> question.getCategory().equals("Software Development"));
	    }
			
			if (poolOfQuestions.isEmpty()) {
				//in case we overflow the question level return to the beginning
				if (nextQuestionLevel > 10)
					nextQuestionLevel = 0;
				else
					nextQuestionLevel ++;
				
				continue;
			} 
			
			return poolOfQuestions.toArray(EMPTY)[r.nextInt(poolOfQuestions.size())];					
		}
		//weird?!?
		for (Collection<TriviaQuestion> c : questionsPerLevel.values()){
		  for (TriviaQuestion q : c){
		    if (!session.getQuestionsAsked().contains(q)){//1st not asked
		      return q;
		    }
		  }
		}
		//we can return any question, all of them have been asked already
		throw new IllegalStateException("too many questions, too much time");		
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
	  int correct = 0;
	  while (itr.hasPrevious()) {
	    TriviaQuestionResult result = itr.previous();

	    if (result.isCorrect()) {
	      multiplier =  multiplier > 8 ? multiplier : multiplier + 1;
	      totalScore += multiplier * 100;
	      correct++;
	    } else {
	      multiplier = multiplier - 2 < 0 ? 0 : multiplier - 2;
	    }
	  }

	  //S. Simeonoff  - penalty for button mash	  
	  if (answers.size() > GameResult.BUTTON_MASH_THRESHOLD && correct > 0){
	    float c = correct;
	    c /= answers.size();
	    c -=.3;
	    if (c < 0){
	      c*=-100;
	      int idx = (int) c;

	       int[] penalty = {1, 2, 4, 5, 7, 13};//each missing percent increases penalty, up to 13%
	       idx = Math.max(0, Math.min(idx, penalty.length-1));
	       totalScore *= 100- penalty[idx];
	       totalScore /= 100;	           	      
	    }	    	   
	  }
	  
	  return totalScore;
	}
	
	private int calculateMultiplier(List<TriviaQuestionResult> answers) {
		int multiplier = 0;
		for (TriviaQuestionResult result : answers) {
			if (result.isCorrect()) {
				multiplier =  multiplier > 8 ? multiplier : multiplier + 1;
			}
			else {
				multiplier = (multiplier - 2) < 0 ? 0 : multiplier - 2;
			}
		}
		
		return multiplier;
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
