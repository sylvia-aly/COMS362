package coms362.cards.slapjack;

import coms362.cards.abstractcomp.Move;
import coms362.cards.abstractcomp.Player;
import coms362.cards.abstractcomp.Table;
import coms362.cards.app.ViewFacade;
import coms362.cards.events.remote.AddToPileRemote;
import coms362.cards.events.remote.HideCardRemote;
import coms362.cards.events.remote.RemoveFromPileRemote;
import coms362.cards.events.remote.SetGameTitleRemote;
import coms362.cards.events.remote.ShowCardRemote;
import coms362.cards.events.remote.ShowPlayerScore;
import coms362.cards.events.remote.ShowButtonRemote;
import coms362.cards.generalclasses.DealButton;
import coms362.cards.model.Card;
import coms362.cards.model.Pile;

public class SJMove implements Move {

	private Card c;
	private Player p;
	private Pile playerPile;
	private Pile centerPile;
	private boolean isMatchOver;
	private int playerWin;
	//private int cardCount = 0;
	
	
	public SJMove(Card c, Player p, Pile playerPile, Pile centerPile){
		this.c = c;
		this.p = p;
		this.playerPile = playerPile;
		this.centerPile = centerPile;
		isMatchOver = false;
	}
	
	@Override
	public void apply(Table table) {
		//applies whatever move to the table
		if(p == table.getPlayer(1)) {
			if(this.c.getRank() == 11) {
				//a jack has a rank of 11
				table.removeFromPile(SJRules.CENTER_PILE, c);
				c.setX(300);
				c.setY(450);
				table.addToPile(SJRules.PLAYER1_PILE, c);
			}
			table.removeFromPile(SJRules.PLAYER1_PILE, c);
			table.addToPile(SJRules.CENTER_PILE, c);
			//add the center amount to the player's score
			table.setCurrentPlayer(2);
			//win condition
			if(table.getPile(SJRules.PLAYER1_PILE).getCards().isEmpty() || p.getScore() >= 52){
				//table.setMatchOver(true);
				isMatchOver = true;
				playerWin = 2;
			}
		}
		else {
			if(c.getRank() == 11) {
				//a jack has a rank of 11
				table.removeFromPile(SJRules.CENTER_PILE, c);
				c.setX(300);
				c.setY(150);
				table.addToPile(SJRules.PLAYER2_PILE, c);
			}
			table.removeFromPile(SJRules.PLAYER2_PILE, c);
			table.addToPile(SJRules.CENTER_PILE, c);
			table.setCurrentPlayer(1);
			//win condition
			if(table.getPile(SJRules.PLAYER2_PILE).getCards().isEmpty() || p.getScore() >= 52){
				//table.setMatchOver(true);
				isMatchOver = true;
				playerWin = 1;
			}
		}
	}
	
	@Override
	public void apply(ViewFacade view) {
		if(isMatchOver) {
			view.send(new SetGameTitleRemote("Player " + playerWin + " Wins"));
			String remoteId = view.getRemoteId(DealButton.kSelector);
			view.send(new ShowButtonRemote(remoteId));
			isMatchOver = false;
		}
		
		view.send(new HideCardRemote(c));
		if(c.getRank() == 11 && c.isFaceUp()) {
			view.send(new RemoveFromPileRemote(centerPile, c));
			view.send(new AddToPileRemote(playerPile, c));
		}
		else {
			view.send(new RemoveFromPileRemote(playerPile, c));
			view.send(new AddToPileRemote(centerPile, c));
		}
		view.send(new ShowCardRemote(c));
		view.send(new ShowPlayerScore(p, null));
		

	}
}
