package coms362.cards.slapjack;

import coms362.cards.abstractcomp.RulesDispatchBase;
import coms362.cards.abstractcomp.Move;
import coms362.cards.abstractcomp.Player;
import coms362.cards.abstractcomp.Rules;
import coms362.cards.abstractcomp.RulesDispatch;
import coms362.cards.abstractcomp.Table;
import coms362.cards.events.inbound.CardEvent;
import coms362.cards.events.inbound.ConnectEvent;
import coms362.cards.events.inbound.DealEvent;
import coms362.cards.events.inbound.Event;
import coms362.cards.events.inbound.EventUnmarshallers;
import coms362.cards.events.inbound.GameRestartEvent;
import coms362.cards.events.inbound.InitGameEvent;
import coms362.cards.events.inbound.NewPartyEvent;
import coms362.cards.events.inbound.SetQuorumEvent;
import coms362.cards.abstractcomp.SetQuorumCmd;
import coms362.cards.generalclasses.CreatePlayerCmd;
import coms362.cards.generalclasses.DropEventCmd;
import coms362.cards.generalclasses.PartyRole;
import coms362.cards.model.Card;
import coms362.cards.model.Pile;

public class SJRules extends RulesDispatchBase
implements Rules, RulesDispatch {
	public static final String CENTER_PILE = "centerPile";
	public static final String PLAYER1_PILE = "player1Pile";
	public static final String PLAYER2_PILE = "player2Pile";
	public static int centerCount = 0;
	public static int player1Score = 0;
	public static int player2Score = 0;
	 
	public SJRules() {
		registerEvents();
	}
	public Move eval(Event nextE, Table table, Player player) {
		return nextE.dispatch(this, table, player);
	}
	
	public Move apply(CardEvent e, Table table, Player player){			
		
		//take cards from center pile and append to end of arrayList
		
		Pile centerPile = table.getPile(CENTER_PILE);
		Pile Player1Pile = table.getPile(PLAYER1_PILE);
		Pile Player2Pile = table.getPile(PLAYER2_PILE);
		centerPile.setFaceUp(true);
		Pile pile = null;
		Card c;
		if(table.getCurrentPlayer() != player) {
			return new DropEventCmd();
		}
		
		if(table.getCurrentPlayer().getScore() == 0) {
			table.getCurrentPlayer().addToScore(26);
		}
		centerCount = centerPile.getCards().size();
		if(table.getCurrentPlayer() == table.getPlayer(1)) {
			//check center pile first
			if(centerPile.getCard(e.getId()) != null) {
			  c = centerPile.getCard(e.getId());
			  if(c == null) { 
				  return new DropEventCmd(); 
			  } 
			  c.setFaceUp(true);
			  pile = Player1Pile;
			  table.addToScore(table.getCurrentPlayer(), centerCount);
			  if(c.isFaceUp()) {
				//centerPile = fromPile and pile = toPile
				  return new SJMove(c, player, centerPile, pile); 
			  }
			}
			 c = Player1Pile.getCard(e.getId());
			 if(c == null) {
				 return new DropEventCmd();
			 }
			 pile = Player1Pile;
			 table.addToScore(table.getCurrentPlayer(), -1);
			 table.setCurrentPlayer(2);
			 //pile = fromPile and centerPile = toPile
			 return new SJMove(c, player, pile, centerPile);	
		}
		else {
			if(centerPile.getCard(e.getId()) != null) {
				  c = centerPile.getCard(e.getId());
				  if(c == null) { 
					  return new DropEventCmd(); 
				  } 
				  c.setFaceUp(true);
				  pile = Player2Pile;
				  table.addToScore(table.getCurrentPlayer(), centerCount);
				  //centerPile = fromPile and pile = toPile
				  if(c.isFaceUp()) {
					  return new SJMove(c, player, centerPile, pile); 
				  }
				}
			 c = Player2Pile.getCard(e.getId());
			 if (c == null) {
					return new DropEventCmd();
			 }
			 pile = Player2Pile;
			 table.addToScore(table.getCurrentPlayer(), -1);
			 table.setCurrentPlayer(1);
			 //turnCount++;
		}
		//pile = fromPile and centerPile = toPile
		return new SJMove(c, player, pile, centerPile);	
	}
	
	public Move apply(DealEvent e, Table table, Player player){
		return new SJDealCommand(table, player);
	}
	
	public Move apply(InitGameEvent e, Table table, Player player){
		return new SJInitCmd(table.getPlayerMap(), "Slapjack", table);
	}
	
	public Move apply(NewPartyEvent e, Table table, Player player) {
		if (e.getRole() == PartyRole.player){
			return new CreatePlayerCmd( e.getPosition(), e.getSocketId());
		}
		return new DropEventCmd();
	}
	
//	public Move apply(ReDealEvent e, Table table, Player player) {
//		return new SJInitCmd(table.getPlayerMap(), "Slapjack", table);
//	}
	
	public Move apply(SetQuorumEvent e, Table table, Player player){
		return new SetQuorumCmd(e.getQuorum());
	}
	
	public Move apply(ConnectEvent e, Table table, Player player){
		Move rval = new DropEventCmd(); 
		System.out.println("Rules apply ConnectEvent "+e);
		if (!table.getQuorum().exceeds(table.getPlayers().size()+1)){
			if (e.getRole() == PartyRole.player){				
				rval = new CreatePlayerCmd( e.getPosition(), e.getSocketId());
			}			
		}
		System.out.println("SlapjackRules connectHandler rval = "+rval);
		return rval;
	}

	/**
	 * We rely on Rules to register the appropriate input events with
	 * the unmarshaller. This avoids excessive complexity in the 
	 * abstract factory and there is a natural dependency between 
	 * the rules and the game input events.  
	 */
	private void registerEvents() {
		EventUnmarshallers handlers = EventUnmarshallers.getInstance();
		handlers.registerHandler(InitGameEvent.kId, (Class) InitGameEvent.class); 
		handlers.registerHandler(DealEvent.kId, (Class) DealEvent.class); 
		handlers.registerHandler(CardEvent.kId, (Class) CardEvent.class); 
		handlers.registerHandler(GameRestartEvent.kId, (Class) GameRestartEvent.class); 
		handlers.registerHandler(NewPartyEvent.kId, (Class) NewPartyEvent.class);
	}
}

