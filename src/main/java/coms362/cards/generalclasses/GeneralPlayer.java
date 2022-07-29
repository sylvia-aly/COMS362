package coms362.cards.generalclasses;

import coms362.cards.abstractcomp.Player;

public class GeneralPlayer implements Player {

	private int score = 0;
	private int playerNum;
	private String socketId = null; 
	
	public GeneralPlayer(int i) {
		this.playerNum = i;
	}

	public GeneralPlayer(Integer position, String socketId) {
		playerNum = position;
		this.socketId = socketId;
	}

	public int addToScore(int amount) {
		return score += amount;
	}

	public int getPlayerNum() {
		return playerNum;
	}
	
	public String getSocketId(){
		return socketId;
	}

	@Override
	public int getScore() {
		return score;
	}
	
	public void setScore(int score) {
		this.score = score;
	}
	
	@Override 
	public String toString(){
		return String.format("Player: pos=%d, socket=%s, score=%d%n" , playerNum, socketId, score);
	}


}
