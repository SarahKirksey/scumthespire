package battleaimod.savestate.actions.grid;

import battleaimod.savestate.actions.ActionState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.unique.SkillFromDeckToHandAction;

public class SkillFromDeckToHandActionState implements ActionState
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
