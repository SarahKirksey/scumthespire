package battleaimod.savestate.actions.untested;

import battleaimod.savestate.selectscreen.IGridSelectActionState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.unique.SkillFromDeckToHandAction;

public class SkillFromDeckToHandActionState implements IGridSelectActionState
{
    public SkillFromDeckToHandActionState(AbstractGameAction action) {
        this((SkillFromDeckToHandAction) action);
    }

    public SkillFromDeckToHandActionState(SkillFromDeckToHandAction action)
    {
        throw new RuntimeException("SkillFromDeckToHandActionState is not yet implemented!");
    }
    
    @Override
    public AbstractGameAction loadAction()
    {
        throw new RuntimeException("SkillFromDeckToHandActionState is not yet implemented!");
    }
}
