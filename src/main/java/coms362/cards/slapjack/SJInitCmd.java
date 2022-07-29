package coms362.cards.slapjack;

import java.util.Map;
import java.util.Random;

import coms362.cards.abstractcomp.Move;
import coms362.cards.abstractcomp.Player;
import coms362.cards.abstractcomp.Table;
import coms362.cards.app.ViewFacade;
import coms362.cards.events.remote.CreateButtonRemote;
import coms362.cards.events.remote.CreatePileRemote;
import coms362.cards.events.remote.SetBottomPlayerTextRemote;
import coms362.cards.events.remote.SetGameTitleRemote;
import coms362.cards.events.remote.SetupTable;
import coms362.cards.fiftytwo.P52Rules;
import coms362.cards.generalclasses.DealButton;
import coms362.cards.model.Card;
import coms362.cards.model.Location;
import coms362.cards.model.Pile;

public class SJInitCmd implements Move {

	private Table table;
	private Map<Integer, Player> players;
	private String title;
	
	public SJInitCmd(Map<Integer, Player> players, String game, Table table) {
		this.players = players;
		this.title = game;
		this.table = table;
	}

	@Override
	public void apply(Table table) {
		Pile player1Pile = new Pile(SJRules.PLAYER1_PILE, new Location(300,550));
		Pile player2Pile = new Pile(SJRules.PLAYER2_PILE, new Location(550,300));
		Pile centerPile = new Pile(SJRules.CENTER_PILE, new Location(300,300));
		centerPile.setFaceUp(true);
		//An array to hold a un-shuffle deck of card
		Card[] c = new Card[52];
		// Placeholder for initializing deck and dealing cards
		int count = 0;
		try {
			for (String suit : Card.suits) {
				for (int i = 1; i <= 13; i++) {
					
						Card card = new Card();
						card.setSuit(suit);
						card.setRank(i);
						card.setX(300);
						card.setY(550);
						card.setRotate(0);
						card.setFaceUp(false);
						c[count] = card;
						// player1Pile.addCard(card);
						count++;
						continue;
				}
			}

			//shuffling algo on the deck of card c 
			Random rand = new Random();
			for (int i = 0; i < 52; i++) {
				int r = i + rand.nextInt(52 - i);
				Card temp = c[r];
				c[r] = c[i];
				c[i] = temp;

			}
			
			//splitting the shuffle deck card into two piles
			for(int i = 0; i < 52; i++) {
				
				if(i%2 == 0) {
					c[i].setX(300);
					c[i].setY(450);
				player1Pile.addCard(c[i]);	
				}
				else {
					c[i].setX(300);
					c[i].setY(150);
					c[i].setRotate(0);
					player2Pile.addCard(c[i]);		
				}	
			}
			table.addPile(player1Pile);
			table.addPile(player2Pile);
			table.addPile(centerPile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void apply(ViewFacade view) {
		view.send(new SetupTable());
		view.send(new SetGameTitleRemote(title));
		
		for (Player p : players.values()){
			String role = (p.getPlayerNum() == 1) ? "Dealer" : "Player "+p.getPlayerNum();
			view.send(new SetBottomPlayerTextRemote(role, p));
		}
		
		// Still need to create Center and Player piles
		view.send(new CreatePileRemote(table.getPile(SJRules.CENTER_PILE)));
		view.send(new CreatePileRemote(table.getPile(SJRules.PLAYER1_PILE)));
		view.send(new CreatePileRemote(table.getPile(SJRules.PLAYER2_PILE)));
		DealButton dealButton = new DealButton("DEAL", new Location(0, 0));
		view.register(dealButton); //so we can find it later. 
		view.send(new CreateButtonRemote(dealButton));
	}

}
