package battleaimod.savestate.actions.grid;

import battleaimod.savestate.actions.ActionState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.defect.SeekAction;

public class SeekActionState implements ActionState
{
    public SeekActionState(AbstractGameAction action) {
        this((SeekAction) action);
    }

    public SeekActionState(SeekAction action)
    {
        throw new RuntimeException("SeekActionState is not yet implemented!");
    }
    
    @Override
    public AbstractGameAction loadAction()
    {
        throw new RuntimeException("SeekActionState is not yet implemented!");
    }
}
