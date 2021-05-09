package battleaimod.savestate.actions.untested;

import battleaimod.savestate.actions.ActionState;
import battleaimod.savestate.selectscreen.IGridSelectActionState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.defect.SeekAction;

public class SeekActionState implements IGridSelectActionState
{
    private final int amount;
    
    public SeekActionState(AbstractGameAction action) {
        this((SeekAction) action);
    }

    public SeekActionState(SeekAction action)
    {
        this.amount = action.amount;
    }
    
    @Override
    public AbstractGameAction loadCurrentAction()
    {
        return new SeekAction(amount);
    }
}
