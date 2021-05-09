package battleaimod.savestate.actions.untested;

import battleaimod.savestate.selectscreen.IGridSelectActionState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.unique.AttackFromDeckToHandAction;

public class AttackFromDeckToHandActionState implements IGridSelectActionState
{
    private final int amount;
    
    public AttackFromDeckToHandActionState(AbstractGameAction action) {
        this((AttackFromDeckToHandAction) action);
    }

    public AttackFromDeckToHandActionState(AttackFromDeckToHandAction action)
    {
        this.amount = action.amount;
    }
    
    @Override
    public AbstractGameAction loadCurrentAction()
    {
        return new AttackFromDeckToHandAction(this.amount);
    }
}
