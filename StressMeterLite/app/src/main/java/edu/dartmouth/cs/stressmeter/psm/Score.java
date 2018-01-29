package edu.dartmouth.cs.stressmeter.psm;

import java.io.Serializable;

public class Score implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int score;
	
	private String timestamp;
	
	public Score(String timestamp, int score){
		this.score=score;
		this.timestamp=timestamp;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	

}
