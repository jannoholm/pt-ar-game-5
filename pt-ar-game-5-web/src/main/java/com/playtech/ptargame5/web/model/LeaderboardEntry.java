package com.playtech.ptargame5.web.model;

import com.playtech.ptargame5.web.LogUtils;

import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;

@Document(collection = "leaderboard", schemaVersion = "1.0")
public class LeaderboardEntry implements Cloneable, Comparable<LeaderboardEntry>{
  private static final int DEFAULT_POSITION = 9999;
	@Id
	private String nickname;
	private int position;
	private int bestScore;
	private int correctAnswers;
	private int totalQuestions;
	private int gamesPlayed;
	private long lastModification;
	
	public LeaderboardEntry(){
	}
	
	public LeaderboardEntry(String nickName){
	  this.nickname = nickName;
	  this.position = DEFAULT_POSITION;
	  this.gamesPlayed = 1;
	}
	
	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getBestScore() {
		return bestScore;
	}

	public void setBestScore(int bestScore) {
		this.bestScore = bestScore;
	}
	
	
	public void combine(LeaderboardEntry o) {
	  this.bestScore = Math.max(bestScore, o.bestScore);
	  this.totalQuestions += o.totalQuestions;
	  this.correctAnswers += o.correctAnswers;
	  this.gamesPlayed += o.gamesPlayed;
	  this.lastModification = Math.max(this.lastModification, o.lastModification);	  
    
  }
	public int getCorrectAnswers() {
		return correctAnswers;
	}

	public void setCorrectAnswers(int correctAnswers) {
		this.correctAnswers = correctAnswers;
	}

	public int getTotalQuestions() {
		return totalQuestions;
	}

	public void setTotalQuestions(int totalQuestions) {
		this.totalQuestions = totalQuestions;
	}

	public int getGamesPlayed() {
		return gamesPlayed;
	}

	public void setGamesPlayed(int gamesPlayed) {
		this.gamesPlayed = gamesPlayed;
	}

	@Override
	public String toString() {
		return LogUtils.toString(this);
	}


	public long getLastModification() {
    return lastModification;
  }

  public void setLastModification(long lastModification) {
    this.lastModification = lastModification;
  }

  @Override
  public LeaderboardEntry clone() {
    try{
      return (LeaderboardEntry) super.clone();
    }catch (CloneNotSupportedException e) {throw new AssertionError(e); }
  }

  @Override
  public int compareTo(LeaderboardEntry o) {
    //best score wins
    int c = o.bestScore - bestScore; //i.e. reverse
    if (c != 0)
      return c;
    
    //time, 1st to arrive wins
    c = Long.compare(lastModification, o.lastModification);
    if (c != 0)
      return c;
    
    //games played/questions, lower is better
    c = gamesPlayed - o.gamesPlayed;
    if (c != 0)
      return c;
    
    c = totalQuestions - o.totalQuestions;
    if (c != 0)
      return c;
   
    c = o.correctAnswers - correctAnswers;//more correct answers is better
    if (c != 0)
      return c;
    
    return String.CASE_INSENSITIVE_ORDER.compare(nickname, o.nickname);//ahh well (alphabetical order)        
  }

  
	
}
