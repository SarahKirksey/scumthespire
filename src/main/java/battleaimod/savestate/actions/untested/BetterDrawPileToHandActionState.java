package battleaimod.savestate.actions.untested;

import basemod.ReflectionHacks;
import battleaimod.savestate.actions.CurrentActionState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.BetterDrawPileToHandAction;

public class BetterDrawPileToHandActionState implements CurrentActionState {
	private final int numberOfCards;
	private final boolean optional;
	
	public BetterDrawPileToHandActionState(AbstractGameAction action) {
		this((BetterDrawPileToHandAction) action);
	}
	
	public BetterDrawPileToHandActionState(BetterDrawPileToHandAction action)
	{
		this.numberOfCards = ReflectionHacks.getPrivate(action, BetterDrawPileToHandAction.class, "numberOfCards");
		this.optional = ReflectionHacks.getPrivate(action, BetterDrawPileToHandAction.class, "optional");
	}
	
	@Override
	public AbstractGameAction loadCurrentAction()
	{
		return new BetterDrawPileToHandAction(numberOfCards, optional);
	}
}
