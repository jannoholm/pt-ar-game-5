package com.playtech.ptargame5.web.model;

import java.util.List;

import com.playtech.ptargame5.web.LogUtils;

import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;

@Document(collection = "games", schemaVersion = "1.0")
public class GameResult implements Comparable<GameResult> {

	@Id
	private String gameId;
	private String nickname;
	private Integer questionsAttempted;
	private Integer correctAnswers;
	private Integer totalScore;
	long time;
	private List<GameResultAnswer> answers;
	
	public GameResult(){
	  this.time = System.currentTimeMillis();
	}
	
	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public Integer getQuestionsAttempted() {
		return questionsAttempted;
	}

	public void setQuestionsAttempted(Integer questionsAttempted) {
		this.questionsAttempted = questionsAttempted;
	}

	public Integer getCorrectAnswers() {
		return correctAnswers;
	}

	public void setCorrectAnswers(Integer correctAnswers) {
		this.correctAnswers = correctAnswers;
	}

	public Integer getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(Integer totalScore) {
		this.totalScore = totalScore;
	}

	public List<GameResultAnswer> getAnswers() {
		return answers;
	}

	public void setAnswers(List<GameResultAnswer> answers) {
		this.answers = answers;
	}

	public long getTime(){
	  return time;
	}
	
	public void setTime(long time) {
    this.time = time;
  }

  @Override
	public String toString() {
		return LogUtils.toString(this);
	}

  @Override
  public int compareTo(GameResult o) {
    int c = Long.compare(time, o.time);
    if (c!=0)
      return c;
    c = nickname.compareTo(o.getNickname());
    if (c!=0)
      return c;
    return gameId.compareTo(o.gameId);  
  }
  
}
