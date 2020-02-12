package com.playtech.ptargame5.web.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.playtech.ptargame5.web.db.DbAccess;
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
}
