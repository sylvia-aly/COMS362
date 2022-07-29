package coms362.cards.slapjack;

import coms362.cards.abstractcomp.GameFactory;
import coms362.cards.abstractcomp.Player;
import coms362.cards.abstractcomp.Rules;
import coms362.cards.abstractcomp.Table;
import coms362.cards.abstractcomp.View;
import coms362.cards.abstractcomp.ViewFactory;
import coms362.cards.generalclasses.GeneralPlayer;
import coms362.cards.generalclasses.GeneralPlayerView;
import coms362.cards.generalclasses.PartyRole;
import coms362.cards.model.PlayerFactory;
import coms362.cards.model.TableBase;
import coms362.cards.streams.RemoteTableGateway;

public class SJGameFactory implements GameFactory, PlayerFactory, ViewFactory{

	@Override
	public Rules createRules() {
		return new SJRules();
	}

	@Override
	public Table createTable() {
		return new TableBase(this);
	}

	@Override
	public View createView(PartyRole role, Integer num, String socketId, RemoteTableGateway gw ) {
		return new GeneralPlayerView(num, socketId, gw);
	}

	@Override
	public Player createPlayer( Integer position, String socketId) {
		return new GeneralPlayer(position, socketId);
	}

	@Override
	public PlayerFactory createPlayerFactory() {
		return this;
	}
	
}
