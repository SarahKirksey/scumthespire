package battleaimod.savestate.actions;

import basemod.ReflectionHacks;
import battleaimod.fastobjects.actions.DiscardCardActionFast;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class DiscardActionState implements ActionState
{
    private final boolean isRandom;
    private final boolean endTurn;
    public final int numDiscarded;
    
    public final int amount;
    public final AbstractCreature target;
    public final AbstractCreature source;
    
    
    public DiscardActionState(DiscardAction action) {
        numDiscarded = DiscardAction.numDiscarded;
        endTurn = ReflectionHacks.getPrivate(action, DiscardAction.class, "endTurn");
        isRandom = ReflectionHacks.getPrivate(action, DiscardAction.class, "isRandom");
        
        amount = action.amount;
        target = action.target;
        source = action.source;
    }
    
    public DiscardActionState(DiscardCardActionFast action) {
        numDiscarded = DiscardAction.numDiscarded;
        endTurn = ReflectionHacks.getPrivate(action, DiscardCardActionFast.class, "endTurn");
        isRandom = ReflectionHacks.getPrivate(action, DiscardCardActionFast.class, "isRandom");
        
        amount = action.amount;
        target = action.target;
        source = action.source;
    }

    @Override
    public DiscardCardActionFast loadAction() {
        DiscardCardActionFast result = new DiscardCardActionFast(target, source, amount, isRandom, endTurn);
        DiscardAction.numDiscarded = numDiscarded;

        // This will make the action only trigger the second half of the update
        result.secondHalfOnly = true;

        return result;
    }

    @SpirePatch(
            clz = DiscardAction.class,
            paramtypez = {},
            method = "update"
    )
    public static class NoDoubleDiscardPatch {
        public static void Postfix(DiscardAction _instance) {
            // Force the action to stay in the the manager until cards are selected
            if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved && AbstractDungeon.isScreenUp) {
                _instance.isDone = false;
            }
        }
    }
}
