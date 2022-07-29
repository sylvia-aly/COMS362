package coms362.cards.slapjack;

import java.util.Random;

import coms362.cards.abstractcomp.Move;
import coms362.cards.abstractcomp.Player;
import coms362.cards.abstractcomp.Table;
import coms362.cards.app.ViewFacade;
import coms362.cards.events.remote.CreateCardRemote;
import coms362.cards.events.remote.HideButtonRemote;
import coms362.cards.events.remote.HideCardRemote;
import coms362.cards.events.remote.SetGameTitleRemote;
import coms362.cards.events.remote.UpdateCardRemote;
import coms362.cards.events.remote.UpdatePileRemote;
import coms362.cards.fiftytwo.P52Rules;
import coms362.cards.generalclasses.DealButton;
import coms362.cards.model.Card;
import coms362.cards.model.Pile;

public class SJDealCommand implements Move {

	private Table table;
	private boolean updPile = false;

    public SJDealCommand(Table table, Player player) {
        this.table = table;
    }
	
	@Override
	public void apply(Table table) {
		// TODO Auto-generated method stub
        //The if statement is to prevent a double shuffle, as initcmd already does this for us
		if(!table.getPile(SJRules.CENTER_PILE).getCards().isEmpty()) {
			Pile pp1 = table.getPile(SJRules.PLAYER1_PILE);
			Pile pp2 = table.getPile(SJRules.PLAYER2_PILE);
			Pile cp = table.getPile(SJRules.CENTER_PILE);
			
			Card[] pp1temp = new Card[pp1.getCards().size()];
			Card[] pp2temp = new Card[pp2.getCards().size()];
			Card[] cptemp = new Card[cp.getCards().size()];
			Card[] newDeck = new Card[52];
			
			int counter = 0;
			int newDeckCounter = 0;
			//Next three for loops also put all cards into a single array to then be shuffled
			//Get temp deck of player 1
			for(Card c : pp1.getCards()) {
				pp1temp[counter] = c;
				newDeck[newDeckCounter] = pp1temp[counter];
				counter++;
				newDeckCounter++;
			}
			counter = 0;
			
			//get temp deck of player 2
			for(Card c : pp2.getCards()) {
				pp2temp[counter] = c;
				newDeck[newDeckCounter] = pp2temp[counter];
				counter++;
				newDeckCounter++;
			}
			counter = 0;
			
			//get temp deck of center
			for(Card c : cp.getCards()) {
				cptemp[counter] = c;
				newDeck[newDeckCounter] = cptemp[counter];
				counter++;
				newDeckCounter++;
			}
			counter = 0;
			
			//These for loops are to empty each pile
			for(int i = 0; i < pp1temp.length; i++) {
				table.removeFromPile(SJRules.PLAYER1_PILE, pp1temp[i]);
			}
			for(int i = 0; i < pp2temp.length; i++) {
				table.removeFromPile(SJRules.PLAYER2_PILE, pp2temp[i]);
			}
			for(int i = 0; i < cptemp.length; i++) {
				table.removeFromPile(SJRules.CENTER_PILE, cptemp[i]);
			}
			
			//Shuffle algo copy and pasted (credit to Sylvia Nguyen)
			Random rand = new Random();
			for (int i = 0; i < 52; i++) {
				int r = i + rand.nextInt(52 - i);
				Card temp = newDeck[r];
				newDeck[r] = newDeck[i];
				newDeck[i] = temp;

			}
			
			for(int i = 0; i < 52; i++) {
				
				if(i%2 == 0) {
					newDeck[i].setX(300);
					newDeck[i].setY(450);
					newDeck[i].setFaceUp(false);
					table.addToPile(SJRules.PLAYER1_PILE, newDeck[i]);
				}
				else {
					newDeck[i].setX(300);
					newDeck[i].setY(150);
					newDeck[i].setRotate(0);
					newDeck[i].setFaceUp(false);
					table.addToPile(SJRules.PLAYER2_PILE, newDeck[i]);

							
				}
			}
		}

	}

	@Override
	public void apply(ViewFacade views) {
		// TODO
		try {
			
			
			views.send(new UpdatePileRemote(table.getPile(SJRules.PLAYER1_PILE)));
			views.send(new UpdatePileRemote(table.getPile(SJRules.PLAYER2_PILE)));
			views.send(new UpdatePileRemote(table.getPile(SJRules.CENTER_PILE)));
			
			//views.send(new HideButtonRemote(views.getRemoteId(table.getPile(SJRules.PLAYER1_PILE).getRemoteId())));
			//views.send(new HideButtonRemote(views.getRemoteId(table.getPile(SJRules.PLAYER2_PILE).getRemoteId())));
			//views.send(new HideButtonRemote(views.getRemoteId(table.getPile(SJRules.CENTER_PILE).getRemoteId())));
            
			
            String remoteId = views.getRemoteId(DealButton.kSelector);
            views.send(new HideButtonRemote(remoteId));
            
            //Placeholder until shuffle and deal function is implemented
            Pile p1 = table.getPile(SJRules.PLAYER1_PILE);
            Pile p2 = table.getPile(SJRules.PLAYER2_PILE);
            if (p1 == null || p2 == null) {
                return;
            }
            for (Card c: p1.getCards()) {
            	views.send(new HideCardRemote(c));
            }
            for (Card c: p2.getCards()) {
            	views.send(new HideCardRemote(c));
            }
            for (Card c : p1.getCards()) {
                String outVal = "";
                views.send(new CreateCardRemote(c));
                views.send(new HideCardRemote(c));
                views.send(new UpdateCardRemote(c));
                System.out.println(outVal);
            }
            for (Card c : p2.getCards()) {
                String outVal = "";
                views.send(new CreateCardRemote(c));
                views.send(new HideCardRemote(c));
                views.send(new UpdateCardRemote(c));
                System.out.println(outVal);
            }
            views.send(new SetGameTitleRemote("SlapJack"));
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

}
