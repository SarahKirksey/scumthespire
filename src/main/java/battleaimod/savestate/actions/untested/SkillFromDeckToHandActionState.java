package battleaimod.savestate.actions.untested;

import battleaimod.savestate.selectscreen.IGridSelectActionState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.unique.SkillFromDeckToHandAction;

public class SkillFromDeckToHandActionState implements IGridSelectActionState
{
    private final int amount;
    
    public SkillFromDeckToHandActionState(AbstractGameAction action) {
        this((SkillFromDeckToHandAction) action);
    }
    
    public SkillFromDeckToHandActionState(SkillFromDeckToHandAction action)
    {
        this.amount = action.amount;
    }
    
    @Override
    public AbstractGameAction loadCurrentAction()
    {
        return new SkillFromDeckToHandAction(this.amount);
    }
}
