package battleaimod.savestate.actions.untested;

import basemod.ReflectionHacks;
import battleaimod.savestate.actions.ActionState;
import battleaimod.savestate.selectscreen.IGridSelectActionState;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.unique.DiscardPileToTopOfDeckAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import static battleaimod.patches.MonsterPatch.shouldGoFast;

public class DiscardPileToTopOfDeckActionState implements IGridSelectActionState
{
    public final AbstractCreature source;
    
    public DiscardPileToTopOfDeckActionState(AbstractGameAction action) {
        this((DiscardPileToTopOfDeckAction) action);
    }

    public DiscardPileToTopOfDeckActionState(DiscardPileToTopOfDeckAction action) {
        source = action.source;
    }

    @Override
    public DiscardPileToTopOfDeckAction loadAction() {
        return new DiscardPileToTopOfDeckAction(source);
    }

    @SpirePatch(
            clz = DiscardPileToTopOfDeckAction.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {AbstractCreature.class}
    )
    public static class NoFxConstructorPatchOther {
        public static void Postfix(DiscardPileToTopOfDeckAction _instance, AbstractCreature source) {
            // I don't know if this patch is necessary.
            if (shouldGoFast()) {
                ReflectionHacks
                        .setPrivate(_instance, AbstractGameAction.class, "duration", Settings.ACTION_DUR_FAST);
            }
        }
    }

    @SpirePatch(
            clz = DiscardPileToTopOfDeckAction.class,
            paramtypez = {},
            method = "update"
    )
    public static class NoDoubleExhumePatch {
        public static void Postfix(DiscardPileToTopOfDeckAction _instance) {
            // Force the action to stay in the the manager until cards are selected
            if (AbstractDungeon.isScreenUp) {   // TODO I guess? Not really sure.
                _instance.isDone = false;
            }
        }
    }
}
