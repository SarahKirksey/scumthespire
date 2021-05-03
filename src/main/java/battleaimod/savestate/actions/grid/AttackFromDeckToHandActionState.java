package battleaimod.savestate.actions.grid;

import battleaimod.savestate.actions.ActionState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.unique.AttackFromDeckToHandAction;

public class AttackFromDeckToHandActionState implements ActionState
{
    public AttackFromDeckToHandActionState(AbstractGameAction action) {
        this((AttackFromDeckToHandAction) action);
    }

    public AttackFromDeckToHandActionState(AttackFromDeckToHandAction action)
    {
        throw new RuntimeException("AttackFromDeckToHandActionState is not yet implemented!");
    }
    
    @Override
    public AbstractGameAction loadAction()
    {
        throw new RuntimeException("AttackFromDeckToHandActionState is not yet implemented!");
    }
}
