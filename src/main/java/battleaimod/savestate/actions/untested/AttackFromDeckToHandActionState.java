package battleaimod.savestate.actions.untested;

import battleaimod.savestate.selectscreen.IGridSelectActionState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.unique.AttackFromDeckToHandAction;

public class AttackFromDeckToHandActionState implements IGridSelectActionState
{
    public AttackFromDeckToHandActionState(AbstractGameAction action) {
        this((AttackFromDeckToHandAction) action);
    }

    public AttackFromDeckToHandActionState(AttackFromDeckToHandAction action)
    {
        throw new RuntimeException("AttackFromDeckToHandActionState is not yet implemented!");
    }
    
    @Override
    public AbstractGameAction loadCurrentAction()
    {
        throw new RuntimeException("AttackFromDeckToHandActionState is not yet implemented!");
    }
}
